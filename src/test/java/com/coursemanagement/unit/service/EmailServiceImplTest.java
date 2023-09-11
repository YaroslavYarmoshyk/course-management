package com.coursemanagement.unit.service;

import com.coursemanagement.config.properties.CourseProperties;
import com.coursemanagement.exeption.SystemException;
import com.coursemanagement.exeption.enumeration.SystemErrorCode;
import com.coursemanagement.service.EmailService;
import com.coursemanagement.service.impl.EmailServiceImpl;
import jakarta.mail.Address;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;

import java.util.function.Consumer;
import java.util.stream.Stream;

import static com.coursemanagement.util.AssertionsUtils.assertThrowsWithMessage;
import static com.coursemanagement.util.TestDataUtils.FIRST_STUDENT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.mockito.Mockito.*;

@ExtendWith(value = MockitoExtension.class)
class EmailServiceImplTest {

    @InjectMocks
    private EmailServiceImpl emailService;
    @Mock
    private JavaMailSender javaMailSender;
    @Mock
    private CourseProperties courseProperties;
    @Captor
    private ArgumentCaptor<MimeMessagePreparator> messageCaptor;

    private static final String TEST_BASE_URL = "https://test.com";
    private static final String CONFIRMATION_TOKEN = "confirmationToken";

    @BeforeEach
    void setUp() {
        when(courseProperties.getBaseUrl()).thenReturn(TEST_BASE_URL);
    }

    @TestFactory
    @DisplayName("Test email creation")
    Stream<DynamicTest> testEmailCreation() {
        return Stream.of(
                dynamicTest("Test email confirmation",
                        () -> testSuccessfulFlow(
                                (emailService) -> emailService.sendEmailConfirmation(FIRST_STUDENT, CONFIRMATION_TOKEN),
                                "Confirm your email"
                        )),
                dynamicTest("Test reset password confirmation",
                        () -> testSuccessfulFlow(
                                (emailService) -> emailService.sendResetPasswordConfirmation(FIRST_STUDENT, CONFIRMATION_TOKEN),
                                "Password Reset Request"
                        )),
                dynamicTest("Test email confirmation sending failure",
                        () -> testFailureFlow((emailService) -> emailService.sendEmailConfirmation(FIRST_STUDENT, CONFIRMATION_TOKEN))),
                dynamicTest("Test reset password confirmation sending failure",
                        () -> testFailureFlow((emailService) -> emailService.sendResetPasswordConfirmation(FIRST_STUDENT, CONFIRMATION_TOKEN)))
        );

    }

    void testSuccessfulFlow(Consumer<EmailService> emailServiceConsumer, final String expectedSubject) throws Exception {
        emailServiceConsumer.accept(emailService);
        verify(javaMailSender, atLeastOnce()).send(messageCaptor.capture());

        final MimeMessage mimeMessage = new MimeMessage((Session) null);
        messageCaptor.getValue().prepare(mimeMessage);

        assertEquals(1, mimeMessage.getAllRecipients().length);

        final Address recipientEmail = mimeMessage.getAllRecipients()[0];
        final Address sender = mimeMessage.getFrom()[0];
        final String subject = mimeMessage.getSubject();
        assertEquals(FIRST_STUDENT.getEmail(), recipientEmail.toString());
        assertEquals("course-administration <course-administration>", sender.toString());
        assertEquals(expectedSubject, subject);
    }

    void testFailureFlow(Consumer<EmailService> emailServiceConsumer) {
        doThrow(new SystemException("Sending email confirmation was failed", SystemErrorCode.INTERNAL_SERVER_ERROR))
                .when(emailServiceConsumer).accept(emailService);

        assertThrowsWithMessage(
                () -> emailServiceConsumer.accept(emailService),
                SystemException.class,
                "Sending email confirmation was failed"
        );
    }
}
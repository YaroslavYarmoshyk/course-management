package com.coursemanagement.service.impl;

import com.coursemanagement.config.properties.CourseProperties;
import com.coursemanagement.exeption.SystemException;
import com.coursemanagement.exeption.enumeration.SystemErrorCode;
import com.coursemanagement.model.User;
import com.coursemanagement.service.EmailService;
import com.coursemanagement.util.EmailUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

import static com.coursemanagement.util.Constants.EMAIL_CONFIRMATION_SUBJECT;
import static com.coursemanagement.util.Constants.EMAIL_CONFIRMATION_URL;
import static com.coursemanagement.util.Constants.EMAIL_SENDER;
import static com.coursemanagement.util.Constants.RESET_PASSWORD_CONFIRMATION_SUBJECT;
import static com.coursemanagement.util.Constants.RESET_PASSWORD_CONFIRMATION_URL;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender javaMailSender;
    private final CourseProperties courseProperties;

    @Override
    public void sendEmailConfirmation(final User user, final String token) {
        final String confirmationUrl = courseProperties.getBaseUrl() + EMAIL_CONFIRMATION_URL + token;
        final String context = EmailUtils.getEmailConfirmationTemplate(confirmationUrl);
        sendEmail(user, EMAIL_CONFIRMATION_SUBJECT, context);
    }

    @Override
    public void sendResetPasswordConfirmation(final User user, final String token) {
        final String confirmationUrl = courseProperties.getBaseUrl() + RESET_PASSWORD_CONFIRMATION_URL + token;
        final String context = EmailUtils.getResetPasswordTemplate(confirmationUrl);
        sendEmail(user, RESET_PASSWORD_CONFIRMATION_SUBJECT, context);
    }

    private void sendEmail(final User user, final String subject, final String context) {
        try {
            final MimeMessagePreparator mailMessage = mimeMessage -> {
                final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.name());
                message.setFrom(EMAIL_SENDER, EMAIL_SENDER);
                message.setTo(user.getEmail());
                message.setSubject(subject);
                message.setText(context, true);
            };
            javaMailSender.send(mailMessage);
        } catch (Exception e) {
            throw new SystemException("Sending email confirmation was failed", SystemErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}

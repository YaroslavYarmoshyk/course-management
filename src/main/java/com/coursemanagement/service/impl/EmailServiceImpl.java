package com.coursemanagement.service.impl;

import com.coursemanagement.exeption.SystemException;
import com.coursemanagement.model.User;
import com.coursemanagement.service.EmailService;
import com.coursemanagement.util.EmailUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

import static com.coursemanagement.util.Constants.EMAIL_CONFIRMATION_SUBJECT;
import static com.coursemanagement.util.Constants.EMAIL_CONFIRMATION_URL;
import static com.coursemanagement.util.Constants.EMAIL_SENDER;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender javaMailSender;
    @Value("${course-management.url.base}")
    private String baseUrl;

    @Override
    public void sendEmailConfirmation(final User user, final String token) {
        try {
            final MimeMessagePreparator mailMessage = mimeMessage -> {
                final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.name());
                message.setFrom(EMAIL_SENDER, EMAIL_SENDER);
                message.setTo(user.getEmail());
                message.setSubject(EMAIL_CONFIRMATION_SUBJECT);
                final String confirmationUrl = baseUrl + EMAIL_CONFIRMATION_URL + token;
                message.setText(EmailUtils.getEmailConfirmationTemplate(confirmationUrl), true);
            };
            javaMailSender.send(mailMessage);
        } catch (Exception e) {
            throw new SystemException("Sending email confirmation was failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

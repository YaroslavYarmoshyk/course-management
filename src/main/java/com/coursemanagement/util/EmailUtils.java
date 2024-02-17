package com.coursemanagement.util;

import com.coursemanagement.exception.SystemException;
import com.coursemanagement.exception.enumeration.SystemErrorCode;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static com.coursemanagement.util.Constants.EMAIL_CONFIRMATION_TEMPLATE_PATH;
import static com.coursemanagement.util.Constants.RESET_PASSWORD_CONFIRMATION_TEMPLATE_PATH;

public final class EmailUtils {
    private static final String VERIFY_EMAIL_LINK_ID = "verify-button-link";
    private static final String RESET_PASSWORD_LINK_ID = "reset-password-link";

    public static String getEmailConfirmationTemplate(final String confirmationUrl) {
        return getTemplate(confirmationUrl, EMAIL_CONFIRMATION_TEMPLATE_PATH, VERIFY_EMAIL_LINK_ID);
    }

    public static String getResetPasswordTemplate(final String confirmationUrl) {
        return getTemplate(confirmationUrl, RESET_PASSWORD_CONFIRMATION_TEMPLATE_PATH, RESET_PASSWORD_LINK_ID);
    }

    private static String getTemplate(final String confirmationUrl, final String filePath, final String elementId) {
        try (final InputStream inputStream = EmailUtils.class.getResourceAsStream(filePath)) {
            final String htmlContent = IOUtils.toString(Objects.requireNonNull(inputStream), StandardCharsets.UTF_8);
            final Document document = Jsoup.parse(htmlContent, "", Parser.xmlParser());
            final Element link = document.getElementById(elementId);
            if (Objects.nonNull(link)) {
                link.attr("href", confirmationUrl);
            }
            return document.toString();
        } catch (Exception e) {
            throw new SystemException("Cannot provide template", SystemErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}

package com.coursemanagement.util;

import com.coursemanagement.enumeration.SystemErrorCode;
import com.coursemanagement.exeption.SystemException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
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
        try {
            final Document document = Jsoup.parse(new File(filePath), StandardCharsets.UTF_8.name());
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

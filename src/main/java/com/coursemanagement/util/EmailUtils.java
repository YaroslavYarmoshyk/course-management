package com.coursemanagement.util;

import com.coursemanagement.exeption.SystemException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.http.HttpStatus;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static com.coursemanagement.util.Constants.EMAIL_CONFIRMATION_TEMPLATE_PATH;

public final class EmailUtils {

    public static String getEmailConfirmationTemplate(final String confirmationUrl) {
        try {
            final Document document = Jsoup.parse(new File(EMAIL_CONFIRMATION_TEMPLATE_PATH), StandardCharsets.UTF_8.name());
            final Element link = document.getElementById("verify-button-link");
            if (Objects.nonNull(link)) {
                link.attr("href", confirmationUrl);
            }
            return document.toString();
        } catch (Exception e) {
            throw new SystemException("Cannot provide template", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

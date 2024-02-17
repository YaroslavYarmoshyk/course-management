package com.coursemanagement.util;

import com.coursemanagement.exception.SystemException;
import com.coursemanagement.exception.enumeration.SystemErrorCode;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import jakarta.mail.Multipart;
import jakarta.mail.Part;
import jakarta.mail.internet.MimeMessage;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageUtils {

    public static MimeMessage getFirstReceivedMimeMessage(final GreenMailExtension greenMailExtension) {
        final MimeMessage[] receivedMessages = greenMailExtension.getReceivedMessages();
        return receivedMessages[0];
    }


    public static String getTokenFromMessage(final MimeMessage mimeMessage) throws Exception {
        final Pattern pattern = Pattern.compile("token=(.*?)\"");
        final String htmlContent = extractHtmlFromMimeMessage(mimeMessage);
        final Matcher matcher = pattern.matcher(htmlContent);

        if (matcher.find()) {
            return URLDecoder.decode(matcher.group(1), StandardCharsets.UTF_8);
        }
        throw new SystemException("Cannot find token in html content of message: " + mimeMessage, SystemErrorCode.INTERNAL_SERVER_ERROR);
    }

    private static String extractHtmlFromMimeMessage(final MimeMessage mimeMessage) throws Exception {
        final Object content = mimeMessage.getContent();

        if (content instanceof final Multipart multipart) {
            final int count = multipart.getCount();
            for (int i = 0; i < count; i++) {
                final Part part = multipart.getBodyPart(i);
                if (part.isMimeType("multipart/related")) {
                    return extractHtmlFromMultipartRelated((Multipart) part.getContent());
                }
            }
        }
        throw new SystemException("Cannot get html from mime message: " + mimeMessage, SystemErrorCode.INTERNAL_SERVER_ERROR);
    }

    private static String extractHtmlFromMultipartRelated(final Multipart multipart) throws Exception {
        final int count = multipart.getCount();
        for (int i = 0; i < count; i++) {
            final Part part = multipart.getBodyPart(i);
            if (part.isMimeType("text/html")) {
                return part.getContent().toString();
            }
        }
        throw new SystemException("Cannot get html from mime message part: " + multipart, SystemErrorCode.INTERNAL_SERVER_ERROR);
    }
}

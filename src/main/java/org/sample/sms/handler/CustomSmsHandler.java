package org.sample.sms.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.event.IdentityEventException;
import org.wso2.carbon.identity.event.event.Event;
import org.wso2.carbon.identity.event.handler.notification.DefaultNotificationHandler;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class CustomSmsHandler extends DefaultNotificationHandler {
    private static final Log log = LogFactory.getLog(CustomSmsHandler.class);

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            .withLocale(Locale.UK)
            .withZone(ZoneId.of("UTC"));

    private final AtomicLong retrievalReferenceNumberGen = new AtomicLong(123456789012L);  // 12-digit number

    @Override
    protected Map<String, String> buildNotificationData(final Event event) throws IdentityEventException {
        final Map<String, String> defaultResponse = super.buildNotificationData(event);

        try {
            final Long retrievalReferenceNumber = retrievalReferenceNumberGen.getAndIncrement();
            final String now = formatter.format(Instant.now());
            final String rrnHeaderVal = String.format("%s-%s", retrievalReferenceNumber, now);

            defaultResponse.put(Constants.X_RETRIEVAL_REFERENCE_NUMBER, rrnHeaderVal);
        } catch (final Exception e) {
            log.error("Error while generating retrieval reference number, returning default values.", e);
        }

        return defaultResponse;
    }

    @Override
    public String getName() {
        return "customSmsHandler";
    }

    private static final class Constants {
        private static final String X_RETRIEVAL_REFERENCE_NUMBER = "X-Retrieval-Reference-Number";
    }
}

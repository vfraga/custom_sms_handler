package org.sample.sms.handler.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.sample.sms.handler.CustomSmsHandler;
import org.wso2.carbon.identity.event.handler.AbstractEventHandler;

@Component(
        name = "custom.sms.handler.bundle",
        immediate = true)
public class CustomSmsHandlerBundleComponent {
    private static final Log log = LogFactory.getLog(CustomSmsHandlerBundleComponent.class);

    @Activate
    protected void activate(final ComponentContext context) {
        final CustomSmsHandler customAuditLogger = new CustomSmsHandler();

        final ServiceRegistration<?> serviceRegistrationResult = context.getBundleContext()
                .registerService(AbstractEventHandler.class.getName(), customAuditLogger, null);

        if (serviceRegistrationResult == null) {
            log.error("Error registering CustomSmsHandler as a AbstractEventHandler.");
        } else {
            log.info("CustomSmsHandler successfully registered as a AbstractEventHandler.");
        }

        log.info("Custom bundle activated.");
    }

    @Deactivate
    protected void deactivate(final ComponentContext ignored) {
        log.info("Custom bundle deactivated.");
    }
}

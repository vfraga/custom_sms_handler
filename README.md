# Custom SMS Handler

This project includes a custom notification event handler `CustomSmsHandler` that extends the default implementation 
`DefaultNotificationHandler` to override the `buildNotificationData` method and include additional information to the
Event properties, making them available in the `SMSPublisher` event stream (`id_gov_sms_notify_stream:1.0.0`).

[Look here](https://is.docs.wso2.com/en/5.10.0/develop/writing-a-custom-event-handler) for more information on event handlers.

---

### Configuration

Add the below to the `<IS_HOME>/repository/conf/deployment.toml` file:
```toml
# Disable the default SMS handler by emptying the subscriptions array
[identity_mgt.events.schemes.'default.notification.sender']
subscriptions = []

# Add the custom SMS handler's required settings and properties
[[event_handler]]
name = "customSmsHandler"
subscriptions = ["TRIGGER_SMS_NOTIFICATION"]
properties.'subscription.TRIGGER_SMS_NOTIFICATION.stream' = "id_gov_sms_notify_stream:1.0.0"
properties.'subscription.TRIGGER_SMS_NOTIFICATION.claim.mobile' = "http://wso2.org/claims/mobile"
```
----

### Usage

1. Build the project using Maven:
   ```bash
   mvn clean install
   ```
2. Copy the `target/custom_sms_handler-1.0.0-SNAPSHOT.jar` file to the `<IS_HOME>/repository/components/dropins` directory.
3. Add the configuration mentioned in the _Configuration_ section.
4. Create a file named `SMSPublisher.xml` in the `<IS_HOME>/repository/deployment/server/eventpublishers` directory with the following content:
    ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <eventPublisher name="SMSPublisher" processing="enable"
        statistics="disable" trace="disable" xmlns="http://wso2.org/carbon/eventpublisher">
        <from streamName="id_gov_sms_notify_stream" version="1.0.0"/>
        <mapping customMapping="enable" type="json"> <!-- Available 'type' values: json, xml, text -->
            <inline>{"to"={{mobile}}, "text"={{body}}}</inline>
        </mapping>
        <to eventAdapterType="http">
            <!-- 
            Available properties: 
            
            http.proxy.host    - The proxy server host (e.g., localhost)
            http.proxy.port    - The proxy server port (e.g., 8080)
            http.client.method - The standard HTTP client method (e.g., HttpGet, HttpPost, etc.)
    
            http.url           - The target HTTP/HTTPS URL
            http.username      - The username for HTTP Basic authentication (if required)
            http.password      - The password for HTTP Basic authentication (if required)
            http.headers       - Comma-separated additional HTTP headers
    
            Observations: Any usage of XML reserved characters must be escaped.
            -->
    
            <property name="http.client.method">httpPost</property>
            <property name="http.url">https://webhook.site/db38fba4-d07d-4f54-be81-ed2f6e9cacb6</property>
            <property name="http.header">Accept: application/json,X-Retrieval-Reference-Number"="{{X-Retrieval-Reference-Number}}"</property>
        </to>
    </eventPublisher>
    ```
5. Start the server.
6. [Create an Identity Provider for SMS OTP](https://is.docs.wso2.com/en/5.10.0/learn/configuring-sms-otp), 
but don't set any values to the settings under the _SMS OTP Configuration_ section. Only toggle the _Enable_ checkbox.
7. Create a new user in the WSO2 Identity Server and set the mobile number claim to a valid number.
8. [Create a new service provider](https://is.docs.wso2.com/en/5.10.0/learn/adding-and-configuring-a-service-provider/)
and [add an authentication step for SMS OTP](https://is.docs.wso2.com/en/5.10.0/learn/configuring-sms-otp/#configuring-the-service-provider).
9. Login to the service provider using the user created in step 7. The SMS API call should've included the `X-Retrieval-Reference-Number` header in the request.

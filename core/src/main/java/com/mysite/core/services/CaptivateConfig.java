package com.mysite.core.services;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Captivate Integration Service Configuration", description = "Service Configuration")
public @interface CaptivateConfig {

    @AttributeDefinition(name = "Captivate Prime Client ID", description = "Captivate Prime Client ID")
    String client_id();

    @AttributeDefinition(name = "Captivate Prime Client Secret", description = "Captivate Prime Client Secret")
    String client_secret();

    @AttributeDefinition(name = "Captivate Prime Auth Code", description = "Captivate Prime Auth Code")
    String client_code();

    @AttributeDefinition(name = "Captivate Prime Token Endpoint", description = "Captivate Prime Token Endpoint")
    String tokenEndPoints();

    @AttributeDefinition(name = "API Key for Alpha Vantage", description = "Captivate Prime Token Endpoint")
    String alphaVantageAPIKey();

    @AttributeDefinition(name = "Endpoint URL for Alpha Vantage", description = "Captivate Prime Token Endpoint")
    String alphaVantageEndPoint();
}

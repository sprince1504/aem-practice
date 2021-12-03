package com.mysite.core.services;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Factory Configuration Example", description = "Factory Configuration")
public @interface FactoryConfigOCD {
    @AttributeDefinition(name = "Endpoint to connect to API", description = "EndPoint Connection")
    String apiEndPoint();
    @AttributeDefinition(name = "Site Identifier", description = "Identify the site")
    String siteIdentifier();
}

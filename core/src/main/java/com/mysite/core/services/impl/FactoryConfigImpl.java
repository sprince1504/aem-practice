package com.mysite.core.services.impl;

import com.mysite.core.services.FactoryConfig;
import com.mysite.core.services.FactoryConfigOCD;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.metatype.annotations.Designate;

@Component(service= FactoryConfig.class,immediate=true,configurationPolicy = ConfigurationPolicy.REQUIRE)
@Designate(ocd=FactoryConfigOCD.class, factory=true)
public class FactoryConfigImpl implements FactoryConfig {
    private String apiendpoint;
    private String siteIdentifier;
    @Override
    public String endPointUrl() {
        return apiendpoint;
    }

    @Override
    public String siteIdentifier() {
        return siteIdentifier;
    }
}

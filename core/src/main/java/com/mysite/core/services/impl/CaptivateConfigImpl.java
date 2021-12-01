package com.mysite.core.services.impl;

import com.mysite.core.services.CaptivateConfig;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;

import java.lang.annotation.Annotation;

@Component(service = CaptivateConfig.class, immediate = true)
@Designate(ocd = CaptivateConfig.class)
public class CaptivateConfigImpl implements CaptivateConfig {

    private CaptivateConfig captivateConfig;

    @Activate
    protected void activate(CaptivateConfig captivateConfig) {
        this.captivateConfig = captivateConfig;
    }

    @Override
    public String client_id() {
        return this.captivateConfig.client_id();
    }

    @Override
    public String client_secret() {
        return this.captivateConfig.client_secret();
    }

    @Override
    public String client_code() {
        return this.captivateConfig.client_code();
    }

    @Override
    public String tokenEndPoints() {
        return this.captivateConfig.tokenEndPoints();
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return null;
    }
}

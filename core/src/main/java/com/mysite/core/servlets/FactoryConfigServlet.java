package com.mysite.core.servlets;

import com.mysite.core.services.FactoryConfig;
import com.mysite.core.services.FactoryConfigOCD;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.util.HashMap;
import java.util.Map;

@Component(service= Servlet.class, property={
        "sling.servlet.methods=" + HttpConstants.METHOD_GET,
        "sling.servlet.paths="+ "/bin/factoryConfig"
})
public class FactoryConfigServlet extends SlingSafeMethodsServlet {
    private Logger log = LoggerFactory.getLogger(FactoryConfigServlet.class);

    private Map<String,FactoryConfig> factoryConfigMap;

    @Reference(name = "FactoryConfig", cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    protected synchronized void bindOSGIFactoryConfigDemo(final FactoryConfig factoryConfig) {
        if (factoryConfigMap == null) {
            factoryConfigMap = new HashMap<>();
        }
        factoryConfigMap.put(factoryConfig.endPointUrl(), factoryConfig);
    }

    protected synchronized void unbindOSGIFactoryConfigDemo(final FactoryConfig factoryConfig) {

        factoryConfigMap.remove(factoryConfig.endPointUrl());
    }

    @Reference(target="(siteIdentifier=demosite)")
    private FactoryConfigOCD factoryConfigOCD;

    @Override
    protected void doGet(final SlingHttpServletRequest req, final SlingHttpServletResponse resp) {
        String siteIdentifier = req.getParameter("siteidentifier");
        try {
            FactoryConfig factoryconfig = factoryConfigMap.get(siteIdentifier);
            String apiendPoint = factoryconfig.endPointUrl();
            log.info("Config values of identifier={} are {}, {}",apiendPoint);
            resp.getWriter().write("Specific instance of a factory config="+factoryconfig.endPointUrl());
        }catch(Exception e){
            log.error("Error in Factory Configuration"+ e);
        }
    }
}
package com.mysite.core.servlets;

import com.mysite.core.services.CaptivateConfig;
import com.mysite.core.services.CaptivativePrimeService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.json.JSONObject;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.io.IOException;

@Component(immediate = true, service = Servlet.class, property = {
        ServletResolverConstants.SLING_SERVLET_METHODS +"="+ HttpConstants.METHOD_GET,
        ServletResolverConstants.SLING_SERVLET_PATHS + "=/bin/captivate/login",
        Constants.SERVICE_DESCRIPTION + "= Servlet to Fetch the Stock quote",
        Constants.SERVICE_VENDOR + "= Captivate"
})
public class CaptivativePrimeServlet extends SlingAllMethodsServlet {
    public static final Logger LOGGER = LoggerFactory.getLogger(CaptivativePrimeServlet.class);
    @Reference
    CaptivativePrimeService captivativePrimeService;
    @Reference
    CaptivateConfig captivateConfig;
    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response){
        String client_id = captivateConfig.client_id();
        String client_secret = captivateConfig.client_secret();
        String client_code= captivateConfig.client_code();
        String urlParameters  = "client_id="+client_id+"&client_secret="+client_secret+"&code="+client_code;
        try{
            JSONObject jsonObject = captivativePrimeService.createConnection(captivateConfig.tokenEndPoints(),urlParameters);
            response.getWriter().write(jsonObject.toString());
        }catch (IOException e){
            LOGGER.error("Error in captivate servlet");
        }
    }
}
package com.mysite.core.servlets;

import com.mysite.core.services.CaptivateConfig;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletResourceTypes;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
@Component(service = { Servlet.class })
@SlingServletResourceTypes(
        resourceTypes="mysite/components/page",
        methods=HttpConstants.METHOD_GET,
        selectors="Laravel")
@ServiceDescription("Simple Laravel Servlet")
public class AlphaVantageStockServlet extends SlingAllMethodsServlet {
    private static final Logger logger = LoggerFactory.getLogger(AlphaVantageStockServlet.class);
    @Reference
    CaptivateConfig captivateConfig;
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
            String symbol = request.getParameter("symbol");
            String apikey = captivateConfig.alphaVantageAPIKey();
            JSONObject getQuoteRes = null;
            HttpResponse httpresponse = null;
            HttpGet httpGet = null;
            try {
                HttpClient httpclient = HttpClientBuilder.create().build();
                httpGet = new HttpGet(captivateConfig.alphaVantageEndPoint()+"?function=GLOBAL_QUOTE&symbol="+symbol+"&apikey="+apikey);
                httpresponse = httpclient.execute(httpGet);
                final int statusCode = httpresponse.getStatusLine().getStatusCode();
                if (statusCode != HttpStatus.SC_OK) {
                    logger.info("Response Code is " + statusCode);
                }
                String getResult = EntityUtils.toString(httpresponse.getEntity());
                getQuoteRes = (JSONObject) new JSONTokener(getResult).nextValue();
                }
        catch(Exception e) {
                logger.error("Error during fetching the response form Alpha Vantage :" + e);
        }finally{
                if(httpGet!=null){
                    httpGet.releaseConnection();
                }
            }
        response.getWriter().write(getQuoteRes.toString());
    }
}

package com.mysite.core.services.impl;

import com.mysite.core.services.GraphqlConnection;
import com.mysite.core.constants.Constants;
import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

@Component(service = GraphqlConnection.class)
public class GraphqlConnectionImpl implements GraphqlConnection {

    public static final Logger LOGGER = LoggerFactory.getLogger(GraphqlConnectionImpl.class);

    @Override
    public JSONObject createConnection(String query) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        HttpURLConnection con;
        try {
            URL url = new URL(""+ "/graphql");
            con = (HttpURLConnection) url.openConnection();
            if(null != con) {
                con.setDoInput(true);
                con.setDoOutput(true);
                con.setInstanceFollowRedirects(false);
                con.setRequestMethod(Constants.POST);
                con.setRequestProperty(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON);
                con.setConnectTimeout(1000);
                con.setReadTimeout(1000);
                this.sendData(con, query);
                int responseCode = con.getResponseCode();
                if (responseCode != HttpStatus.SC_OK) {
                    LOGGER.info("Response Code is {}", responseCode);
                }
                jsonObject = this.getReadFull(con.getInputStream());
            }
            return jsonObject;
        } catch (ProtocolException e) {
            LOGGER.error("ProtocolException in createConnection method {}", e.getMessage());
        } catch (MalformedURLException e) {
            LOGGER.error("MalformedURLException in createConnection method {}", e.getMessage());
        } catch (IOException e) {
            LOGGER.error("IOException in createConnection method {}", e.getMessage());
        }
        return jsonObject;
    }

    private void sendData(HttpURLConnection con, String data) throws IOException {
        DataOutputStream dataOutputStream = null;
        try {
            dataOutputStream = new DataOutputStream(con.getOutputStream());
            dataOutputStream.writeBytes(data);
            dataOutputStream.flush();
            dataOutputStream.close();
        } catch (IOException exception) {
            LOGGER.error("IOException in sendData method {}", exception.getMessage());
        } finally {
            this.closeQuietly(dataOutputStream);
        }
    }

    private JSONObject getReadFull(InputStream is) throws IOException, JSONException {
        BufferedReader bufferedReader = null;
        JSONObject jsonproducts = new JSONObject();
        String response;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(is));
            response = bufferedReader.readLine();
            bufferedReader.close();
            JSONObject jsonObject = new JSONObject(response);
            return jsonproducts;
        } catch (IOException ioe) {
            LOGGER.error("IOException in getReadFull method {}", ioe.getMessage());
        } finally {
            this.closeQuietly(bufferedReader);
        }
        return jsonproducts;
    }

    private void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException ex) {
            LOGGER.error("IOException in closeQuietly method {}", ex.getMessage());
        }
    }
}
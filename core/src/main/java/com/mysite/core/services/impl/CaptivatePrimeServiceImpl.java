package com.mysite.core.services.impl;

import com.mysite.core.services.CaptivativePrimeService;
import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

@Component(service = CaptivativePrimeService.class)
public class CaptivatePrimeServiceImpl implements CaptivativePrimeService {

    public static final Logger LOGGER = LoggerFactory.getLogger(CaptivatePrimeServiceImpl.class);

    JSONObject jsonObject = new JSONObject();
    @Override
    public JSONObject createConnection(String requestUrl,String urlParameters){
        try{
            TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }
                }
            };
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HostnameVerifier allHostsValid = new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

            byte[] postData = urlParameters.getBytes( StandardCharsets.UTF_8 );
            int postDataLength = postData.length;
            URL url = new URL(requestUrl);
            HttpURLConnection conn= (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setInstanceFollowRedirects(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("charset", "utf-8");
            conn.setRequestProperty("Content-Length", Integer.toString(postDataLength ));
            conn.setUseCaches(false);
            sendData(conn,postData);
            int responseCode = conn.getResponseCode();
            if (responseCode != HttpStatus.SC_OK) {
                LOGGER.info("Response Code is {}", responseCode);
            }
            jsonObject = this.getReadFull(conn.getInputStream());
            return jsonObject;
        }
        catch (IOException | JSONException | NoSuchAlgorithmException | KeyManagementException e){
            LOGGER.error("Error in captivatePrimeServiceImpl" + e);
        }
        return jsonObject;
    }
    private void sendData(HttpURLConnection con, byte[] data) throws IOException {
        DataOutputStream dataOutputStream = null;
        try {
            dataOutputStream = new DataOutputStream(con.getOutputStream());
            dataOutputStream.write(data);
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
            return jsonObject;
        } catch (IOException ioe) {
            LOGGER.error("IOException in getReadFull method {}", ioe.getMessage());
        } finally {
            this.closeQuietly(bufferedReader);
        }
        return jsonObject;
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

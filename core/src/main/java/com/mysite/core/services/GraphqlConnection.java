package com.mysite.core.services;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public interface GraphqlConnection {
    public JSONObject createConnection(String query) throws IOException, JSONException;
}

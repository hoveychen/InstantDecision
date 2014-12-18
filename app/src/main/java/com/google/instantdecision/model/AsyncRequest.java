package com.google.instantdecision.model;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by xiaoqin on 14-12-17.
 */
public class AsyncRequest extends AsyncTask<String, Void, String>  {
    private static String HOST = "http://112.95.228.73:5000/";
    @Override
    protected String doInBackground(String... urls) {
        if(urls.length < 1 ) return "urls can't be empty.";
        String host = urls[0];
        HttpClient client = new DefaultHttpClient(Utils.httpParams());
        HttpGet get = new HttpGet(HOST + host);
        HttpResponse response = null;
        try {
             response = client.execute(get);
            JSONObject obj = Utils.getResponseJson(response.getEntity().getContent());
            if (obj == null) {
                return "response error.";
            }
            String error = obj.getString("error");
            return error;
        } catch (ClientProtocolException e) {
            return "protocol error.";
        } catch (IOException e) {
            return "io exception";
        } catch(JSONException ex) {
            return "succ";
        }

    }

    @Override
    protected void onPostExecute(String result) {
        Log.i("goog", result);
    }
}

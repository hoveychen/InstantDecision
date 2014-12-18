package com.google.instantdecision.model;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Timer;
import java.util.Map;
import android.util.Log;
import java.util.TimerTask;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

public class ServerInteraction {

    public static ArrayList<Vote> storage = new ArrayList<Vote>();
    private static String HOST = "http://112.95.228.73:5000/";
    static final int kRefreshInterval = 10000;

    public static ArrayList<Vote> getVotes() {
        HttpClient client = new DefaultHttpClient(Utils.httpParams());
        ArrayList<Vote> votes = new ArrayList<Vote>();
        HttpGet get = new HttpGet(HOST + "surveys");
        try {
            HttpResponse response = client.execute(get);
            JSONObject obj = Utils.getResponseJson(response.getEntity().getContent());
            if (obj == null) {
                return votes;
            }
            Utils.populate(votes, obj);
        } catch (ClientProtocolException e) {
            Log.e("goog-instant", e.getMessage());
        } catch (IOException e) {
            Log.e("goog-instant", e.getMessage());
        }
        Collections.reverse(votes);
        return votes;
    }

    public static void sendTicket(Ticket ticket, String voteId) {
        try {
            StringBuilder sb = new StringBuilder("selection/create/").append(voteId).append("?owner=")
                    .append(URLEncoder.encode(ticket.getIdentifier().getName(), "UTF-8")).append("&");
            for (Option op : ticket.getSelection().getSelections()) {
                if (op != null) {
                    sb.append("option_id=").append(URLEncoder.encode(op.getId(), "UTF-8")).append("&");
                }
            }
            AsyncRequest task = new AsyncRequest();
            task.execute(new String[]{sb.toString()});
        } catch (UnsupportedEncodingException e) {
            Log.e("goog-instant", e.getMessage());
        }
    }

    public static void startTimer() {
        Timer timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                ArrayList<Vote> votes = getVotes();
                synchronized (ServerInteraction.class) {
                    storage = votes;
                }
            }
        }, 0, kRefreshInterval);
    }

    public static void createVote(Vote vote) {
        //survey/new?name=survey_abc&owner=zhiyuwang&option=oo1&option=oo2&active=true&multi_selection=true
        ArrayList<NameValuePair> data = new ArrayList<NameValuePair>();
        data.add(new BasicNameValuePair("name", vote.getTitle()));
        data.add(new BasicNameValuePair("owner", vote.getCreator().getDeviceId()));

        for (Option op : vote.getOptions()) {
            data.add(new BasicNameValuePair("option", op.getTitle()));
//            data.put("option", op.getTitle());
        }
        data.add(new BasicNameValuePair("active", Boolean.toString(vote.isActive())));
        data.add(new BasicNameValuePair("multi_selection", Boolean.toString(vote.isMultiSelect())));
        data.add(new BasicNameValuePair("num_tickets", String.valueOf(vote.getNumTicket())));
        StringBuilder sb = new StringBuilder("survey/new?");
        try {
            for (NameValuePair pair: data) {
                sb.append(pair.getName()).append("=").append(URLEncoder.encode(pair.getValue(), "UTF-8")).append("&");
            }
            AsyncRequest task = new AsyncRequest();
            task.execute(new String[]{sb.toString()});
        } catch (UnsupportedEncodingException ex) {

        }
    }

    public static ArrayList<Vote> fetchVotes() {
        return storage;
    }


}

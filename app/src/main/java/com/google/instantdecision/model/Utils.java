package com.google.instantdecision.model;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import android.util.Log;

//import android.util.Log;

public class Utils {
    static final int kDefaultNumTickets = 1000;

    public static HttpParams httpParams() {
        HttpParams httpParameters = new BasicHttpParams();
        int timeoutConnection = 5000;
        HttpConnectionParams.setConnectionTimeout(httpParameters,
                timeoutConnection);
        // Set the default socket timeout (SO_TIMEOUT)
        // in milliseconds which is the timeout for waiting for data.
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutConnection);
        return httpParameters;
    }

	public static void populateSelections(Vote vote, Ticket ticket,
			JSONArray selections) {
		if (selections == null) {
			return;
		}
		ArrayList<Option> options = vote.getOptions();
		// List<Option> tickOptions = ticket.getSelection().getSelections();
		for (int i = 0; i < selections.length(); i++) {
			for (Option option : options) {
				try {
					if (option.getId().equals(selections.getString(i))) {
						ticket.getSelection().addSelection(option);
					}
				} catch (JSONException e) {
				}
			}
		}
	}

	public static void populateTicket(Vote vote, JSONArray tickets,
			boolean multi) {
		try {
			for (int i = 0; i < tickets.length(); i++) {
				JSONObject ticketObj = tickets.getJSONObject(i);
				Ticket ticket = new Ticket(multi);
				Identifier identifier = ticket.getIdentifier();
				// String owner = ticketObj.getString("owner");
				// if (owner != null) {
				// identifier.setDeviceId(owner);
				// }
				String name = ticketObj.getString("owner");
				if (name != null) {
					identifier.setName(name);
                    identifier.setDeviceId(name);
				}
				populateSelections(vote, ticket,
						ticketObj.getJSONArray("selections"));
                vote.getTickets().add(ticket);
			}
		} catch (JSONException e) {
//			Log.e("google-instantdecision", e.getMessage());
		}
	}

	public static void populateOptions(Vote vote, JSONArray options) {
		try {
			for (int i = 0; i < options.length(); i++) {
				JSONObject optionObj = options.getJSONObject(i);
				Option option = new Option();
				String title = optionObj.getString("content");
				String id = optionObj.getString("id");
				if (title != null) {
					option.setTitle(title);
				}
				if (id != null) {
					option.setId(id);
				}
                Log.i("goog", option.getId());
				vote.getOptions().add(option);
			}
		} catch (JSONException e) {
//			Log.e("google-instantdecision", e.getMessage());
		}
	}

	public static void populate(ArrayList<Vote> votes, JSONObject obj) {
		JSONArray surveys;
		try {
			surveys = obj.getJSONArray("surveys");
			for (int i = 0; i < surveys.length(); i++) {
				JSONObject voteObj = surveys.getJSONObject(i);
				Vote vote = new Vote(voteObj.getString("id"));
				vote.setTitle(voteObj.getString("name"));
                Identifier identifier  = new Identifier();
                identifier.setName(voteObj.getString("owner"));
                identifier.setDeviceId(voteObj.getString("owner"));
                vote.setCreator(identifier);
				populateOptions(vote, voteObj.getJSONArray("options"));
				populateTicket(vote, voteObj.getJSONArray("selections"), voteObj.getBoolean("multi_selection"));
                vote.setActive(voteObj.getBoolean("active"));
                vote.setMultiSelect(voteObj.getBoolean("multi_selection"));
                vote.setNumTicket(voteObj.getInt("num_tickets"));
				votes.add(vote);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static JSONObject getResponseJson(InputStream input) {
		JSONObject finalResult = null;
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					input, "UTF-8"));
			StringBuilder builder = new StringBuilder();
			for (String line = null; (line = reader.readLine()) != null;) {
				builder.append(line).append("\n");
			}
            Log.i("goog", builder.toString());
			JSONTokener tokener = new JSONTokener(builder.toString());
			finalResult = new JSONObject(tokener);
		} catch (Exception e) {
            Log.e("goog", e.getMessage());
		}
		return finalResult;
	}
//	public static void main(String args[]) {
//		ArrayList<Vote> votes = new ArrayList<Vote>();
//		JSONObject obj;
//		try {
//			obj = new JSONObject("{\"surveys\":[{\"id\":1,\"name\":\"survey1\",\"options\":[{\"content\":\"op1\",\"id\":1},{\"content\":\"op2\",\"id\":2},{\"content\":\"op3\",\"id\":3},{\"content\":\"op4\",\"id\":4}],\"owner\":\"zzb1\",\"selections\":[[\"zhiyuwang\",[1,2]]]}]}");
//			Utils.populate(votes, obj);
//			for(Vote vote : votes ){
//				System.out.println(vote.getTitle());
//			}
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
}

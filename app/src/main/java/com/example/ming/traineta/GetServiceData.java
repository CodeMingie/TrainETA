package com.example.ming.traineta;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;


import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by Ming on 2/21/2016.
 */
public class GetServiceData {

    Context context;
    private String key = "wX9NwuHnZU2ToO7GmGR9uw";
    //private String mbta = "http://realtime.mbta.com/developer/api/v2/stopsbylocation?api_key=wX9NwuHnZU2ToO7GmGR9uw&lat=42.346961&lon=-71.076640&format=json";
    //private String ETA = "http://realtime.mbta.com/developer/api/v2/predictionsbystop?api_key=wX9NwuHnZU2ToO7GmGR9uw&stop=place-bbsta&format=json";

    public GetServiceData(Context context)
    {
        this.context = context;
    }

    public String GetStationsUrl()
    {
        String url = "http://realtime.mbta.com/developer/api/v1/stopsbyroute?api_key=" + key + "&route=Orange";
        return url;
    }

    public HashMap<String, String> GetStations(int direction_id)
    {
        HashMap<String, String> fList = new HashMap<String, String>();

        String url = GetStationsUrl();
        JSONObject eta = getJSON(url);
        try {
            JSONArray direction = eta.getJSONArray("direction");

            //for (int i = 0; i < direction.length(); i++)
            //{
                JSONObject stopsObject = direction.getJSONObject(direction_id);
                JSONArray stops = stopsObject.getJSONArray("stop");

                for (int j = 0; j < stops.length(); j++)
                {
                    JSONObject stopObject = stops.getJSONObject(j);
                    String station_name = stopObject.getString("parent_station");
                    String station_key = stopObject.getString("parent_station_name");
                    fList.put(station_key, station_name);
                }
            //}
        } catch (Exception e) {
            e.printStackTrace();
        }

        return fList;
    }

    public String GetUrl(String station)
    {
        String url = "http://realtime.mbta.com/developer/api/v2/predictionsbystop?api_key=" + key + "&stop=" + station + "&format=json";
        return url;
    }

    public ArrayList<Trip> GetTrain(String station, int direction_int) {
        ArrayList<Trip> fList = new ArrayList<Trip>();

        try {
            String url = GetUrl(station);
            JSONObject eta = getJSON(url);
            JSONArray mode = eta.getJSONArray("mode");
            JSONArray route = mode.getJSONObject(0).getJSONArray("route");
            JSONArray direction = route.getJSONObject(0).getJSONArray("direction");

            //for (int i = 0; i < direction.length(); i++) {
            JSONArray trip = direction.getJSONObject(direction_int).getJSONArray("trip");

            for (int j = 0; j < trip.length(); j++) {
                JSONObject o = trip.getJSONObject(j);
                String tripName = o.getString("trip_name");
                String headsign = o.getString("trip_headsign");
                int time = o.getInt("pre_dt");

                Trip t = new Trip(tripName, station, headsign, time, "");
                fList.add(t);
            }
            //}
        }
        catch (Exception e)
        {
            Toast.makeText(this.context, e.getMessage(), Toast.LENGTH_LONG);
        }

        return fList;
    }

    public JSONObject getJSON(String url) {
        JSONObject jsonObject = null;
        try {
            String data = getJSON(url, 10000);
            jsonObject = new JSONObject(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public String getJSON(String url, int timeout) {
        HttpURLConnection c = null;
        try {
            URL u = new URL(url);
            c = (HttpURLConnection) u.openConnection();
            c.setRequestMethod("GET");
            c.setRequestProperty("Content-length", "0");
            c.setUseCaches(false);
            c.setAllowUserInteraction(false);
            c.setConnectTimeout(timeout);
            c.setReadTimeout(timeout);
            c.connect();
            int status = c.getResponseCode();

            switch (status) {
                case 200:
                case 201:
                    BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line+"\n");
                    }
                    br.close();
                    return sb.toString();
            }

        } catch (MalformedURLException ex) {
            //Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
            Log.d(new Exception().getStackTrace()[0].getMethodName(), ex.getMessage());
        } catch (IOException ex) {
            Log.d(new Exception().getStackTrace()[0].getMethodName(), ex.getMessage());
            //Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (c != null) {
                try {
                    c.disconnect();
                } catch (Exception ex) {
                    Log.d(new Exception().getStackTrace()[0].getMethodName(), ex.getMessage());
                    //Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return null;
    }

}

package com.basics.ming.traineta;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


import android.widget.Toast;

import com.basics.ming.traineta.dummy.Routes;

import java.util.HashMap;

/**
 * Created by Ming on 2/21/2016.
 */
public class GetServiceData {

    Context context;
    //private String key = "wX9NwuHnZU2ToO7GmGR9uw";
    private String key = "r_vbkgrZEEOJwKgosF-xDg";
    public GetServiceData(Context context)
    {
        this.context = context;
    }

    public String GetRoutesUrl()
    {
        String routeUrl = "http://realtime.mbta.com/developer/api/v2/routes?api_key=" + key + "&format=json";
        return routeUrl;
    }

    public String GetStationsUrl(String route)
    {
        String url = "http://realtime.mbta.com/developer/api/v1/stopsbyroute?api_key=" + key + "&route=" + route;
        return url;
    }

    public ArrayList<Routes> GetRoutes()
    {
        ArrayList<Routes> r = new ArrayList<Routes>();

        try
        {
            String url = GetRoutesUrl();
            JSONObject eta = getJSON(url);
            JSONArray mode = eta.getJSONArray("mode");

            for (int j = 0; j < mode.length(); j++)
            {
                JSONObject route = mode.getJSONObject(j);

                JSONArray routeArray = route.getJSONArray("route");

                for (int k = 0; k < routeArray.length(); k++) {

                    JSONObject routeJsonObject = routeArray.getJSONObject(k);

                    String route_id = routeJsonObject.getString("route_id");
                    String route_name = routeJsonObject.getString("route_name");
                    Routes routeObject = new Routes();

                    routeObject.routeId = route_id;
                    routeObject.routeName = route_name;

                    r.add(routeObject);
                }
            }

        }
        catch (Exception ex)
        {
            Log.d("GetRoutes exception.", ex.getMessage());
        }

        return r;
    }

    public String GetStationUrl(String station)
    {
        String url = "http://realtime.mbta.com/developer/api/v2/predictionsbystop?api_key=" + key + "&stop=" + station + "&format=json";
        return url;
    }

    public HashMap<String, String> GetStations(String directionName, String route)
    {
        int direction_id = 0;

        if (directionName == "Inbound")
            direction_id = 0;
        else
            direction_id = 1;

        HashMap<String, String> fList = new HashMap<String, String>();

        String url = GetStationsUrl(route);
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

    public ArrayList<Trip> GetTrain(String station, String directionName, String stationName) {

        int direction_int = 0;

        if (directionName == "Inbound")
            direction_int = 0;
        else
            direction_int = 1;

        ArrayList<Trip> fList = new ArrayList<Trip>();

        try {
            String url = GetStationUrl(station);
            JSONObject eta = getJSON(url);
            JSONArray mode = eta.getJSONArray("mode");
            JSONArray route = mode.getJSONObject(0).getJSONArray("route");
            JSONArray direction = route.getJSONObject(0).getJSONArray("direction");

            //for (int i = 0; i < direction.length(); i++) {
            JSONObject jo = direction.getJSONObject(direction_int);
            JSONArray trip = jo.getJSONArray("trip");

            for (int j = 0; j < trip.length(); j++) {
                JSONObject o = trip.getJSONObject(j);
                String tripName = o.getString("trip_name");
                String headsign = o.getString("trip_headsign");
                int time = o.getInt("pre_dt");

                Trip t = new Trip(tripName, stationName, headsign, time, "");
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

    private String getJSON(String url, int timeout) {
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

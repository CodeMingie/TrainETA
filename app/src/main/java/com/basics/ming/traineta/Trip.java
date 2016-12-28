package com.basics.ming.traineta;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Ming on 3/6/2016.
 */
public class Trip {

    private String trip;
    private String headsign;
    private int time;
    public String id;
    private String station;
    private String direction_name;

    public Trip(String trip, String station, String headsign, int time, String id)
    {
        this.trip = trip;
        this.headsign = headsign;
        this.time = time;
        this.id = id;
        this.station = station;

    }

    public long GetTimeLeft()
    {
        Date now = new Date();
        Date futureDate = new Date(time*1000L);
        long timeLeft = futureDate.getTime() - now.getTime();
        return timeLeft;
    }

    public String GetDepartureTime()
    {
        SimpleDateFormat sf = new SimpleDateFormat("h:mm a");
        Date date = new Date(time*1000L);
        String formattedDate = sf.format(date);
        return "Departure time: " + formattedDate;
    }

    public String GetContent()
    {
        SimpleDateFormat sf = new SimpleDateFormat("h:mm a");
        Date date = new Date(time*1000L);
        String formattedDate = sf.format(date);
        String content = "Train Direction:" + headsign + "\n" + "Station: " + station + "\n Departure time: " + formattedDate;
        return content;
    }
}

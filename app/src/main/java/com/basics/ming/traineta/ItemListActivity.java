package com.basics.ming.traineta;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;


import com.basics.ming.traineta.dummy.Routes;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ItemDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ItemListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     * Async task
     */
    private boolean mTwoPane;
    public String route = "";
    public String favRoute = "";
    public String favStation = "";

    HashMap<String, String> routes = new HashMap<>();
    HashMap<String, String> stations = new HashMap<>();

    @Override
    protected void onStop()
    {
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString("fav_route", favRoute);
        editor.putString("fav_station", favStation);
        editor.commit();

        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //favRoute = "Green-C";
        //favStation = "place-tapst";

        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        favRoute = sharedPref.getString("fav_route","");
        favStation = sharedPref.getString("fav_station","");

        setContentView(com.basics.ming.traineta.R.layout.activity_item_list);

        Toolbar toolbar = (Toolbar) findViewById(com.basics.ming.traineta.R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        View recyclerView = findViewById(com.basics.ming.traineta.R.id.item_list);

        assert recyclerView != null;
        try {
            setupRecyclerView((RecyclerView) recyclerView);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (findViewById(com.basics.ming.traineta.R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        GetLines();
        GetDirections();
        //Refresh();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuItem refresh = menu.add(0, 1, 0, "Refresh");
        refresh.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (item.getItemId()) {
            case 1:
                Refresh();
                return true;
        }

        return true;
    }

    private void GetLines()
    {
        Spinner lineSpinner = (Spinner) findViewById(com.basics.ming.traineta.R.id.line);
        ArrayList<String> line = new ArrayList<>();

        GetServiceData s = new GetServiceData(this.getApplicationContext());
        ArrayList<Routes> r = s.GetRoutes();

        int index = 0;
        int selectedRouteIndex = 0;
        for (Routes route:
             r) {
            line.add(route.routeName);
            if (route.routeId.equals(favRoute))
            {
                selectedRouteIndex = index;
            }
            index++;
            routes.put(route.routeName, route.routeId);
        }

        ArrayAdapter a = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, line);
        lineSpinner.setAdapter(a);
        lineSpinner.setSelection(selectedRouteIndex);
        lineSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String t = (String) parentView.getItemAtPosition(position);

                if (t == "")
                    return;

                route = t;
                favRoute = routes.get(t);
                GetStations();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });
    }

    private void GetDirections()
    {
        Spinner direction_spinner = (Spinner) findViewById(com.basics.ming.traineta.R.id.direction_spiner);
        ArrayList<String> directions = new ArrayList<>();
        directions.add("Inbound");
        directions.add("Outbound");

        ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_dropdown_item,
                directions);

        direction_spinner.setAdapter(spinnerArrayAdapter);
        direction_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String t = (String) parentView.getItemAtPosition(position);

                GetStations();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });

    }

    private void GetStations()
    {
        Spinner directionDropdown = (Spinner) findViewById(com.basics.ming.traineta.R.id.direction_spiner);
        String direction = (String) directionDropdown.getSelectedItem();
        Spinner routeDropdown = (Spinner) findViewById(com.basics.ming.traineta.R.id.line);
        String lineName = (String) routeDropdown.getSelectedItem();
        String lineId = this.routes.get(lineName);

        this.stations = new GetServiceData(this.getApplicationContext()).GetStations(direction, lineId);

        int selectedStationIndex = 0;
        int index = 0;
        ArrayList<String> spinnerArray = new ArrayList<String>();
        for (String key: stations.keySet()) {
            if (stations.get(key).equals(favStation))
                selectedStationIndex = index;

            index++;
            spinnerArray.add(key);
        }

        Spinner spinner = (Spinner) findViewById(com.basics.ming.traineta.R.id.stations);

        ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_dropdown_item,
                spinnerArray);

        spinner.setAdapter(spinnerArrayAdapter);
        spinner.setSelection(selectedStationIndex);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                favStation = stations.get(parentView.getItemAtPosition(position));
                Refresh();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });
    }

    private void Refresh()
    {
        Spinner stationSpinner = (Spinner) findViewById(com.basics.ming.traineta.R.id.stations);
        Spinner directionSpinner = (Spinner) findViewById(com.basics.ming.traineta.R.id.direction_spiner);
        String station = (String) stationSpinner.getSelectedItem();
        String station_key = stations.get(station);

        String direction = (String) directionSpinner.getSelectedItem();

        ArrayList<Trip> l = new GetServiceData(this.getApplicationContext()).GetTrain(station_key, direction, station);
        RecyclerView recyclerView = (RecyclerView) findViewById(com.basics.ming.traineta.R.id.item_list);
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(l));
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) throws JSONException {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(new ArrayList<Trip>()));
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final ArrayList<Trip> mValues;

        public SimpleItemRecyclerViewAdapter(ArrayList<Trip> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(com.basics.ming.traineta.R.layout.item_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            //holder.mIdView.setText(mValues.get(position).id);
            holder.mContentView.setText(mValues.get(position).GetDepartureTime());

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString(ItemDetailFragment.ARG_ITEM_ID, holder.mItem.id);
                        ItemDetailFragment fragment = new ItemDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(com.basics.ming.traineta.R.id.item_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, ItemDetailActivity.class);
                        intent.putExtra(ItemDetailFragment.ARG_ITEM_MILLI, holder.mItem.GetTimeLeft());
                        String content = holder.mItem.GetContent();
                        intent.putExtra(ItemDetailFragment.ARG_STATION, content);
                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mIdView;
            public final TextView mContentView;
            public Trip mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mIdView = (TextView) view.findViewById(com.basics.ming.traineta.R.id.id);
                mContentView = (TextView) view.findViewById(com.basics.ming.traineta.R.id.content);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }

    }
}

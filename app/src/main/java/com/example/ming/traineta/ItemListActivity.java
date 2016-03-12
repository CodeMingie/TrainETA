package com.example.ming.traineta;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.example.ming.traineta.dummy.DummyContent;

import org.json.JSONException;

import java.lang.reflect.Array;
import java.sql.Ref;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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
    private int direction_int = 0;

    HashMap<String, String> stations = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_item_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        View recyclerView = findViewById(R.id.item_list);
        assert recyclerView != null;
        try {
            setupRecyclerView((RecyclerView) recyclerView);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
        GetDirections();
        //Refresh();
    }

    private void GetDirections()
    {
        Spinner direction_spinner = (Spinner) findViewById(R.id.direction_spiner);
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

                if (t == "Inbound") {
                    GetStations(0);
                    direction_int = 0;
                }
                else {
                    GetStations(1);
                    direction_int = 1;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });

    }

    private void GetStations(int direction_id)
    {
        ArrayList<String> spinnerArray = new ArrayList<String>();
        this.stations = new GetServiceData(this.getApplicationContext()).GetStations(direction_id);

        for (String key: stations.keySet()) {
            spinnerArray.add(key);
        }

        Spinner spinner = (Spinner) findViewById(R.id.planets_spinner);

        ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_dropdown_item,
                spinnerArray);

        spinner.setAdapter(spinnerArrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
//                String t = (String) parentView.getItemAtPosition(position);
//                String station_key = stations.get(t);
//
//                Toast toast = Toast.makeText(parentView.getContext(), station_key, Toast.LENGTH_LONG);
//                toast.show();

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
        Spinner spinner = (Spinner) findViewById(R.id.planets_spinner);
        String station = (String) spinner.getSelectedItem();
        String station_key = stations.get(station);

        ArrayList<Trip> l = new GetServiceData(this.getApplicationContext()).GetTrain(station_key, direction_int);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.item_list);
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(l));
        //recyclerView.notifyAll();
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
                    .inflate(R.layout.item_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            //holder.mIdView.setText(mValues.get(position).id);
            holder.mContentView.setText(mValues.get(position).GetContent());

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString(ItemDetailFragment.ARG_ITEM_ID, holder.mItem.id);
                        ItemDetailFragment fragment = new ItemDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.item_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, ItemDetailActivity.class);
                        //intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, holder.mItem.id);
                        intent.putExtra(ItemDetailFragment.ARG_ITEM_MILLI, holder.mItem.GetTimeLeft());

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
                mIdView = (TextView) view.findViewById(R.id.id);
                mContentView = (TextView) view.findViewById(R.id.content);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }
}

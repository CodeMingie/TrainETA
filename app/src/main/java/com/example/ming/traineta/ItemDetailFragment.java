package com.example.ming.traineta;

import android.app.Activity;
import android.os.CountDownTimer;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ming.traineta.dummy.DummyContent;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link ItemListActivity}
 * in two-pane mode (on tablets) or a {@link ItemDetailActivity}
 * on handsets.
 */
public class ItemDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";
    public static final String ARG_ITEM_MILLI = "item_mill";
    /**
     * The dummy content this fragment is presenting.
     */
    private DummyContent.DummyItem mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        if (getArguments().containsKey(ARG_ITEM_MILLI)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            long milli = getArguments().getLong(ARG_ITEM_MILLI);
            mItem = new DummyContent.DummyItem("", "", "", milli, "");

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mItem.content);
            }
        }

        super.onCreate(savedInstanceState);
    }
    private CountDownTimer t;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.item_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (mItem != null) {

            new CountDownTimer(mItem.milli, 1000) {

                TextView mTextField = ((TextView) rootView.findViewById(R.id.item_detail));

                public void onTick(long millisUntilFinished) {
                    String text = new SimpleDateFormat("mm:ss").format(new Date(millisUntilFinished));
                    mTextField.setText("ETA (min / sec): " + text);
                }

                public void onFinish() {
                    mTextField.setText("done!");
                }
            }.start();
        }

        return rootView;
    }
}

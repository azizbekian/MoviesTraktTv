package com.azizbekian.movies.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.azizbekian.movies.R;
import com.azizbekian.movies.entity.ConnectivityChangeEvent;
import com.azizbekian.movies.fragment.movies.MoviesFragment;
import com.azizbekian.movies.utils.FragmentUtils;

import de.greenrobot.event.EventBus;

/**
 * Created on April 02, 2016.
 *
 * @author Andranik Azizbekian (azizbekyanandranik@gmail.com)
 */
public class MainActivity extends AppCompatActivity {

    /**
     * Listens for connectivity change events.
     */
    private BroadcastReceiver mConnectivityChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            EventBus.getDefault().post(new ConnectivityChangeEvent());
        }
    };

    private static final IntentFilter sConnectivityChangeFilter = new IntentFilter();

    static {
        sConnectivityChangeFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (null == savedInstanceState) {
            FragmentUtils.addRootFragment(this, R.id.content_frame, MoviesFragment.class, MoviesFragment.TAG);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(mConnectivityChangeReceiver, sConnectivityChangeFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mConnectivityChangeReceiver);
    }

}

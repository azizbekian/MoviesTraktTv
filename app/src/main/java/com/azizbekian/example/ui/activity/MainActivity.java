package com.azizbekian.example.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.azizbekian.example.entity.ConnectivityChangeEvent;
import com.azizbekian.example.ui.fragment.MainFragment;
import com.azizbekian.example.utils.FragmentUtils;

import de.greenrobot.event.EventBus;
import com.azizbekian.example.R;

import static com.azizbekian.example.utils.FragmentUtils.TAG_ROOT_FRAGMENT;

/**
 * Created on April 02, 2016.
 *
 * @author Andranik Azizbekian (azizbekyanandranik@gmail.com)
 */
public class MainActivity extends AppCompatActivity {

    private BroadcastReceiver mConnectivityChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            EventBus.getDefault().post(new ConnectivityChangeEvent());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (null == savedInstanceState) {
            FragmentUtils.addRootFragment(this, R.id.content_frame, MainFragment.class, null, TAG_ROOT_FRAGMENT);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(mConnectivityChangeReceiver, intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mConnectivityChangeReceiver);
    }

}

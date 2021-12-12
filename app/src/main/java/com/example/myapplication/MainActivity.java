package com.example.myapplication;

import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.Keep;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Parcel;
import android.util.Log;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.myapplication.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                System.loadLibrary("binderhack");
                MainActivity.start();
                getPackageManager().getInstalledApplications(0);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    /**
     * This function is not necessary!
     * If not provided, binderhack will print all the binder calls.
     * This function will be called by native-c code.
     *
     * @return HashMap<String, Set<String>>. see demo below for detail
     */
    @Keep
    private static HashMap getInterestBinders() {
        HashMap<String, Set<String>> monitorBinderMap = new HashMap<>();
        HashSet<String> amFuncs = new HashSet<>();
        amFuncs.add("activityPaused");
        amFuncs.add("updateConfiguration");
        amFuncs.add("checkPermission");
        amFuncs.add("startActivityAsUser");

        monitorBinderMap.put("android.app.IActivityManager", amFuncs);

        HashSet<String> pmFuncs = new HashSet<>();
        pmFuncs.add("getInstalledApplications");
        monitorBinderMap.put("android.content.pm.IPackageManager", pmFuncs);
        return monitorBinderMap;
    }

    /**
     *
     * @param interfaceName likely as android.content.pm.IPackageManager
     * @param funcName likely as getInstalledApplications
     * @param data see {@link android.os.IBinder}->transact(...)
     * @param reply see {@link android.os.IBinder}->transact(...)
     * @return TRUE represents you've decided to intercept the origin call.
     */
    @Keep
    private static boolean transactStart(Object interfaceName, Object funcName, Parcel data, Parcel reply) {
        Log.d("WHULZZ", String.format("transactStart %s %s", interfaceName, funcName));
        return false;
    }

    /**
     *
     * @param interfaceName likely as android.content.pm.IPackageManager
     * @param funcName likely as getInstalledApplications
     * @param data see {@link android.os.IBinder}->transact(...)
     * @param reply reply see {@link android.os.IBinder}->transact(...)
     * @param originRet this is the origin result
     * @return I advice you to use {@param originRet}
     */
    @Keep
    private static boolean transactEnd(Object interfaceName, Object funcName, Parcel data, Parcel reply, boolean originRet) {
        Log.d("WHULZZ", String.format("transactEnd %s %s", interfaceName, funcName));
        return originRet;
    }

    /**
     * start binder monitor
     */
    private static native void start();

    /**
     * end binder monitor
     */
    private static native void end();
}
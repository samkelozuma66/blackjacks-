package com.example.blackjacks;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.widget.Toast;

import com.example.blackjacks.ui.slideshow.SlideshowFragment;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.blackjacks.databinding.ActivityMainBinding;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.SECONDS;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    String email;
    JSONArray jobj;
    JSONObject userDetailsObj;
    JSONObject allocationObj;
    JSONObject intervalObj;
    JSONObject shiftObj;
    JSONObject cpObj;
    String cpInfo;
    ScheduledExecutorService scheduler;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        email = i.getStringExtra("username");
        Timer timer = new Timer();
        scheduler = Executors.newScheduledThreadPool(1);
        final Runnable runnable = new Runnable() {
            int countdownStarter = 120;

            public void run() {

                System.out.println(countdownStarter);
                countdownStarter--;

                if (countdownStarter < 0) {
                    System.out.println("Timer Over!");
                    sendNotification(email,"Failed to attend to device in 2 min");
                    scheduler.shutdown();
                }
            }
        };
        String userDetails = getUserdata();
        try {
            jobj = new JSONArray(userDetails);
            userDetailsObj = new JSONObject(jobj.getString(0));

            String allocation = getAllocation(userDetailsObj.getString("id"));
            jobj = new JSONArray(allocation);
            allocationObj = new JSONObject(jobj.getString(0));

            String interval = getInterval(allocationObj.getString("site_id"));
            jobj = new JSONArray(interval);
            intervalObj = new JSONObject(jobj.getString(0));

            String shift = getShift(allocationObj.getString("shift_id"));
            jobj = new JSONArray(shift);
            shiftObj = new JSONObject(jobj.getString(0));
            Date startTime = new Date();
            LocalDateTime now = LocalDateTime.now();
            String time = shiftObj.getString("start_time");
            time = time.substring(0,time.lastIndexOf(":"));
            time = time.substring(0,time.lastIndexOf(":"));

            Time m = new Time(Integer.parseInt(time),0,0);
            Calendar cal = new GregorianCalendar();
            SimpleDateFormat format = new SimpleDateFormat();

            startTime = cal.getTime();
            if (startTime.getHours()    < m.getHours())
                startTime.setHours(m.getHours());

            if (startTime.getMinutes()  < m.getMinutes())
                startTime.setMinutes(m.getMinutes());


            if (startTime.getSeconds()  < m.getSeconds())
                startTime.setSeconds(m.getSeconds());

            //Date startTime = (java.util.Date)format.parse(time);
            String inter = intervalObj.getString("interval");

            Double dinter = Double.parseDouble(inter);

            int minu  = (int)(60 * dinter);

            System.out.println("minu " + minu * 4  * 1000);
            System.out.println("startTime  " + startTime);

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    System.out.println("Time to patrol");

                    MediaPlayer mp = MediaPlayer.create(getBaseContext(),R.raw.sound_noti);
                    mp.setLooping(true);
                    NotificationManager notificationManager = (NotificationManager) getSystemService(getBaseContext().NOTIFICATION_SERVICE);
                    String NOTIFICATION_CHANNEL_ID = "my_channel_id_01";

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_HIGH);

                        // Configure the notification channel.
                        notificationChannel.setDescription("Channel description");
                        notificationChannel.enableLights(true);
                        notificationChannel.setLightColor(Color.RED);
                        notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
                        notificationChannel.enableVibration(true);
                        notificationManager.createNotificationChannel(notificationChannel);
                    }


                    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getBaseContext(), NOTIFICATION_CHANNEL_ID);

                    notificationBuilder.setAutoCancel(true)
                            .setDefaults(Notification.DEFAULT_ALL)
                            .setWhen(System.currentTimeMillis())
                            .setSmallIcon(R.mipmap.ic_launcher_foreground)
                            .setTicker("Hearty365")
                            .setPriority(Notification.PRIORITY_MAX)
                            .setContentTitle("Time To Patrol")
                            .setContentText("It time to do your rounds!")
                            .setContentInfo("Info");
                    Intent intent = new Intent("action.name");
                    PendingIntent pIntent = PendingIntent.getBroadcast(getBaseContext(), 1, intent, 0);
                    notificationBuilder.addAction(R.drawable.ic_menu_share, "OK",pIntent);

                    BroadcastReceiver br = new MyBroadcastReceiver(mp,scheduler);

                    IntentFilter filter = new IntentFilter("action.name");
                    //filter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);

                    registerReceiver(br,filter);
                    notificationManager.notify(/*notification id*/1, notificationBuilder.build());
                    /*NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getBaseContext(),"My_Chanel");
                    mBuilder.setSmallIcon(R.mipmap.ic_launcher_foreground);
                    mBuilder.setContentTitle("It Time to Patrol");
                    mBuilder.setContentText("It time to do your rounds ");
                    mBuilder.setPriority(NotificationCompat.PRIORITY_MAX);

                    //Intent resultIntent = new Intent(getBaseContext(), MainActivity.class);
                    //TaskStackBuilder stackBuilder = TaskStackBuilder.create(getBaseContext());
                    //stackBuilder.addParentStack(MainActivity.class);

                    // Adds the Intent that starts the Activity to the top of the stack
                    //stackBuilder.addNextIntent(resultIntent);
                    //PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
                    //mBuilder.setContentIntent(resultPendingIntent);

                    NotificationManager mNotificationManager = (NotificationManager) getSystemService(getBaseContext().NOTIFICATION_SERVICE);

// notificationID allows you to update the notification later on.
                    mNotificationManager.notify(1, mBuilder.build());*/
                    scheduler.scheduleAtFixedRate(runnable, 0, 1, SECONDS);
                    //scheduler.schedule(runnable,0,SECONDS);
                    //scheduler.execute(runnable);
                    mp.start();
                }
            },startTime,minu * 60 * 1000);
            //startTime.setTime(new Tim);




            //cpInfo = getCpInfo(userDetailsObj.getString("id"));
            //jobj = new JSONArray(cpInfo);
            //cpObj = new JSONObject(jobj.getString(0));


        } catch (Exception e) {
            e.printStackTrace();
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        /*Timer timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {

            }
        },1000);
*/
        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "If you want to send Report go to Menu", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            // display error state to the user
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("TESTING RESULT " + requestCode);
        System.out.println("TESTING FINE   " + resultCode);

        System.out.println("TESTING RESULT_OK   " + RESULT_OK);
        System.out.println("TESTING REQUEST_IMAGE_CAPTURE   " + REQUEST_IMAGE_CAPTURE);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            System.out.println("SAMKELO ZUMA");
            //imageView.setImageBitmap(imageBitmap);
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_HOME)) {
            //Toast.makeText(this, "You pressed the home button!", Toast.LENGTH_LONG).show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        String res = logout(email);
        Toast.makeText(this, res, Toast.LENGTH_LONG).show();
        super.onDestroy();
    }
    public String logout(String email)
    {

        String ad = "http://192.168.1.41/userm/app_logout.php?"
                + "email=" + email;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        String adJson = "";
        JSONArray jobj = null;
        StrictMode.setThreadPolicy(policy);
        try {
            URL url = new URL(ad);
            InputStream ist = (InputStream) url.getContent();

            BufferedReader reader = new BufferedReader(new InputStreamReader(ist));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            System.out.println(result.toString());

            adJson = result.toString();
            //jobj = new JSONArray(adJson);
            //jobj.length();

            //System.out.println("ss " + jobj.getJSONObject(0).getString("header"));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e);
        }
        return adJson;
    }
    public String getUserdata() {
        Intent i = getIntent();
        String username = i.getStringExtra("username");
        System.out.println("Medical Appointment " + username);
        String ad = "http://192.168.1.41/userm/app_get_userdetails.php?"
                + "username=" + username;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        String adJson = "";
        JSONArray jobj = null;
        StrictMode.setThreadPolicy(policy);
        try {
            URL url = new URL(ad);
            InputStream ist = (InputStream) url.getContent();

            BufferedReader reader = new BufferedReader(new InputStreamReader(ist));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            System.out.println(result.toString());

            adJson = result.toString();
            //jobj = new JSONArray(adJson);
            //jobj.length();

            //System.out.println("ss " + jobj.getJSONObject(0).getString("header"));
        } catch (IOException  e) {
            e.printStackTrace();
            System.out.println(e);
        }
        return adJson;
    }
    public String getAllocation(String user_id)
    {
        String ad = "http://192.168.1.41/userm/app_get_allocation.php?"
                + "user_id=" + user_id;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        String adJson = "";
        JSONArray jobj = null;
        StrictMode.setThreadPolicy(policy);
        try {
            URL url = new URL(ad);
            InputStream ist = (InputStream) url.getContent();

            BufferedReader reader = new BufferedReader(new InputStreamReader(ist));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            System.out.println(result.toString());

            adJson = result.toString();
            //jobj = new JSONArray(adJson);
            //jobj.length();

            //System.out.println("ss " + jobj.getJSONObject(0).getString("header"));
        } catch (IOException  e) {
            e.printStackTrace();
            System.out.println(e);
        }
        return adJson;
    }

    public String getShift(String shift_id)
    {
        String ad = "http://192.168.1.41/userm/app_get_shift.php?"
                + "shift_id=" + shift_id;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        String adJson = "";
        JSONArray jobj = null;
        StrictMode.setThreadPolicy(policy);
        try {
            URL url = new URL(ad);
            InputStream ist = (InputStream) url.getContent();

            BufferedReader reader = new BufferedReader(new InputStreamReader(ist));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            System.out.println(result.toString());

            adJson = result.toString();
            //jobj = new JSONArray(adJson);
            //jobj.length();

            //System.out.println("ss " + jobj.getJSONObject(0).getString("header"));
        } catch (IOException  e) {
            e.printStackTrace();
            System.out.println(e);
        }
        return adJson;
    }

    public String getInterval(String  site_id)
    {
        String ad = "http://192.168.1.41/userm/app_get_interval.php?"
                + "site_id=" + site_id;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        String adJson = "";
        JSONArray jobj = null;
        StrictMode.setThreadPolicy(policy);
        try {
            URL url = new URL(ad);
            InputStream ist = (InputStream) url.getContent();

            BufferedReader reader = new BufferedReader(new InputStreamReader(ist));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            System.out.println(result.toString());

            adJson = result.toString();
            //jobj = new JSONArray(adJson);
            //jobj.length();

            //System.out.println("ss " + jobj.getJSONObject(0).getString("header"));
        } catch (IOException  e) {
            e.printStackTrace();
            System.out.println(e);
        }
        return adJson;
    }
    public static class MyBroadcastReceiver extends BroadcastReceiver {
        MediaPlayer mediap;
        ScheduledExecutorService scheduler;
        public MyBroadcastReceiver(MediaPlayer mplayer,ScheduledExecutorService scheduler)
        {
            this.mediap = mplayer;
            this.scheduler = scheduler;
        }
        private static final String TAG = "MyBroadcastReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {

            mediap.stop();
            scheduler.shutdown();
            StringBuilder sb = new StringBuilder();
            sb.append("Action: " + intent.getAction() + "\n");
            sb.append("URI: " + intent.toUri(Intent.URI_INTENT_SCHEME).toString() + "\n");
            String log = sb.toString();
            Log.d(TAG, log);
            System.out.println("log"+log);
            Toast.makeText(context, log, Toast.LENGTH_LONG).show();
        }

    }

    public String sendNotification(String email,String notification)
    {
        String ad = "http://192.168.1.41/userm/app_send_notification.php?"
                + "email=" + email
                + "&notification=" + notification;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        String adJson = "";
        JSONArray jobj = null;
        StrictMode.setThreadPolicy(policy);
        try {
            URL url = new URL(ad);
            InputStream ist = (InputStream) url.getContent();

            BufferedReader reader = new BufferedReader(new InputStreamReader(ist));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            System.out.println(result.toString());

            adJson = result.toString();
            //jobj = new JSONArray(adJson);
            //jobj.length();

            //System.out.println("ss " + jobj.getJSONObject(0).getString("header"));
        } catch (IOException  e) {
            e.printStackTrace();
            System.out.println(e);
        }
        return adJson;
    }
}
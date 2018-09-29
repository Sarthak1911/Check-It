package app.com.example.majsarthak.check_it;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class NotifyService extends Service{

    Timer timer;
    TimerTask do_task;
    Intent send_notification;
    PendingIntent pending;
    Notification notification;
    NotificationManager manage_notification;
    DataHelper helper;
    SQLiteDatabase db;
    SQLQueries sqlQueries;
    Cursor result;
    Uri sound_path;
    MainActivity date;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate()
    {
        Log.i("C", "onCreate");

        timer = new Timer();
        sound_path = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.metalgearsoild);
        helper = new DataHelper(getApplicationContext());
        db = helper.getWritableDatabase();
        sqlQueries = new SQLQueries();
        date = new MainActivity();

        do_task = new TimerTask() {
            @Override
            public void run()
            {
                if (getUncheckedCount() > 0)
                {
                    buildNotification();
                }
            }
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i("S", "onStartCommand");
        timer.schedule(do_task, 0, (long) 3.6e+6);
        return Service.START_NOT_STICKY;
    }

     private void buildNotification()
    {
        send_notification = new Intent(getApplicationContext(), MainActivity.class);
        pending = PendingIntent.getActivity(this, 0, send_notification, 0);
        notification = new Notification.Builder(this)
                .setTicker("Title")
                .setContentTitle("Tasks are awaiting")
                .setSmallIcon(R.mipmap.ic_check_circle_outline_black_48dp)
                .setContentText("You got " + String.valueOf(getUncheckedCount()) + " incomplete task(s)")
                .setSound(sound_path)
                .setVibrate(new long[] {0, 1000, 0, 0, 0})
                .setLights(Color.WHITE, 2000, 2000)
                .setContentIntent(pending).build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        manage_notification = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manage_notification.notify(0,notification);
    }

    private int getUncheckedCount()
    {
        result = db.rawQuery(sqlQueries.getUnCheckedToday(helper.getTableName(), date.getCurrentDate()), null);
        int count = result.getCount();

        result.close();

        return count;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.i("D", "onDestroy");
    }
}

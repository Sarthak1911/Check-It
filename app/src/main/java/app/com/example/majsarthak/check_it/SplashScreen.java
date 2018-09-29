package app.com.example.majsarthak.check_it;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class SplashScreen extends Activity {

    TextView app_name;
    Handler handle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        super.onCreate(savedInstanceState);

        //FullScreen Mode
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

        setContentView(R.layout.activity_splash_screen);

        initialize();

        handle.postDelayed(run, 1000);

    }


    public void initialize()
    {
        app_name = (TextView) findViewById(R.id.app_name);
        handle = new Handler();
    }

    Runnable run = new Runnable() {

        @Override
        public void run() {

            Intent toMain = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(toMain);

        }

    };
}

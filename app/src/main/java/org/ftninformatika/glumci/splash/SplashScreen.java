package org.ftninformatika.glumci.splash;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

import org.ftninformatika.glumci.MainActivity;
import org.ftninformatika.glumci.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

public class SplashScreen extends Activity {

    private SharedPreferences prefs;
    private String splashTime;
    private boolean splash;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        splashTime = prefs.getString(getString(R.string.splashtime_key), "500");

        splash = prefs.getBoolean(getString(R.string.splash_key), false);

        if (splash) {
            setContentView(R.layout.splash_screen);

            ImageView imageView = findViewById(R.id.imageView);
            InputStream is = null;
            try {
                is = getAssets().open("movie_logo.png");
                Drawable drawable = Drawable.createFromStream(is, null);
                imageView.setImageDrawable(drawable);
            } catch (IOException e) {
                e.printStackTrace();
            }

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    startActivity(new Intent(SplashScreen.this, MainActivity.class));
                    finish();
                }
            }, Integer.parseInt(splashTime));
        } else {
            startActivity(new Intent(SplashScreen.this, MainActivity.class));
            finish();
        }
    }
}

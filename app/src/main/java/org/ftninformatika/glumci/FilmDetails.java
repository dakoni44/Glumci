package org.ftninformatika.glumci;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.squareup.picasso.Picasso;

import org.ftninformatika.glumci.database.DatabaseHelper;
import org.ftninformatika.glumci.database.model.FavoriteFIlmovi;
import org.ftninformatika.glumci.database.model.Filmovi;
import org.ftninformatika.glumci.database.model.Glumac;
import org.ftninformatika.glumci.net.MyService;
import org.ftninformatika.glumci.net.model.Detalji;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FilmDetails extends AppCompatActivity {

    public static final String NOTIF_CHANNEL_ID = "notif_channel_007";

    private DatabaseHelper databaseHelper;
    private SharedPreferences prefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.film_detalji);

        createNotificationChannel();

        setupToolbar();

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
    }

    private void getDetail(String imdbKey) {
        HashMap<String, String> queryParams = new HashMap<>();
        //TODO upisi api key
        queryParams.put("apikey", "bb578828");
        queryParams.put("i", imdbKey);


        Call<Detalji> call = MyService.apiInterface().getMovieData(queryParams);
        call.enqueue(new Callback<Detalji>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<Detalji> call, Response<Detalji> response) {
                if (response.code() == 200) {
                    Log.d("REZ", "200");

                    Detalji resp = response.body();
                    if (resp != null) {


                        ImageView image = FilmDetails.this.findViewById(R.id.detalji_slika);

                        Picasso.with(FilmDetails.this).load(resp.getPoster()).into(image);

                        /*TODO : Ako zelimo da se rating prikazuje pomocu RatingBar-a
                        //RatingBar ratingBar = SecondActivity.this.findViewById(R.id.detalji_rating);
                        //String rating = resp.getImdbRating();
                        ratingBar.setRating(Float.parseFloat(rating));*/

                        TextView tvRating = FilmDetails.this.findViewById(R.id.detalji_rating);
                        tvRating.setText("IMDB Rating: " + resp.getImdbRating() + "/10");


                        TextView title = FilmDetails.this.findViewById(R.id.detalji_title);
                        title.setText(resp.getTitle());

                        TextView year = FilmDetails.this.findViewById(R.id.detalji_year);
                        year.setText("(" + resp.getYear() + ")");

                        TextView runtime = FilmDetails.this.findViewById(R.id.detalji_runtime);
                        runtime.setText(resp.getRuntime());

                        TextView genre = FilmDetails.this.findViewById(R.id.detalji_genre);
                        genre.setText(resp.getGenre());

                        TextView writer = FilmDetails.this.findViewById(R.id.detalji_writer);
                        writer.setText(resp.getWriter());

                        TextView director = FilmDetails.this.findViewById(R.id.detalji_director);
                        director.setText(resp.getDirector());

                        TextView actors = FilmDetails.this.findViewById(R.id.detalji_actors);
                        actors.setText(resp.getActors());

                        TextView plot = FilmDetails.this.findViewById(R.id.detalji_plot);
                        plot.setText(resp.getPlot());

                    }
                }
            }

            @Override
            public void onFailure(Call<Detalji> call, Throwable t) {
                Toast.makeText(FilmDetails.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        String imdbKey = getIntent().getStringExtra(ListaFilmova.KEY);
        getDetail(imdbKey);
    }


    public void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_detalji_filmova);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_film:
                deleteFilm();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void deleteFilm() {
        int filmZaBrisanje = getIntent().getExtras().getInt("id",0);
        try {
           getDatabaseHelper().getmFilmDao().deleteById(filmZaBrisanje);
            finish();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        boolean toast = prefs.getBoolean(getString(R.string.toast_key), false);
        boolean notif = prefs.getBoolean(getString(R.string.notif_key), false);

        if (toast) {
            Toast.makeText(this, "Film obrisan", Toast.LENGTH_LONG).show();
        }

        if (notif) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIF_CHANNEL_ID);

            builder.setSmallIcon(android.R.drawable.ic_menu_delete);
            builder.setContentText("Film obrisan");

            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.glumci_logo);

            builder.setLargeIcon(bitmap);
            notificationManager.notify(1, builder.build());
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detalji_meni, menu);
        return super.onCreateOptionsMenu(menu);
    }


    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "My Channel";
            String description = "Description of My Channel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(NOTIF_CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public DatabaseHelper getDatabaseHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        return databaseHelper;
    }


}

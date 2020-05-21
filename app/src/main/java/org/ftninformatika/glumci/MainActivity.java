package org.ftninformatika.glumci;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import org.ftninformatika.glumci.adapteri.MyAdapter;
import org.ftninformatika.glumci.database.DatabaseHelper;
import org.ftninformatika.glumci.database.model.Glumac;
import org.ftninformatika.glumci.dialog.AboutDialog;
import org.ftninformatika.glumci.preferences.SettingsActivity;

import java.sql.SQLException;

public class MainActivity extends AppCompatActivity implements MyAdapter.OnItemClickListener {

    private static final int SELECT_PICTURE = 1;
    private static final String TAG = "PERMISSIONS";
    private String imagePath = null;
    private ImageView preview;

    private DatabaseHelper databaseHelper;
    private SharedPreferences prefs;

    public static String GLUMAC_KEY = "GLUMAC_KEY";

    private AlertDialog dijalog;

    public static final String NOTIF_CHANNEL_ID = "notif_channel_007";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createNotificationChannel();

        setupToolbar();

        prefs = PreferenceManager.getDefaultSharedPreferences(this);


        showGlumac();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_glumac:
                addGlumac();
                refresh();
                break;
            case R.id.settings:
                Intent settings = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(settings);
                break;
            case R.id.about_dialog:
                showDialog();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    private void refresh() {

        RecyclerView recyclerView = findViewById(R.id.rvList);
        if (recyclerView != null) {
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);


            MyAdapter adapter = new MyAdapter(getDatabaseHelper(), this);
            recyclerView.setAdapter(adapter);

        }
    }


    private void showGlumac() {

        final RecyclerView recyclerView = this.findViewById(R.id.rvList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        MyAdapter adapter = new MyAdapter(getDatabaseHelper(), this);
        recyclerView.setAdapter(adapter);

    }

    private void reset() {
        imagePath = "";
        preview = null;
    }

    private void addGlumac() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.add_layout);
        dialog.setTitle("Unesite podatke");
        dialog.setCanceledOnTouchOutside(false);

        Button chooseBtn = dialog.findViewById(R.id.choose1);
        chooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                preview = dialog.findViewById(R.id.preview_image1);
                selectPicture();
            }
        });

        Button add = dialog.findViewById(R.id.add_glumac);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText glumacPrezime = dialog.findViewById(R.id.glumac_prezime);
                EditText glumacIme = dialog.findViewById(R.id.glumac_ime);
                EditText glumacBiografija = dialog.findViewById(R.id.glumac_biografija);
                EditText glumacDatum = dialog.findViewById(R.id.glumac_datum);
                EditText glumacRating = dialog.findViewById(R.id.glumac_rating);

                if (preview == null || imagePath == null) {
                    Toast.makeText(MainActivity.this, "Slika mora biti izabrana", Toast.LENGTH_SHORT).show();
                    return;
                }

                Glumac glumac = new Glumac();
                glumac.setmIme(glumacIme.getText().toString());
                glumac.setmPrezime(glumacPrezime.getText().toString());
                glumac.setmBiografija(glumacBiografija.getText().toString());
                glumac.setmDatum(glumacDatum.getText().toString());
                glumac.setmRating(Float.parseFloat(glumacRating.getText().toString()));
                glumac.setImage(imagePath);

                try {
                    getDatabaseHelper().getmGlumacDao().create(glumac);


                    boolean toast = prefs.getBoolean(getString(R.string.toast_key), false);
                    boolean notif = prefs.getBoolean(getString(R.string.notif_key), false);

                    if (toast) {
                        Toast.makeText(MainActivity.this, "Unet nov glumac", Toast.LENGTH_LONG).show();

                    }

                    if (notif) {
                        showNotification("Unet nov glumac");

                    }

                    refresh();
                    reset();
                } catch (NumberFormatException e) {
                    Toast.makeText(MainActivity.this, "Rating mora biti broj", Toast.LENGTH_SHORT).show();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                dialog.dismiss();


            }

        });

        Button cancel = dialog.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void showDialog() {
        if (dijalog == null) {
            dijalog = new AboutDialog(MainActivity.this).prepareDialog();
        } else {
            if (dijalog.isShowing()) {
                dijalog.dismiss();
            }
        }
        dijalog.show();
    }

    public void showNotification(String poruka) {

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, NOTIF_CHANNEL_ID);
        builder.setSmallIcon(android.R.drawable.ic_input_add);
        builder.setContentTitle("Glumci");
        builder.setContentText(poruka);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.glumci_logo);


        builder.setLargeIcon(bitmap);
        notificationManager.notify(1, builder.build());
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

    public void setupToolbar() {

        Toolbar toolbar = findViewById(R.id.toolbar);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

    }


    @Override
    protected void onStart() {

        super.onStart();
    }

    @Override
    protected void onRestart() {

        super.onRestart();
    }

    @Override
    protected void onResume() {

        super.onResume();
        refresh();
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {

        super.onStop();
    }


    @Override
    protected void onDestroy() {

        super.onDestroy();

        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }

    }

    public DatabaseHelper getDatabaseHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        return databaseHelper;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration configuration) {
        super.onConfigurationChanged(configuration);

    }

    @Override
    public void onItemClick(int position) {
        Intent i = new Intent(MainActivity.this, SecondActivity.class);
        try {
            i.putExtra(GLUMAC_KEY, getDatabaseHelper().getmGlumacDao().queryForAll().get(position).getmId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        startActivity(i);

        Intent i2 = new Intent(MainActivity.this, SecondActivity.class);
        try {
            i2.putExtra(GLUMAC_KEY, getDatabaseHelper().getmGlumacDao().queryForAll().get(position).getmId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            if (selectedImage != null) {
                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                if (cursor != null) {
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    imagePath = cursor.getString(columnIndex);
                    cursor.close();

                    if (preview != null) {
                        preview.setImageBitmap(BitmapFactory.decodeFile(imagePath));
                    }
                }
            }
        }
    }


    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
                return true;
            } else {

                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
        }
    }

    private void
    selectPicture() {
        if (isStoragePermissionGranted()) {
            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, SELECT_PICTURE);
        }
    }
}

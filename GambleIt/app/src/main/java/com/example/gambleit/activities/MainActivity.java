package com.example.gambleit.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gambleit.R;
import com.example.gambleit.adapters.DrawerListAdapter;
import com.example.gambleit.database.AccountDataBase;
import com.example.gambleit.model.NavigationItem;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    EditText firstNumber, secondNumber;
    TextView firstNo,secondNo,thirdNo,forthNo,fifthNo,sixthNo,seventhNo,eighthNo,ninthNo,tenthNo;
    Button go;
    Random random=new Random();
    Toolbar toolbar;
    SQLiteDatabase sqLiteDatabase;
    AccountDataBase accountDataBase;
    Cursor data;
    String credit, lot;
    ArrayList<NavigationItem> drawerItems;
    DrawerLayout drawerLayout;
    ListView drawerList;
    private RelativeLayout drawerPane;
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firstNumber=findViewById(R.id.firstNumber);
        secondNumber=findViewById(R.id.secondNumber);
        firstNo=findViewById(R.id.firstNo);
        secondNo=findViewById(R.id.secondNo);
        thirdNo=findViewById(R.id.thirdNo);
        forthNo=findViewById(R.id.forthNo);
        fifthNo=findViewById(R.id.fifthNo);
        sixthNo=findViewById(R.id.sixthNo);
        seventhNo=findViewById(R.id.seventhNo);
        eighthNo=findViewById(R.id.eighthNo);
        ninthNo=findViewById(R.id.ninthNo);
        tenthNo=findViewById(R.id.tenthNo);
        go=findViewById(R.id.go);

        setupToolbar();

        accountDataBase=new AccountDataBase(getApplicationContext());
        sqLiteDatabase=accountDataBase.getWritableDatabase();
        sqLiteDatabase=accountDataBase.getReadableDatabase();
        accountDataBase.addData(1,"0","0");

        fillData();
        setupDrawer();
    }

    private void setupToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.menu_drawer);
            actionBar.setHomeButtonEnabled(true);
            actionBar.show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem menuCredit = menu.findItem(R.id.kredit);
        menuCredit.setTitle("Kredit: "+credit);
       // Toast.makeText(MainActivity.this,"Menu",Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.kredit:
                break;
        }
            return super.onOptionsItemSelected(item);
        }

     public String refreshCredit(){
         data=accountDataBase.getListContents();
         if (data.getCount() == 0) {

         } else {
             while (data.moveToNext()) {
                 credit=data.getString(1);
             }
         };
        return credit;
    }

    @Override
    protected void onResume() {
        super.onResume();
        data=accountDataBase.getListContents();
        if (data.getCount() == 0) {

        } else {
            while (data.moveToNext()) {
                credit=data.getString(1);
                lot=data.getString(2);
            }
        };
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshCredit();
                if(Integer.parseInt(credit)>=Integer.parseInt(lot)) {
                    startAsyncTask(10);
                }else{
                    Toast.makeText(MainActivity.this,"Nemate dovoljno novca",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void setupDrawer() {
        drawerList = findViewById(R.id.left_drawer);
        drawerLayout = findViewById(R.id.drawer_layout);
        drawerPane = findViewById(R.id.drawerPane);

        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String title = "Gamble It";
                switch (i) {
                    case 0:
                        Intent intent=new Intent(MainActivity.this,CreditActivity.class);
                        startActivity(intent);
                        break;
                    case 1:
                        break;
                    case 2:
                        break;

                }
                drawerList.setItemChecked(i, true);
                setTitle(title);
                drawerLayout.closeDrawer(drawerPane);
            }
        });
        drawerList.setAdapter(new DrawerListAdapter(getApplicationContext(), drawerItems));


        drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.app_name,
                R.string.app_name
        ) {
            public void onDrawerClosed(View view) {
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu();
            }
        };
    }

    private void fillData() {
        drawerItems = new ArrayList<>();
        drawerItems.add(new NavigationItem(getString(R.string.creditS), R.drawable.ic_credit));
        drawerItems.add(new NavigationItem(getString(R.string.rulesS), R.drawable.ic_rules));

    }

    private void startAsyncTask(int secs){
        new MyAsyncTask().execute(secs);
    }

    public class MyAsyncTask extends AsyncTask<Integer, Integer, Void> {

        @Override
        protected void onPreExecute() {
            accountDataBase.updateData("1", String.valueOf(Integer.parseInt(credit) - Integer.parseInt(lot)), lot);
            refreshCredit();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            firstNo.setText(String.valueOf(random.nextInt(10)+0));
            secondNo.setText(String.valueOf(random.nextInt(10)+0));
            thirdNo.setText(String.valueOf(random.nextInt(10)+0));
            forthNo.setText(String.valueOf(random.nextInt(10)+0));
            fifthNo.setText(String.valueOf(random.nextInt(10)+0));
            sixthNo.setText(String.valueOf(random.nextInt(10)+0));
           seventhNo.setText(String.valueOf(random.nextInt(10)+0));
            eighthNo.setText(String.valueOf(random.nextInt(10)+0));
            ninthNo.setText(String.valueOf(random.nextInt(10)+0));
            tenthNo.setText(String.valueOf(random.nextInt(10)+0));
            //ovde da probam da skidam i dodajem kredit
            if(firstNumber.getText().toString().equals(firstNo.getText().toString())
                    &&secondNumber.getText().toString().equals(secondNo.getText().toString())) {
                accountDataBase.updateData("1", String.valueOf(Integer.parseInt(credit) + Integer.parseInt(lot) * 100),
                        lot);
                refreshCredit();
            }
            if(firstNumber.getText().toString().equals(secondNo.getText().toString())
                    &&secondNumber.getText().toString().equals(thirdNo.getText().toString())) {
                accountDataBase.updateData("1", String.valueOf(Integer.parseInt(credit) + Integer.parseInt(lot) * 90),
                        lot);
                refreshCredit();
            }
            if(firstNumber.getText().toString().equals(thirdNo.getText().toString())
                    &&secondNumber.getText().toString().equals(forthNo.getText().toString())) {
                accountDataBase.updateData("1", String.valueOf(Integer.parseInt(credit) + Integer.parseInt(lot) * 80),
                        lot);
                refreshCredit();
            }
            if(firstNumber.getText().toString().equals(forthNo.getText().toString())
                    &&secondNumber.getText().toString().equals(fifthNo.getText().toString())) {
                accountDataBase.updateData("1", String.valueOf(Integer.parseInt(credit) + Integer.parseInt(lot) * 70),
                        lot);
                refreshCredit();
            }
            if(firstNumber.getText().toString().equals(fifthNo.getText().toString())
                    &&secondNumber.getText().toString().equals(sixthNo.getText().toString())) {
                accountDataBase.updateData("1", String.valueOf(Integer.parseInt(credit) + Integer.parseInt(lot) * 60),
                        lot);
                refreshCredit();
            }
            if(firstNumber.getText().toString().equals(sixthNo.getText().toString())
                    &&secondNumber.getText().toString().equals(seventhNo.getText().toString())) {
                accountDataBase.updateData("1", String.valueOf(Integer.parseInt(credit) + Integer.parseInt(lot) * 50),
                        lot);
                refreshCredit();
            }
            if(firstNumber.getText().toString().equals(seventhNo.getText().toString())
                    &&secondNumber.getText().toString().equals(eighthNo.getText().toString())) {
                accountDataBase.updateData("1", String.valueOf(Integer.parseInt(credit) + Integer.parseInt(lot) * 40),
                        lot);
                refreshCredit();
            }
            if(firstNumber.getText().toString().equals(eighthNo.getText().toString())
                    &&secondNumber.getText().toString().equals(ninthNo.getText().toString())) {
                accountDataBase.updateData("1", String.valueOf(Integer.parseInt(credit) + Integer.parseInt(lot) * 30),
                        lot);
                refreshCredit();
            }
            if(firstNumber.getText().toString().equals(ninthNo.getText().toString())
                    &&secondNumber.getText().toString().equals(tenthNo.getText().toString())) {
                accountDataBase.updateData("1", String.valueOf(Integer.parseInt(credit) + Integer.parseInt(lot) * 20),
                        lot);
                refreshCredit();
            }

            refreshCredit();

            invalidateOptionsMenu();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            firstNo.setText(String.valueOf(random.nextInt(10)+0));
            secondNo.setText(String.valueOf(random.nextInt(10)+0));
            thirdNo.setText(String.valueOf(random.nextInt(10)+0));
            forthNo.setText(String.valueOf(random.nextInt(10)+0));
            fifthNo.setText(String.valueOf(random.nextInt(10)+0));
            sixthNo.setText(String.valueOf(random.nextInt(10)+0));
            seventhNo.setText(String.valueOf(random.nextInt(10)+0));
            eighthNo.setText(String.valueOf(random.nextInt(10)+0));
            ninthNo.setText(String.valueOf(random.nextInt(10)+0));
            tenthNo.setText(String.valueOf(random.nextInt(10)+0));

        }

        @Override
        protected Void doInBackground(Integer... integers) {
            for(int i=0;i<integers[0];i++){
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                publishProgress(integers[0]-i);
            }
            return null;
        }
    }
}

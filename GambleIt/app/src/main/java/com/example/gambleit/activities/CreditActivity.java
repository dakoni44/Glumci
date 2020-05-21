package com.example.gambleit.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.gambleit.R;
import com.example.gambleit.database.AccountDataBase;

public class CreditActivity extends AppCompatActivity {

    SQLiteDatabase sqLiteDatabase;
    AccountDataBase accountDataBase;
    Cursor data;
    String credit, lot;

    EditText addCredit, addLot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit);

        accountDataBase=new AccountDataBase(getApplicationContext());
        sqLiteDatabase=accountDataBase.getWritableDatabase();
        sqLiteDatabase=accountDataBase.getReadableDatabase();

         addCredit = findViewById(R.id.addCredit);
         addLot = findViewById(R.id.addLot);

        data=accountDataBase.getListContents();
        if (data.getCount() == 0) {

        } else {
            while (data.moveToNext()) {
                credit=data.getString(1);
                lot=data.getString(2);
            }
        };

         addCredit.setText(credit);
         addLot.setText(lot);
    }

    private void addCredit() {

        Button add = findViewById(R.id.baddCredit);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Integer.parseInt(addCredit.getText().toString())>=Integer.parseInt(addLot.getText().toString())) {
                    accountDataBase.updateData("1", addCredit.getText().toString(),
                            addLot.getText().toString());

                    Intent intent=new Intent(CreditActivity.this,MainActivity.class);
                    startActivity(intent);

                }else{
                    Toast.makeText(CreditActivity.this,"Ulog mora biti manji ili isti kao kredit",Toast.LENGTH_SHORT).show();
                }

            }

        });

        Button cancel = findViewById(R.id.cancelCredit);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(CreditActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        addCredit();
    }
}

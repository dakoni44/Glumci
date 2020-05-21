package com.example.gambleit.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class AccountDataBase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME="Account.db";

    private static final String TABLE_NAME = "Account";
    private static final String COLUMN_ID = "id";
    private static final String USER_CREDIT = "credit";
    private static final String USER_LOT = "lot";


    SQLiteDatabase database;

    public AccountDataBase(Context context) {
        super(context, DATABASE_NAME, null,3);
        database=this.getWritableDatabase();
        database=this.getReadableDatabase();
}

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+TABLE_NAME+" (id INTEGER PRIMARY KEY, credit TEXT, lot " +
                "TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Account");
        onCreate(db);
    }
    public boolean addData( int id, String credit,String lot){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put("id",id);
        contentValues.put("credit",credit);
        contentValues.put("lot",lot);

        long result= db.insert(TABLE_NAME,null,contentValues);
        db.close();
        if(result==-1){
            return false;
        }else{
            return true;
        }
    }

    public Cursor getListContents(){
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor data= db.rawQuery("SELECT * FROM " + TABLE_NAME,null);
        return data;
    }

    public boolean updateData( String idS, String credit,String lot){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put("id",idS);
        contentValues.put("credit",credit);
        contentValues.put("lot",lot);
        db.update(TABLE_NAME,contentValues,"id = ?",new String[]{idS});
        return true;
    }

    public Integer deleteData(String id){
        SQLiteDatabase db=this.getWritableDatabase();
        return db.delete(TABLE_NAME,"id = ?", new String[] {id});
    }

    public boolean deleteAllData(){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("drop table if exists "+ TABLE_NAME);
        onCreate(db);
        return true;
    }

}

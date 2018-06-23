package com.cwt.pillboxpioneer.clock;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据库操作
 * Created by 曹吵吵 on 2018/6/20 0020.
 */

public class ClockSQLiteHelper extends SQLiteOpenHelper {
    public static final int MAX_NUM=4;
    public static final String ID="id";
    public static final String STATE="state";
    public static final String HOUR="hour";
    public static final String MINUTE="minute";
    private static final String DATABASE_NAME="clock_store.db";
    private static final String CLOCK_TABLE_NAME ="clock_table";
    private static final int DATABASE_VERSION = 2;
    private static final String CREATE_CLOCK_TABLE = "create table "+ CLOCK_TABLE_NAME +" ("
            + ID+" integer,"
            + STATE+" integer, "
            + HOUR+" integer, "
            + MINUTE+" integer)";
    private static final int[] HOURS_FOR_INIT={5,20,9,10};     //彩蛋
    private static final int[] MINUTES_FOR_INIT={17,2,8,8};     //彩蛋
    //private SQLiteDatabase db;



    public ClockSQLiteHelper(Context context){
        this(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    private ClockSQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_CLOCK_TABLE);
        //db=sqLiteDatabase;
        initTable(sqLiteDatabase);
        Log.e("CLOCK","创建数据库");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists "+ CLOCK_TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    private void initTable(SQLiteDatabase db){
        for (int i=0;i<MAX_NUM;i++){
            ContentValues values=new ContentValues();
            values.put(ID,i);
            values.put(STATE,0);
            values.put(HOUR,HOURS_FOR_INIT[i]);
            values.put(MINUTE,MINUTES_FOR_INIT[i]);
            db.insert(CLOCK_TABLE_NAME,null,values);
        }
    }

    public List<Clock> queryAllClocks(SQLiteDatabase db){
        List<Clock> list=new ArrayList<>();
        Cursor cursor=db.query(CLOCK_TABLE_NAME,null,null,null,null,null,null);
        if (cursor.moveToFirst()){
            do {
                Clock clock=new Clock();
                clock.id=cursor.getInt(cursor.getColumnIndex(ID));
                clock.state=cursor.getInt(cursor.getColumnIndex(STATE));
                clock.hour=cursor.getInt(cursor.getColumnIndex(HOUR));
                clock.minute=cursor.getInt(cursor.getColumnIndex(MINUTE));
                list.add(clock);
                Log.e("CLOCK",clock.toString());
            } while (cursor.moveToNext());
        }
        return list;
    }

    public void updateTable(SQLiteDatabase db, Clock clock){
        int id=clock.id;
        ContentValues values=new ContentValues();
        values.put(STATE,clock.state);
        values.put(HOUR,clock.hour);
        values.put(MINUTE,clock.minute);
        db.update(CLOCK_TABLE_NAME,values,"id = ?",new String[]{String.valueOf(id)});
    }

    public void saveClockConfig(SQLiteDatabase db,List<Clock> list){
        for (int i=0;i<list.size();i++)
            updateTable(db,list.get(i));
    }
}

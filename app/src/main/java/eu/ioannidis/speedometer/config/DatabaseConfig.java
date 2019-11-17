package eu.ioannidis.speedometer.config;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import eu.ioannidis.speedometer.models.SpeedViolationModel;

public class DatabaseConfig extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "speedometer_db";
    private static final int DATABASE_VERSION = 1;

    private static final String CREATE_TABLE =
            "create table " + SpeedViolationContract.SpeedViolationEntry.TABLE_NAME + "("
            + SpeedViolationContract.SpeedViolationEntry.ID + " integer primary key autoincrement,"
            + SpeedViolationContract.SpeedViolationEntry.LONGITUDE + " real,"
            + SpeedViolationContract.SpeedViolationEntry.LATITUDE + " real,"
            + SpeedViolationContract.SpeedViolationEntry.SPEED + " integer not null,"
            + SpeedViolationContract.SpeedViolationEntry.TIMESTAMP + " text);";

    private static final String DROP_TABLE = "drop table if exists " + SpeedViolationContract.SpeedViolationEntry.TABLE_NAME;
    private static final String SELECT_ALL = "select * from " + SpeedViolationContract.SpeedViolationEntry.TABLE_NAME;


    public DatabaseConfig(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
        Log.d("Database Operations", "Database is created...");

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE);
        Log.d("Database Operations", "Table is created...");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(DROP_TABLE);
        Log.d("Database Operations", "Table is removed...");
        sqLiteDatabase.execSQL(CREATE_TABLE);
        Log.d("Database Operations", "Table is created...");
    }

    public void addViolation(SpeedViolationModel s) {
        ContentValues values = new ContentValues();
        values.put(SpeedViolationContract.SpeedViolationEntry.LONGITUDE, s.getLongitude());
        values.put(SpeedViolationContract.SpeedViolationEntry.LATITUDE, s.getLatitude());
        values.put(SpeedViolationContract.SpeedViolationEntry.SPEED, s.getSpeed());
        values.put(SpeedViolationContract.SpeedViolationEntry.TIMESTAMP, s.getTimestamp().toString());

        SQLiteDatabase db = getWritableDatabase();
        db.insert(SpeedViolationContract.SpeedViolationEntry.TABLE_NAME, null, values);
        System.out.println(values.toString());
        db.close();
    }

    public List<SpeedViolationModel> getViolations() {
        List<SpeedViolationModel> result = new ArrayList<>();

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery(SELECT_ALL, null);
        cursor.moveToFirst();
        System.out.println("ADASDASDASDASDASDASDASDASd");
        while (!cursor.isAfterLast()) {
            SpeedViolationModel speedViolationModel = new SpeedViolationModel();

            speedViolationModel.setId(cursor.getInt(cursor.getColumnIndex("id")));
            speedViolationModel.setLongitude(cursor.getDouble(cursor.getColumnIndex("longitude")));
            speedViolationModel.setLatitude(cursor.getDouble(cursor.getColumnIndex("latitude")));
            speedViolationModel.setSpeed(cursor.getInt(cursor.getColumnIndex("speed")));
            speedViolationModel.setTimestamp(Timestamp.valueOf(cursor.getString(cursor.getColumnIndex("timestamp"))));

            result.add(speedViolationModel);
            cursor.moveToNext();
        }
        db.close();
        return result;
    }

}

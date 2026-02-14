package lonerltd.button2;

import static lonerltd.button2.SoundFile.FileEntry.COLUMN_NAME_PATH;
import static lonerltd.button2.SoundFile.FileEntry.TABLE_NAME;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.media.tv.ad.TvAdView;
import android.provider.BaseColumns;


public class SoundFileDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "SoundFile.db";
    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + TABLE_NAME
            + "(" + SoundFile.FileEntry._ID + " INTEGER PRIMARY KEY, " + COLUMN_NAME_PATH + " TEXT)";
    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TABLE_NAME;
    public SoundFileDbHelper(Context c){
        super(c, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db){
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public int numberOfRows(){
        return (int) DatabaseUtils.queryNumEntries(getReadableDatabase(), TABLE_NAME);
    }

    public long add(String path){
        SQLiteDatabase wdb = getWritableDatabase();
        ContentValues vals = new ContentValues();
        vals.put(COLUMN_NAME_PATH, path);
        return wdb.insert(TABLE_NAME, null, vals);
    }
    public void remove(int id){
        SQLiteDatabase wdb = getWritableDatabase();
        Cursor cursor = wdb.rawQuery("SELECT "+BaseColumns._ID+" FROM "+TABLE_NAME, null);
        if(cursor.move(id+1)){
            wdb.delete(TABLE_NAME, BaseColumns._ID+"=?", new String[]{cursor.getString(0)}) ;
        }
    }
    public String get(int id){
        SQLiteDatabase rdb = getReadableDatabase();
        Cursor cursor = rdb.rawQuery("SELECT "+COLUMN_NAME_PATH+" FROM "+TABLE_NAME, null);
        return cursor.move(id+1)? cursor.getString(0) : "";
    }
}

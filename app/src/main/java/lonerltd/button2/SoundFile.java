package lonerltd.button2;

import static lonerltd.button2.SoundFile.FileEntry.COLUMN_NAME_ENTRY;
import static lonerltd.button2.SoundFile.FileEntry.TABLE_NAME;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public final class SoundFile {
    private SoundFile(){};

    public static class FileEntry implements BaseColumns{
        public static final String TABLE_NAME = "entry";
        public static final String COLUMN_NAME_ENTRY = "entry";
    }
}

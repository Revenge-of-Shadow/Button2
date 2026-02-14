package lonerltd.button2;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class AudioUriResolver {
    private static final String MEDIA_DOCUMENTS = "com.android.providers.media.documents";
    private static final String TAG = "AudioUriResolver";

    public static String resolve(Context context, Uri uri){
        if(uri == null) return null;

        String authority = uri.getAuthority();

        if(MEDIA_DOCUMENTS.equals(authority)){
            Uri audioUri = toMediaStoreAudioUri(uri);
            if(audioUri != null) return audioUri.toString();
        }

        return copyToAppStorage(context, uri);
    }
    public static Uri toPlaybackUri(String stored){
        return stored == null? null : Uri.parse(stored);
    }

    private static Uri toMediaStoreAudioUri(Uri documentUri){
        try{
            String docId = DocumentsContract.getDocumentId(documentUri);
            String[] parts = docId.split(":");

            if(parts.length < 2 || !"audio".equals(parts[0])) return null;

            long id = Long.parseLong(parts[1]);
            return ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);
        }
        catch (Exception e){
            Log.e(TAG, "Failed to resolve MediaStore audio URI.", e);
            return null;
        }
    }

    private static String copyToAppStorage(Context context, Uri sourceUri){
        String name = getDisplayName(context, sourceUri);

        if(name == null){
            String segnent = sourceUri.getLastPathSegment();
            name = (segnent != null) ? new File(segnent).getName() : null;
        }
        if (name == null) name = "audio_"+System.currentTimeMillis()+".mp3";
        File dest = new File(context.getFilesDir(), name);
        if(dest.exists()){
            String base = name.contains(".")? name.substring(0, name.lastIndexOf('.')) : name;
            String ext = name.contains(".")? name.substring(name.lastIndexOf('.')) : "";
            dest = new File(context.getFilesDir(), base+"_"+System.currentTimeMillis()+ext);
        }

        try(InputStream in = context.getContentResolver().openInputStream(sourceUri);
                OutputStream out = new FileOutputStream(dest)){
            if(in == null){
                Log.e(TAG, "Could not open input stream for: "+sourceUri);
                return null;
            }

            byte[] buf = new byte[8192];
            int len;
            while ((len = in.read(buf)) != -1) out.write(buf, 0, len);

            return Uri.fromFile(dest).toString();
        }
        catch (IOException e){
            Log.e(TAG, "Copy failed.", e);
            if(dest.exists()) dest.delete();
            return null;
        }
    }

    private static String getDisplayName(Context context, Uri uri){
        try(Cursor cursor = context.getContentResolver().query(
                uri, new String[]{OpenableColumns.DISPLAY_NAME}, null, null, null)){
            if(cursor != null && cursor.moveToFirst()) return cursor.getString(0);
        } catch (Exception ignored){}
        return null;
    }
}

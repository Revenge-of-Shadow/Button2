package lonerltd.button2;

import android.Manifest;
import android.app.ActionBar;
import android.app.ComponentCaller;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.BaseColumns;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private final int PERMISSION_REQUEST_CODE = 104;
    private void requestAudioPermissionThenPick() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+
            if (checkSelfPermission(Manifest.permission.READ_MEDIA_AUDIO)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{Manifest.permission.READ_MEDIA_AUDIO},
                        PERMISSION_REQUEST_CODE
                );
                return;
            }
        } else {
            // Android 12 and below
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_CODE
                );
                return;
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] results) {
        super.onRequestPermissionsResult(requestCode, permissions, results);
        if (requestCode == PERMISSION_REQUEST_CODE
                && results.length > 0
                && results[0] == PackageManager.PERMISSION_GRANTED) {
        }
    }


    ArrayList<MediaPlayer> players;
    private SoundFileDbHelper helper;
    void refreshRows(){
        TableRow.LayoutParams half_params = new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT,
                0.50f
        );
        TableRow.LayoutParams bigger_params = new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT,
                0.55f
        );
        TableRow.LayoutParams smaller_params = new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT,
                0.15f
        );

        TableLayout table = (TableLayout) findViewById(R.id.table);
        table.removeAllViews();

        TableRow firstRow = new TableRow(getApplicationContext());

        Button add = new Button(getApplicationContext());
        add.setText("+");
        add.setLayoutParams(half_params);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestAudioPermissionThenPick();
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("audio/*");
                startActivityForResult(intent, 748);
            }
        });
        Button stopAll = new Button(getApplicationContext());
        stopAll.setText("◼");
        stopAll.setLayoutParams(half_params);
        stopAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        firstRow.addView(add);
        firstRow.addView(stopAll);
        table.addView(firstRow);

        for (int i = 0; i < helper.numberOfRows(); ++i) {
            int finalI = i;

            TableRow row = new TableRow(getApplicationContext());
            row.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT
            ));

            TextView title = new TextView(getApplicationContext());
            title.setText(helper.get(i));
            Button play = new Button(getApplicationContext());
            play.setText("⏯");
            play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri uri = AudioUriResolver.toPlaybackUri(helper.get(finalI));
                    MediaPlayer p = MediaPlayer.create(getApplicationContext(), uri);
                    p.setLooping(false);
                    p.start();
                }
            });

            Button loop = new Button(getApplicationContext());
            loop.setText("\uD83D\uDD03");
            loop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });

            Button rem = new Button(getApplicationContext());
            rem.setText("\uD83D\uDDD1");
            rem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    helper.remove(finalI);
                    refreshRows();
                }
            });


            title.setLayoutParams(bigger_params);
            play.setLayoutParams(smaller_params);
            loop.setLayoutParams(smaller_params);
            rem.setLayoutParams(smaller_params);

            row.addView(title);
            row.addView(play);
            row.addView(loop);
            row.addView(rem);

            table.addView(row);
        }


    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        helper = new SoundFileDbHelper(getApplicationContext());
        refreshRows();
    }

    @Override
    protected void onDestroy() {
        helper.close();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 748 && resultCode == RESULT_OK) {
            Uri raw = data.getData();
            String toStore = AudioUriResolver.resolve(getApplicationContext(), raw);
            if(toStore!=null){
                helper.add(toStore);
            } else{
                Toast.makeText(getApplicationContext(), "Could not read file.", Toast.LENGTH_SHORT).show();
            }
            refreshRows();
        }
    }
}
package lonerltd.button2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;

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


    MediaPlayer[] players;
    int nextPlayer = 0;
    private void addPlayer(Uri uri, boolean looping){
        if(players[nextPlayer] != null){
            players[nextPlayer].stop();
            players[nextPlayer].release();
        }
        try {
            players[nextPlayer] = new MediaPlayer();
            players[nextPlayer].setDataSource(getApplicationContext(), uri);
            players[nextPlayer].setLooping(looping);
            players[nextPlayer].setAudioAttributes(new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build());
            players[nextPlayer].prepareAsync();

            players[nextPlayer].setOnErrorListener((mp, what, extra) -> {
                Log.e("DEBUG", "MediaPlayer error: what=" + what + " extra=" + extra);
                return true;
            });
            players[nextPlayer].setOnPreparedListener(mp -> {
                Log.d("DEBUG", "MediaPlayer starting");
                mp.start();
            });
        } catch (IOException e) {
            players[nextPlayer].release();
        }

        if (nextPlayer == players.length - 1) {
            nextPlayer = 0;
        } else {
            ++nextPlayer;
        }
    }


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
                    addPlayer(uri, false);
                }
            });

            Button loop = new Button(getApplicationContext());
            loop.setText("\uD83D\uDD03");
            loop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri uri = AudioUriResolver.toPlaybackUri(helper.get(finalI));
                    addPlayer(uri, true);
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
        players = new MediaPlayer[16];

        //        Button add = new Button(getApplicationContext());
//        add.setText("+");
//        add.setLayoutParams(half_params);
        (findViewById(R.id.btAdd)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestAudioPermissionThenPick();
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("audio/*");
                startActivityForResult(intent, 748);
            }
        });
//        Button stopAll = new Button(getApplicationContext());
//        stopAll.setText("◼");
//        stopAll.setLayoutParams(half_params);
        (findViewById(R.id.btStopAll)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i = 0; i < players.length; ++i){
                    if(players[i]!=null){
                        players[i].stop();
                    }
                }
            }
        });
//        firstRow.addView(add);
//        firstRow.addView(stopAll);
//        table.addView(firstRow);

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
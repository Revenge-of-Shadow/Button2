package lonerltd.button2;

import android.app.ComponentCaller;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private SoundFileDbHelper helper;
    void refreshRows(){
        TableLayout table = (TableLayout) findViewById(R.id.table);
        table.removeAllViews();

        TableRow firstRow = new TableRow(getApplicationContext());
        Button add = new Button(getApplicationContext());
        add.setText("+");
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent()
                        .setType("*/*")
                        .setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select a sound file."), 748);
            }
        });
        Button stopAll = new Button(getApplicationContext());
        stopAll.setText("◼");
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

                }
            });

            Button loop = new Button(getApplicationContext());
            loop.setText("\uD83D\uDDD8");
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
            helper.add(data.getDataString());
            refreshRows();
        }
    }
}
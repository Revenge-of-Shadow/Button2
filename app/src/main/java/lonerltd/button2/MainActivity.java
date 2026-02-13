package lonerltd.button2;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private SoundFileDbHelper helper;
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
        //helper.add("test");
        TableLayout table = (TableLayout) findViewById(R.id.table);

        for (int i = 0; i < helper.numberOfRows(); ++i) {
            TableRow row = new TableRow(getApplicationContext());
            row.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT
            ));
            TextView title = new TextView(getApplicationContext());
            title.setText(helper.get(i));
            Button play = new Button(getApplicationContext());
            play.setText("⏯");

            row.addView(title);
            row.addView(play);

            table.addView(row);
        }
    }

    @Override
    protected void onDestroy() {
        helper.close();
        super.onDestroy();
    }
}
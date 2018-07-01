package journal.kwame.com.journal.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

import journal.kwame.com.journal.Database.AppExecutors;
import journal.kwame.com.journal.Database.Journal;
import journal.kwame.com.journal.Database.JournalDatabase;
import journal.kwame.com.journal.R;
import journal.kwame.com.journal.Utils.FireBaseJournal;
import journal.kwame.com.journal.Utils.UserPreference;

/**
 * Created by user on 6/28/2018.
 */

public class NewJournalActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private JournalDatabase journalDatabase;
    private EditText title, content;
    private UserPreference preference;
    private String id = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_journal);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.new_journal));
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        journalDatabase = JournalDatabase.getInstance(this);
        preference = new UserPreference(this);
        mDatabase = FirebaseDatabase.getInstance().getReference("journals").child(preference.getUserToken());

        title = findViewById(R.id.title);
        content = findViewById(R.id.content);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.journal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.save) {
            saveJournal();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveJournal() {
        final String journalTitle = title.getText().toString();
        final String journalContent = content.getText().toString();

        if (journalContent.trim().length() == 0 || journalTitle.trim().length() == 0) {
            Toast.makeText(NewJournalActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
        }else {
            id = mDatabase.push().getKey();
            mDatabase.child(id).child("title").setValue(journalTitle);
            mDatabase.child(id).child("content").setValue(journalContent);
            mDatabase.child(id).child("status").setValue(1);
            mDatabase.child(id).child("date").setValue(new Date().getTime());
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    journalDatabase.journalDao().insertJournal(new Journal(journalTitle, journalContent, new Date(), 1, id));
                }
            });
            finish();
        }


    }
}

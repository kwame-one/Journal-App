package journal.kwame.com.journal.Activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import journal.kwame.com.journal.Database.AppExecutors;
import journal.kwame.com.journal.Database.Journal;
import journal.kwame.com.journal.Database.JournalDatabase;
import journal.kwame.com.journal.Fragment.DeleteDialogFragment;
import journal.kwame.com.journal.R;
import journal.kwame.com.journal.Utils.FireBaseJournal;
import journal.kwame.com.journal.Utils.UserPreference;

/**
 * Created by user on 6/29/2018.
 */

public class JournalDetails extends AppCompatActivity implements DeleteDialogFragment.DeleteDialogListener {

    private EditText title, content;
    private TextView date;
    private DatabaseReference mDatabase;
    private JournalDatabase journalDatabase;
    private String key;
    private int id;
    private UserPreference preference;
    private String journalTitle;
    private String journalContent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_details);
        Bundle bundle = getIntent().getExtras();
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Edit Journal");

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
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
        date = findViewById(R.id.date);

        title.setText(bundle.getString("title"));
        content.setText(bundle.getString("content"));
        date.setText("Edited on "+convertToDate(bundle.getLong("date")));
        key = bundle.getString("key");
        id = bundle.getInt("id");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.journal_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.trash:
                DialogFragment dialog = new DeleteDialogFragment();
                dialog.show(getSupportFragmentManager(), "DeleteDialogFragment");
                return  true;
            case R.id.save:
                updateJournal();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateJournal() {
        journalTitle = title.getText().toString();
        journalContent = content.getText().toString();

        if (journalContent.trim().length() == 0 || journalTitle.trim().length() == 0) {
            Toast.makeText(JournalDetails.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
        }else {
            mDatabase.child(key).child("title").setValue(journalTitle);
            mDatabase.child(key).child("content").setValue(journalContent);
            mDatabase.child(key).child("status").setValue(1);
            mDatabase.child(key ).child("date").setValue(new Date().getTime());
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    journalDatabase.journalDao().updateJournal(journalTitle, journalContent, new Date(), 1, id);
                }
            });
            finish();
        }
    }


    private String convertToDate(long milli){
        Date date = new Date(milli);
        Calendar cal = new GregorianCalendar();
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
        sdf.setCalendar(cal);
        cal.setTime(date);
        return sdf.format(date);

    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        mDatabase.child(key).child("title").setValue(journalTitle);
        mDatabase.child(key).child("content").setValue(journalContent);
        mDatabase.child(key).child("date").setValue(new Date().getTime());
        mDatabase.child(key).child("status").setValue(0);
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                journalDatabase.journalDao().trashJournal(0, id);
            }
        });
        JournalDetails.this.finish();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        dialog.dismiss();
    }
}

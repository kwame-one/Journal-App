package journal.kwame.com.journal.Fragment;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import journal.kwame.com.journal.Activity.JournalDetails;
import journal.kwame.com.journal.Activity.NewJournalActivity;
import journal.kwame.com.journal.Adapter.JournalAdapter;
import journal.kwame.com.journal.Database.AppExecutors;
import journal.kwame.com.journal.Database.Journal;
import journal.kwame.com.journal.Database.JournalDatabase;
import journal.kwame.com.journal.Interface.ItemClickListener;
import journal.kwame.com.journal.R;
import journal.kwame.com.journal.Utils.FireBaseJournal;
import journal.kwame.com.journal.Utils.UserPreference;

/**
 * Created by user on 6/29/2018.
 */

public class HomeFragment extends Fragment implements ItemClickListener {
    private FloatingActionButton addJournal;
    private RecyclerView recyclerView;
    private JournalDatabase database;
    private JournalAdapter adapter;
    private TextView textView;
    private List<FireBaseJournal> list = new ArrayList<>();
    private DatabaseReference firebaseDatabase;
    private UserPreference preference;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View view =  inflater.inflate(R.layout.fragment_home, container, false);
        initView(view);
       return view;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(getString(R.string.home));
    }

    private void initView(View view) {
        preference = new UserPreference(getActivity());
        database = JournalDatabase.getInstance(getActivity());
        firebaseDatabase = FirebaseDatabase.getInstance().getReference("journals").child(preference.getUserToken());
        addJournal = view.findViewById(R.id.fab);
        textView = view.findViewById(R.id.text);
        recyclerView = view.findViewById(R.id.recycler_view_journals);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        adapter = new JournalAdapter(getActivity());
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
        progressBar = view.findViewById(R.id.loading);

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                System.out.println("count " +database.journalDao().getJournalsCount());
                if(database.journalDao().getJournalsCount() < 1) {
                    getDataFromFirebase();
                    progressBar.setVisibility(View.INVISIBLE);
                }else {
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });

        loadJournals();


        addJournal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), NewJournalActivity.class));
            }
        });


    }

    @Override
    public void onResume() {
        super.onResume();
        loadJournals();

    }

    private void getDataFromFirebase() {
        firebaseDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshots : dataSnapshot.getChildren()){
                    final String id = firebaseDatabase.push().getKey();
                    final String title = snapshots.child("title").getValue().toString();
                    final String content = snapshots.child("content").getValue().toString();
                    final long date = Long.parseLong(snapshots.child("date").getValue().toString());
                    final int status = Integer.parseInt(snapshots.child("status").getValue().toString());

                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            database.journalDao().insertJournal(new Journal(title, content, new Date(date), status, id));
                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.INVISIBLE);
                recyclerView.setVisibility(View.INVISIBLE);
                textView.setText(databaseError.getMessage().toString());
                textView.setVisibility(View.VISIBLE);
            }
        });

    }

    private void loadJournals() {
        final LiveData<List<Journal>> journals = database.journalDao().getAllJournals();
        journals.observe(this, new Observer<List<Journal>>() {
            @Override
            public void onChanged(@Nullable List<Journal> journals) {
                int size = journals == null ? 0 : journals.size();
                if(size == 0) {
                    //no journals found
                    recyclerView.setVisibility(View.INVISIBLE);
                    textView.setVisibility(View.VISIBLE);
                }else {
                    //journals available
                    progressBar.setVisibility(View.INVISIBLE);
                    textView.setVisibility(View.INVISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);
                    adapter.setJournals(journals);
                    for(Journal journal : journals) {
                        System.out.println("id "+journal.getId());
                        System.out.println("status "+journal.getStatus());
                        list.add(new FireBaseJournal(journal.getId(), journal.getTitle(), journal.getContent(), journal.getDate().getTime(), journal.getStatus(), journal.getDataKey()));
                    }
                }
            }
        });
    }

    @Override
    public void onItemClick(View view, int position) {
        FireBaseJournal model = list.get(position);
        Bundle bundle = new Bundle();
        bundle.putInt("id", model.getId());
        bundle.putString("title", model.getTitle());
        bundle.putString("content", model.getContent());
        bundle.putLong("date", model.getDate());
        bundle.putInt("status", model.getStatus());
        bundle.putString("key", model.getKey());
        System.out.println("idaaa "+model.getId());

        Intent intent = new Intent(getActivity(), JournalDetails.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}

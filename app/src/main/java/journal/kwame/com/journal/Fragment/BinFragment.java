package journal.kwame.com.journal.Fragment;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import journal.kwame.com.journal.Adapter.TrashJournalAdapter;
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

public class BinFragment extends Fragment implements ItemClickListener{
    private RecyclerView recyclerView;
    private JournalDatabase database;
    private TrashJournalAdapter adapter;
    private TextView textView;
    private int size;
    private DatabaseReference databaseReference;
    private UserPreference preference;
    private List<FireBaseJournal> list = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_bin, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(getString(R.string.bin));
    }

    private void initView(View view) {
        database = JournalDatabase.getInstance(getActivity());
        preference = new UserPreference(getActivity());
        databaseReference = FirebaseDatabase.getInstance().getReference("journals").child(preference.getUserToken());
        textView = view.findViewById(R.id.text);
        recyclerView = view.findViewById(R.id.recycler_view_journals);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        adapter = new TrashJournalAdapter(getActivity());
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);

        loadTrashedJournals();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadTrashedJournals();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
      //
            inflater.inflate(R.menu.bin, menu);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.clear:
                emptyBin();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void loadTrashedJournals() {
        final LiveData<List<Journal>> journals = database.journalDao().getTrashedJournals();
        journals.observe(this, new Observer<List<Journal>>() {
            @Override
            public void onChanged(@Nullable List<Journal> journals) {
                size = journals == null ? 0 : journals.size();
                if(size == 0) {
                    //no journals found
                    recyclerView.setVisibility(View.INVISIBLE);
                    textView.setVisibility(View.VISIBLE);

                }else {
                    //journals available
                    textView.setVisibility(View.INVISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);
                    adapter.setJournals(journals);

                    for (Journal item : journals) {
                        list.add(new FireBaseJournal(item.getId(), item.getTitle(), item.getContent(), "click to restore", item.getStatus(), item.getDataKey()));
                    }

                }
            }
        });
    }



    public void emptyBin() {
        final AlertDialog.Builder alert =  new AlertDialog.Builder(getActivity());
        alert.setMessage("Are you sure you want to move to bin?");
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                for(final FireBaseJournal items : list) {
                        Query query = databaseReference.child(items.getKey()).orderByChild("status").equalTo(0);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            databaseReference.child(items.getKey()).removeValue();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        database.journalDao().trashJournalsPermanently();
                    }
                });
            }
        });
        alert.create().show();

    }


    @Override
    public void onItemClick(View view, int position) {
        FireBaseJournal item = list.get(position);
        showPopupMenu(view, item);

    }

    private void showPopupMenu(View view, final FireBaseJournal item) {
        PopupMenu popupMenu = new PopupMenu(getActivity(), view);
        popupMenu.getMenuInflater().inflate(R.menu.popup, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
               switch (menuItem.getItemId()) {
                   case R.id.restore:
                       restore(item);
                       return true;
                   case R.id.delete:
                        delete(item);
                       return true;
                   default:
                       return false;
               }
            }
        });

        popupMenu.show();
    }

    private void restore(final FireBaseJournal item) {
        databaseReference.child(item.getKey()).child("status").setValue(1);
        databaseReference.child(item.getKey()).child("title").setValue(item.getTitle());
        databaseReference.child(item.getKey()).child("content").setValue(item.getContent());
        databaseReference.child(item.getKey() ).child("date").setValue(new Date().getTime());
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                database.journalDao().trashJournal(1, item.getId());
            }
        });
    }

    private void delete(final FireBaseJournal item) {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setMessage("Are you sure you wanna delete journal permanently?");
        alert.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                final Query query = databaseReference.child(item.getKey());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        databaseReference.child(item.getKey()).removeValue();
                        AppExecutors.getInstance().diskIO().execute(new Runnable() {
                            @Override
                            public void run() {
                                database.journalDao().deleteJournal(item.getId());
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alert.create().show();
    }
}

package journal.kwame.com.journal.Database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.Date;
import java.util.List;

/**
 * Created by user on 6/28/2018.
 */

@Dao
public interface JournalDao {
    @Query("SELECT * FROM journals where status = 1 ORDER BY id DESC")
    LiveData<List<Journal>> getAllJournals();

    @Query("SELECT * FROM JOURNALS WHERE status = 0 ORDER BY id DESC")
    LiveData<List<Journal>> getTrashedJournals();

    @Insert
    void insertJournal(Journal journal);

    @Query("UPDATE journals set status = :status where id = :id")
    void trashJournal(int status, int id);

    @Query("UPDATE journals SET title = :title, content = :content, time = :time, status = :status WHERE id = :id")
    void updateJournal(String title, String content, Date time, int status, int id);

    @Query("DELETE FROM journals WHERE status = 0")
    void trashJournalsPermanently();


    @Query("SELECT COUNT(*) FROM journals")
    int getJournalsCount();

    @Query("DELETE FROM journals WHERE id = :id")
    void deleteJournal(int id);

    @Query("DELETE FROM journals")
    void clearTableOnSignOut();

}

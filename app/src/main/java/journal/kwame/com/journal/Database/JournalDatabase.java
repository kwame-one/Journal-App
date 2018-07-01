package journal.kwame.com.journal.Database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

/**
 * Created by user on 6/28/2018.
 */

@Database(entities = {Journal.class}, version = 1, exportSchema = false)
@TypeConverters(TimeConverter.class)
public abstract class JournalDatabase extends RoomDatabase{
    private static JournalDatabase instance;
    private static final Object LOCK = new Object();
    private static final String DB_NAME = "journals";

    public static JournalDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (LOCK){
                instance = Room.databaseBuilder(context.getApplicationContext(), JournalDatabase.class, JournalDatabase.DB_NAME).build();
            }
        }
        return instance;
    }

    public abstract JournalDao journalDao();
}

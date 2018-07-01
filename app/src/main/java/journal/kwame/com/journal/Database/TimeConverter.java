package journal.kwame.com.journal.Database;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;

/**
 * Created by user on 6/28/2018.
 */

public class TimeConverter {
    @TypeConverter
    public static Date toDate(Long timestamp) {
        return timestamp == null ? null : new Date(timestamp);
    }

    @TypeConverter
    public static Long toTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

}

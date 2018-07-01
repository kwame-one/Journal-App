package journal.kwame.com.journal.Database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

/**
 * Created by user on 6/28/2018.
 */
@Entity(tableName = "journals")
public class Journal {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "time")
    private Date date;

    @ColumnInfo(name = "content")
    private String content;

    @ColumnInfo(name = "status")
    private int status;

    @ColumnInfo(name = "dateKey")
    private String dataKey;

    @Ignore
    public Journal(String title, String content, Date date, int status, String dataKey) {
        this.title = title;
        this.date = date;
        this.content = content;
        this.status = status;
        this.dataKey = dataKey;
    }

    public Journal(int id, String title, String content, Date date, int status) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.content = content;
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDataKey() {
        return dataKey;
    }

    public void setDataKey(String dataKey) {
        this.dataKey = dataKey;
    }
}

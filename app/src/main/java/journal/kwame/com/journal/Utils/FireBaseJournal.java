package journal.kwame.com.journal.Utils;

/**
 * Created by user on 6/29/2018.
 */

public class FireBaseJournal {

    private int id;
    private String title;
    private String content;
    private long date;
    private int status;
    private String key;
    private String text;

    public FireBaseJournal(int id, String title, String content, long date, int status, String key) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.date = date;
        this.status = status;
        this.key = key;
    }

    public FireBaseJournal(int id, String title, String content, String text, int status, String key) {
        this.title = title;
        this.id = id;
        this.content = content;
        this.text = text;
        this.status = status;
        this.key = key;
    }
//
//    public FireBaseJournal(String title, String content, long date, int status) {
//        this.title = title;
//        this.content = content;
//        this.date = date;
//        this.status = status;
//    }

    public int getId() {
        return id;
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

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}

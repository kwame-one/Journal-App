package journal.kwame.com.journal.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.github.curioustechizen.ago.RelativeTimeTextView;

import java.util.List;
import java.util.Random;

import journal.kwame.com.journal.Database.Journal;
import journal.kwame.com.journal.Interface.ItemClickListener;
import journal.kwame.com.journal.R;

/**
 * Created by user on 6/28/2018.
 */

public class JournalAdapter extends RecyclerView.Adapter<JournalAdapter.JournalViewHolder>{

    private List<Journal> journals;
    private Context context;
    private ItemClickListener listener;

    public JournalAdapter(Context context) {
        this.context = context;
    }

    @Override
    public JournalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new JournalViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_journal_row, parent, false));
    }

    @Override
    public void onBindViewHolder(JournalViewHolder holder, int position) {
        Journal model = journals.get(position);
        holder.title.setText(model.getTitle());
        holder.content.setText(model.getContent());
        holder.dateTime.setReferenceTime(model.getDate().getTime());
        holder.layout.setBackgroundColor(generateColor());

    }

    private int generateColor() {
        Random random = new Random();
        return Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }

    public void setJournals(List<Journal> journals) {
        this.journals = journals;
        notifyDataSetChanged();
    }

    public List<Journal> getJournals() {
        return journals;
    }

    public void setOnItemClickListener(ItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        if(journals == null)
            return 0;
        return journals.size();
    }

    public class JournalViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView title, content;
        private RelativeTimeTextView dateTime;
        FrameLayout layout;

        public JournalViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            dateTime = itemView.findViewById(R.id.date);
            content = itemView.findViewById(R.id.content);
            layout = itemView.findViewById(R.id.color);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
           listener.onItemClick(view, getAdapterPosition());
        }
    }
}

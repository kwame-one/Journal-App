package journal.kwame.com.journal.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.curioustechizen.ago.RelativeTimeTextView;

import java.util.List;

import journal.kwame.com.journal.Database.Journal;
import journal.kwame.com.journal.Interface.ItemClickListener;
import journal.kwame.com.journal.R;

/**
 * Created by user on 6/28/2018.
 */

public class TrashJournalAdapter extends RecyclerView.Adapter<TrashJournalAdapter.TrashJournalViewHolder>{

    private List<Journal> journals;
    private Context context;
    private ItemClickListener listener;

    public TrashJournalAdapter(Context context) {
        this.context = context;
    }

    @Override
    public TrashJournalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TrashJournalViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_trash_journal_row, parent, false));
    }

    @Override
    public void onBindViewHolder(TrashJournalViewHolder holder, int position) {
        Journal model = journals.get(position);
        holder.title.setText(model.getTitle());
        holder.content.setText(model.getContent());

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

    public class TrashJournalViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView title, content;
        private ImageView more;

        public TrashJournalViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            content = itemView.findViewById(R.id.content);
            more = itemView.findViewById(R.id.more);
            more.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onItemClick(view, getAdapterPosition());
        }
    }
}

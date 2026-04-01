package org.catrobat.catroid.ObjectTrainAndRecognition.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.catrobat.catroid.R;

import java.util.ArrayList;
import java.util.List;

public class LabelAdapter extends RecyclerView.Adapter<LabelAdapter.Holder> {

    public static class LabelRow {
        public final Integer labelId; // null => not trained yet
        public final String name;
        public final boolean deletable;
        public LabelRow(Integer labelId, String name, boolean deletable) {
            this.labelId = labelId; this.name = name; this.deletable = deletable;
        }
    }

    public interface OnTap { void onTap(LabelRow row); }
    public interface OnDelete { void onDelete(LabelRow row); }

    private final List<LabelRow> items = new ArrayList<>();
    private final OnTap onTap;
    private final OnDelete onDelete;

    public LabelAdapter(OnTap onTap, OnDelete onDelete) {
        this.onTap = onTap; this.onDelete = onDelete;
    }

    public void submit(List<LabelRow> rows) {
        items.clear();
        if (rows != null) items.addAll(rows);
        notifyDataSetChanged();
    }

    @NonNull @Override public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_label, parent, false);
        return new Holder(v);
    }

    @Override public void onBindViewHolder(@NonNull Holder h, int position) {
        LabelRow row = items.get(position);
        h.bind(row, onTap, onDelete);
    }

    @Override public int getItemCount() { return items.size(); }

    static class Holder extends RecyclerView.ViewHolder {
        final TextView tvLabel;
        final ImageButton btnDelete;
        Holder(@NonNull View itemView) {
            super(itemView);
            tvLabel = itemView.findViewById(R.id.tvLabel);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
        void bind(final LabelRow row, final OnTap onTap, final OnDelete onDelete) {
            tvLabel.setText(row.name);
            tvLabel.setOnClickListener(v -> onTap.onTap(row));
            if (row.deletable && row.labelId != null) {
                btnDelete.setVisibility(View.VISIBLE);
                btnDelete.setOnClickListener(v -> onDelete.onDelete(row));
            } else {
                btnDelete.setVisibility(View.GONE);
                btnDelete.setOnClickListener(null);
            }
        }
    }
}

package com.tricks.math_tricks.listAdapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tricks.math_tricks.R;
import com.tricks.math_tricks.listeners.OnNotesInteractionListener;

import java.io.File;
import java.util.Date;
import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> {

    private final List<File> file_list;
    private final OnNotesInteractionListener mListener;

    public NotesAdapter(List<File> files, OnNotesInteractionListener listener){
        this.file_list = files;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public NotesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final NotesAdapter.ViewHolder holder, int position) {
        holder.mItem = file_list.get(position);
        holder.itemName.setText(file_list.get(position).getName());
        holder.dateTimeStamp.setText((new Date(file_list.get(position).lastModified())).toString());
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.OnNotesItemClicked(holder.mItem);
            }
        });
        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mListener.OnNotesItemLongClicked(holder.mItem);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return file_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public View mView;
        public TextView itemName;
        public TextView dateTimeStamp;
        public File mItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            itemName = (TextView)itemView.findViewById(R.id.note_item);
            dateTimeStamp = (TextView)itemView.findViewById(R.id.date_time_stamp);
        }
    }
}

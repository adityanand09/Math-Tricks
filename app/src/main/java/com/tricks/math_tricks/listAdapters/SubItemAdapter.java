package com.tricks.math_tricks.listAdapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.tricks.math_tricks.R;
import com.tricks.math_tricks.contentStructure.SscTopicObject;
import com.tricks.math_tricks.listeners.OnSucTopicFragmentInteractionListener;

import java.util.List;

public class SubItemAdapter extends RecyclerView.Adapter<SubItemAdapter.ViewHolder> {

    private final List<SscTopicObject> mItems;
    private final OnSucTopicFragmentInteractionListener mListener;

    public SubItemAdapter(List<SscTopicObject> items, OnSucTopicFragmentInteractionListener listener){
        this.mItems = items;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sub_topic_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        holder.items = mItems.get(position);
        holder.mIcon.setImageDrawable(mItems.get(position).getTopicIcon());
        holder.mTopic.setText(mItems.get(position).getTopicName().replaceAll("[0123456789]", ""));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onTopicItemListener(mItems.get(position).getTopicPath() + "/" + mItems.get(position).getTopicName());
            }
        });

    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public final View mView;
        public final ImageView mIcon;
        public final TextView mTopic;
        public SscTopicObject items;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;
            mIcon = (ImageView)itemView.findViewById(R.id.sub_topic_icon);
            mTopic = (TextView)itemView.findViewById(R.id.sub_topic);
        }
    }
}

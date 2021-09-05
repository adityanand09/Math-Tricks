package com.tricks.math_tricks.listAdapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.tricks.math_tricks.R;
import com.tricks.math_tricks.contentStructure.SscChapObject;
import com.tricks.math_tricks.listeners.OnChapFragmentInteractionListener;

import java.util.List;

public class ChapItemAdapter extends RecyclerView.Adapter<ChapItemAdapter.ViewHolder> {

    private final List<SscChapObject> mChaps;
    private final OnChapFragmentInteractionListener mListener;

    public ChapItemAdapter(List<SscChapObject> chapItem, OnChapFragmentInteractionListener listener) {
        this.mChaps = chapItem;
        this.mListener = listener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chap_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mChaps.get(position);
        holder.mIcon.setImageDrawable(mChaps.get(position).getChapIcon());
        holder.mTopic.setText(mChaps.get(position).getChapName().replaceAll("[0123456789]",""));
        holder.mDescription.setText(mChaps.get(position).getChapDetails());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                        mListener.onChapItemListener(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mChaps.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mIcon;
        public final TextView mTopic;
        public final TextView mDescription;
        public SscChapObject mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIcon = (ImageView)view.findViewById(R.id.chap_icon);
            mTopic = (TextView) view.findViewById(R.id.topic);
            mDescription = (TextView) view.findViewById(R.id.description);
        }
    }
}

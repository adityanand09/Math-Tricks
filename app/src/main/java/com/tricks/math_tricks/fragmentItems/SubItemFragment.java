package com.tricks.math_tricks.fragmentItems;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tricks.math_tricks.R;
import com.tricks.math_tricks.contentStructure.SscTopicObject;
import com.tricks.math_tricks.listAdapters.SubItemAdapter;
import com.tricks.math_tricks.listeners.OnSucTopicFragmentInteractionListener;

import java.util.ArrayList;
import java.util.List;

public class SubItemFragment extends Fragment {

    private static final String TAG = "SubItemFragment";
    private static final int SPACING = 10;
    private OnSucTopicFragmentInteractionListener mListener;
    private SubItemAdapter adapter;
    private Configuration configuration;
    private List<SscTopicObject> items;

    public SubItemFragment(){
        super();
    }

    public SubItemFragment (List<SscTopicObject> mList){
        this.items = mList;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setRetainInstance(true);
        Log.d(TAG, "onCreate");
        configuration = getContext().getResources().getConfiguration();

        if (savedInstanceState != null){
            this.items = savedInstanceState.getParcelableArrayList("savedList");
            adapter = new SubItemAdapter(items, mListener);
        }
        else
            adapter = new SubItemAdapter(items, mListener);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("savedList", new ArrayList<SscTopicObject>(items));
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sub_topic_list_frag, container, false);

        if (view instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) view;
            GridLayoutManager gridLayoutManager;
            if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
                gridLayoutManager = new GridLayoutManager(getContext(), 1, GridLayoutManager.VERTICAL, false);
            else {
                gridLayoutManager = new GridLayoutManager(getContext(), 3, GridLayoutManager.VERTICAL, false);
                recyclerView.addItemDecoration(new itemDecoration(SPACING));
            }
            recyclerView.setLayoutManager(gridLayoutManager);
            // Set the adapter
            recyclerView.setAdapter(adapter);
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSucTopicFragmentInteractionListener) {
            mListener = (OnSucTopicFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        this.configuration = newConfig;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null)
            mListener = (OnSucTopicFragmentInteractionListener) getContext();
    }
}

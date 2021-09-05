package com.tricks.math_tricks.fragmentItems;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class itemDecoration extends RecyclerView.ItemDecoration {
    int mSpacing;

    public itemDecoration(int spacing) {
        this.mSpacing = spacing;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.left = mSpacing;
        outRect.right = mSpacing;
        outRect.top = mSpacing;
        outRect.bottom = mSpacing;
    }
}


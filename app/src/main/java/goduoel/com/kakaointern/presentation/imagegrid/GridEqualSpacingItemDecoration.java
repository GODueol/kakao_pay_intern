package goduoel.com.kakaointern.presentation.imagegrid;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import goduoel.com.kakaointern.utils.UnitUtil;

public class GridEqualSpacingItemDecoration extends RecyclerView.ItemDecoration {


    private int spanCount;
    private int spacingDp;

    GridEqualSpacingItemDecoration(int spanCount, int spacingDp) {
        this.spanCount = spanCount;
        this.spacingDp = spacingDp;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view); // item position
        int column = position % spanCount; // item column
        int spacingPx = UnitUtil.convertDpToPixel(parent.getContext(), spacingDp);
        
        outRect.left = spacingPx - column * spacingPx / spanCount; // spacing - column * ((1f / spanCount) * spacing)
        outRect.right = (column + 1) * spacingPx / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

        if (position < spanCount) { // top edge
            outRect.top = spacingPx;
        }
        outRect.bottom = spacingPx; // item bottom

    }
}

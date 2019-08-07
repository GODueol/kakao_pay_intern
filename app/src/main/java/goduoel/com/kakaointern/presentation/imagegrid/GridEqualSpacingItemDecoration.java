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
        int position = parent.getChildAdapterPosition(view);
        int column = position % spanCount;
        int spacingPx = UnitUtil.convertDpToPixel(parent.getContext(), spacingDp);

        if (column == 0) {
            outRect.left = spacingPx;
        }
        outRect.right = spacingPx;

        if (position < spanCount) {
            outRect.top = spacingPx;
        }
        outRect.bottom = spacingPx;

    }
}

package goduoel.com.kakaointern.presentation.imagegrid;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import goduoel.com.kakaointern.presentation.imagedetail.PinchScaleImageView;

public class DynamicColumGridView extends RecyclerView {

    private GridLayoutManager gridLayoutManager;

    private static final int DEFAULT_SPAN_COUNT = 4;
    private static final int MAX_SPAN = 7;
    private static final int MIN_SPAN = 2;
    private int spanCount = DEFAULT_SPAN_COUNT;
    private static final int SPAN_SLOP = 7;
    private ScaleGestureDetector scaleGestureDetector;

    public DynamicColumGridView(@NonNull Context context) {
        this(context, null, 0);
    }

    public DynamicColumGridView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DynamicColumGridView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init(context);
    }

    private void init(Context context) {
        scaleGestureDetector = new ScaleGestureDetector(context, new DynamicColumGridView.ScaleListener());

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            spanCount = MAX_SPAN;
        }
        gridLayoutManager = new GridLayoutManager(context, spanCount);
        setLayoutManager(gridLayoutManager);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (scaleGestureDetector != null) {
            scaleGestureDetector.onTouchEvent(ev);
        }

        return scaleGestureDetector.onTouchEvent(ev) || super.onTouchEvent(ev);
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {

            float scaleFactor = detector.getScaleFactor();

            if (Float.isNaN(scaleFactor) || Float.isInfinite(scaleFactor))
                return false;
            if (scaleFactor < 0) {
                return false;
            }

            if (gestureTolerance(detector)) {
                if (scaleFactor > 1f) {
                    setSpan(++spanCount);
                } else if (scaleFactor < 1f) {
                    setSpan(--spanCount);
                }
            }

            return true;
        }
    }

    private void setSpan(int spanCount) {

        if (spanCount < MIN_SPAN) {
            this.spanCount = MIN_SPAN;
            return;
        }

        if (spanCount > MAX_SPAN) {
            this.spanCount = MAX_SPAN;
            return;
        }

        gridLayoutManager.setSpanCount(spanCount);
    }

    private boolean gestureTolerance(@NonNull ScaleGestureDetector detector) {
        final float spanDelta = Math.abs(detector.getCurrentSpan() - detector.getPreviousSpan());
        return spanDelta > SPAN_SLOP;
    }
}

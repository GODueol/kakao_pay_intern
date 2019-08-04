package goduoel.com.kakaointern.presentation.imagedetail;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;

public class PinchScaleImageView extends AppCompatImageView {

    private ScaleGestureDetector scaleGestureDetector;
    private final Matrix matrix;
    private float currentScale;
    private float centerX;
    private float centerY;
    private final float[] mMatrixValues = new float[9];
    private static final int SPAN_SLOP = 7;

    public PinchScaleImageView(Context context) {
        this(context, null);
    }

    public PinchScaleImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PinchScaleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        matrix = new Matrix();
        scaleGestureDetector = new ScaleGestureDetector(context, new ScaleListener());
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (scaleGestureDetector != null) {
            scaleGestureDetector.onTouchEvent(ev);
        }

        return scaleGestureDetector.onTouchEvent(ev) || super.onTouchEvent(ev);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int saveCount = canvas.save();
        canvas.concat(matrix);
        super.onDraw(canvas);
        canvas.restoreToCount(saveCount);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        reset();
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

            if (centerX == 0.0f) {
                centerX = getWidth() / 2.0f;
            }

            if (centerY == 0.0f) {
                centerY = getHeight() / 2.0f;
            }

            if (getScale() < 3.0f || scaleFactor < 1f) {
                matrix.postScale(scaleFactor, scaleFactor, centerX, centerY);
                invalidate();
            }


            return true;
        }
    }

    private boolean gestureTolerance(@NonNull ScaleGestureDetector detector) {
        final float spanDelta = Math.abs(detector.getCurrentSpan() - detector.getPreviousSpan());
        return spanDelta > SPAN_SLOP;
    }

    public float getScale() {
        return (float) Math.sqrt((float) Math.pow(getValue(matrix, Matrix.MSCALE_X), 2) + (float) Math.pow
                (getValue(matrix, Matrix.MSKEW_Y), 2));
    }

    private float getValue(Matrix matrix, int whichValue) {
        matrix.getValues(mMatrixValues);
        return mMatrixValues[whichValue];
    }

    private void reset() {
        matrix.reset();
        currentScale = 1.0f;
        invalidate();
    }

}

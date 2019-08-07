package goduoel.com.kakaointern.presentation.imagemenu;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import goduoel.com.kakaointern.databinding.ViewOverlayMenuBinding;

public class OverlayMenuView extends ConstraintLayout {

    private ViewOverlayMenuBinding binder;
    private OnClickListener onShareListener;
    private OnClickListener onDownListener;
    private OnClickListener onSiteListener;
    private OnClickListener onBackListner;


    public OverlayMenuView(Context context) {
        this(context, null, 0);
    }

    public OverlayMenuView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OverlayMenuView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        binder = ViewOverlayMenuBinding.inflate(LayoutInflater.from(context), this, true);
        binder.btnDownload.setOnClickListener(v -> {
            if (onDownListener != null) {
                onDownListener.onClick(v);
            }
        });

        binder.btnShare.setOnClickListener(v -> {
            if (onShareListener != null) {
                onShareListener.onClick(v);
            }
        });
        binder.btnSite.setOnClickListener(v -> {
            if (onSiteListener != null) {
                onSiteListener.onClick(v);
            }
        });

        binder.btnBack.setOnClickListener(v -> {
            if (onBackListner != null) {
                onBackListner.onClick(v);
            }
        });
    }

    public void setLayoutShow(boolean isShow) {
        if (isShow) {
            showing();
        } else {
            hinding();
        }
    }

    private void showing() {
        AnimatorSet animatorSet = new AnimatorSet();

        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                animatorSet.removeAllListeners();
                binder.layoutBottomMenu.setVisibility(View.VISIBLE);
                binder.layoutTopMenu.setVisibility(View.VISIBLE);
            }
        });


        ObjectAnimator slideUp = ObjectAnimator.ofFloat(binder.layoutBottomMenu, "translationY", binder.layoutTopMenu.getHeight(), 0);
        ObjectAnimator slideDown = ObjectAnimator.ofFloat(binder.layoutTopMenu, "translationY", -binder.layoutBottomMenu.getHeight(), 0);

        animatorSet.playTogether(slideUp, slideDown);
        animatorSet.setDuration(500);
        animatorSet.start();


    }

    private void hinding() {
        AnimatorSet animatorSet = new AnimatorSet();

        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                animatorSet.removeAllListeners();
                binder.layoutTopMenu.setVisibility(View.GONE);
                binder.layoutBottomMenu.setVisibility(View.GONE);
            }
        });

        ObjectAnimator slideUp = ObjectAnimator.ofFloat(binder.layoutTopMenu, "translationY", 0, -binder.layoutTopMenu.getHeight());
        ObjectAnimator slideDown = ObjectAnimator.ofFloat(binder.layoutBottomMenu, "translationY", 0, binder.layoutBottomMenu.getHeight());

        animatorSet.playTogether(slideUp, slideDown);
        animatorSet.setDuration(500);
        animatorSet.start();
    }

    public void setOnShareListener(OnClickListener onShareListener) {
        this.onShareListener = onShareListener;
    }

    public void setOnDownListener(OnClickListener onDownListener) {
        this.onDownListener = onDownListener;
    }

    public void setOnSiteListener(OnClickListener onSiteListener) {
        this.onSiteListener = onSiteListener;
    }

    public void setOnBackListner(OnClickListener onBackListner) {
        this.onBackListner = onBackListner;
    }

}

package goduoel.com.kakaointern.presentation.intro;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;

import goduoel.com.kakaointern.R;
import goduoel.com.kakaointern.databinding.ActivityIntroBinding;
import goduoel.com.kakaointern.presentation.BaseActivity;
import goduoel.com.kakaointern.presentation.imagegrid.ImageGridActivity;

public class IntroActivity extends BaseActivity<ActivityIntroBinding> {

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_intro;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLottie();
    }

    private void initLottie() {
        binding.lottieIntro.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                Intent intent = new Intent(IntroActivity.this, ImageGridActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}

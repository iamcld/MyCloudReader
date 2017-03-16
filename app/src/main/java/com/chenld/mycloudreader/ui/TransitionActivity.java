package com.chenld.mycloudreader.ui;

import android.content.Intent;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.bumptech.glide.Glide;
import com.chenld.mycloudreader.MainActivity;
import com.chenld.mycloudreader.R;
import com.chenld.mycloudreader.app.ConstantsImageUrl;
import com.chenld.mycloudreader.databinding.ActivityTransitionBinding;
import com.chenld.mycloudreader.utils.CommonUtils;

import java.util.Random;

import static com.chenld.mycloudreader.utils.CommonUtils.getResoure;

public class TransitionActivity extends AppCompatActivity {
    private ActivityTransitionBinding mBinding;
    private boolean isIn;
    private boolean animationEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_transition);

        int i = new Random().nextInt(ConstantsImageUrl.TRANSITION_URLS.length);
        //1、效率最高
        mBinding.ivDefultPic.setImageDrawable(CommonUtils.getDrawable(R.drawable.img_transition_default));
        //2、UI线程中对图片读取和解析的,所以有可能对一个Activity的启动造成延迟
        //mBinding.ivDefultPic.setImageResource(R.drawable.img_transition_default);
        //3、把Bitmap对象封装成Drawable对象,然后调用setImageDrawable来设置图片，如果频繁调用，最好直接调用setImageDrawable
        //mBinding.ivDefultPic.setImageBitmap(BitmapFactory.decodeResource(getResoure(), R.drawable.img_transition_default));

        Glide.with(this)
                .load(ConstantsImageUrl.TRANSITION_URLS[i])
                .placeholder(R.drawable.img_transition_default)//设置加载中和加载失败的情况.也就是加载中的图片，可放个gif
                .error(R.drawable.img_transition_default)//失败图片
                .into(mBinding.ivPic);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mBinding.ivDefultPic.setVisibility(View.GONE);
            }
        }, 1500);
        
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                toMainActivity();
            }
        }, 3500);

//        Animation animation = AnimationUtils.loadAnimation(this, R.anim.transition_anim);
//        animation.setAnimationListener(animationListener);
//        mBinding.ivPic.startAnimation(animation);
        mBinding.tvJump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toMainActivity();
//                animationEnd();
            }
        });


    }

    /**
     * 实现监听跳转效果
     */
    private Animation.AnimationListener animationListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
            animationEnd();
        }

        @Override
        public void onAnimationEnd(Animation animation) {

        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };

    private void animationEnd() {
        synchronized (TransitionActivity.this){
            if (!animationEnd){
                animationEnd = true;
                mBinding.ivPic.clearAnimation();
                toMainActivity();
            }
        }
    }

    private void toMainActivity() {
        if (isIn){
            return;
        }
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

        overridePendingTransition(R.anim.screen_zoom_in, R.anim.screen_zoom_out);
        finish();
        isIn = true;
    }
}

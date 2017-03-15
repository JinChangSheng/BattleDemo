package com.example.jinchangsheng.battledemo;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private static final int MSG_TYPE_IMAGE_IN_TOP = 0; // 进入时初始化动画类型
    private static final int MSG_TYPE_IMAGE_IN_BOTTOM = 1; // 进入时初始化动画类型
    private static final int ANIM_TYPE_TOP = 2; // 顶部动画
    private static final int ANIM_TYPE_BOOTOM = 3; // 底部动画
    private static final int ANIM_TYPE_TOP_SHOW = 4; // 顶部出牌动画
    private static final int ANIM_TYPE_BOOTOM_SHOW = 5; // 底部出牌动画
    private static final int ANIM_TYPE_TOP_BACK = 6; // 顶部收排动画
    private static final int ANIM_TYPE_BOOTOM_BACK = 7; // 底部收排动画
    Handler uiHandler;
    private LinearLayout ll_content_top;
    private LinearLayout ll_content_bottom;
    private Button btn_play_card;
    private Button btn_play_card_bottom;
    private RelativeLayout rl_content;
    private TextView tv_answer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    private void initView() {
        rl_content=(RelativeLayout)findViewById(R.id.rl_content);
        ll_content_top = (LinearLayout) findViewById(R.id.ll_content_top);
        ll_content_bottom = (LinearLayout) findViewById(R.id.ll_content_bottom);
        btn_play_card = (Button) findViewById(R.id.btn_play_card);
        btn_play_card.setOnClickListener(this);
        btn_play_card_bottom = (Button) findViewById(R.id.btn_play_card_bottom);
        btn_play_card_bottom.setOnClickListener(this);
        // 定义handler专门负责线程交互
        uiHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Bundle data = msg.getData();
                int cycleNum = data.getInt("cycleNum");
                if (msg.what == MSG_TYPE_IMAGE_IN_TOP) {
                    processInAnim(ANIM_TYPE_TOP,cycleNum);
                }else if (msg.what==MSG_TYPE_IMAGE_IN_BOTTOM){
                    processInAnim(ANIM_TYPE_BOOTOM,cycleNum);
                }else if (msg.what==ANIM_TYPE_TOP_BACK){
                    //缩小并平移到原来位置
                    ll_content_top.getChildAt(1).startAnimation(AnimationUtils.loadAnimation(MainActivity.this,
                            R.anim.card_back_top));
                    scaleAnim.cancel();
                    closeAnimation(tv_answer);
                }else if (msg.what==ANIM_TYPE_BOOTOM_BACK){
                    ll_content_bottom.getChildAt(2).startAnimation(AnimationUtils.loadAnimation(MainActivity.this,
                            R.anim.card_back_bottom));
                    scaleAnim.cancel();
                    tv_answer.setVisibility(View.GONE);
                }
            }
        };

        tv_answer=new TextView(this);
        tv_answer .setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        tv_answer.setTextColor(Color.parseColor("#ffffff"));
        tv_answer.setBackground(this.getResources().getDrawable(R.drawable.cardgame_public_fightscenes_btn_tyre));
        tv_answer.setGravity(Gravity.CENTER);

    }

    private void initData() {
        startInAnim();
    }

    /**
     * 处理的是初始化image飞入动画
     */
    private void processInAnim(int type, int cycleNum) {
        if (ANIM_TYPE_TOP == type) {
            Animation animation = AnimationUtils.loadAnimation(this,
                    R.anim.card_in_top);
            Interpolator interpolator = new OvershootInterpolator(0.8f);
            animation.setInterpolator(interpolator);
            View view = ll_content_top.getChildAt(cycleNum);
            view.setVisibility(View.VISIBLE);
            view.startAnimation(animation);
        }else if (ANIM_TYPE_BOOTOM==type){
            Animation animation = AnimationUtils.loadAnimation(this,
                    R.anim.card_in_bottom);
            Interpolator interpolator = new OvershootInterpolator(0.8f);
            animation.setInterpolator(interpolator);
            View view = ll_content_bottom.getChildAt(cycleNum);
            view.setVisibility(View.VISIBLE);
            view.startAnimation(animation);
        }
    }

    /**
     * 出牌动画
     */
    private void showCardAnim(int type){
        if (type==ANIM_TYPE_TOP_SHOW){
            Animation animation = AnimationUtils.loadAnimation(this,
                    R.anim.play_a_hand_top);
            Interpolator interpolator = new OvershootInterpolator(0.8f);
            animation.setInterpolator(interpolator);
            final View view = ll_content_top.getChildAt(1);
            final int[] location = new int[2];
            view.getLocationInWindow(location);
            Log.i("loaction.....", "(" + location[0] + "," + location[1] + ")");
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    showAnswer(location[0] - 30, location[1] + view.getHeight() + 150);
                    uiHandler.sendEmptyMessageDelayed(ANIM_TYPE_TOP_BACK, 2000);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            view.startAnimation(animation);
        }else if (type==ANIM_TYPE_BOOTOM_SHOW){
            Animation animation = AnimationUtils.loadAnimation(this,
                    R.anim.play_a_hand_bottom);
            Interpolator interpolator = new OvershootInterpolator(0.8f);
            animation.setInterpolator(interpolator);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    uiHandler.sendEmptyMessageDelayed(ANIM_TYPE_BOOTOM_BACK, 2000);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            View view = ll_content_bottom.getChildAt(2);
            view.startAnimation(animation);
        }
    }

    private void showAnswer(int left,int top){
        RelativeLayout.LayoutParams lp=new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(left, top, 0, 0);
        tv_answer.setText("车胎");
        tv_answer.setLayoutParams(lp);
        tv_answer.setVisibility(View.VISIBLE);
        showAnimation(tv_answer);
        rl_content.removeView(tv_answer);
        rl_content.addView(tv_answer);
    }
    ScaleAnimation scaleAnim;
    //放大
    public void showAnimation(final View view) {
        scaleAnim= new ScaleAnimation(0f, 1.0f, 0f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        scaleAnim.setFillAfter(true);
        scaleAnim.setDuration(500);
        scaleAnim.setInterpolator(new AccelerateInterpolator());//快速到达终点超出一小步然后回到终点
        view.startAnimation(scaleAnim);
    }

    //关闭缩小文字
    public void closeAnimation(final View view) {
        final ScaleAnimation scaleAnim = new ScaleAnimation(1.0f, 0f, 1.0f, 0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        scaleAnim.setFillAfter(true);
        scaleAnim.setDuration(300);
        scaleAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                tv_answer.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(scaleAnim);
    }

    /**
     * 启动飞入动画
     */
    public void startInAnim() {
        new MyThread(MSG_TYPE_IMAGE_IN_TOP, ll_content_top.getChildCount(), 150).start();
        new MyThread(MSG_TYPE_IMAGE_IN_BOTTOM, ll_content_top.getChildCount(), 150).start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_play_card:
                showCardAnim(ANIM_TYPE_TOP_SHOW);//    出牌
                break;
            case R.id.btn_play_card_bottom:
                showCardAnim(ANIM_TYPE_BOOTOM_SHOW);//    出牌
                break;
        }
    }

    /**
     * 这个是专门处理飞入动画的线程
     */
    class MyThread extends Thread {
        private int num; // 循环次数
        private int type; // 事件类型
        private int sleepTime; // sleep的时间

        public MyThread(int type, int num, int sleepTime) {
            this.type = type;
            this.num = num;
            this.sleepTime = sleepTime;
        }

        @Override
        public void run() {
            for (int i = 0; i < num; i++) {

                Message msg = uiHandler.obtainMessage();
                msg.what = type;
                Bundle data = new Bundle();
                data.putInt("cycleNum", i);
                msg.setData(data);
                msg.sendToTarget();

                try {
                    sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }


}

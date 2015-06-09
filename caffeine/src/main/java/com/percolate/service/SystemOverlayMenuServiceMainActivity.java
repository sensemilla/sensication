package com.percolate.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LayoutAnimationController;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.percolate.R;
import com.percolate.service.library.FloatingActionButton;
import com.percolate.service.library.FloatingActionMenu;
import com.percolate.service.library.SubActionButton;
import com.percolate.service.library.animation.RotateAndTranslateAnimation;
import com.percolate.youtube.ui.search.YouTubeActivity;


public class SystemOverlayMenuServiceMainActivity extends AbstractService {

    public static final int MSG_VALUE = 1;
    public static final int MSG_SQRT = 2;
    public static int MSG_BtnClickMain;
    private static SystemOverlayMenuServiceMainActivity systemOverlayMenuServiceMainActivity;
    private final IBinder mBinder = new LocalBinder();
    private int counter = 0, incrementby = 1;

    private FloatingActionButton rightLowerButton;
    private FloatingActionButton leftBottomButton;

    private FloatingActionMenu rightLowerMenu;
    private FloatingActionMenu leftbottomvlcmenu;

    private boolean serviceWillBeDismissed;
    public SubActionButton tcSub1;
    public SubActionButton tcSub2;
    public SubActionButton tcSub3;
    public SubActionButton tcSub4;
    public SubActionButton tcSub5;
    public SubActionButton tcSub6;
    public SubActionButton tcSub7;
    private NotificationManager nm;
    private boolean mExpanded = false;
    public View taskfabview;
    private int mLeftHolderWidth;
    private int mChildSize;

    /* the distance between child Views */
    private int mChildGap;
    private String TAG = "SystemOverlayMenuServiceMainActivity";

    public SystemOverlayMenuServiceMainActivity() {
    }

    public SystemOverlayMenuServiceMainActivity(Intent intent) {
        onBind(intent);

    }

    public static SystemOverlayMenuServiceMainActivity getSystemOverlayMenuService() {
        return systemOverlayMenuServiceMainActivity;
    }
    public class LocalBinder extends Binder {
        public SystemOverlayMenuServiceMainActivity getServiceTasks() {
            // Return this instance of LocalService so clients can call public methods
            return SystemOverlayMenuServiceMainActivity.this;
        }
    }

    @Override
    public void onStartService() {
        showNotification();

        Log.i("MyTaskService", "Service started.");

    }

    @Override
    public void onStopService() {
        nm.cancel(getClass().getSimpleName().hashCode());
        Log.i("MyTaskService", "Service Stopped.");
    }

    @Override
    public void onReceiveMessage(Message msg) {
        // Get argument from message, take square root and respond right away
        if (msg.what == MSG_VALUE) {
            double sqrt = Math.sqrt(SystemOverlayMenuServiceMainActivity.MSG_BtnClickMain);// todo msg.arg1);

            // Send bundle
            // todo Message newMsg = Message.obtain(null, MSG_SQRT);
            Message newMsg = Message.obtain(null, MSG_BtnClickMain, 4, 0); // todo Message.obtain(null, SystemOverlayMenuServiceMainActivity.MSG_BtnClickMain);
            Bundle b = new Bundle();
            b.putDouble("sqrt", sqrt);
            newMsg.setData(b);

            send(newMsg);
           // send(Message.obtain(null, MSG_BtnClickMain, 4, 0));

            Log.v(TAG, "message received and send");
        }
    }



    @Override
    public void onCreate() {
        super.onCreate();
        SystemOverlayMenuServiceMainActivity.systemOverlayMenuServiceMainActivity = this;
        serviceWillBeDismissed = false;

        // Set up the white button on the lower right corner
        // more or less with default parameter
        final ImageView fabIconNew = new ImageView(this);
        fabIconNew.setImageDrawable(getResources().getDrawable(R.drawable.ic_more_pressed));
        WindowManager.LayoutParams params = FloatingActionButton.Builder.getDefaultSystemWindowParams(this);
        params.width = 54;
        params.height = 54;

        params.horizontalMargin = 0.2000f;
        params.verticalMargin = 0.1075f;


        rightLowerButton = new FloatingActionButton.Builder(this)
                .setContentView(fabIconNew)
                .setSystemOverlay(true)
                .setLayoutParams(params)
                .setPosition(FloatingActionButton.POSITION_TOP_CENTER)
                .build();

        SubActionButton.Builder rLSubBuilder = new SubActionButton.Builder(this);
        ImageView rlIcon1 = new ImageView(this);
        ImageView rlIcon2 = new ImageView(this);
        ImageView rlIcon3 = new ImageView(this);
        ImageView rlIcon4 = new ImageView(this);

        rlIcon1.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_chat_light));
        rlIcon2.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_camera_light));
        rlIcon3.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_video_light));
        rlIcon4.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_place_light));

        // Build the menu with default options: light theme, 90 degrees, 72dp radius.
        // Set 4 default SubActionButtons
        SubActionButton rlSub1 = rLSubBuilder.setContentView(rlIcon1).build();
        SubActionButton rlSub2 = rLSubBuilder.setContentView(rlIcon2).build();
        SubActionButton rlSub3 = rLSubBuilder.setContentView(rlIcon3).build();
        SubActionButton rlSub4 = rLSubBuilder.setContentView(rlIcon4).build();
        rightLowerMenu = new FloatingActionMenu.Builder(this, true)
                .addSubActionView(rlSub1, rlSub1.getLayoutParams().width, rlSub1.getLayoutParams().height)
                .addSubActionView(rlSub2, rlSub2.getLayoutParams().width, rlSub2.getLayoutParams().height)
                .addSubActionView(rlSub3, rlSub3.getLayoutParams().width, rlSub3.getLayoutParams().height)
                .addSubActionView(rlSub4, rlSub4.getLayoutParams().width, rlSub4.getLayoutParams().height)
                .setStartAngle(180)
                .setEndAngle(0)
                .attachTo(rightLowerButton)
                .build();


        rightLowerButton.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    rightLowerButton.startAnimation(createHintSwitchAnimation(isExpanded()));
                    switchState(true);
                    if (isExpanded()) {
                        fabIconNew.setImageResource(R.drawable.ic_more_circle_pressed_o);
                    } else
                        fabIconNew.setImageResource(R.drawable.ic_more_pressed);

                }

                return false;
            }
        });

        rightLowerButton.setVisibility(View.GONE);
        ////////////////////////////////////////////////////////

        // Set up the large red button on the top center side
        // With custom button and content sizes and margins
        int redActionButtonSize = getResources().getDimensionPixelSize(R.dimen.red_action_button_size);
        int redActionButtonMargin = getResources().getDimensionPixelOffset(R.dimen.action_button_margin)-8;
        int redActionButtonContentSize = getResources().getDimensionPixelSize(R.dimen.red_action_button_content_size);
        int redActionButtonContentMargin = getResources().getDimensionPixelSize(R.dimen.red_action_button_content_margin) - 12;
        int redActionMenuRadius = getResources().getDimensionPixelSize(R.dimen.red_action_menu_radius) + 100;
        int blueSubActionButtonSize = getResources().getDimensionPixelSize(R.dimen.blue_sub_action_button_size) + 16;
        int blueSubActionButtonContentMargin = getResources().getDimensionPixelSize(R.dimen.blue_sub_action_button_content_margin);

        ImageView fabIconStar = new ImageView(this);
        fabIconStar.setImageDrawable(getResources().getDrawable(R.drawable.maineyemenu));

        FloatingActionButton.LayoutParams fabIconStarParams = new FloatingActionButton.LayoutParams(redActionButtonContentSize, redActionButtonContentSize);
        fabIconStarParams.setMargins(redActionButtonContentMargin,
                redActionButtonContentMargin,
                redActionButtonContentMargin,
                redActionButtonContentMargin);

        WindowManager.LayoutParams params2 = FloatingActionButton.Builder.getDefaultSystemWindowParams(this);
        params2.width = redActionButtonSize+100;
        params2.height = redActionButtonSize-36;
       // params2.x = +320;

        leftBottomButton = new FloatingActionButton.Builder(this)
                .setSystemOverlay(true)
                .setContentView(fabIconStar, fabIconStarParams)
                .setBackgroundDrawable(R.drawable.lilaframe)
                .setPosition(FloatingActionButton.POSITION_BOTTOM_RIGHT)
                .setLayoutParams(params2)
                .build();
        // Set up customized SubActionButtons for the right center menu
        SubActionButton.Builder tCSubBuilder = new SubActionButton.Builder(this);
        tCSubBuilder.setBackgroundDrawable(getResources().getDrawable(R.drawable.composer_button_normalhologreenorange));

        SubActionButton.Builder tCSubMiddleBuilder = new SubActionButton.Builder(this);
        tCSubMiddleBuilder.setBackgroundDrawable(getResources().getDrawable(R.drawable.composer_button_normalholorosapink));

        SubActionButton.Builder tCRedBuilder = new SubActionButton.Builder(this);
        tCRedBuilder.setBackgroundDrawable(getResources().getDrawable(R.drawable.composer_button_normalhololilaorange));

        FrameLayout.LayoutParams blueContentParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        blueContentParams.setMargins(blueSubActionButtonContentMargin,
                blueSubActionButtonContentMargin,
                blueSubActionButtonContentMargin,
                blueSubActionButtonContentMargin);


        // Set custom layout params
        FrameLayout.LayoutParams blueParams = new FrameLayout.LayoutParams(blueSubActionButtonSize, blueSubActionButtonSize);
        tCSubBuilder.setLayoutParams(blueParams);
        tCRedBuilder.setLayoutParams(blueParams);
        tCSubMiddleBuilder.setLayoutParams(blueParams);

        ImageView tcIcon1 = new ImageView(this);
        ImageView tcIcon2 = new ImageView(this);
        ImageView tcIcon3 = new ImageView(this);
        ImageView tcIcon4 = new ImageView(this);
        ImageView tcIcon5 = new ImageView(this);
        ImageView tcIcon6 = new ImageView(this);
        ImageView tcIcon7 = new ImageView(this);


        tcIcon1.setImageDrawable(getResources().getDrawable(R.drawable.ffwdblue));
        tcIcon2.setImageDrawable(getResources().getDrawable(R.drawable.pinred));
        tcIcon3.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_camera));
        tcIcon4.setImageDrawable(getResources().getDrawable(R.drawable.refreshorangebutton));
        tcIcon5.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_picture));
        tcIcon6.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_cancel));
        tcIcon7.setImageDrawable(getResources().getDrawable(R.drawable.backdarklila));


        tcSub1 = tCSubBuilder.setContentView(tcIcon1, blueContentParams).build();
        tcSub2 = tCSubBuilder.setContentView(tcIcon2, blueContentParams).build();
        tcSub3 = tCSubBuilder.setContentView(tcIcon3, blueContentParams).build();
        tcSub4 = tCSubBuilder.setContentView(tcIcon4, blueContentParams).build();
        tcSub5 = tCSubBuilder.setContentView(tcIcon5, blueContentParams).build();
        tcSub6 = tCRedBuilder.setContentView(tcIcon6, blueContentParams).build();
        tcSub7 = tCRedBuilder.setContentView(tcIcon7, blueContentParams).build();

        // Build another menu with custom options
        leftbottomvlcmenu = new FloatingActionMenu.Builder(this, true)
                .addSubActionView(tcSub1, tcSub1.getLayoutParams().width +6, tcSub1.getLayoutParams().height+6)
                .addSubActionView(tcSub2, tcSub2.getLayoutParams().width +10, tcSub2.getLayoutParams().height+10)
                .addSubActionView(tcSub3, tcSub3.getLayoutParams().width +6, tcSub3.getLayoutParams().height+6)
                .addSubActionView(tcSub4, tcSub4.getLayoutParams().width+12, tcSub4.getLayoutParams().height+12)
                .addSubActionView(tcSub5, tcSub5.getLayoutParams().width +6, tcSub5.getLayoutParams().height+6)
                .addSubActionView(tcSub6, tcSub6.getLayoutParams().width +10, tcSub6.getLayoutParams().height+10)
                .addSubActionView(tcSub7, tcSub7.getLayoutParams().width +6, tcSub7.getLayoutParams().height+6)
                .setRadius(redActionMenuRadius-100)
                .setStartAngle(130)
                .setEndAngle(340)
                .attachTo(leftBottomButton)
                .build();

        leftbottomvlcmenu.setStateChangeListener(new FloatingActionMenu.MenuStateChangeListener() {
            @Override
            public void onMenuOpened(FloatingActionMenu menu) {
            rightLowerButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void onMenuClosed(FloatingActionMenu menu) {
                if (serviceWillBeDismissed) {
                    SystemOverlayMenuServiceMainActivity.this.stopSelf();
                    serviceWillBeDismissed = false;
                }
                rightLowerButton.startAnimation(createHintSwitchAnimation(isExpanded()));
                switchState(true);
                rightLowerButton.setVisibility(View.GONE);
            }
        });

        tcSub1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                counter += incrementby;
                send(Message.obtain(null, MSG_BtnClickMain, 1, 0));
                leftbottomvlcmenu.close(true);
            }
        });
        tcSub2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    counter += incrementby;
                    // Send data as simple integer
                    send(Message.obtain(null, MSG_BtnClickMain, 2, 0));

                }
                catch (Throwable t) { //you should always ultimately catch all exceptions in timer tasks.
                    Log.e("TimerTick", "Timer Tick Failed.", t);
                }
                leftbottomvlcmenu.close(true);
            }
        });
        tcSub3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send(Message.obtain(null, MSG_BtnClickMain, 3, 0));
                leftbottomvlcmenu.close(true);
            }
        });
        tcSub4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send(Message.obtain(null, MSG_BtnClickMain, 4, 0));
                leftbottomvlcmenu.close(true);
            }
        });
        tcSub5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send(Message.obtain(null, MSG_BtnClickMain, 5, 0));
                leftbottomvlcmenu.close(true);
            }
        });
        tcSub6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send(Message.obtain(null, MSG_BtnClickMain, 6, 0));
                leftbottomvlcmenu.close(true);
            }
        });
        // make the red button terminate the service
        tcSub7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serviceWillBeDismissed = true; // the order is important
                onStopService();
                send(Message.obtain(null, MSG_BtnClickMain, 7, 0));
                leftbottomvlcmenu.close(true);
            }
        });
    }

    @Override
    public void onDestroy() {
        if(rightLowerMenu != null && rightLowerMenu.isOpen()) rightLowerMenu.close(false);
        if(leftbottomvlcmenu != null && leftbottomvlcmenu.isOpen()) leftbottomvlcmenu.close(false);
        if(rightLowerButton != null) rightLowerButton.detach();
        if(leftBottomButton != null) leftBottomButton.detach();

        super.onDestroy();
    }

    public boolean isExpanded() {
        return mExpanded;
    }

    private void showNotification() {
        nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        // In this sample, we'll use the same text for the ticker and the expanded notification
        // Set the icon, scrolling text and timestamp
        // The PendingIntent to launch our activity if the user selects this notification
        // Set the info for the views that show in the notification panel.

        String text = "Home" + getString(R.string.service_started, getClass().getSimpleName());
        Notification notification = new Notification(R.drawable.octotask, text, System.currentTimeMillis());
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, YouTubeActivity.class), 0);
        notification.setLatestEventInfo(this, getClass().getSimpleName(), text, contentIntent);

        // Send the notification.
        // We use a layout id because it is a unique number.  We use it later to cancel.

        nm.notify(getClass().getSimpleName().hashCode(), notification);
    }

    public Animation bindItemAnimation(final View child, final boolean isClicked, final long duration) {
        Animation animation = createItemDisapperAnimation(duration, isClicked);
        child.setAnimation(animation);

        return animation;
    }



    private void itemDidDisappear() {
        final int itemCount = rightLowerButton.getChildCount();
        for (int i = 0; i < itemCount; i++) {
            View item = rightLowerButton.getChildAt(i);
            item.clearAnimation();
        }

        switchState(false);
    }

    private static Animation createItemDisapperAnimation(final long duration, final boolean isClicked) {
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(new ScaleAnimation(1.0f, isClicked ? 2.0f : 0.0f, 1.0f, isClicked ? 2.0f : 0.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f));
        animationSet.addAnimation(new AlphaAnimation(1.0f, 0.0f));

        animationSet.setDuration(duration);
        animationSet.setInterpolator(new DecelerateInterpolator());
        animationSet.setFillAfter(true);

        return animationSet;
    }

    public static Animation createHintSwitchAnimation(final boolean expanded) {
        Animation animation = new RotateAnimation(expanded ? 135 : 0, expanded ? 0 : 135, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setStartOffset(0);
        animation.setDuration(100);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.setFillAfter(true);


        return animation;
    }

    public void switchState(final boolean showAnimation) {
        if (showAnimation) {
            final int childCount = rightLowerButton.getChildCount();
            for (int i = 0; i < childCount; i++) {
                bindChildAnimation(rightLowerButton.getChildAt(i), i, 300);
            }
        }

        mExpanded = !mExpanded;

        if (!showAnimation) {
            rightLowerButton.requestLayout();
        }

        rightLowerButton.invalidate();
    }

    /**
     * refers to {@link LayoutAnimationController#getDelayForView(View view)}
     */
    private static long computeStartOffset(final int childCount, final boolean expanded, final int index,
                                           final float delayPercent, final long duration, Interpolator interpolator) {
        final float delay = delayPercent * duration;
        final long viewDelay = (long) (getTransformedIndex(expanded, childCount, index) * delay);
        final float totalDelay = delay * childCount;

        float normalizedDelay = viewDelay / totalDelay;
        normalizedDelay = interpolator.getInterpolation(normalizedDelay);

        return (long) (normalizedDelay * totalDelay);
    }


    private static int getTransformedIndex(final boolean expanded, final int count, final int index) {
        return count - 1 - index;
    }

    private static Animation createExpandAnimation(float fromXDelta, float toXDelta, float fromYDelta, float toYDelta,
                                                   long startOffset, long duration, Interpolator interpolator) {
        Animation animation = new RotateAndTranslateAnimation(0, toXDelta, 0, toYDelta, 0, 720);
        animation.setStartOffset(startOffset);
        animation.setDuration(duration);
        animation.setInterpolator(interpolator);
        animation.setFillAfter(true);

        return animation;
    }

    private static Animation createShrinkAnimation(float fromXDelta, float toXDelta, float fromYDelta, float toYDelta,
                                                   long startOffset, long duration, Interpolator interpolator) {
        AnimationSet animationSet = new AnimationSet(false);
        animationSet.setFillAfter(true);

        final long preDuration = duration / 2;
        Animation rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setStartOffset(startOffset);
        rotateAnimation.setDuration(preDuration);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setFillAfter(true);

        animationSet.addAnimation(rotateAnimation);

        Animation translateAnimation = new RotateAndTranslateAnimation(0, toXDelta, 0, toYDelta, 360, 720);
        translateAnimation.setStartOffset(startOffset + preDuration);
        translateAnimation.setDuration(duration - preDuration);
        translateAnimation.setInterpolator(interpolator);
        translateAnimation.setFillAfter(true);

        animationSet.addAnimation(translateAnimation);

        return animationSet;
    }

    private void bindChildAnimation(final View child, final int index, final long duration) {
        final boolean expanded = mExpanded;
        final int childCount = rightLowerButton.getChildCount();
        Rect frame = computeChildFrame(!expanded, mLeftHolderWidth, index, mChildGap, mChildSize);

        final int toXDelta = frame.left - child.getLeft();
        final int toYDelta = frame.top - child.getTop();

        Interpolator interpolator = mExpanded ? new AccelerateInterpolator() : new OvershootInterpolator(1.5f);
        final long startOffset = computeStartOffset(childCount, mExpanded, index, 0.1f, duration, interpolator);
// todo 4 = 0
        Animation animation = mExpanded ? createShrinkAnimation(0, toXDelta , 0, toYDelta, startOffset, duration,
                interpolator) : createExpandAnimation(0, toXDelta, 0, toYDelta, startOffset, duration, interpolator);

        final boolean isLast = getTransformedIndex(expanded, childCount, index) == childCount - 1;
        animation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (isLast) {
                    rightLowerButton.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            onAllAnimationsEnd();
                        }
                    }, 0);
                }
            }
        });

        child.setAnimation(animation);
    }

    private static Rect computeChildFrame(final boolean expanded, final int paddingLeft, final int childIndex,
                                          final int gap, final int size) {
        final int left = expanded ? (paddingLeft + childIndex * (gap + size) + gap) : ((paddingLeft - size) / 2);

        return new Rect(left, 0, left + size, size);
    }

    private void onAllAnimationsEnd() {
        final int childCount = rightLowerButton.getChildCount();
        for (int i = 0; i < childCount; i++) {
            rightLowerButton.getChildAt(i).clearAnimation();
        }

        rightLowerButton.requestLayout();
    }

}

package com.github.florent37.singledateandtimepicker.dialog;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.github.florent37.singledateandtimepicker.PickerViewAnimateUtil;
import com.github.florent37.singledateandtimepicker.R;

import static android.content.Context.WINDOW_SERVICE;

public class BottomSheetHelper {

    private Context context;
    private int layoutId;

    private View view;
    private View container;
    private Listener listener;

    private Handler handler;
    private WindowManager windowManager;

    public BottomSheetHelper(Context context, int layoutId) {
        this.context = context;
        this.layoutId = layoutId;
        this.handler = new Handler(Looper.getMainLooper());
    }


    private void init() {
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
        if (context instanceof Activity) {
            windowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);

            view = LayoutInflater.from(context).inflate(layoutId, null, true);

            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(
                    // Shrink the window to wrap the content rather than filling the screen
                    WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_PANEL,
                    // Don't let it grab the input focus
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    // Make the underlying application window visible through any transparent parts
                    PixelFormat.TRANSLUCENT);

            if ((layoutParams.softInputMode
                    & WindowManager.LayoutParams.SOFT_INPUT_IS_FORWARD_NAVIGATION) == 0) {
                WindowManager.LayoutParams nl = new WindowManager.LayoutParams();
                nl.copyFrom(layoutParams);
                nl.softInputMode |= WindowManager.LayoutParams.SOFT_INPUT_IS_FORWARD_NAVIGATION;
                layoutParams = nl;
            }
            windowManager.addView(view, layoutParams);
            initAnimation();
            view.setBackgroundColor(Color.parseColor("#60000000"));
            container = view.findViewById(R.id.sheetContentLayout);
            view.findViewById(R.id.bottom_sheet_background)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            hide();
                        }
                    });

            if (listener != null) {
                listener.onLoaded(view);
            }
        }
//            }
//        }, 100);
    }

    private Animation outAnim;
    private Animation inAnim;

    protected void initAnimation() {
        inAnim = getInAnimation();
        outAnim = getOutAnimation();
    }

    private int gravity = Gravity.BOTTOM;

    public Animation getInAnimation() {
        int res = PickerViewAnimateUtil.getAnimationResource(this.gravity, true);
        return AnimationUtils.loadAnimation(context, res);
    }

    public Animation getOutAnimation() {
        int res = PickerViewAnimateUtil.getAnimationResource(this.gravity, false);
        return AnimationUtils.loadAnimation(context, res);
    }

    public BottomSheetHelper setListener(Listener listener) {
        this.listener = listener;
        return this;
    }

    public void display() {
        init();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                final ObjectAnimator objectAnimator =
//                        ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, view.getHeight(), 0);
//                // Set the layer type to hardware
//                objectAnimator.addListener(new AnimatorListenerAdapter() {
//
//                    @Override
//                    public void onAnimationEnd(Animator animation) {
//                        if (listener != null) {
//                            listener.onOpen();
//                        }
//                    }
//                });
//                objectAnimator.start();
//            }
//        }, 400);
        inAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if (listener != null) {
                    listener.onOpen();
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        container.startAnimation(inAnim);
    }

    public void hide() {
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                final ObjectAnimator objectAnimator =
//                        ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, 0, view.getHeight());
//                objectAnimator.addListener(new AnimatorListenerAdapter() {
//                    @Override
//                    public void onAnimationEnd(Animator animation) {
//                        view.setVisibility(View.GONE);
//                        if (listener != null) {
//                            listener.onClose();
//                        }
//                        remove();
//                    }
//                });
//                objectAnimator.start();
//            }
//        }, 100);
        outAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);
                if (listener != null) {
                    listener.onClose();
                }
                remove();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        container.startAnimation(outAnim);
    }

    public void dismiss() {
        remove();
    }

    private void remove() {
        if (view.getWindowToken() != null) windowManager.removeView(view);
    }

    public interface Listener {
        void onOpen();

        void onLoaded(View view);

        void onClose();
    }
}

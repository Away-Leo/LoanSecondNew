package com.pingxun.ui.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Created by Administrator on 2019/7/21.
 */

public class BezierView extends View {

    private Paint paint;
    private Path mPath;
    private int mItemWidth = 600;

    private ValueAnimator mAnimator;
    private int mOffsetX;

    public BezierView(Context context) {
        this(context, null);
    }

    public BezierView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BezierView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);

        mPath = new Path();

        mAnimator = ValueAnimator.ofInt(0, mItemWidth);
        mAnimator.addUpdateListener(animation -> {
            mOffsetX = (int) animation.getAnimatedValue();
            invalidate();
        });

        mAnimator.setInterpolator(new LinearInterpolator());

        mAnimator.setDuration(1000);
        mAnimator.setRepeatCount(-1);
        mAnimator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPath.reset();
        int halfItem = mItemWidth / 2;
        //必须先减去一个浪的宽度，以便第一遍动画能够刚好位移出一个波浪，形成无限波浪的效果
        mPath.moveTo(-mItemWidth + mOffsetX, halfItem);
        for (int i = -mItemWidth; i < mItemWidth + getWidth(); i += mItemWidth) {
            mPath.rQuadTo(halfItem / 2, -100, halfItem, 0);
            mPath.rQuadTo(halfItem / 2, 100, halfItem, 0);
        }
        canvas.drawPath(mPath, paint);
    }

}

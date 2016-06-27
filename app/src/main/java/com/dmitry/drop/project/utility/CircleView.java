package com.dmitry.drop.project.utility;

/**
 * Created by Dmitry on 25/06/2016.
 *
 * CircleView is used to display the open post animation when a post is clicked
 * An instance of this class is created and added to a layout in order to be animated
 */
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.dmitry.drop.project.R;

public class CircleView extends View
{
    private Paint mFillPaint, mStrokePaint;
    private Context mContext;
    private int mX;
    private int mY;

    public CircleView(Context context, int x, int y)
    {
        super(context);
        mContext = context;
        this.mX = x;
        this.mY = y;
        init();
    }

    private void init()
    {
        mFillPaint = new Paint();
        mFillPaint.setAntiAlias(true);
        mFillPaint.setStyle(Paint.Style.FILL);
        mFillPaint.setColor(ContextCompat.getColor(mContext, R.color.white_transp));

        mStrokePaint = new Paint();
        mStrokePaint.setAntiAlias(true);
        mStrokePaint.setStyle(Paint.Style.STROKE);
        mStrokePaint.setColor(ContextCompat.getColor(mContext, R.color.white));
        mStrokePaint.setStrokeWidth(1);
    }

    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        canvas.drawCircle(mX, mY, 100, mFillPaint);
        canvas.drawCircle(mX, mY, 100, mStrokePaint);
    }
}

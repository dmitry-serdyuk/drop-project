package com.dmitry.drop.project.utility;

/**
 * Created by Dima on 25/06/2016.
 */
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.dmitry.drop.project.R;

public class CircleView extends View
{
    private Paint fillPaint, strokePaint;
    private Context mContext;
    int x;
    int y;

    public CircleView(Context context, int x, int y)
    {
        super(context);
        mContext = context;
        this.x = x;
        this.y = y;
        init();
    }

    private void init()
    {
        fillPaint = new Paint();
        fillPaint.setAntiAlias(true);
        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setColor(ContextCompat.getColor(mContext, R.color.white_transp));

        strokePaint = new Paint();
        strokePaint.setAntiAlias(true);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setColor(ContextCompat.getColor(mContext, R.color.white));
        strokePaint.setStrokeWidth(1);
    }

    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        canvas.drawCircle(x, y, 100, fillPaint);
        canvas.drawCircle(x, y, 100, strokePaint);
    }

}

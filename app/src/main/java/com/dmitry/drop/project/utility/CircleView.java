package com.dmitry.drop.project.utility;

/**
 * Created by Dima on 25/06/2016.
 */
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.dmitry.drop.project.R;

public class CircleView extends View
{
    private static final int DEFAULT_CIRCLE_COLOR = Color.RED;

    private int circleColor = DEFAULT_CIRCLE_COLOR;
    private Paint fillPaint, strokePaint;
    private float radius;
    int x;
    int y;

    public CircleView(Context context, float radius, int x, int y)
    {
        super(context);
        this.radius = radius;
        this.x = x;
        this.y = y;
        init(context);
    }

    private void init(Context context)
    {
        fillPaint = new Paint();
        fillPaint.setAntiAlias(true);
        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setColor(ContextCompat.getColor(context, R.color.light_blue_50_transp));

        strokePaint = new Paint();
        strokePaint.setAntiAlias(true);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setColor(ContextCompat.getColor(context, R.color.blue));
        strokePaint.setStrokeWidth(1);
    }

    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        canvas.drawCircle(x, y, radius, fillPaint);
        canvas.drawCircle(x, y, radius, strokePaint);
    }
}

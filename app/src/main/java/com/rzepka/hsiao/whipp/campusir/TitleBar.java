package com.rzepka.hsiao.whipp.campusir;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class TitleBar extends View {

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public TitleBar(Context context) {
        super(context);
    }

    public TitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void onDraw(Canvas canvas){
        super.onDraw(canvas);

        paint.setColor(Color.parseColor("#a10000"));
        canvas.drawRect(-1, -1, 1200, 300, paint);
    }

}

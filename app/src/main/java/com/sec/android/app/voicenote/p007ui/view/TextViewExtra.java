package com.sec.android.app.voicenote.p007ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.TextView;
import com.sec.android.app.voicenote.C0690R;

/* renamed from: com.sec.android.app.voicenote.ui.view.TextViewExtra */
public class TextViewExtra extends TextView {
    private Paint mPaint;
    private Rect mRect;

    public TextViewExtra(Context context) {
        super(context);
    }

    public TextViewExtra(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mRect = new Rect();
        this.mPaint = new Paint();
        this.mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        this.mPaint.setColor(context.getColor(C0690R.C0691color.stt_stroke_line_color));
    }

    public TextViewExtra(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public TextViewExtra(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        int height = getHeight() / getLineHeight();
        if (getLineCount() > height) {
            height = getLineCount();
        }
        Rect rect = this.mRect;
        Paint paint = this.mPaint;
        int lineBounds = getLineBounds(0, rect);
        float dimension = getResources().getDimension(C0690R.dimen.stt_line_margin_top_text);
        int i = lineBounds;
        for (int i2 = 0; i2 < height; i2++) {
            float f = ((float) i) + dimension;
            Paint paint2 = paint;
            canvas.drawLine((float) rect.left, f, (float) rect.right, f, paint2);
            float f2 = f + 1.0f;
            canvas.drawLine((float) rect.left, f2, (float) rect.right, f2, paint2);
            i += getLineHeight();
        }
        super.onDraw(canvas);
    }
}

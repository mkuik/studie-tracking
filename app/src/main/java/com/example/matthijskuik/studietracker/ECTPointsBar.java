package com.example.matthijskuik.studietracker;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.example.matthijskuik.studietracker.R;

/**
 * TODO: document your custom view class.
 */
public class ECTPointsBar extends View {

    private int successColor;
    private int failedColor;
    private int unknownColor;
    private int textColor;

    private float textSize;

    private int successValue;
    private int failedValue;
    private int unknownValue;

    private Paint paint = new Paint();
    private Rect textBounds = new Rect();

    private TextPaint mTextPaint;
//    private float mTextWidth;
//    private float mTextHeight;

    public ECTPointsBar(Context context) {
        super(context);
        init(null, 0);
    }

    public ECTPointsBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public ECTPointsBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    public int getSuccessValue() {
        return successValue;
    }

    public void setSuccessValue(int successValue) {
        this.successValue = successValue;
    }

    public int getUnknownValue() {
        return unknownValue;
    }

    public void setUnknownValue(int unknownValue) {
        this.unknownValue = unknownValue;
    }

    public int getFailedValue() {
        return failedValue;
    }

    public void setFailedValue(int failedValue) {
        this.failedValue = failedValue;
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.ECTPointsBar, defStyle, 0);

        successColor = a.getColor(R.styleable.ECTPointsBar_successColor, Color.GREEN);
        failedColor = a.getColor(R.styleable.ECTPointsBar_failedColor, Color.RED);
        unknownColor = a.getColor(R.styleable.ECTPointsBar_unkownColor, Color.GRAY);
        textColor = a.getColor(R.styleable.ECTPointsBar_textColor, Color.WHITE);

        textSize = a.getDimension(R.styleable.ECTPointsBar_textSize, 40);

        successValue = a.getInteger(R.styleable.ECTPointsBar_successScore, 0);
        failedValue = a.getInteger(R.styleable.ECTPointsBar_failedScore, 0);
        unknownValue = a.getInteger(R.styleable.ECTPointsBar_unknownScore, 0);

        a.recycle();

        // Set up a default TextPaint object
        mTextPaint = new TextPaint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.LEFT);
        mTextPaint.setTextSize(textSize);
        mTextPaint.setColor(textColor);

//        // Update TextPaint and text measurements from attributes
//        invalidateTextPaintAndMeasurements();
    }

    public int getSumValue() {
        return successValue + failedValue + unknownValue;
    }

//    private void invalidateTextPaintAndMeasurements() {
//        mTextPaint.setTextSize(textSize);
//        mTextPaint.setColor(textColor);
//        mTextWidth = mTextPaint.measureText(mExampleString);
//
//        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
//        mTextHeight = fontMetrics.bottom;
//    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // TODO: consider storing these as member variables to reduce
        // allocations per draw cycle.
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int contentWidth = getWidth() - paddingLeft - paddingRight;
        int contentHeight = getHeight() - paddingTop - paddingBottom;

        final float sum = getSumValue();
        if (sum != 0) {
            final float successWidth = (successValue / sum) * contentWidth;
            final float failedWidth = (failedValue / sum) * contentWidth;
            final float unknownWidth = (unknownValue / sum) * contentWidth;

            paint.setStrokeWidth(0);
            paint.setColor(successColor);
            canvas.drawRect(0, 0, successWidth, contentHeight, paint);
            paint.setColor(failedColor);
            canvas.drawRect(successWidth, 0, successWidth + failedWidth, contentHeight, paint);
            paint.setColor(unknownColor);
            canvas.drawRect(successWidth + failedWidth, 0, contentWidth, contentHeight, paint);

            paint.setColor(textColor);
            paint.setTextSize(textSize);

            if (successValue > 0) {
                final String text = String.format("%d", successValue);
                paint.getTextBounds(text, 0, text.length(), textBounds);
                canvas.drawText(text, successWidth / 2 - textBounds.width() / 2,
                        contentHeight / 2 + textBounds.height() / 2, paint);
            }
            if (failedValue > 0) {
                final String text = String.format("%d", failedValue);
                paint.getTextBounds(text, 0, text.length(), textBounds);
                canvas.drawText(text, successWidth + failedWidth / 2 - textBounds.width() / 2,
                        contentHeight / 2 + textBounds.height() / 2, paint);
            }
            if (unknownValue > 0) {
                final String text = String.format("%d", unknownValue);
                paint.getTextBounds(text, 0, text.length(), textBounds);
                canvas.drawText(text, (successWidth + failedWidth) + unknownWidth / 2 -
                        textBounds.width() / 2, contentHeight / 2 + textBounds.height() / 2, paint);
            }
        } else {
            paint.setStrokeWidth(0);
            paint.setColor(unknownColor);
            canvas.drawRect(0, 0, contentWidth, contentHeight, paint);
        }

//        // Draw the text.
//        canvas.drawText(mExampleString,
//                paddingLeft + (contentWidth - mTextWidth) / 2,
//                paddingTop + (contentHeight + mTextHeight) / 2,
//                mTextPaint);
//
//        // Draw the example drawable on top of the text.
//        if (mExampleDrawable != null) {
//            mExampleDrawable.setBounds(paddingLeft, paddingTop,
//                    paddingLeft + contentWidth, paddingTop + contentHeight);
//            mExampleDrawable.draw(canvas);
//        }
    }
}

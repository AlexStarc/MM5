/**
 *
 */
package com.sandrstar.android.gallery;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
/**
 * @author AlexStarc (sandrstar at hotmail dot com)
 *
 */
public class GalleryStaticTextExView extends View {
    private static final int RIGHT_ANGLE = 90;
    private static final int FULL_ANGLE = 360;
    private String mText = " ";
    private Integer mColor = Color.WHITE;
    private Typeface mTypeface = null;
    private Paint mTextPaint = null;
    private int mSize = 0;
    // rotation angle in degrees
    private Integer mRotation = 0;
    /* These dimensions are common and doesn't depend on cutting / view placing
     *  and to be used in onMeasure() in order to eliminate multiple calculations
     */
    private Integer mWidth = 0;
    private Integer mHeight = 0;
    private Integer mXOffset = 0;
    private Integer mYOffset = 0;

    /**
     * @see android.view.View#View(Context)
     *
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc.
     */
    public GalleryStaticTextExView(Context context) {
        super(context);

        initGalleryStaticTextExView();
    }

    /**
     * @see android.view.View#View(Context, AttributeSet)
     *
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc.
     * @param attrs The attributes of the XML tag that is inflating the view.
     */
    public GalleryStaticTextExView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initGalleryStaticTextExView();

        // here we might need to parse attributes from our custom view
        TypedArray aAttr = context.obtainStyledAttributes(attrs,
                                                          R.styleable.GalleryStaticTextExView);

        // now obtain every attribute separately
        // Text
        CharSequence textStr = aAttr.getString(R.styleable.GalleryStaticTextExView_text);

        if(null != textStr) {
            setText(textStr.toString());
        }

        // Color
        mTextPaint.setColor(aAttr.getColor(R.styleable.GalleryStaticTextExView_textColor, Color.WHITE));

        // Typeface
        switch(aAttr.getInt(R.styleable.GalleryStaticTextExView_textStyle, 0)) {
        // normal
        case 0:
            mTypeface = Typeface.create((String)null, Typeface.NORMAL);
            break;

        // bold
        case 1:
            mTypeface = Typeface.create((String)null, Typeface.BOLD);
            break;

        // italic
        case 2:
            mTypeface = Typeface.create((String)null, Typeface.ITALIC);
            break;

        default:
            break;
        }

        // Rotation
        setRotation(aAttr.getInt(R.styleable.GalleryStaticTextExView_textRotation, 0));

        int textSize = aAttr.getDimensionPixelOffset(R.styleable.GalleryStaticTextExView_textSize, -1);

        if(textSize > 0) {
            setTextSize(textSize);
        }
    }

    /**
     * Function to init fields of the view
     */
    private void initGalleryStaticTextExView() {
        mTextPaint = new Paint();
        setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        setColor(Color.WHITE);
    }

    /**
     * @param text the text to set
     */
    public void setText(String text) {
        this.mText = text;
        validateDimensions();
        requestLayout();
        invalidate();
    }

    /**
     * @return the text
     */
    public String getText() {
        return mText;
    }

    /**
     * @param mColor the mColor to set
     */
    public void setColor(int color) {
        this.mColor = color;
        mTextPaint.setColor(color);
        invalidate();
    }

    /**
     * @return the mColor
     */
    public int getColor() {
        return mColor;
    }

    /**
     * @param mTypeface the mTypeface to set
     */
    public void setTypeface(Typeface mTypeface) {
        this.mTypeface = mTypeface;
        mTextPaint.setTypeface(mTypeface);
        validateDimensions();
        requestLayout();
        invalidate();
    }

    /**
     * @return the mTypeface
     */
    public Typeface getTypeface() {
        return mTypeface;
    }

    /**
     * @param mRotation the mRotation to set. Currently only from -360 to 360 angles are supported.
     *
     */
    public void setRotation(Integer mRotation) {
        this.mRotation = mRotation % FULL_ANGLE;
        validateDimensions();
        requestLayout();
        invalidate();
    }

    /**
     * @return the mRotation
     */
    public Integer getRotation() {
        return mRotation;
    }

    /**
     * @param mSize the mSize to set
     */
    public void setTextSize(int mSize) {
        this.mSize = mSize;
        mTextPaint.setTextSize(mSize);
        validateDimensions();
        requestLayout();
        invalidate();
    }

    /**
     * @return the mSize
     */
    public int getTextSize() {
        return mSize;
    }

    /**
     * @see android.view.View#measure(int, int)
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWidth(widthMeasureSpec),
                             measureHeight(heightMeasureSpec));
    }

    /**
     * Determines the width of this view
     *
     * @param measureSpec A measureSpec packed into an int
     * @return The width of the view, honoring constraints from measureSpec
     */
    private int measureWidth(int measureSpec) {
        int textWidth = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if(specMode == MeasureSpec.EXACTLY) {
            // We were told how big to be
            textWidth = specSize;
        } else {
            textWidth = mWidth;
            textWidth += getPaddingLeft() + getPaddingRight();

            if (specMode == MeasureSpec.AT_MOST) {
                // Respect AT_MOST value if that was what is called for by measureSpec
                textWidth = Math.min(textWidth, specSize);
            }
        }

        return textWidth;
    }

    /**
     * Determines the height of this view
     *
     * @param measureSpec A measureSpec packed into an int
     * @return The height of the view, honoring constraints from measureSpec
     */
    private int measureHeight(int measureSpec) {
        int textHeight = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            // We were told how big to be
            textHeight = specSize;
        } else {
            textHeight = mHeight;
            textHeight += getPaddingTop() + getPaddingBottom();

            if (specMode == MeasureSpec.AT_MOST) {
                // Respect AT_MOST value if that was what is called for by measureSpec
                textHeight = Math.min(textHeight, specSize);
            }
        }
        return textHeight;
    }

    /**
     * @see android.view.View#onDraw(Canvas)
     */
    @Override
    protected void onDraw(Canvas canvas) {
        Rect clipRect = canvas.getClipBounds();

        canvas.translate(clipRect.left + getPaddingLeft() + mXOffset, clipRect.top + getPaddingTop() + mYOffset);
        canvas.rotate(-getRotation(), 0, 0);
        canvas.drawText(mText, 0, 0, mTextPaint);
        super.onDraw(canvas);
    }

    /**
     * Validates mWidth and mHeight variables or calculates them for first time
     */
    private void validateDimensions() {
        Integer textNormalWidth = (int) mTextPaint.measureText(mText);
        Integer textNormalHeight = (int)(-mTextPaint.ascent() + mTextPaint.descent());
        Double  widthXProjection  = 0.0;
        Double  widthYProjection  = 0.0;
        Double  heightXProjection = 0.0;
        Double  heightYProjection = 0.0;

        if(mRotation != 0) {
            widthXProjection  = textNormalWidth * Math.cos(Math.toRadians(Math.abs(mRotation)));
            heightXProjection = textNormalHeight * Math.cos(Math.toRadians(RIGHT_ANGLE + Math.abs(mRotation)));
            widthYProjection  = textNormalWidth * Math.sin(Math.toRadians(Math.abs(mRotation)));
            heightYProjection = textNormalHeight * Math.sin(Math.toRadians(RIGHT_ANGLE + Math.abs(mRotation)));

            // rotation angle presented, so prepare recalculation
            mWidth = (int)(Math.abs(widthXProjection) + Math.abs(heightXProjection));
            mHeight = (int)(Math.abs(widthYProjection) + Math.abs(heightYProjection));

            /* we've prepared width and height, but also we need to determine needed offsets
             * in order to eliminate clipping of text then rotating
             */
            mXOffset = (int)Math.abs(((widthXProjection >= 0.0) ? 0.0 : widthXProjection) +
                                     ((heightXProjection >= 0.0) ? 0.0 : heightXProjection));

            mYOffset = (int)(((widthYProjection >= 0.0) ? widthYProjection : 0.0) +
                             ((heightYProjection >= 0.0) ? heightYProjection : 0.0));
        } else {
            mWidth = textNormalWidth;
            mHeight = textNormalHeight;
            mXOffset = 0;
            mYOffset = mHeight;
        }
    }
}

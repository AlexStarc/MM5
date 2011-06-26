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
    public GalleryStaticTextExView(final Context context) {
        super(context);

        initGalleryStaticTextExView();
    }

    /**
     * @see android.view.View#View(Context, AttributeSet)
     *
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc.
     * @param attrs The attributes of the XML tag that is inflating the view.
     */
    public GalleryStaticTextExView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        initGalleryStaticTextExView();

        // here we might need to parse attributes from our custom view
        final TypedArray aAttr = context.obtainStyledAttributes(attrs,
                                                          R.styleable.GalleryStaticTextExView);

        // now obtain every attribute separately
        // Text
        final CharSequence textStr = aAttr.getString(R.styleable.GalleryStaticTextExView_text);

        if(null != textStr) {
            setText(textStr.toString());
        }

        // Color
        this.mTextPaint.setColor(aAttr.getColor(R.styleable.GalleryStaticTextExView_textColor, Color.WHITE));

        // Typeface
        switch(aAttr.getInt(R.styleable.GalleryStaticTextExView_textStyle, 0)) {
        // normal
        case 0:
            this.mTypeface = Typeface.create((String)null, Typeface.NORMAL);
            break;

        // bold
        case 1:
            this.mTypeface = Typeface.create((String)null, Typeface.BOLD);
            break;

        // italic
        case 2:
            this.mTypeface = Typeface.create((String)null, Typeface.ITALIC);
            break;

        default:
            break;
        }

        // Rotation
        setRotation(aAttr.getInt(R.styleable.GalleryStaticTextExView_textRotation, 0));

        final int textSize = aAttr.getDimensionPixelOffset(R.styleable.GalleryStaticTextExView_textSize, -1);

        if(textSize > 0) {
            setTextSize(textSize);
        }
    }

    /**
     * Function to init fields of the view
     */
    private void initGalleryStaticTextExView() {
        this.mTextPaint = new Paint();
        setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        setColor(Color.WHITE);
    }

    /**
     * @param text the text to set
     */
    public void setText(final String text) {
        this.mText = text;
        validateDimensions();
        requestLayout();
        invalidate();
    }

    /**
     * @return the text
     */
    public String getText() {
        return this.mText;
    }

    /**
     * @param mColor the mColor to set
     */
    public void setColor(final int color) {
        this.mColor = color;
        this.mTextPaint.setColor(color);
        invalidate();
    }

    /**
     * @return the mColor
     */
    public int getColor() {
        return this.mColor;
    }

    /**
     * @param mTypeface the mTypeface to set
     */
    public void setTypeface(final Typeface mTypeface) {
        this.mTypeface = mTypeface;
        this.mTextPaint.setTypeface(mTypeface);
        validateDimensions();
        requestLayout();
        invalidate();
    }

    /**
     * @return the mTypeface
     */
    public Typeface getTypeface() {
        return this.mTypeface;
    }

    /**
     * @param mRotation the mRotation to set. Currently only from -360 to 360 angles are supported.
     *
     */
    public void setRotation(final Integer mRotation) {
        this.mRotation = mRotation % FULL_ANGLE;
        validateDimensions();
        requestLayout();
        invalidate();
    }

    /**
     * @return the mRotation
     */
    public Integer getRotation() {
        return this.mRotation;
    }

    /**
     * @param mSize the mSize to set
     */
    public void setTextSize(final int mSize) {
        this.mSize = mSize;
        this.mTextPaint.setTextSize(mSize);
        validateDimensions();
        requestLayout();
        invalidate();
    }

    /**
     * @return the mSize
     */
    public int getTextSize() {
        return this.mSize;
    }

    /**
     * @see android.view.View#measure(int, int)
     */
    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        setMeasuredDimension(measureWidth(widthMeasureSpec),
                             measureHeight(heightMeasureSpec));
    }

    /**
     * Determines the width of this view
     *
     * @param measureSpec A measureSpec packed into an int
     * @return The width of the view, honoring constraints from measureSpec
     */
    private int measureWidth(final int measureSpec) {
        int textWidth = 0;
        final int specMode = MeasureSpec.getMode(measureSpec);
        final int specSize = MeasureSpec.getSize(measureSpec);

        if(specMode == MeasureSpec.EXACTLY) {
            // We were told how big to be
            textWidth = specSize;
        } else {
            textWidth = this.mWidth;
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
    private int measureHeight(final int measureSpec) {
        int textHeight = 0;
        final int specMode = MeasureSpec.getMode(measureSpec);
        final int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            // We were told how big to be
            textHeight = specSize;
        } else {
            textHeight = this.mHeight;
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
    protected void onDraw(final Canvas canvas) {
        final Rect clipRect = canvas.getClipBounds();

        canvas.translate(clipRect.left + getPaddingLeft() + this.mXOffset, clipRect.top + getPaddingTop() + this.mYOffset);
        canvas.rotate(-getRotation(), 0, 0);
        canvas.drawText(this.mText, 0, 0, this.mTextPaint);
        super.onDraw(canvas);
    }

    /**
     * Validates mWidth and mHeight variables or calculates them for first time
     */
    private void validateDimensions() {
        final Integer textNormalWidth = (int) this.mTextPaint.measureText(this.mText);
        final Integer textNormalHeight = (int)(-this.mTextPaint.ascent() + this.mTextPaint.descent());
        Double  widthXProjection  = 0.0;
        Double  widthYProjection  = 0.0;
        Double  heightXProjection = 0.0;
        Double  heightYProjection = 0.0;

        if(this.mRotation != 0) {
            widthXProjection  = textNormalWidth * Math.cos(Math.toRadians(Math.abs(this.mRotation)));
            heightXProjection = textNormalHeight * Math.cos(Math.toRadians(RIGHT_ANGLE + Math.abs(this.mRotation)));
            widthYProjection  = textNormalWidth * Math.sin(Math.toRadians(Math.abs(this.mRotation)));
            heightYProjection = textNormalHeight * Math.sin(Math.toRadians(RIGHT_ANGLE + Math.abs(this.mRotation)));

            // rotation angle presented, so prepare recalculation
            this.mWidth = (int)(Math.abs(widthXProjection) + Math.abs(heightXProjection));
            this.mHeight = (int)(Math.abs(widthYProjection) + Math.abs(heightYProjection));

            /* we've prepared width and height, but also we need to determine needed offsets
             * in order to eliminate clipping of text then rotating
             */
            this.mXOffset = (int)Math.abs(((widthXProjection >= 0.0) ? 0.0 : widthXProjection) +
                                     ((heightXProjection >= 0.0) ? 0.0 : heightXProjection));

            this.mYOffset = (int)(((widthYProjection >= 0.0) ? widthYProjection : 0.0) +
                             ((heightYProjection >= 0.0) ? heightYProjection : 0.0));
        } else {
            this.mWidth = textNormalWidth;
            this.mHeight = textNormalHeight;
            this.mXOffset = 0;
            this.mYOffset = this.mHeight;
        }
    }
}

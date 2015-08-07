package com.zj.example.custom.imageview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * getMeasuredWidth要在onMeasure之后才能取到值,可以在onMeasure,或者onSizeChanged中获取
 *
 * 自定义圆形ImageView
 * Created by zhengjiong on 15/8/6.
 */
public class CircleImageView extends ImageView{
    private static final boolean DEBUG = true;
    private static final String TAG = "zj";

    //默认边框宽度为0
    private int mBorderWidth = 0;
    private int mBorderColor;

    private Paint mBitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mOnTouchPaint = new Paint();

    private Bitmap mBitmap;
    private BitmapShader mBitmapShader;
    private Matrix mBitmapMatrix = new Matrix();

    public CircleImageView(Context context) {
        this(context, null);
        //debug("CircleImageView constructor 1");
    }

    public CircleImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        //debug("CircleImageView constructor 2");
    }

    public CircleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        debug("CircleImageView constructor 3");

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView);

        mBorderWidth = typedArray.getDimensionPixelSize(R.styleable.CircleImageView_border_width, 0);
        mBorderColor = typedArray.getColor(R.styleable.CircleImageView_border_color, Color.BLACK);
    }

    private void init() {
        setup();
    }

    private void setup() {
        super.setScaleType(ScaleType.CENTER_CROP);

        mOnTouchPaint.setStyle(Paint.Style.FILL);
        mOnTouchPaint.setColor(Color.TRANSPARENT);

        //设置边框画笔
        mBorderPaint.setColor(mBorderColor);
        mBorderPaint.setStrokeWidth(mBorderWidth);
        mBorderPaint.setStyle(Paint.Style.STROKE);


        //设置图片画笔
        mBitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        mBitmapPaint.setShader(mBitmapShader);

        //设置缩放和位移
        updateShaderMatrix();
    }

    private void updateShaderMatrix() {

        //bitmap的高和宽 取最小值来计算比例
        float scale = 1;
        float dx = 0;
        float dy = 0;

        debug("bitmap height=" + mBitmap.getHeight() + " ,width=" + mBitmap.getWidth());

        debug("getWidth=" + getWidth() + " ,mBitmap.getWidth=" + mBitmap.getWidth() + " ,scale = " + scale);

        if (mBitmap.getWidth() > mBitmap.getHeight()) {
            scale = getWidth() / (float) mBitmap.getHeight();
            dx = (getWidth() - mBitmap.getWidth() * scale) / 2;
        } else {
            scale = getWidth() / (float) mBitmap.getWidth();
            dy = (getHeight() - mBitmap.getHeight() * scale) / 2;
        }


        mBitmapMatrix.set(null);

        //设置图片缩小放大比例
        mBitmapMatrix.setScale(scale, scale);

        //设置图片位移
        mBitmapMatrix.postTranslate(dx, dy);

        //mBitmapMatrix.postTranslate(-50, 0);
        mBitmapShader.setLocalMatrix(mBitmapMatrix);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mBitmap == null) {
            debug("onDraw mBitmap == null");
            return;
        }
        debug("onDraw");

        //画圆形图片
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2, mBitmapPaint);

        //画边框
        canvas.drawCircle(getWidth() / 2, getHeight() /2 , (getWidth()- mBorderWidth) / 2, mBorderPaint);


        canvas.drawCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2, mOnTouchPaint);
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        mBitmap = drawable2Bitmap(drawable);
        debug("setImageDrawable");
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        debug("onSizeChanged w = " + w);

        init();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mOnTouchPaint.setColor(0x45000000);
                debug("action down");

                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mOnTouchPaint.setColor(Color.TRANSPARENT);
                debug("action up");
                break;
        }
        invalidate();
        return true;
    }

    public Bitmap drawable2Bitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        return null;
    }

    public static void debug(String msg) {
        if (DEBUG) {
            Log.i(TAG, msg);
        }
    }

}

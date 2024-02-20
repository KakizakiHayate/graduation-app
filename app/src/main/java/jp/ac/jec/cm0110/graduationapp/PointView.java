package jp.ac.jec.cm0110.graduationapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class PointView extends View implements SensorEventListener {
    private final int TEXTSIZE = 40; // 文字のフォントサイズ
    private float value = 0.0f; // 予想価値
    private float difficulty = 0.0f; // 発見難度
    private float probability = 0.0f; // 取得確率

    private Paint paint;

    private static final int MATRIX_SIZE = 16;
    float[] inR = new float[MATRIX_SIZE];
    float[] outR = new float[MATRIX_SIZE];

    float[] mOrientation = new float[3];
    float[] mMagnetic = new float[3];
    float[] mAccelerometer = new float[3];

    boolean isShowTotal;
    int totalScore;

    int percentage = 0;

    public PointView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public PointView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PointView(Context context) {
        super(context);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextSize(TEXTSIZE);
        paint.setColor(Color.argb(255, 255, 255, 255));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        value = radToDeg(mOrientation[0]);
        difficulty = radToDeg(mOrientation[1]);
        probability = radToDeg(mOrientation[2]);
        paint.setColor(Color.RED);
        canvas.drawText("予想価値:" + value, 0, TEXTSIZE * 1, paint);
        paint.setColor(Color.BLUE);
        canvas.drawText("発見難度:" + difficulty, 0, TEXTSIZE * 2, paint);
        paint.setColor(Color.GREEN);
        canvas.drawText("取得確率:" + probability, 0, TEXTSIZE * 3, paint);

        if (isShowTotal) {
            canvas.drawText("お宝度:" + ((percentage / 100) - 20) + "%",
                    canvas.getWidth() - (TEXTSIZE * 9),
                    canvas.getHeight() - (TEXTSIZE), paint);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    // 丸め計算用
    private static float radToDeg(float rad) {
        return (float) (rad * 180 / Math.PI);
    }

    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        synchronized (this) {
            switch (event.sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    mAccelerometer = event.values.clone();
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    mMagnetic = event.values.clone();
                    break;
            }
        }
        if (mAccelerometer != null && mMagnetic != null) {
            SensorManager.getRotationMatrix(inR, null, mAccelerometer, mMagnetic);

            SensorManager.remapCoordinateSystem(inR, SensorManager.AXIS_Z, SensorManager.AXIS_X, outR);
            SensorManager.getOrientation(outR, mOrientation);
            invalidate();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isShowTotal == false) {
                    isShowTotal = true;
                }
                int difficultyInt = (int) (Math.abs(difficulty) * 100000);
                int valueInt = (int) (Math.abs(value) * 100000);
                int probabilityInt = (int) (Math.abs(probability) * 100000);

                // 合計を計算
                totalScore = difficultyInt + valueInt + probabilityInt;

                // パーセンテージに変換
                int maxTotalScore = 300000; // 3つの値の合計の最大値 (100000 + 100000 + 100000)
                percentage = (int) ((totalScore / (float) maxTotalScore) * 100);

                if ( ((percentage / 100) - 20) >= 80 ) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            new AlertDialog.Builder(getContext()).setTitle("スコア警告").setMessage("お宝度が80を超えましたのでゲームクリア！").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    CameraActivity camera = (CameraActivity) getContext();
                                    Intent intent = new Intent();
                                    intent.putExtra("SEND", true);
                                    camera.setResult(Activity.RESULT_OK, intent);
                                    camera.finish();
                                }
                            }).show();
                        }
                    });
                }
                break;
        }
        return true;
    }
}

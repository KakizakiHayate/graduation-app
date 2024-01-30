package jp.ac.jec.cm0110.graduationapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.common.util.concurrent.ListenableFuture;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class CameraActivity extends AppCompatActivity {

    private PreviewView previewView;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;

    private PointView pointView;
    private SensorManager manager;
    private TextView textTimer;
    final SimpleDateFormat dataFormat = new SimpleDateFormat("ss", Locale.US);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        previewView = findViewById(R.id.previewView);
        pointView = findViewById(R.id.pointView);
        manager = (SensorManager)getSystemService(SENSOR_SERVICE);
        textTimer = findViewById(R.id.timer);
        textTimer.setText(dataFormat.format(0));

        startCamera();

        long countNumber = 60000;
        long interval = 1000;

        final CountDown countDown = new CountDown(countNumber, interval);
        countDown.start();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Sensor accele = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor magnet = manager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        manager.registerListener(pointView, accele, SensorManager.SENSOR_DELAY_GAME);
        manager.registerListener(pointView, magnet, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();

        manager.unregisterListener(pointView);
    }

    private void startCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(
                new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                            Preview preview = new Preview.Builder()
                                    .build();

                            // フロントカメラかバックカメラかを指定する
                            CameraSelector cameraSelector = new CameraSelector.Builder()
                                    .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                                    .build();

                            preview.setSurfaceProvider(previewView.getSurfaceProvider());

                            Camera camera = cameraProvider.bindToLifecycle(
                                    (LifecycleOwner) CameraActivity.this, cameraSelector, preview);

                        } catch (ExecutionException | InterruptedException e) {
                            // No errors need to be handled for this Future.
                            // This should never be reached.
                        }
                    }
                }, ContextCompat.getMainExecutor(this));
    }

    public class CountDown extends CountDownTimer {

        CountDown(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            Intent intent = new Intent();
            intent.putExtra("SEND", false);
            setResult(RESULT_OK, intent);
            finish();
        }

        @Override
        public void onTick(long l) {
            textTimer.setText(dataFormat.format(l));
        }
    }
}
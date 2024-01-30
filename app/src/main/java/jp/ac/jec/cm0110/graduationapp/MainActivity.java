package jp.ac.jec.cm0110.graduationapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_CODE = 1;
    private TextView gameReuslt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.ibtnStart).setOnClickListener(this);
        gameReuslt = findViewById(R.id.game);
    }

    @Override
    public void onClick(View view) {
        if (view == null) { return; }
        int id = view.getId();
        if (id == R.id.ibtnStart) {
            Intent intent = new Intent(MainActivity.this, CameraActivity.class);
            startActivityForResult(intent, REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (REQUEST_CODE):
                boolean result = data.getBooleanExtra("SEND", false);

                if (result) {
                    gameReuslt.setText("ゲームクリア");
                } else {
                    gameReuslt.setText("ゲーム失敗");
                }

                break;
            default:
                break;
        }
    }
}
package com.example.flashlight;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity implements View.OnClickListener {

    private ImageView powerImageView;

    private CameraManager camManager;
    private Camera cam;

    private boolean lightIsOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        powerImageView = (ImageView) findViewById(R.id.iv_poweronoff);
        powerImageView.setOnClickListener(this);
    }

    private void flashLightOn() {
        // >= android 6.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (camManager == null) {
                camManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
            }
            try {
                // Usually back camera is at 0 position.
                String cameraId = camManager.getCameraIdList()[0];
                camManager.setTorchMode(cameraId, true);
            } catch (Exception e) {
                showError(e);
            }
            return;
        }

        try {
            if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
                cam = Camera.open();
                Camera.Parameters p = cam.getParameters();
                p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                cam.setParameters(p);
                cam.startPreview();
            }
        } catch (Exception e) {
            showError(e);
        }
    }

    private void flashLightOff() {
        // >= android 6.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                String cameraId = camManager.getCameraIdList()[0];
                camManager.setTorchMode(cameraId, false);
            } catch (Exception e) {
                showError(e);
            }
            return;
        }

        try {
            if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
                cam.stopPreview();
                cam.release();
                cam = null;
            }
        } catch (Exception e) {
            showError(e);
        }
    }

    private void showError(Exception e) {
        String message = "Error: " + e.getMessage();
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.iv_poweronoff) {
            if (lightIsOn) {
                flashLightOff();
                powerImageView.setImageResource(R.drawable.poweroff);
            } else {
                flashLightOn();
                powerImageView.setImageResource(R.drawable.poweron);
            }
            lightIsOn = !lightIsOn;
        }
    }
}
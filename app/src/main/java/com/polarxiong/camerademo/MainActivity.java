package com.polarxiong.camerademo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * Created by zhantong on 16/4/28.
 */
public class MainActivity extends Activity implements View.OnClickListener {

    private CameraPreview mPreview;
    private ImageView mediaPreview;
    private Button buttonCapturePhoto;
    private Button buttonCaptureVideo;
    private Button buttonSettings;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initCamera();
        initView();

    }

    private void initCamera() {
        mPreview = new CameraPreview(this);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);

        SettingsFragment.passCamera(mPreview.getCameraInstance());
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        SettingsFragment.setDefault(PreferenceManager.getDefaultSharedPreferences(this));
        SettingsFragment.init(PreferenceManager.getDefaultSharedPreferences(this));
    }

    private void initView() {
        buttonSettings = (Button) findViewById(R.id.button_settings);
        buttonSettings.setOnClickListener(this);

        buttonCapturePhoto = (Button) findViewById(R.id.button_capture_photo);
        buttonCapturePhoto.setOnClickListener(this);

        buttonCaptureVideo = (Button) findViewById(R.id.button_capture_video);
        buttonCaptureVideo.setOnClickListener(this);

        mediaPreview = (ImageView) findViewById(R.id.media_preview);
        mediaPreview.setOnClickListener(this);
    }


    public void onPause() {
        super.onPause();
        mPreview = null;
    }

    public void onResume() {
        super.onResume();
        if (mPreview == null) {
            initCamera();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_settings:
                getFragmentManager().beginTransaction().replace(R.id.camera_preview, new SettingsFragment()).addToBackStack(null).commit();
                break;
            case R.id.button_capture_photo:
                mPreview.takePicture(mediaPreview);
                break;
            case R.id.button_capture_video:
                if (mPreview.isRecording()) {
                    mPreview.stopRecording(mediaPreview);
                    buttonCaptureVideo.setText("录像");
                } else {
                    if (mPreview.startRecording()) {
                        buttonCaptureVideo.setText("停止");
                    }
                }
                break;
            case R.id.media_preview:
                Intent intent = new Intent(MainActivity.this, ShowPhotoVideo.class);
                intent.setDataAndType(mPreview.getOutputMediaFileUri(), mPreview.getOutputMediaFileType());
                startActivityForResult(intent, 0);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        if (mPreview != null) {
            mPreview.releaseData();
            mPreview = null;
        }
        super.onDestroy();
    }
}

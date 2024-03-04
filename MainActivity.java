/**
 * (basic description of the program or class)
 * CSE 535
 * SmartHome Gesture Control Application
 * Completion time: (estimation of hours spent on this program)
 * 14 days
 * @author (your name)
 * Kai Chi
 * with code snippets from the internet
 * @version (a version number or a date)
 * v1.0 (phase 1)
 */

package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    static String[] fileNames = new String[] {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "LightOn", "LightOff", "FanOn", "FanOff", "IncreaseFanSpeed", "DecreaseFanSpeed", "SetThermo"};
    private GestureDetector.OnGestureListener ogl;
    private GestureDetector gd;
    private int swipe_min_distance;
    private int swipe_min_velocity;
    private int cv;
    View.OnClickListener ocl;
    SurfaceHolder.Callback shc;
    SurfaceView mSurfaceView;
    SurfaceHolder mHolder;
    EditText editText1, editText2;
    Spinner mySpinner;
    AdapterView.OnItemSelectedListener oisl;
    Button updateButton, playButton1, recordButton, playButton2, uploadButton;
    MediaRecorder mMediaRecorder;
    CameraDevice mCameraDevice;
    Integer mSensorOrientation;
    CaptureRequest.Builder mPreviewBuilder;
    CameraCaptureSession mPreviewSession;
    CameraDevice.StateCallback mStateCallback;
    TextView countDownView;
    int timeLimit, timeInterval;
    CountDownTimer countDownTimer;
    String IP, LastName;
    int gestureID;
    int[] practiceNumber = new int[17];
    String settingsPath, videoPath;
    VideoView videoView1, videoView2;
    String uploadResult;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // We shall take the video in portrait orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        swipe_min_distance = 100;
        swipe_min_velocity = 100;
        cv = 1;
        ogl = new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(@NonNull MotionEvent motionEvent) {
                return false;
            }

            @Override
            public void onShowPress(@NonNull MotionEvent motionEvent) {

            }

            @Override
            public boolean onSingleTapUp(@NonNull MotionEvent motionEvent) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent motionEvent, @NonNull MotionEvent motionEvent1, float v, float v1) {
                return false;
            }

            @Override
            public void onLongPress(@NonNull MotionEvent motionEvent) {

            }

            @Override
            public boolean onFling(MotionEvent motionEvent, @NonNull MotionEvent motionEvent1, float v, float v1) {
                float x1, x2, distance, velocity;

                x1 = motionEvent.getX();
                x2 = motionEvent1.getX();
                distance = Math.abs(x2 - x1);
                velocity = Math.abs(v);
                if (distance>swipe_min_distance && velocity>swipe_min_velocity) {
                    if (x1>x2 && cv<4) { // swipe left
                        ++cv;
                    }
                    else if (x1<x2 && cv>1) { //swipe right
                        --cv;
                    }
                    switch (cv) {
                        case 1:
                            act1();
                            break;
                        case 2:
                            act2();
                            break;
                        case 3:
                            act3();
                            break;
                        case 4:
                            act4();
                            break;
                    }
                }
                return false;
            }
        };
        gd = new GestureDetector(this, ogl);

        ocl = new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                if (v.getId()==R.id.updateButton) {
                    IP = editText1.getText().subSequence(0, editText1.getText().length()).toString();
                    LastName = editText2.getText().subSequence(0, editText2.getText().length()).toString();
                    Toast.makeText(getApplicationContext(), "Updated!", Toast.LENGTH_SHORT).show();
                }
                else if (v.getId()==R.id.playButton1) {
                    playVideo1();
                }
                else if (v.getId()==R.id.recordButton) {
                    if (recordButton.getText().equals("Practice")) {
                        recordButton.setText("Stop");
                        closePreviewSession();
                        initRecorder();
                        startRecordingVideo();
                        countDownTimer.start();
                    }
                    else if (recordButton.getText().equals("Stop")) {
                        recordButton.setText("Practice");
                        mMediaRecorder.stop();
                        mMediaRecorder.reset();
                        countDownTimer.cancel();
                        countDownView.setText(String.valueOf(timeLimit/1000));
                        openCamera();
                    }
                }
                else if (v.getId()==R.id.playButton2) {
                    playVideo2();
                }
                else if (v.getId()==R.id.uploadButton) {
                    int result;
                    Thread myThread = new Thread() {
                        public void run() {
                            uploadVideo();
                        }
                    };
                    myThread.start();
                    try {
                        myThread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(getApplicationContext(), uploadResult, Toast.LENGTH_LONG).show();
                    if (uploadResult.equals("Success!")) {
                        practiceNumber[gestureID]++;
                    }
                }
            }
        };

        oisl = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                gestureID = mySpinner.getSelectedItemPosition();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };

        shc = new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {

            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
                closeCamera();
            }
        };

        mStateCallback = new CameraDevice.StateCallback() {

            @Override
            public void onOpened(@NonNull CameraDevice cameraDevice) {
                mCameraDevice = cameraDevice;
                startPreview();
            }

            @Override
            public void onDisconnected(@NonNull CameraDevice cameraDevice) {
                cameraDevice.close();
                mCameraDevice = null;
            }

            @Override
            public void onError(@NonNull CameraDevice cameraDevice, int error) {
                cameraDevice.close();
                mCameraDevice = null;
            }
        };

        timeLimit = 5000;
        timeInterval = 1000;
        countDownTimer = new CountDownTimer(timeLimit, timeInterval) {
            @Override
            public void onTick(long millisUntilFinished) {
                countDownView.setText(String.valueOf(millisUntilFinished/1000));
            }

            @Override
            public void onFinish() {
                if (recordButton.getText().equals("Stop")) {
                    recordButton.performClick();
                }
                //countDownView.setText(String.valueOf(timeLimit/1000));
            }
        };

        cleanUp();
        readSettings();
        act1();
    }

    @Override
    public boolean onTouchEvent(MotionEvent me) {
        return gd.onTouchEvent(me);
    }
    @Override
    public void onStop() {
        super.onStop();
        writeSettings();
    }
    private void act1() {
        setContentView(R.layout.screen1);
        editText1 = (EditText) findViewById(R.id.editText1);
        editText1.setText(IP);
        editText2 = (EditText) findViewById(R.id.editText2);
        editText2.setText(LastName);
        updateButton = (Button) findViewById(R.id.updateButton);
        updateButton.setOnClickListener(ocl);
    }
    private void act2() {
        setContentView(R.layout.screen2);
        videoView1 = (VideoView) findViewById(R.id.videoView1);
        mySpinner = (Spinner) findViewById(R.id.chooseVideoSpinner);
        mySpinner.setOnItemSelectedListener(oisl);
        mySpinner.setSelection(gestureID);
        playButton1 = (Button) findViewById(R.id.playButton1);
        playButton1.setOnClickListener(ocl);
    }
    private void act3() {
        setContentView(R.layout.screen3);
        mSurfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        mHolder = mSurfaceView.getHolder();
        mHolder.addCallback(shc);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        countDownView = (TextView) findViewById(R.id.countDownView);
        countDownView.setText(String.valueOf(timeLimit/1000));
        recordButton = (Button) findViewById(R.id.recordButton);
        recordButton.setOnClickListener(ocl);
        openCamera();
    }

    private void act4() {
        setContentView(R.layout.screen4);
        videoView2 = (VideoView) findViewById(R.id.videoView2);
        playButton2 = (Button) findViewById(R.id.playButton2);
        playButton2.setOnClickListener(ocl);
        uploadButton = (Button) findViewById(R.id.uploadButton);
        uploadButton.setOnClickListener(ocl);
    }
    private void readSettings() {
        settingsPath = getFilesDir().getAbsolutePath()+"/settings.txt";

        File f = new File(settingsPath);
        byte[] buffer = null;

        if (f.exists()) {
            try {
                FileInputStream fis = new FileInputStream(f);
                buffer = new byte[fis.available()];
                fis.read(buffer);
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            try {
                InputStream is = getResources().openRawResource(R.raw.settings);
                buffer = new byte[is.available()];
                is.read(buffer);
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /*
        Format:
        10.0.1.2
        Chi
        0
        0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
         */

        String s;
        String[] sa, sa2;

        s = new String(buffer);
        sa = s.split("\r\n");
        sa2 = sa[3].split(",");
        IP = sa[0];
        LastName = sa[1];
        gestureID = Integer.parseInt(sa[2]);
        for (int i=0; i<17; i++) {
            practiceNumber[i] = Integer.parseInt(sa2[i]);
        }
        videoPath = getFilesDir().getAbsolutePath()+"/"+getResources().getStringArray(R.array.spinnerItems)[gestureID]+"_PRACTICE_"+practiceNumber[gestureID]+"_"+LastName+".mp4";
    }
    private void writeSettings() {
        File f = new File(settingsPath);
        String s;

        s = IP+"\r\n"+LastName+"\r\n"+gestureID+"\r\n";
        for (int i=0; i<16; i++) {
            s += practiceNumber[i];
            s += ",";
        }
        s += practiceNumber[16];

        try {
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(s.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void playVideo1() {
        try {
            InputStream is = getAssets().open("H-"+ fileNames[gestureID]+".mp4");
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();
            FileOutputStream fos = openFileOutput("temp.mp4", MODE_PRIVATE);
            fos.write(buffer);
            fos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView1);
        videoView1.setMediaController(mediaController);
        videoView1.setVideoPath(getFilesDir()+"/temp.mp4");
        videoView1.start();
    }
    @SuppressWarnings("MissingPermission")
    private void openCamera() {
        CameraManager manager = (CameraManager) this.getSystemService(Context.CAMERA_SERVICE);
        try {
            String cameraId = manager.getCameraIdList()[0];

            manager.openCamera(cameraId, mStateCallback, null);
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
            mSensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
        } catch (CameraAccessException e) {
            Toast.makeText(getApplicationContext(), "Cannot access the camera.", Toast.LENGTH_SHORT).show();
        }
    }
    private void closeCamera() {
        closePreviewSession();
        if (mCameraDevice != null) {
            mCameraDevice.close();
            mCameraDevice = null;
        }
        if (mMediaRecorder != null) {
            mMediaRecorder.release();
            mMediaRecorder = null;
        }
    }
    private void startPreview() {
        try {
            mPreviewBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
        } catch (CameraAccessException e) {
            throw new RuntimeException(e);
        }

        mPreviewBuilder.addTarget(mHolder.getSurface());

        try {
            mCameraDevice.createCaptureSession(Collections.singletonList(mHolder.getSurface()),
                    new CameraCaptureSession.StateCallback() {

                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession session) {
                            mPreviewSession = session;
                            updatePreview();
                        }

                        @Override
                        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                            Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
                        }
                    }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
    private void updatePreview() {
        if (mCameraDevice != null) {
            try {
                mPreviewBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
                mPreviewSession.setRepeatingRequest(mPreviewBuilder.build(), null, null);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
    }
    private void closePreviewSession() {
        if (mPreviewSession != null) {
            mPreviewSession.close();
            mPreviewSession = null;
        }
    }

    /* Init the MediaRecorder. The order the methods are called in is vital to
     * its correct functioning.
     */
    @SuppressLint("MissingPermission")
    private void initRecorder() {
        videoPath = getFilesDir().getAbsolutePath()+"/"+getResources().getStringArray(R.array.spinnerItems)[gestureID]+"_PRACTICE_"+practiceNumber[gestureID]+"_"+LastName+".mp4";

        if (mMediaRecorder == null) {
            mMediaRecorder = new MediaRecorder();
        }

        mMediaRecorder.setOrientationHint(mSensorOrientation);
        mMediaRecorder.setPreviewDisplay(mSurfaceView.getHolder().getSurface());
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);


        mMediaRecorder.setOutputFile(videoPath);

        // No limit. Don't forget to check the space on disk.
        mMediaRecorder.setMaxDuration(-1);
        mMediaRecorder.setVideoFrameRate(15);

        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);

        try {
            mMediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (IllegalStateException e) { // This is thrown if the previous calls are not made in the proper order
            e.printStackTrace();
        }
    }
    private void startRecordingVideo() {
        try {
            mPreviewBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
            List<Surface> surfaces = new ArrayList<>();

            // Set up Surface for the camera preview
            surfaces.add(mSurfaceView.getHolder().getSurface());
            mPreviewBuilder.addTarget(mSurfaceView.getHolder().getSurface());

            // Set up Surface for the MediaRecorder
            Surface recorderSurface = mMediaRecorder.getSurface();
            surfaces.add(recorderSurface);
            mPreviewBuilder.addTarget(recorderSurface);

            // Start a capture session
            // Once the session starts, we can update the UI and start recording
            mCameraDevice.createCaptureSession(surfaces, new CameraCaptureSession.StateCallback() {

                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    mPreviewSession = cameraCaptureSession;
                    updatePreview();
                    mMediaRecorder.start();
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                    Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
    private void playVideo2() {
        File f = new File(videoPath);
        if (f.exists()) {
            MediaController mediaController = new MediaController(this);
            mediaController.setAnchorView(videoView2);
            videoView2.setMediaController(mediaController);
            videoView2.setVideoPath(videoPath);
            videoView2.start();
        }
        else {
            Toast.makeText(getApplicationContext(), "File not exist", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadVideo() {
        HttpURLConnection conn = null;
        OutputStream os;
        //DataOutputStream dos = null;

        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary =  "*****";
        byte[] buffer;
        String urlString = "http://"+IP+"/upload.php";

        File f = new File(videoPath);
        if (f.exists()) {
            try
            {
                FileInputStream fileInputStream = new FileInputStream(f);
                buffer = new byte[fileInputStream.available()];
                fileInputStream.read(buffer);
                fileInputStream.close();

                URL url = new URL(urlString);
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true);
                // Allow Outputs
                conn.setDoOutput(true);
                // Don't use a cached copy.
                conn.setUseCaches(false);
                // Use a post method.
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);

                os = conn.getOutputStream();
                os.write((twoHyphens + boundary + lineEnd).getBytes());
                os.write(("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + f.getName() +"\"" + lineEnd).getBytes());
                os.write(lineEnd.getBytes());
                os.write(buffer);
                os.write(lineEnd.getBytes());
                os.write((twoHyphens + boundary + twoHyphens + lineEnd).getBytes());
                os.flush();
                os.close();

                //------------------ read the SERVER RESPONSE
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String temp;

                uploadResult = "";
                while ((temp = br.readLine())!=null) {
                    uploadResult += temp;
                }
                br.close();
                conn.disconnect();
            }
            catch (MalformedURLException ex)
            {
                uploadResult = "Malformed URL";
                ex.printStackTrace();
            }
            catch (IOException ioe)
            {
                uploadResult = "Connection error";
                ioe.printStackTrace();
            }
        }
        else {
            uploadResult = "File not exist";
        }
    }
    private void scan() { // helper function for debugging
        File f = new File(getFilesDir().getAbsolutePath());

        if (f.list()!=null) {
            for (String file : f.list()) {
                System.out.println(file);
            }
        }
    }
    private void cleanUp() {
        File f = new File(getFilesDir().getAbsolutePath());
        File temp;

        if (f.list()!=null) {
            for (String file : f.list()) {
                if (file.endsWith(".mp4")) {
                    temp = new File(getFilesDir().getAbsolutePath() + "/" + file);
                    temp.delete();
                }
            }
        }
    }
}
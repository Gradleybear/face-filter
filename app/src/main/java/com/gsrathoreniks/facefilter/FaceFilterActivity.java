package com.gsrathoreniks.facefilter;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.images.Size;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.gsrathoreniks.facefilter.camera.CameraSourcePreview;
import com.gsrathoreniks.facefilter.camera.GraphicOverlay;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Timestamp;

import static android.graphics.Bitmap.Config.ARGB_8888;
import static android.view.View.GONE;

//import android.graphics.Paint;

public class FaceFilterActivity extends AppCompatActivity {


    private final Thread observer = new Thread("observer") {

        {
            setDaemon(true);
        }

        public void run() {

            while( !isInterrupted() ) {
                /*
                TextGraphic mTextGraphic = new TextGraphic(mGraphicOverlay);
                mGraphicOverlay.add(mTextGraphic);
                //mTextGraphic.updateText(2);*/
            }

        };
    };
    BitmapFactory.Options opt = new BitmapFactory.Options();

    private static final String TAG = "FaceTracker";
    private File imageFile;
    private CameraSource mCameraSource = null;
    public static int typeFace;
    private String mTempPhotoPath;

  //  private int typeFlash = 0;
  //  private boolean flashmode = false;
 //  private Camera mCamera;
    private static final int MASK[] = {
        //   R.id.no_filter,
          // R.id.hair,
          //  R.id.op,
          // R.id.snap,
          //  R.id.glasses2,
          //  R.id.glasses3,
          //  R.id.glasses4,
          // R.id.glasses5,
            R.id.morgan2,
            R.id.fill,
            R.id.moustache,
            R.id.cap
    };

    private CameraSourcePreview mPreview;
    private GraphicOverlay mGraphicOverlay;
    private static final int RC_HANDLE_GMS = 9001;
    // permission request codes need to be < 256
    private static final int RC_HANDLE_CAMERA_PERM = 2;

    //==============================================================================================
    // Activity Methods
    //==============================================================================================

    /**
     * Initializes the UI and initiates the creation of a face detector.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_face_filter);
        mPreview =(CameraSourcePreview) findViewById(R.id.preview);
        mGraphicOverlay =(GraphicOverlay) findViewById(R.id.faceOverlay);
        /*
        mTextGraphic = new TextGraphic(mGraphicOverlay);
        mGraphicOverlay.add(mTextGraphic);
        mContext = this;
        */
        ImageButton face = findViewById(R.id.face);
        face.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (findViewById(R.id.scrollView).getVisibility() == GONE) {
                    findViewById(R.id.scrollView).setVisibility(View.VISIBLE);
                    ((ImageButton) findViewById(R.id.face)).setImageResource(R.drawable.face_select);
                } else {
                    findViewById(R.id.scrollView).setVisibility(GONE);
                    ((ImageButton) findViewById(R.id.face)).setImageResource(R.drawable.face);
                }
            }
        });

       /* ImageButton no_filter = (ImageButton) findViewById(R.id.no_filter);
        no_filter.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                typeFace = 0;
                findViewById(MASK[typeFace]).setBackgroundResource(R.drawable.round_background);
                findViewById(MASK[typeFace]).setBackgroundResource(R.drawable.round_background_select);
            }
        });*/




        ImageButton morgan2 = (ImageButton) findViewById(R.id.morgan2);
        morgan2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                findViewById(MASK[typeFace]).setBackgroundResource(R.drawable.round_background);
                typeFace = 0;
                findViewById(MASK[typeFace]).setBackgroundResource(R.drawable.round_background_select);
              Intent intent =new Intent(FaceFilterActivity.this, Morgan2.class);
                startActivity(intent);

            }
        });

        ImageButton fill = (ImageButton) findViewById(R.id.fill);
        fill.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                findViewById(MASK[typeFace]).setBackgroundResource(R.drawable.round_background);
                typeFace = 1;
                findViewById(MASK[typeFace]).setBackgroundResource(R.drawable.round_background_select);
            }
        });

        ImageButton moustache = (ImageButton) findViewById(R.id.moustache);
        moustache.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                findViewById(MASK[typeFace]).setBackgroundResource(R.drawable.round_background);
                typeFace = 2;
                findViewById(MASK[typeFace]).setBackgroundResource(R.drawable.round_background_select);
            }
        });

        ImageButton cap = (ImageButton) findViewById(R.id.cap);
        cap.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                findViewById(MASK[typeFace]).setBackgroundResource(R.drawable.round_background);
                typeFace =3;
                findViewById(MASK[typeFace]).setBackgroundResource(R.drawable.round_background_select);
            }
        });



        /*{final ImageButton flash;
        flash = findViewById(R.id.flash);
        flash.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int blue;
                if (!flashmode) {
                    flashmode = true;
                    blue = 0;
                    FLASH_MODE_ON = "on";
                } else {
                    flashmode = false;
                    blue = 255;
                    FLASH_MODE_ON = "off";
                }
                flash.setColorFilter(Color.argb(255, 255, 255, blue));
            }
        });
         } */



        ImageButton camera = (ImageButton) findViewById(R.id.camera);
        camera.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                takeImage();
            }
        });

        // Check for the camera permission before accessing the camera.  If the
        // permission is not granted yet, request permission.
        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int ep = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (rc == PackageManager.PERMISSION_GRANTED && ep == PackageManager.PERMISSION_GRANTED  ) {
            createCameraSource();
        } else {
            requestCameraPermission();
        }
    }

    private void takeImage() {
        try{
            /*
           android.hardware.Camera.open(android.hardware.Camera.CameraInfo.CAMERA_FACING_BACK)
           releaseCameraSource();
           releaseCamera();
           openCamera(android.hardware.Camera.CameraInfo.CAMERA_FACING_BACK);
           setUpCamera(camera);
           Thread.sleep(1000);
           */
            mCameraSource.takePicture(null, new CameraSource.PictureCallback() {

                private File imageFile;
                 @Override
               public void onPictureTaken(byte[] bytes) {
                    try {

                        // convert byte array into bitmap
                        Bitmap loadedImage = null;
                        Bitmap rotatedBitmap = null;
                        // opt.inMutable = true;
                        Bitmap opRotated =null;
                        Bitmap op= screenShot(mGraphicOverlay);
                   loadedImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.length,opt);
                        // rotate Image
                        Matrix rotateMatrix = new Matrix();
                      rotateMatrix.postRotate(getWindowManager().getDefaultDisplay().getRotation());
                        rotatedBitmap = Bitmap.createBitmap(loadedImage, 0, 0,
                                loadedImage.getWidth(), loadedImage.getHeight(),
                                rotateMatrix, false);
                        String state = Environment.getExternalStorageState();
                        File folder = null;
                        if (state.contains(Environment.MEDIA_MOUNTED)) {
                            folder = new File(Environment
                                    .getExternalStorageDirectory()+"/faceFilter");
                        } else {
                            folder = new File(Environment
                                    .getExternalStorageDirectory() + "/faceFilter");
                        }
                        boolean success = true;
                        if (!folder.exists()) {
                            success = folder.mkdirs();
                        }
                        if (success) {
                            java.util.Date date = new java.util.Date();
                            imageFile = new File(folder.getAbsolutePath()
                                    + File.separator
                                    + "Image.jpg");
                            imageFile.createNewFile();
/*
                            mTempPhotoPath= imageFile.getAbsolutePath();
                            Toast.makeText(getBaseContext(), "Image saved successfully" +mTempPhotoPath ,
                                    Toast.LENGTH_LONG).show();*/
                        }
                        else {
                            Toast.makeText(getBaseContext(), "Image Not saved",
                                    Toast.LENGTH_SHORT).show();
                                   return ;
                        }
                        //save image into gallery
                        rotatedBitmap = resize(rotatedBitmap, 480, 640);
                       // rotatedBitmap = GetClickedImage.getFace(mContext,rotatedBitmap);
                        op = resize(op, 480, 640);
                        opRotated = Bitmap.createBitmap(op, 0, 0,
                                rotatedBitmap.getWidth(), rotatedBitmap.getHeight(),
                                rotateMatrix, false);
                        loadedImage= overlay(rotatedBitmap,op);
                        rotateMatrix.reset();
                        ByteArrayOutputStream ostream = new ByteArrayOutputStream();
                        loadedImage.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
                        FileOutputStream fout = new FileOutputStream(imageFile);
                        fout.write(ostream.toByteArray());
                        fout.close();

                        Intent share = new Intent(Intent.ACTION_SEND);
                        share.setType("image/jpeg");
                        ContentValues values = new ContentValues();
                        values.put(MediaStore.Images.Media.DATE_TAKEN,
                                System.currentTimeMillis());
                        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

                        Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                values);
                        Log.d(TAG,"URI="+uri);


                        OutputStream outstream;
                        try {
                            outstream = getContentResolver().openOutputStream(uri);
                            rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outstream);
                            outstream.close();
                        } catch (Exception e) {
                            System.err.println(e.toString());
                        }

                        share.putExtra(Intent.EXTRA_STREAM, uri);
                        startActivity(Intent.createChooser(share, "Share Image"));
                       /* values.put(MediaStore.MediaColumns.DATA,
                                imageFile.getAbsolutePath());
                        Log.d(TAG,"imagefile="+imageFile);
                        setResult(Activity.RESULT_OK);*/ //add this
                       /* finish();
                        onPause();*/
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        }catch (Exception ex){
                        ex.printStackTrace();
        }

    }
    /*public  Bitmap setBground(Bitmap bmp1, Bitmap bmp2) {
        setContentView(R.layout.morgan2_layout);
        ImageView imgview = (ImageView) findViewById(R.id.imageView);
        imgview.setBackground(getDrawable((R.drawable.morgan2)));
        Drawable myDrawable = imgview.getDrawable();
        bmp1= ((BitmapDrawable) myDrawable).getBitmap();
        Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(bmp1, new Matrix(), null);
        canvas.drawBitmap(bmp2, 0, 0, null);
        return bmOverlay;
    }*/

    public static Bitmap overlay(Bitmap bmp1, Bitmap bmp2) {
        Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(bmp1, new Matrix(), null);
        canvas.drawBitmap(bmp2, 0, 0, null);
        return bmOverlay;
    }
       private Bitmap resize(Bitmap image, int maxWidth, int maxHeight) {
        if (maxHeight > 0 && maxWidth > 0) {
            int width = image.getWidth();
            int height = image.getHeight();
            float ratioBitmap = (float) width / (float) height;
            float ratioMax = (float) maxWidth / (float) maxHeight;
            int finalWidth = maxWidth;
            int finalHeight = maxHeight;
            if (ratioMax > 1) {
                finalWidth = (int) ((float) maxHeight * ratioBitmap);
            } else {
                finalHeight = (int) ((float) maxWidth / ratioBitmap);
            }
            image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
            return image;
        } else {
            return image;
        }
    }
    private static void galarrayAddPic(Context context,String ImagePath)
    {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(ImagePath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }

    /**
     * Handles the requesting of the camera permission.  This includes
     * showing a "Snackbar" message of why the permission is needed then
     * sending the request.
     */
    final String[] permissions = new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");



        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        final Activity thisActivity = this;

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions,
                        RC_HANDLE_CAMERA_PERM);
            }
        };

        Snackbar.make(mGraphicOverlay, R.string.permission_camera_rationale,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok, listener)
                .show();
    }

    /**
     * Creates and starts the camera.  Note that this uses a higher resolution in comparison
     * to other detection examples to enable the barcode detector to detect small barcodes
     * at long distances.
     */

    private void createCameraSource() {
        Context context = getApplicationContext();
        FaceDetector detector = new FaceDetector.Builder(context)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .setMode(FaceDetector.ACCURATE_MODE)
                .build();

        detector.setProcessor(
                new MultiProcessor.Builder<>(new GraphicFaceTrackerFactory())
                        .build());

        new MultiProcessor.Builder<>(new GraphicTextTrackerFactory()).build();

        if (!detector.isOperational()) {
            // Note: The first time that an app using face API is installed on a device, GMS will
            // download a native library to the device in order to do detection.  Usually this
            // completes before the app is run for the first time.  But if that download has not yet
            // completed, then the above call will not detect any faces.
            //
            // isOperational() can be used to check if the required native library is currently
            // available.  The detector will automatically become operational once the library
            // download completes on device.
            Log.w(TAG, "Face detector dependencies are not yet available.");
        }


       /* DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        Log.d(TAG,"Metrics Preview width and height="+width+" "+height);
        mCameraSource = new CameraSource.Builder(context, detector)
                .setRequestedPreviewSize(width, height)
                .setFacing(CameraSource.CAMERA_FACING_FRONT)
                .setRequestedFps(30.0f)
                .build();*/


    Size displaySize = getScreenAspectRatio();
        mCameraSource = new CameraSource.Builder(context, detector)
                .setRequestedPreviewSize(displaySize.getHeight(),displaySize.getWidth())
                .setAutoFocusEnabled(true)
                .setFacing(CameraSource.CAMERA_FACING_FRONT)
                .setRequestedFps(15.0f)
                .build();


        /*
        TextGraphic mTextGraphic = new TextGraphic(mGraphicOverlay);
        mGraphicOverlay.add(mTextGraphic);
        mTextGraphic.updateText(2);*/


    }

    /**
     * Restarts the camera.
     */
    @Override
    protected void onResume() {
        super.onResume();

        startCameraSource();
    }

    /**
     * Stops the camera.
     */
    @Override
    protected void onPause() {
        super.onPause();
        mPreview.stop();
    }

    /**
     * Releases the resources associated with the camera source, the associated detector, and the
     * rest of the processing pipeline.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCameraSource != null) {
            mCameraSource.release();
           finish();
        }
    }

    /**
     * Callback for the result from requesting permissions. This method
     * is invoked for every call on {@link #requestPermissions(String[], int)}.
     * <p>
     * <strong>Note:</strong> It is possible that the permissions request interaction
     * with the user is interrupted. In this case you will receive empty permissions
     * and results arrays which should be treated as a cancellation.
     * </p>
     *
     * @param requestCode  The request code passed in {@link #requestPermissions(String[], int)}.
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either {@link PackageManager#PERMISSION_GRANTED}
     *                     or {@link PackageManager#PERMISSION_DENIED}. Never null.
     * @see #requestPermissions(String[], int)
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera permission granted - initialize the camera source");
            // we have permission, so create the camerasource
            createCameraSource();
            return;
        }

        Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Face Tracker sample")
                .setMessage(R.string.no_camera_permission)
                .setPositiveButton(R.string.ok, listener)
                .show();
    }


    //////// this method share your image
   /* private void shareBitmap (Bitmap bitmap,String fileName, String contentUri) {

        try {
            File file = new File(getApplicationContext().getCacheDir(), fileName + ".png");
            FileOutputStream fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
            file.setReadable(true, false);
            final Intent intent = new Intent( android.content.Intent.ACTION_SEND);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Intent.EXTRA_STREAM, contentUri);
            intent.setType("image/png");
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }*/


    public Bitmap screenShot(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),
                view.getHeight(), ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

//==============================================================================================
    // Camera Source Preview
    //==============================================================================================

    /**
     * Starts or restarts the camera source, if it exists.  If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private void startCameraSource() {

        // check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource, mGraphicOverlay);
            }

            catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

    private class GraphicTextTrackerFactory implements MultiProcessor.Factory<String> {
        @Override
        public Tracker<String> create(String face) {
            return new GraphicTextTracker(mGraphicOverlay);
        }
    }

    private class GraphicTextTracker extends Tracker<String> {
        private GraphicOverlay mOverlay;
        private TextGraphic mTextGraphic ;

        GraphicTextTracker(GraphicOverlay overlay) {
            mOverlay = overlay;
            mTextGraphic = new TextGraphic(overlay);
        }

       /* public void onUpdate() {
            mOverlay.add(mTextGraphic);
            mTextGraphic.updateText(3);
        }

        @Override
        public void onDone() {
            mOverlay.remove(mTextGraphic);
        }*/
    }

    //==============================================================================================
    // Graphic Face Tracker
    //==============================================================================================

    /**
     * Factory for creating a face tracker to be associated with a new face.  The multiprocessor
     * uses this factory to create face trackers as needed -- one for each individual.
     */
    private class GraphicFaceTrackerFactory implements MultiProcessor.Factory<Face> {
        @Override
        public Tracker<Face> create(Face face) {
            return new GraphicFaceTracker(mGraphicOverlay);
        }
    }

    /**
     * Face tracker for each detected individual. This maintains a face graphic within the app's
     * associated face overlay.
     */


    private class GraphicFaceTracker extends Tracker<Face> {
        private GraphicOverlay mOverlay;
        private   FaceGraphic mFaceGraphic;

           GraphicFaceTracker(GraphicOverlay overlay) {
            mOverlay = overlay;
            mFaceGraphic = new FaceGraphic(overlay,typeFace);
        }

        /**
         * Start tracking the detected face instance within the face overlay.
         */
       // @Override
      //  public void onNewItem(int faceId, Face item) {
          //  mFaceGraphic.setId(faceId);
      //  }


        /**
         * Update the position/characteristics of the face within the overlay.
         */


        @Override
        public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face) {
            mOverlay.add(mFaceGraphic);
            mFaceGraphic.updateFace(face,typeFace);
            SparseArray result = detectionResults.getDetectedItems();
         /*   Frame frame= detectionResults.
            Bitmap bitmap =

                Bitmap faceBitmap = Bitmap.createBitmap(bitmap, (int) face.getPosition().x, (int) face.getPosition().y, (int) face.getWidth(), (int) face.getHeight());*/

        }

        /**
         * Hide the graphic when the corresponding face was not detected.  This can happen for
         * intermediate frames temporarily (e.g., if the face was momentarily blocked from
         * view).
         */
        @Override
        public void onMissing(FaceDetector.Detections<Face> detectionResults) {
            mOverlay.remove(mFaceGraphic);
        }

        /**
         * Called when the face is assumed to be gone for good. Remove the graphic annotation from
         * the overlay.
         */
        @Override
        public void onDone() {
            mOverlay.remove(mFaceGraphic);

        }
    }

    private Size getScreenAspectRatio(){
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        return new Size(displayMetrics.widthPixels,displayMetrics.heightPixels);
    }
}

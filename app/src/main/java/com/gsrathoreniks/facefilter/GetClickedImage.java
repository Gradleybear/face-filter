package com.gsrathoreniks.facefilter;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.SparseArray;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.gsrathoreniks.facefilter.camera.GraphicOverlay;


import java.io.FileOutputStream;

import static com.gsrathoreniks.facefilter.FaceFilterActivity.typeFace;

public class GetClickedImage {

   static private GraphicOverlay mGraphicOverlay;
    static Bitmap getFace(Context context, Bitmap data) {

        FaceDetector detector = new FaceDetector.Builder(context)
                .setTrackingEnabled(false)
                .setProminentFaceOnly(true)
                .build();
        Frame frame = new Frame.Builder().setBitmap(data).build();
        SparseArray<Face> faces = detector.detect(frame);
        FaceGraphic mFaceGraphic = new FaceGraphic(mGraphicOverlay, typeFace);
        Canvas canvas = new Canvas(data);
        for (int i = 0; i < faces.size(); ++i) {
            Face face = faces.valueAt(i);
            float x = face.getPosition().x;
            float y = face.getPosition().y;
            float x2 = x / 4 + face.getWidth();
            float y2 = y / 4 + face.getHeight();
            canvas.drawBitmap(mFaceGraphic.op, x2, y2, new Paint());


        }
        detector.release();
        return data;
    }
}
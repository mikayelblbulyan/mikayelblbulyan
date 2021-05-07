package com.app.jsinnovations.ui.croperino;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.IOException;


public class Croperino {

    public static void runCropImage(File file, Fragment ctx, Activity activity, boolean isScalable, int aspectX, int aspectY, int color, int bgColor) {
        Intent intent = new Intent(activity, CropImage.class);
        intent.putExtra(CropImage.IMAGE_PATH, file.getPath());
        intent.putExtra(CropImage.SCALE, isScalable);
        intent.putExtra(CropImage.ASPECT_X, aspectX);
        intent.putExtra(CropImage.ASPECT_Y, aspectY);
        intent.putExtra("color", color);
        intent.putExtra("bgColor", bgColor);
        ctx.startActivityForResult(intent, 3);
    }


    public static void prepareCamera(Activity activity, Fragment fragment, Context context, String path) {
        File tempFile = new File(context.getExternalCacheDir() + path);
        if (tempFile.exists())
            openCamera(tempFile, fragment, activity);
        else
            try {
                if(tempFile.createNewFile())
                    openCamera(tempFile, fragment, activity);
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    private static void openCamera(File tempFile, Fragment fragment, Activity activity){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);

        Uri mImageCaptureUri;
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            if (Uri.fromFile(tempFile) != null) {
                if (Build.VERSION.SDK_INT >= 23) {
                    mImageCaptureUri = ImageFileProvider.getUriForFile(activity,
                            activity.getApplicationContext().getPackageName() + ".provider",
                            tempFile);
                } else {
                    mImageCaptureUri = Uri.fromFile(tempFile);
                }
            } else {
                mImageCaptureUri = ImageFileProvider.getUriForFile(activity,
                        activity.getApplicationContext().getPackageName() + ".provider",
                        tempFile);
            }
        } else {
            mImageCaptureUri = InternalStorageContentProvider.CONTENT_URI;
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
        intent.putExtra("return-data", true);
        fragment.startActivityForResult(intent, 0);
    }
    public static void prepareGallery(Activity activity, File tempFile) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        activity.startActivityForResult(Intent.createChooser(intent, "data"), 1);
    }
}

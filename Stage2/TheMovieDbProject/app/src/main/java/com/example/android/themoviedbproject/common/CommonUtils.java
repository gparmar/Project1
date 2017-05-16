package com.example.android.themoviedbproject.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by gparmar on 08/05/17.
 */

public class CommonUtils {
    public static boolean isNotEmpty(String s){
        return (s != null) && !s.isEmpty();
    }

    public static boolean isEmpty(String s){
        return !isNotEmpty(s);
    }

    public static void putSharedPref(Context context, String name, Object object) {
        SharedPreferences prefs = context.getSharedPreferences("TheMovieDb", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(name, object.toString());
        editor.commit();
    }

    public static String getSharedPref(Context context, String name, String defaultVal) {
        SharedPreferences prefs = context.getSharedPreferences("TheMovieDb", Context.MODE_PRIVATE);
        return prefs.getString(name, defaultVal);
    }

    public static void loadImageIntoImageView(Context context,
            String imageUrl, ImageView iv) {
        //First check if the image has been saved into the local file system
        Bitmap bmp = fetchFileFromLocalDirectory(imageUrl);

        //If not then fetch it using Picasso and save it in the local file system
        if (bmp == null) {
            new GetBitmapTask(context, iv, imageUrl).execute();
        } else {
            iv.setImageBitmap(bmp);
        }
    }

    private static void createDirectoryAndSaveFile(Bitmap imageToSave, String fileName) {
        if (fileName != null && fileName.startsWith("/")) {
            fileName = fileName.replace("/","");
        }

        File direct = new File(Environment.getExternalStorageDirectory() + "/"+Constants.LOCAL_IMAGES_FOLDER);

        if (!direct.exists()) {
            direct = new File("/sdcard/"+Constants.LOCAL_IMAGES_FOLDER);
            direct.mkdirs();
        }

        File file = new File(direct, fileName);
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            imageToSave.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static Bitmap fetchFileFromLocalDirectory(String fileName) {
        if (fileName != null && fileName.startsWith("/")) {
            fileName = fileName.replace("/","");
        }
        File direct = new File(Environment.getExternalStorageDirectory() + "/"+Constants.LOCAL_IMAGES_FOLDER);

        if (!direct.exists()) {
            direct = new File("/sdcard/"+Constants.LOCAL_IMAGES_FOLDER);
            direct.mkdirs();
        }

        File file = new File(direct, fileName);
        Bitmap bmp = null;
        if (file.exists()) {
            bmp = BitmapFactory.decodeFile(file.getAbsolutePath());
        }
        return bmp;
    }

    public static class GetBitmapTask extends AsyncTask<Void, Void, Bitmap> {
        private Context context;
        private ImageView iv;
        private String imageUrl;

        public GetBitmapTask(Context context, ImageView iv, String imageUrl) {
            this.context = context;
            this.iv = iv;
            this.imageUrl = imageUrl;
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {
            Bitmap bmp = null;
            if (imageUrl != null && !imageUrl.isEmpty()) {
                try {
                    bmp = Picasso.with(context).load(Constants.IMAGE_BASE_URL+
                            Constants.IMAGE_SIZE+imageUrl).get();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return bmp;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                createDirectoryAndSaveFile(bitmap, imageUrl);
                iv.setImageBitmap(bitmap);
            }
        }
    }
}

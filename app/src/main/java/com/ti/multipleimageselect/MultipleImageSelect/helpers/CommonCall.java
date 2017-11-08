package com.ti.multipleimageselect.MultipleImageSelect.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

/**
 * Created by Ajay on 15/6/16.
 */
public class CommonCall {
    public static DisplayImageOptions getOptions(int loadingImage, int errorImageResourse) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(loadingImage) // resource or drawable
                .showImageForEmptyUri(errorImageResourse) // resource or drawable
                .showImageOnFail(errorImageResourse) // resource or drawable
                .resetViewBeforeLoading(false)  // default
                .cacheInMemory(true) // default
                .cacheOnDisk(true) // default
                .considerExifParams(true) // default

                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
                .bitmapConfig(Bitmap.Config.RGB_565) // default
                .displayer(new SimpleBitmapDisplayer()) // default
                .handler(new Handler()) // default

                .build();
        return options;

    }


    public static void LoadImage(final Context context, final String path,
                                 final ImageView imgView, int loadingImage, int errorImageResourse) {
        try {
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(loadingImage) // resource or drawable
                    .showImageForEmptyUri(errorImageResourse) // resource or drawable
                    .showImageOnFail(errorImageResourse) // resource or drawable
                    .resetViewBeforeLoading(false)  // default
                    .cacheInMemory(true) // default
                    .cacheOnDisk(true) // default
                    .considerExifParams(true) // default

                    .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
                    .bitmapConfig(Bitmap.Config.RGB_565) // default
                    .displayer(new SimpleBitmapDisplayer()) // default
                    .handler(new Handler()) // default

                    .build();

            ImageLoader.getInstance().displayImage(path, imgView, options);


        } catch (OutOfMemoryError e) {
            ImageLoader.getInstance().clearDiskCache();
            ImageLoader.getInstance().clearMemoryCache();
            System.gc();
            Runtime.getRuntime().gc();

            Toast.makeText(context, "MEMORY FULL", 2000).show();

//            CommonCall.PrintLog("OUT OF MEMORY CAUGHT", "OUT OF MEMORY CAUGHT");


//            reload image if out of memory occur
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(loadingImage) // resource or drawable
                    .showImageForEmptyUri(errorImageResourse) // resource or drawable
                    .showImageOnFail(errorImageResourse) // resource or drawable
                    .resetViewBeforeLoading(false)  // default
                    .cacheInMemory(true) // default
                    .cacheOnDisk(true) // default
                    .considerExifParams(true) // default

                    .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
                    .bitmapConfig(Bitmap.Config.RGB_565) // default
                    .displayer(new SimpleBitmapDisplayer()) // default
                    .handler(new Handler()) // default

                    .build();

            ImageLoader.getInstance().displayImage(path, imgView, options);


        }

        /*CommonMethods.getUniversalImageLoader().displayImage(path,imgView);*/
    }



    public static void clearCache() {
        ImageLoader.getInstance().clearDiskCache();
        ImageLoader.getInstance().clearMemoryCache();
    }

    public static void PrintLog(String s1, String s2) {

        Log.e(s1, s2);

    }
}
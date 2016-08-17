package ru.bibliowiki.litclubbs.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.IOException;

import ru.bibliowiki.litclubbs.R;

/**
 * @author by pf on 16.07.2016.
 */
public class ImageDownloadTask {

    private final Context context;
    private String imageUri;
    private ImageSize imageSize;
    private DisplayImageOptions displayImageOptions;
    private Drawable image;

    public ImageDownloadTask(Context context, String imageUri, ImageSize imageSize) {

        this(context, imageUri,
                imageSize,
                new DisplayImageOptions.Builder().showStubImage(R.drawable.ic_hourglass_empty_48dp)
                        .showImageForEmptyUri(R.drawable.ic_empty)
                        .showImageOnFail(R.drawable.ic_empty)
                        .cacheInMemory(true)
                        .cacheOnDisc(true)
                        .bitmapConfig(Bitmap.Config.RGB_565).build());

    }

    public ImageDownloadTask(Context context, String imageUri, ImageSize imageSize, DisplayImageOptions displayImageOptions) {
        this.context = context;
        this.displayImageOptions = displayImageOptions;
        this.imageSize = imageSize;
        this.imageUri = imageUri;
    }

    private Drawable downloadImage(){
        try {
            ImageLoader il = ImageLoader.getInstance();
            il.loadImage(imageUri, imageSize, displayImageOptions, new SimpleImageLoadingListener(){
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage){
                    image = new BitmapDrawable(context.getResources(), loadedImage);
                }
            });
        } catch (Exception e) {
            image = context.getResources().getDrawable(R.drawable.ic_empty);
            RoboErrorReporter.reportError(context, new IOException("Can't download an image at ImageDownloadTask"));
            RoboErrorReporter.reportError(context, e);
        }
        finally {
            return image;
        }
    }

    public static Drawable downloadImage(Context context, String imageUri, ImageSize imageSize, DisplayImageOptions displayImageOptions){
        return (new ImageDownloadTask(context, imageUri, imageSize, displayImageOptions)).downloadImage();
    }

    public static Drawable downloadImage(Context context, String imageUri, ImageSize imageSize){
        return (new ImageDownloadTask(context, imageUri, imageSize)).downloadImage();
    }

}
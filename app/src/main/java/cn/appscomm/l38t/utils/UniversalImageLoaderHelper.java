package cn.appscomm.l38t.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;

import cn.appscomm.l38t.R;
import cn.appscomm.netlib.constant.NetLibConstant;

/**
 * author ：weiliu
 * email ：weiliu@appscomm.cn
 * time : 2016/10/9 11:06
 */
public class UniversalImageLoaderHelper {
    private static final String TAG = UniversalImageLoaderHelper.class.getSimpleName();

    public interface ImageLoaderListener {
        void onLoadingComplete();
    }

    public static void init(Context context) {
        ImageLoaderConfiguration loaderConfiguration = initImageLoaderConfiguration(context);
        ImageLoader.getInstance().init(loaderConfiguration);//全局初始化此配置
    }

    public static void displayImage(String iconUrl, ImageView imageView) {
        if (imageView == null)
            return;
        String imageUri = "";
        if (iconUrl == null || iconUrl.equals("") || iconUrl.endsWith("null") || !iconUrl.endsWith(".jpg")||!iconUrl.endsWith(".jpeg")) {
            imageUri = "drawable://" + R.mipmap.ic_default_pic;
        } else {
            imageUri = NetLibConstant.Domain + iconUrl;
        }
        LogUtil.w(TAG, "imageUri: " + imageUri);
        /**
         *String imageUri = "http://site.com/image.png"; // from Web
         *String imageUri = "file:///mnt/sdcard/image.png"; // from SD card
         *String imageUri = "content://media/external/audio/albumart/13"; // from content provider
         *String imageUri = "assets://image.png"; // from assets
         *String imageUri = "drawable://" + R.drawable.image; // from drawables (only images, non-9patch)
         */
        DisplayImageOptions displayImageOptions = initDisplayImageOptions();
        ImageLoader.getInstance().displayImage(imageUri, imageView, displayImageOptions);
    }

    public static Bitmap loadImage(String iconUrl,Context context) {
        Bitmap bitmap = null;
        if (TextUtils.isEmpty(iconUrl) || iconUrl.endsWith("null") || !iconUrl.endsWith(".jpg")) {
            bitmap = BitmapFactory.decodeResource(context.getApplicationContext().getResources(), R.mipmap.ic_default_pic);
        }else {
            DisplayImageOptions displayImageOptions = initDisplayImageOptions();
            String imageUri = NetLibConstant.Domain + iconUrl;
            bitmap=ImageLoader.getInstance().loadImageSync(imageUri,displayImageOptions);
        }
        return bitmap;
    }


    public static void loadImage(String iconUrl,final ImageLoaderListener loaderListener) {
        if (TextUtils.isEmpty(iconUrl) || iconUrl.endsWith("null") || !iconUrl.endsWith(".jpg")) {
            return;
        }
        String imageUri = NetLibConstant.Domain + iconUrl;
        DisplayImageOptions displayImageOptions = initDisplayImageOptions();
        ImageLoader.getInstance().loadImage(imageUri,displayImageOptions, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                loaderListener.onLoadingComplete();
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });
    }


    private static ImageLoaderConfiguration initImageLoaderConfiguration(Context context) {
        File cacheDir = StorageUtils.getCacheDirectory(context);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .memoryCacheExtraOptions(480, 800) // default = device screen dimensions
                .diskCacheExtraOptions(480, 800, null)
                .threadPoolSize(3) // default
                .threadPriority(Thread.NORM_PRIORITY - 2) // default
                .tasksProcessingOrder(QueueProcessingType.FIFO) // default
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .memoryCache(new WeakMemoryCache())
                .memoryCacheSizePercentage(13) // default
                .diskCache(new UnlimitedDiskCache(cacheDir)) // default
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(100)
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator()) // default
                .imageDownloader(new BaseImageDownloader(context)) // default
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple()) // default
                .writeDebugLogs()
                .build();
        return config;
    }

    private static DisplayImageOptions initDisplayImageOptions() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.ic_default_pic) // resource or drawable
                .showImageForEmptyUri(R.mipmap.ic_default_pic) // resource or drawable
                .showImageOnFail(R.mipmap.ic_default_pic) // resource or drawable
                .resetViewBeforeLoading(false)  // default
                .delayBeforeLoading(0)
                .cacheInMemory(false) // default
                .cacheOnDisk(true) // default
                .considerExifParams(false) // default
                .imageScaleType(ImageScaleType.EXACTLY) // default
                .bitmapConfig(Bitmap.Config.RGB_565) // default
                .build();
        return options;
    }


}

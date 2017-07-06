package cn.appscomm.l38t.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.widget.ImageView;

import java.lang.ref.SoftReference;

import cn.appscomm.l38t.R;
import cn.appscomm.l38t.constant.APPConstant;
import cn.appscomm.netlib.constant.NetLibConstant;


/**
 * Created by weiliu on 2016/8/8.
 */
public class ImageLoaderUtil {

    public interface ImageLoaderUtilListener {
        void downloaded(Bitmap bitmap);
    }

    private static final String TAG = ImageLoaderUtil.class.getSimpleName();

    public static void handleIconUrl(final SoftReference<Context> context, final SoftReference<ImageView> iv_pic, final SoftReference<Handler> mHandler, final String iconUrl) {
        if (context == null || context.get() == null || iv_pic == null || iv_pic.get() == null)
            return;
        if (iconUrl == null || iconUrl.equals("") || iconUrl.endsWith("null") || !(iconUrl.endsWith(".jpg") || iconUrl.endsWith(".png"))) {
            LogUtil.i(TAG, "handleIconUrl: " + iconUrl);
            iv_pic.get().setImageBitmap(BitmapFactory.decodeResource(context.get().getApplicationContext().getResources(), R.mipmap.ic_default_pic));
            return;
        }
        final String fileName = getFileName(iconUrl);
        if (isIconFileExist(fileName)) {
            iv_pic.get().setImageBitmap(BitmapFactory.decodeFile(APPConstant.SDCardDirPath + APPConstant.APP_DIR + fileName));
            LogUtil.i(TAG, "handleIconUrl: " + APPConstant.SDCardDirPath + APPConstant.APP_DIR + fileName);
        } else {
            LogUtil.i(TAG, "handleIconUrl: downloadImgRunnable");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final Bitmap picBitmap = ImageUtil.getImage(context.get().getApplicationContext(), NetLibConstant.Domain + iconUrl);
                    ImageUtil.writeToFile(picBitmap, APPConstant.SDCardDirPath + APPConstant.APP_DIR, fileName);
                    if (mHandler.get() != null) {
                        mHandler.get().post(new Runnable() {
                            @Override
                            public void run() {
                                if (picBitmap != null)
                                    iv_pic.get().setImageBitmap(picBitmap);
                            }
                        });
                    }
                }
            }).start();
        }
    }

    public static void downloadIconImage(final SoftReference<Context> context, final String iconUrl, final ImageLoaderUtilListener loaderUtilListener) {
        Bitmap bitmap = null;
        if (context == null || context.get() == null)
            bitmap = null;
        if (iconUrl == null || iconUrl.equals("") || iconUrl.endsWith("null") || !iconUrl.endsWith(".jpg")) {
            LogUtil.i(TAG, "handleIconUrl: " + iconUrl);
            bitmap = BitmapFactory.decodeResource(context.get().getApplicationContext().getResources(), R.mipmap.ic_default_pic);
        }
        final String fileName = getFileName(iconUrl);
        if (isIconFileExist(fileName)) {
            bitmap = BitmapFactory.decodeFile(APPConstant.SDCardDirPath + APPConstant.APP_DIR + fileName);
            LogUtil.i(TAG, "handleIconUrl: " + APPConstant.SDCardDirPath + APPConstant.APP_DIR + fileName);
        } else {
            LogUtil.i(TAG, "handleIconUrl: downloadImgRunnable");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    LogUtil.i(TAG, "  iconUrl = " + iconUrl);
                    final Bitmap picBitmap = ImageUtil.getImage(context.get().getApplicationContext(), iconUrl);     // ������bitmap
                    ImageUtil.writeToFile(picBitmap, APPConstant.SDCardDirPath + APPConstant.APP_DIR, fileName);   // ���ر���ͷ��
                    if (loaderUtilListener != null) {
                        loaderUtilListener.downloaded(picBitmap);
                    }
                }
            }).start();
        }
    }

    private static String getFileName(String iconUrl) {
        int index = iconUrl.lastIndexOf("/");
        return iconUrl.substring(index + 1);
    }

    private static boolean isIconFileExist(String fileName) {
        LogUtil.i(TAG, "fileName: " + fileName);
        LogUtil.i(TAG, "filePath: " + APPConstant.SDCardDirPath + APPConstant.APP_DIR + fileName);
        return FileUtils.isFileExits(APPConstant.SDCardDirPath + APPConstant.APP_DIR + fileName);
    }
}

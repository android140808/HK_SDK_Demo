package cn.appscomm.l38t.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.SystemClock;
import android.support.annotation.NonNull;

import com.facebook.common.references.CloseableReference;
import com.facebook.common.references.SharedReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.common.RotationOptions;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.image.CloseableBitmap;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by liucheng on 2017/6/19.
 */

public class CameraUtils {

    /**
     * 检测摄像头设备是否可用
     * Check if this device has a camera
     *
     * @param context
     * @return
     */
    public static boolean checkCameraHardware(Context context) {
        if (context != null && context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            return false;
        }
    }

    private static ImagePipeline imagePipeline = Fresco.getImagePipeline();

    /**
     * @param degree   旋转角度
     * @param filePath 文件路径
     * @param context  上下文
     */
    public static void Rotatoin(int degree, String filePath, Context context) {
        if (filePath == null) {
            return;
        }
        File srcFile = new File(filePath);
        if (srcFile.exists() && (srcFile.getName().endsWith("jpg") || srcFile.getName().endsWith("jpeg"))) {
            Uri uri = Uri.fromFile(srcFile);
            RotationOptions rotationOptions = getRotationOptions(degree);
            ImageRequest imageRequest = ImageRequestBuilder
                    .newBuilderWithSource(uri)
                    .setRotationOptions(rotationOptions)
                    .setProgressiveRenderingEnabled(false)
                    .build();
            DataSource<CloseableReference<CloseableImage>> dataSource = imagePipeline.fetchDecodedImage(imageRequest, "11");
            try {
                CloseableReference<CloseableImage> imageReference;
                do {
                    SystemClock.sleep(200);//由于大图片加载存在有延时，故需要while做一个轮询，直到整个图片都加载完毕
                    imageReference = dataSource.getResult();
                } while (imageReference == null);
                try {
                    SharedReference<CloseableImage> underlyingReferenceTestOnly = imageReference.getUnderlyingReferenceTestOnly();
                    CloseableImage closeableImage = underlyingReferenceTestOnly.get();
                    if (closeableImage instanceof CloseableBitmap) {
                        Bitmap underlyingBitmap = ((CloseableBitmap) closeableImage).getUnderlyingBitmap();
                        FileOutputStream fos = new FileOutputStream(srcFile);
                        underlyingBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                        fos.flush();
                        fos.close();
                        // 最后通知图库更新
                        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + srcFile.getAbsolutePath())));
                        LogUtil.i("test-test", "保存成功bitmap成功...,路径为" + filePath);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    CloseableReference.closeSafely(imageReference);
                }
            } finally {
                if (dataSource != null) {
                    dataSource.close();
                }
            }
        }
    }

    @NonNull
    private static RotationOptions getRotationOptions(int degree) {
        RotationOptions rotationOptions;
        switch (degree) {
            case 90:
                rotationOptions = RotationOptions.forceRotation(RotationOptions.ROTATE_90);
                break;
            case 180:
                rotationOptions = RotationOptions.forceRotation(RotationOptions.ROTATE_180);
                break;
            case 270:
                rotationOptions = RotationOptions.forceRotation(RotationOptions.ROTATE_270);
                break;
            case 0:
                rotationOptions = RotationOptions.forceRotation(RotationOptions.NO_ROTATION);
                break;
            default:
                rotationOptions = RotationOptions.autoRotate();
                break;
        }
        return rotationOptions;
    }

    public static int getExifOrientation(String filepath, boolean isFront) {
        int degree = 0;
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(filepath);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        if (exif != null) {
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
            if (orientation != -1) {
                switch (orientation) {
                    case ExifInterface.ORIENTATION_NORMAL:
                        if (isFront) {
                            degree = 270;
                        } else {
                            degree = 90;
                        }
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        degree = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        if (isFront) {
                            degree = 90;
                        } else {
                            degree = 270;
                        }
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        degree = 0;
                        break;
                }
            }
        }
        return degree;
    }

}

package cn.appscomm.l38t.UI.camera;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;
import java.util.List;

/**
 * Created by Administrator on 2017/1/18.
 */

public class CameraPreview extends ViewGroup implements SurfaceHolder.Callback, Camera.AutoFocusCallback {
    private final String TAG = "Preview";

    /**
     * 图片的偏移
     */
    public int moveX = 0;
    public int moveY = 0;

    public boolean isFrontCamera = false;
    public SurfaceView mSurfaceView;
    private SurfaceHolder mHolder;
    public Camera.Size mPreviewSize;
    private List<Camera.Size> mSupportedPreviewSizes;

    public Camera.Size mPictureSize;
    private List<Camera.Size> mSupportedPictureSizes;

    private Camera mCamera;

    CameraPreview(Context context) {
        super(context);
        init(context);
    }

    public CameraPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CameraPreview(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public SurfaceHolder getLouisSurfaceHolder() {
        return mHolder;
    }

    private void init(Context context) {
        mSurfaceView = new SurfaceView(context);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(mSurfaceView, layoutParams);
        // mSurfaceView.setBackgroundColor(Color.RED);

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = mSurfaceView.getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void setCamera(Camera camera) {
        mCamera = camera;
        if (mCamera != null) {
            try {
                mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();
                mSupportedPictureSizes = mCamera.getParameters().getSupportedPictureSizes();
                String model = android.os.Build.MODEL;                                              // 获取手机型号
                String carrier = android.os.Build.MANUFACTURER;                                     // 获取手机厂商
                if (isFrontCamera && (("Nexus 6P".equalsIgnoreCase(model) && "Huawei".equalsIgnoreCase(carrier)) || ("MI 5s".equalsIgnoreCase(model) && "xiaomi".equalsIgnoreCase(carrier)))) {
                    mCamera.setDisplayOrientation(270);
                } else {
                    mCamera.setDisplayOrientation(90);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            requestLayout();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // We purposely disregard child measurements because act as a
        // wrapper to a SurfaceView that centers the camera preview instead
        // of stretching it.
        final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        setMeasuredDimension(width, height);
        if (mSupportedPreviewSizes != null) {
            // 需要宽高切换 因为相机有90度的角度
            mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, height, width);
        }
        if (mSupportedPictureSizes != null) {
            mPictureSize = getOptimalPreviewSize(mSupportedPictureSizes, height, width);
        }

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed && getChildCount() > 0) {
            final View child = getChildAt(0);

            final int width = r - l;
            final int height = b - t;

            int previewWidth = width;
            int previewHeight = height;
            if (mPreviewSize != null) {
                previewWidth = mPreviewSize.height;
                previewHeight = mPreviewSize.width;
            }

            // Center the child SurfaceView within the parent.
            if (width * previewHeight > height * previewWidth) {

                final int scaleWidth = width;
                final int scaleHeight = width * previewHeight / previewWidth;
                moveX = 0;
                moveY = (scaleHeight - height) / 2;
                if (moveY < 0) {
                    moveY = 0;
                }
                child.layout(-moveX, -moveY, scaleWidth, scaleHeight);
            } else {

                final int scaleHeight = height;
                final int scaleWidth = height * previewWidth / previewHeight;
                moveX = (scaleWidth - width) / 2;
                moveY = 0;
                if (moveX < 0) {
                    moveX = 0;
                }
                child.layout(-moveX, -moveY, scaleWidth, scaleHeight);
            }

        }
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, acquire the camera and tell it where
        // to draw.
        try {
            if (mCamera != null) {
                mCamera.setPreviewDisplay(holder);
            }
        } catch (IOException exception) {
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // Surface will be destroyed when we return, so stop the preview.
        if (mCamera != null) {
            mCamera.stopPreview();
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        if (mCamera == null) {
            return;
        }
        try {
            // Now that the size is known, set up the camera parameters and begin
            // the preview.
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
            parameters.setPictureSize(mPictureSize.width, mPictureSize.height);
            requestLayout();

            mCamera.setParameters(parameters);
            mCamera.startPreview();
            mCamera.autoFocus(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAutoFocus(boolean success, Camera camera) {

    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) w / h;
        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        // Try to find an size match aspect ratio and size
        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }
}

/**
 * 
 */
package org.yanzi.camera.preview;

import org.yanzi.camera.CameraInterface;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/** 
 * @author 作者 E-mail: 
 * @version 创建时间：2017-6-1 上午9:44:13 
 * 类说明 
 */
/**
 * @author Administrator
 * 
 */
public class CameraSurfaceView extends SurfaceView implements
		SurfaceHolder.Callback {
	private static final String TAG = "yanzi";
	CameraInterface mCameraInterface;
	Context mContext;
	SurfaceHolder mSurfaceHolder;

	public CameraSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mContext = context;
		mSurfaceHolder = getHolder();
		mSurfaceHolder.setFormat(PixelFormat.TRANSPARENT);// translucent半透明
															// transparent透明
		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		mSurfaceHolder.addCallback(this);
	}


	public SurfaceHolder getSurfaceHolder() {
		return mSurfaceHolder;
	}

	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		Log.i(TAG, "surfaceCreated...");
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		Log.i(TAG, "surfaceChanged...");
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		Log.i(TAG, "surfaceDestroyed...");
		CameraInterface.getInstance().doStopCamera();
	}

}
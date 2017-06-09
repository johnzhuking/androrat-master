/**
 * 
 */
package org.yanzi.activity;

/** 
 * @author 作者 E-mail: 
 * @version 创建时间：2017-6-1 上午9:48:25 
 * 类说明 
 */

import my.app.client.R;

import org.yanzi.camera.CameraInterface;
import org.yanzi.camera.CameraInterface.CamOpenOverCallback;
import org.yanzi.camera.preview.CameraSurfaceView;
import org.yanzi.util.DisplayUtil;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;

/**
 * @author Administrator
 * 
 */
public class CameraActivity extends Activity implements CamOpenOverCallback {
	private static final String TAG = "yanzi";
	CameraSurfaceView surfaceView = null;
	ImageButton shutterBtn;
	float previewRate = -1f;
	int chan;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Thread openThread = new Thread() {
			@Override
			public void run() {
				CameraInterface.getInstance().doOpenCamera(CameraActivity.this);
			}
		};
		this.chan = getIntent().getIntExtra("chan", -1);
		
		Log.e("chan", "chan"+chan);
		openThread.start();
		setContentView(R.layout.activity_camera);
		initUI();
		initViewParams();

		shutterBtn.setOnClickListener(new BtnListeners());
	}


	private void initUI() {
		surfaceView = (CameraSurfaceView) findViewById(R.id.camera_surfaceview);
		shutterBtn = (ImageButton) findViewById(R.id.btn_shutter);
	}

	private void initViewParams() {
		LayoutParams params = surfaceView.getLayoutParams();
		Point p = DisplayUtil.getScreenMetrics(this);
		params.width = p.x;
		params.height = p.y;
		previewRate = DisplayUtil.getScreenRate(this); // 默认全屏的比例预览
		surfaceView.setLayoutParams(params);

		// 手动设置拍照ImageButton的大小为120dip×120dip,原图片大小是64×64
		LayoutParams p2 = shutterBtn.getLayoutParams();
		p2.width = DisplayUtil.dip2px(this, 80);
		p2.height = DisplayUtil.dip2px(this, 80);
		;
		shutterBtn.setLayoutParams(p2);

	}

	

	private class BtnListeners implements OnClickListener {

		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_shutter:
				CameraInterface.getInstance().doTakePicture(chan);
				break;
			default:
				break;
			}
		}
	}

	public void cameraHasOpened() {
		SurfaceHolder holder = surfaceView.getSurfaceHolder();
		CameraInterface.getInstance().doStartPreview(holder, previewRate);
	}

}
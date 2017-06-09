/**
 * 
 */
package my.app.client;

import utils.LogToFile;
import utils.LogcatHelper;
import android.app.Application;

/** 
 * @author 作者 E-mail: 
 * @version 创建时间：2017-6-6 上午8:40:49 
 * 类说明 
 */
/**
 * @author Administrator
 *
 */
public class MyApplication extends Application{

	@Override
	public void onCreate() {
		super.onCreate();
		LogToFile.init(getApplicationContext());
		
		LogcatHelper.getInstance(this).start();
	}
}

/**
 * 
 */
package utils;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/** 
 * @author 作者 E-mail: 
 * @version 创建时间：2017-6-6 下午2:20:03 
 * 类说明 
 */
/**
 * @author Administrator
 * 
 */
public class FileUtils {

	private static SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd");// 日期格式;
	
	public static void getInfoBylogShow(String logShow, String logStr) {
		
		
		String title = "download/"
				+ dateFormat.format(new Date()) + ".txt";
		String fileName = Thread.currentThread().getStackTrace()[2]
				.getFileName();
		String className = Thread.currentThread().getStackTrace()[2]
				.getClassName();
		String methodName = Thread.currentThread().getStackTrace()[2]
				.getMethodName();
		int lineNumber = Thread.currentThread().getStackTrace()[2]
				.getLineNumber();
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat simpleFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		if (logShow.indexOf("console") != -1) {
			System.out.printf("%s--%-20s%-30s%-15s%-6d%-40s\n",
					simpleFormat.format(date), fileName, className, methodName,
					lineNumber, logStr);
		}
		if (logShow.indexOf("file") != -1) {
			FileWriter fileWriter;
			try {
				fileWriter = new FileWriter(title, true);
				fileWriter.write("时间:" + simpleFormat.format(date).toString()
						+ "\r");
				fileWriter.write("信息:" + logStr + "\r\n");
				fileWriter.flush();
				fileWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (logShow.indexOf("html") != -1) {

		}
		if (logShow.indexOf("database") != -1) {

		}
	}
}

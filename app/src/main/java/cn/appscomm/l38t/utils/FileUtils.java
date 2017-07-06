package cn.appscomm.l38t.utils;

import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Properties;

public final class FileUtils {
	private static String _sdCardDirPath;
	private static String _memoryDirPath;
	private static String baseDir = "bluewhale";
	public final static String CONFIGPATH = "config";
	public final static String IMAGEPATH = "images";

	static {
		_sdCardDirPath = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + File.separator;// SD卡的根目录地址
		// _memoryDirPath = getAppContext().getFilesDir().getAbsolutePath() +
		// File.separator;//程序APK的安装地址
	}

	public static File[] getFileList(File f) {
		File[] files = null;
		if (f.exists() && f.isDirectory()) {
			files = f.listFiles();// 列出所有文件
			Arrays.sort(files, new Comparator<File>() {
				@Override
				public int compare(File object1, File object2) {
					long d1 = object1.lastModified();
					long d2 = object2.lastModified();
					if (d1 == d2)
						return 0;
					else
						return d1 < d2 ? 1 : -1;
				}
			});
		}
		return files;
	}

	/**
	 * 返回目录下所有文件
	 * 
	 * @return 所有文件
	 * @param
	 */
	public static File[] getFileList(String dirPath) {
		return getFileList(new File(getFilePath(dirPath)));
	}

	/**
	 * 创建新文件，如果文件存在则覆盖 如SDcard可用，将在SDcard的跟目录下创建一个文件 如不可用，将在程序的安装目录下创建文件
	 * 
	 * @return
	 * @throws IOException
	 */
	public static File createFileByState(String dir, String fileName)
			throws IOException {
		return createFile(dir, fileName, true, true);
	}

	/**
	 * 读取文件，如果文件不存在则创建 如SDcard可用，将在读取SDcard目录中的文件 如不可用，读取程序的安装目录中的文件
	 * 
	 * @return
	 * @throws IOException
	 */
	public static File readFileByState(String dir, String fileName,
			boolean create) throws IOException {
		return createFile(dir, fileName, false, create);
	}

	/**
	 * 删除文件，删除指定目录下的某个文件 如SDcard可用，将删除SDcard目录中指定文件 如不可用，读删除程序的安装目录中指定文件
	 * 
	 * @param dirName
	 * @param fileName
	 * @throws IOException
	 */
	public static boolean deleteFileByState(String dirName, String fileName) {
		File file = new File(getFilePath(dirName) + File.separator + fileName);
		return deleteFile(file);
	}

	/**
	 * 删除文件
	 * 
	 * @param fileFullName
	 *            文件绝对路径+文件名
	 * @return
	 * @throws IOException
	 */
	public static boolean deleteFile(String fileFullName) {
		File file = new File(fileFullName);
		return deleteFile(file);
	}

	/**
	 * 删除SD卡上的目录
	 * 
	 * @param dirName
	 */
	public static boolean deleteDirByState(String dirName) {
		File dir = new File(getFilePath(dirName));
		return deleteDir(dir);
	}

	/**
	 * 判断SDcard是否可用，是将返回true
	 * 
	 * @return
	 */
	public static boolean sdCardExist() {
		String storageState = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equalsIgnoreCase(storageState)) {
			return true;
		} else {
			return false;
		}
	}

	public static String getSdCardDirPath() {
		return _sdCardDirPath + baseDir + File.separator;
	}

	public static String getMemoryDirPath() {
		return _memoryDirPath;
	}

	/**
	 * 判断SDcard是否可用，可用返回SDcard下路径，不可用返回内存下路径
	 * 
	 * @return
	 */
	public static String getFilePath(String path) {
		if (sdCardExist()) {
			if (TextUtils.isEmpty(path))
				return _sdCardDirPath + baseDir;
			else
				return _sdCardDirPath + baseDir + File.separator + path;
		} else {
			if (TextUtils.isEmpty(path))
				return _memoryDirPath;
			else
				return _memoryDirPath + path;
		}
	}

	/**
	 * 创建目录，根据当前系统中SDcard的状态是否可用创建目录
	 * 
	 * @param dirPath
	 *            目录名称
	 * @return
	 * @throws IOException
	 */
	public static File createDirByState(String dirPath) {
		File dir = new File(getFilePath(dirPath));
		if (!dir.exists())
			dir.mkdirs();
		return dir;
	}

	/**
	 * 拷贝文件
	 * 
	 * @param source
	 * @param target
	 * @return
	 */
	public static boolean copy(File source, File target) {
		if (!source.isFile())
			return false;
		FileInputStream in = null;
		FileOutputStream out = null;
		try {
			if (!target.exists() && !target.createNewFile())
				return false;
			in = new FileInputStream(source);
			out = new FileOutputStream(target);
			byte[] buffer = new byte[8 * 1024];
			int count;
			while ((count = in.read(buffer)) != -1) {
				out.write(buffer, 0, count);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (in != null) {
					in.close();
				}
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * 创建文件，根据当前系统中SDcard的状态是否可用创建文件
	 * 
	 * @param dirName
	 *            目录名称
	 * @param fileName
	 *            文件名称
	 * @param overWrite
	 *            是否覆盖
	 * @return
	 * @throws IOException
	 */
	private static File createFile(String dirName, String fileName,
			boolean overWrite, boolean create) throws IOException {
		File dir = createDirByState(dirName);
		File file = new File(dir.getAbsolutePath() + File.separator + fileName);
		if ((overWrite || !file.exists()) && create)
			file.createNewFile();
		return file;
	}

	/**
	 * 删除一个文件
	 * 
	 * @param file
	 * @return
	 */
	private static boolean deleteFile(File file) {
		if (file == null || file.isDirectory())
			return false;
		return file.delete();
	}

	/**
	 * 判断文件是否存在
	 *
	 * @param fullPathName
	 * @return
	 */
	public static boolean isFileExits(String fullPathName) {
		File file=new File(fullPathName);
		if (file == null || !file.exists())
			return false;
		return true;
	}

	/**
	 * 删除一个目录（可以是非空目录）
	 * 
	 * @param dir
	 */
	public static boolean deleteDir(File dir) {
		if (dir == null || !dir.exists() || dir.isFile())
			return false;
		for (File file : dir.listFiles()) {
			if (file.isFile())
				file.delete();
			else if (file.isDirectory())
				deleteDir(file);// 递归
		}
		return dir.delete();
	}

	public static void writeProperties(Properties properties, String fileName) {
		try {
			FileOutputStream s = new FileOutputStream(createFileByState(
					CONFIGPATH, fileName), false);
			properties.store(s, "");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static Properties loadProperties(String fileName) {
		try {
			Properties properties = new Properties();
			FileInputStream s = new FileInputStream(createFileByState(
					CONFIGPATH, fileName));
			properties.load(s);
			return properties;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static File getCacheFile(String imageUri) {
		File cacheFile = null;
		try {
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				File sdCardDir = Environment.getExternalStorageDirectory();
				String fileName = getFileName(imageUri);
				File dir = new File(sdCardDir.getCanonicalPath() + AsynImageLoader.CACHE_DIR);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				cacheFile = new File(dir, fileName);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return cacheFile;
	}

	public static String getFileName(String path) {
		int index = path.lastIndexOf("/");
		return path.substring(index + 1);
	}
}

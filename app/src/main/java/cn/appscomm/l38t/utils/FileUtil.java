package cn.appscomm.l38t.utils;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 1、新建目录 2、新建文件 3、删除文件 4、删除文件夹 5、删除文件夹里面的所有文件 6、复制单个文件 7、复制整个文件夹内容 8、文件拷贝 9、改名
 * 10、移动文件到指定目录 11、移动文件夹到指定目录 12、删除日志文件(指定时间、正则等) 13、获取文件夹里用过滤条件过滤后的文件名，大小写敏感
 * 14、判断是否跟过滤条件匹配 15、转换window风格下的过滤条件成java的正则表达式 16、写文件 17、将指定的zip文件解压到指定的目录下
 * 18、获得某目录(不含子目录)下文件名符合正则表达式的文件总数 19、获得某目录(不含子目录)下文件名符合正则表达式的其中一个文件的文件名
 * 20、删除某目录下文件名符合正则表达式的文件 21、读文件 22、获取文件修改时间 23、简单的文件选择器 1、新建目录 1、新建目录 1、新建目录  23、写日志
 * 
 */
public class FileUtil {
	/**
	 * 新建目录
	 * 
	 * @param folderPath
	 *            String 如 c:/fqf
	 * @return boolean
	 */
	public static void newFolder(String folderPath) {
		try {
			String filePath = folderPath;
			filePath = filePath.toString();
			File myFilePath = new File(filePath);
			if (!myFilePath.exists()) {
				myFilePath.mkdirs();
			}
		} catch (Exception e) {
			System.err.println("新建目录操作出错");
			e.printStackTrace();
		}
	}

	/**
	 * 新建文件
	 * 
	 * @param filePathAndName
	 *            String 文件路径及名称 如c:/fqf.txt
	 * @param fileContent
	 *            String 文件内容
	 * @return boolean
	 */
	public static void newFile(String filePathAndName, String fileContent) {

		try {
			String filePath = filePathAndName;
			filePath = filePath.toString();
			File myFilePath = new File(filePath);
			if (!myFilePath.exists()) {
				myFilePath.createNewFile();
			}
			FileWriter resultFile = new FileWriter(myFilePath);
			PrintWriter myFile = new PrintWriter(resultFile);
			String strContent = fileContent;
			myFile.println(strContent);
			resultFile.close();
		} catch (Exception e) {
			System.err.println("新建目录操作出错");
			e.printStackTrace();

		}

	}

	/**
	 * 删除文件
	 * @param filePathAndName
     */
	public static void delFile(String filePathAndName) {
		try {
			String filePath = filePathAndName;
			filePath = filePath.toString();
			File myDelFile = new File(filePath);
			myDelFile.delete();

		} catch (Exception e) {
			System.err.println("删除文件操作出错");
			e.printStackTrace();

		}

	}

	/**
	 * 删除文件夹
	 * @param folderPath
     */
	public static void delFolder(String folderPath) {
		try {
			delAllFile(folderPath); // 删除完里面所有内容
			String filePath = folderPath;
			filePath = filePath.toString();
			File myFilePath = new File(filePath);
			myFilePath.delete(); // 删除空文件夹

		} catch (Exception e) {
			System.err.println("删除文件夹操作出错");
			e.printStackTrace();

		}

	}

	/**
	 * 删除文件夹里面的所有文件
	 * 
	 * @param path
	 *            String 文件夹路径 如 c:/fqf
	 */
	public static void delAllFile(String path) {
		File file = new File(path);
		if (!file.exists()) {
			return;
		}
		if (!file.isDirectory()) {
			return;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				delAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文件
				delFolder(path + "/" + tempList[i]);// 再删除空文件夹
			}
		}
	}

	/**
	 * 复制单个文件
	 * 
	 * @param oldPath
	 *            String 原文件路径 如：c:/fqf.txt
	 * @param newPath
	 *            String 复制后路径 如：f:/fqf.txt
	 * @return boolean
	 * @throws IOException
	 */
	public static void copyFile(String oldPath, String newPath)
			throws IOException {
		InputStream inStream = null;
		FileOutputStream fs = null;
		try {
			int bytesum = 0;
			int byteread = 0;
			File oldfile = new File(oldPath);
			if (oldfile.exists()) { // 文件存在时
				inStream = new FileInputStream(oldPath); // 读入原文件
				fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[10240];
				while ((byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread; // 字节数 文件大小
					System.out.println(bytesum);
					fs.write(buffer, 0, byteread);
				}
				fs.close();
				inStream.close();
			}
		} catch (Exception e) {
			System.err.println("复制单个文件操作出错" + e.getMessage());
			e.printStackTrace();
			if (fs != null) {
				fs.close();
			}
			if (inStream != null) {
				inStream.close();
			}
		}

	}

	/**
	 * 复制整个文件夹内容
	 * 
	 * @param oldPath
	 *            String 原文件路径 如：c:/fqf
	 * @param newPath
	 *            String 复制后路径 如：f:/fqf/ff
	 * @return boolean
	 */
	public static void copyFolder(String oldPath, String newPath) {

		try {
			(new File(newPath)).mkdirs(); // 如果文件夹不存在 则建立新文件夹
			File a = new File(oldPath);
			String[] file = a.list();
			File temp = null;
			for (int i = 0; i < file.length; i++) {
				if (oldPath.endsWith(File.separator)) {
					temp = new File(oldPath + file[i]);
				} else {
					temp = new File(oldPath + File.separator + file[i]);
				}

				if (temp.isFile()) {
					FileInputStream input = new FileInputStream(temp);
					FileOutputStream output = new FileOutputStream(newPath
							+ "/" + (temp.getName()).toString());
					byte[] b = new byte[1024 * 5];
					int len;
					while ((len = input.read(b)) != -1) {
						output.write(b, 0, len);
					}
					output.flush();
					output.close();
					input.close();
				}
				if (temp.isDirectory()) {// 如果是子文件夹
					copyFolder(oldPath + "/" + file[i], newPath + "/" + file[i]);
				}
			}
		} catch (Exception e) {
			System.err.println("复制整个文件夹内容操作出错");
			e.printStackTrace();

		}

	}

	/**
	 * 文件拷贝
	 * 
	 * @param from
	 *            源文件
	 * @param to
	 *            目标文件
	 * @return 拷贝是否成功
	 */
	public static boolean copyFile(InputStream from, OutputStream to) {
		try {
			byte bt[] = new byte[1024];
			int c;
			while ((c = from.read(bt)) > 0) {
				to.write(bt, 0, c);
			}
			from.close();
			to.close();
			return true;

		} catch (Exception ex) {
			return false;
		}
	}

	/**
	 * 改名
	 * @param oldName
	 * @param newName
     */
	public static void renameFile(String oldName, String newName) {
		try {
			File oldfile = new File(oldName);
			if (oldfile.exists()) { // 文件存在时
				oldfile.renameTo(new File(newName));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 移动文件到指定目录
	 * 
	 * @param oldPath
	 *            String 如：c:/fqf.txt
	 * @param newPath
	 *            String 如：d:/fqf.txt
	 * @throws IOException
	 */
	public static void moveFile(String oldPath, String newPath)
			throws IOException {
		copyFile(oldPath, newPath);
		delFile(oldPath);
	}

	/**
	 * 移动文件到指定目录
	 * 
	 * @param oldPath
	 *            String 如：c:/fqf.txt
	 * @param newPath
	 *            String 如：d:/fqf.txt
	 */
	public static void moveFolder(String oldPath, String newPath) {
		copyFolder(oldPath, newPath);
		delFolder(oldPath);
	}

	/**
	 * 删除日志文件
	 * 
	 * @param sDir
	 *            目录
	 * @param regularMask
	 *            删除文件特征，日志以Log开头
	 * @param lTimeBefore
	 *            时间间隔
	 * @throws ParseException
	 */
	public static void deleteLog(String sDir, String regularMask,
								 long lTimeBefore) throws ParseException {
		File file = new File(sDir);
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date dtNowTime = df.parse(df.format(new Date()));
		if (file.exists()) {
			if (file.isDirectory()) {
				File files[] = file.listFiles();
				int iCount = files.length;
				while (iCount > 0) {
					String sFileName = files[iCount - 1].getName();
					String[] sSubFileName = sFileName.split("_");
					if (sSubFileName[0].equals(regularMask.trim())) {
						long lTime = dtNowTime.getTime()
								- files[iCount - 1].lastModified();
						if (lTimeBefore <= lTime) {
							files[iCount - 1].delete();
						}
					}
					iCount--;
				}
			}
		}
	}

	/**
	 * 获取文件夹里用过滤条件过滤后的文件名，大小写敏感 如：getConfigFilterFile("D:/",
	 * "*.jpg|*.png|*.mp4")
	 * 
	 * @param path
	 *            文件夹路径
	 * @param filterStr
	 *            过滤条件
	 * @return 满足条件的文件名数组
	 */
	public static String[] getConfigFilterFile(String path,
											   final String filterStr) {
		File folder = new File(path);
		FilenameFilter filenameFilter = new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				// TODO Auto-generated method stub
				return name.matches(switchFilterStrFromWin(filterStr));
			}
		};
		return folder.list(filenameFilter);
	}

	/**
	 * 判断是否跟过滤条件匹配 如：isMatchedFile("tb_area.txt", "tb_*.txt")
	 * 
	 * @param fileName
	 *            文件名
	 * @param filterStr
	 *            过滤条件
	 * @return 是否满足
	 */
	public static boolean isMatchedFile(String fileName, String filterStr) {
		return fileName.matches(switchFilterStrFromWin(filterStr));
	}

	/**
	 * 转换window风格下的过滤条件成java的正则表达式
	 * 如：switchFilterStrFromWin("*.txt");返回正则表达式"\S*\.txt"
	 * 
	 * @param filterStr
	 *            过滤条件
	 * @return 转换后的正则表达式
	 */
	public static String switchFilterStrFromWin(String filterStr) {
		return filterStr.replace("*", "\\S*").replace(".", "\\.");
	}

	/**
	 * 写文件
	 * 
	 * @param fileName
	 *            文件名
	 * @param info
	 *            写的内容
	 * @param append
	 *            是否添加
	 * @param encode
	 *            编码
	 * @return 是否成功
	 */
	public static boolean writeFile(String fileName, String info,
									boolean append, String encode) {
		try {
			FileOutputStream fout = new FileOutputStream(fileName, append);
			byte[] bytes = info.getBytes(encode);
			fout.write(bytes);
			fout.close();
			return true;
		} catch (Exception err) {
		}
		return false;
	}


	/**
	 * 获得某目录(不含子目录)下文件名符合正则表达式的文件总数
	 * 
	 * @param dir
	 *            目录
	 * @param regularMask
	 *            正则表达式
	 * @return 文件名符合正则表达式的文件数
	 */
	public static int getFileCount(String dir, String regularMask) {
		int count = 0;

		File file = new File(dir);
		File[] files = file.listFiles();
		if (files == null)
			return count;
		Pattern p = Pattern.compile(regularMask.toLowerCase(Locale.US));
		for (int j = 0; j < files.length; ++j) {
			if (files[j].isDirectory()) {
			} else {
				Matcher fMatcher = p.matcher(files[j].getName().toLowerCase(
						Locale.US));
				if (fMatcher.matches()) {
					++count;
				}
			}
		}
		return count;
	}

	/**
	 * 获得某目录(不含子目录)下文件名符合正则表达式的其中一个文件的文件名
	 * 
	 * @param dir
	 *            目录
	 * @param regularMask
	 *            正则表达式
	 * @return 文件名符合正则表达式的其中一个文件的文件名
	 */
	public static String getFileName(String dir, String regularMask) {
		File file = new File(dir);
		File[] files = file.listFiles();
		if (files == null)
			return "";
		Pattern p = Pattern.compile(regularMask.toLowerCase(Locale.US));
		for (int j = 0; j < files.length; ++j) {
			if (files[j].isDirectory()) {
			} else {
				Matcher fMatcher = p.matcher(files[j].getName().toLowerCase(
						Locale.US));
				if (fMatcher.matches()) {
					return files[j].getName();
				}
			}
		}
		return "";
	}

	private static boolean deleteFiles(File file, Pattern p,
									   boolean includeSubDirFile, boolean delDir, boolean delBefore,
									   long timeBefore) {
		boolean bIsBlank = true;
		// int count=0;
		if (file.exists())// 判断文件是否存在
		{
			if (file.isFile())// 判断是否是文件
			{

				if (delBefore) {
					if (file.lastModified() < timeBefore) {
						Matcher fMatcher = p.matcher(file.getName()
								.toLowerCase(Locale.US));
						if (fMatcher.matches()) {
							// Log.i("test", file.getName());
							file.delete();
							// ++count;
						} else
							bIsBlank = false;
					} else
						bIsBlank = false;
				} else {
					Matcher fMatcher = p.matcher(file.getName().toLowerCase(
							Locale.US));
					if (fMatcher.matches()) {
						file.delete();
						// Log.i("test", file.getName());
						// ++count;
					} else
						bIsBlank = false;
				}
			} else if (file.isDirectory())// 否则如果它是一个目录
			{
				if (includeSubDirFile) {
					File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
					for (int i = 0; i < files.length; i++) {
						// 遍历目录下所有的文件
						// count=count+
						bIsBlank = deleteFiles(files[i], p, includeSubDirFile,
								delDir, delBefore, timeBefore) && bIsBlank; // 把每个文件
																			// 用这个方法进行迭代
					}
				} else
					bIsBlank = false;

				if (delDir && bIsBlank) {
					file.delete();
					// Log.i("test", file.getName());
					// ++count;
				}
			}
		}
		// return count;
		return bIsBlank;
	}

	/**
	 * 删除某目录下文件名符合正则表达式的文件
	 * 
	 * @param dir
	 *            目录
	 * @param regularMask
	 *            正则表达式
	 * @param includeSubDirFile
	 *            是否包含子目录
	 * @param delDir
	 *            如果子目录为空，是否同时删除子目录
	 * @param delBefore
	 *            是否只删除修改时间在某时间之前的文件
	 * @param timeBefore
	 *            修改时间
	 */
	public static void deleteFiles(String dir, String regularMask,
								   boolean includeSubDirFile, boolean delDir, boolean delBefore,
								   long timeBefore) {
		Pattern p = Pattern.compile(regularMask.toLowerCase(Locale.US));
		File file = new File(dir);
		deleteFiles(file, p, includeSubDirFile, delDir, delBefore, timeBefore);
	}

	/**
	 * 读文件
	 * 
	 * @param fileName
	 *            文件名
	 * @return 文件内容
	 */
	public static byte[] readFile(String fileName) {
		try {
			File file = new File(fileName);
			if (!file.exists())
				return null;
			if (file.isDirectory())
				return null;

			FileInputStream fis = new FileInputStream(file);
			int length = fis.available();
			byte[] buffer = new byte[length];
			fis.read(buffer);
			fis.close();
			return buffer;
		} catch (Exception err) {
			return null;
		}
	}


	/**
	 * 获取文件修改时间
	 * 
	 * @param fileStr
	 *            文件名路径 + 文件名
	 * @return 时间字符串
	 */
	public static String getFileLastModifyTime(String fileStr) {
		File file = new File(fileStr);
		Calendar cal = Calendar.getInstance();
		long curTime = cal.getTimeInMillis();
		cal.setTimeInMillis(file.lastModified());
		@SuppressWarnings("deprecation")
		String timeStr = cal.getTime().toLocaleString();
		cal.setTimeInMillis(curTime);
		return timeStr;
	}

	/**
	 * 简单的文件选择器
	 * 
	 * @author LiuWu
	 * 
	 */
	public static class OpenFileView extends LinearLayout implements
			OnItemClickListener {
		private Context Owner;
		private String CurrentPath;
		private Pattern regMask;
		private Vector<String> FileNames;
		private Vector<String> ShowNames;
		private TextView[] ShowTexts;
		private int SelectedIndex;
		private ListView listView;
		private TextView Title;
		public String FileName;

		public OpenFileView(Context context, String Path, String Name,
							String regularMask) {
			super(context);
			init(context, Path, Name, regularMask);
		}

		public OpenFileView(Context context, String fileName, String regularMask) {
			super(context);

			String Path = new File(fileName).getParent();
			if (!Path.equals("/"))
				Path = Path + "/";
			String Name = new File(fileName).getName();

			init(context, Path, Name, regularMask);
		}

		private void init(Context context, String Path, String Name,
						  String regularMask) {
			setOrientation(VERTICAL);
			String fName = "";
			try {
				Title = new TextView(context);
				listView = new ListView(context);
				this.addView(Title);
				this.addView(listView);
				SelectedIndex = -1;
				Owner = context;
				CurrentPath = "/";
				FileNames = new Vector<String>();
				ShowNames = new Vector<String>();
				regMask = Pattern.compile(regularMask.toLowerCase(Locale.US));

				File file = new File(Path);
				if (!file.exists())
					return;
				if (!file.isDirectory())
					return;
				CurrentPath = Path;
				file = new File(Path + Name);
				if (!file.exists())
					return;
				if (!file.isFile())
					return;
				fName = Name;
			} finally {
				refresh(fName);
				listView.setOnItemClickListener(this);
			}
		}

		private class MyAdapter extends ArrayAdapter<String> {
			public MyAdapter(final Context context,
					final int textViewResourceId, final String[] objects) {
				super(context, textViewResourceId, objects);
			}

			public int getCount() {
				// TODO Auto-generated method stub
				return ShowNames.size();
			}

			public String getItem(int arg0) {
				// TODO Auto-generated method stub
				return ShowNames.get(arg0);
			}

			public long getItemId(int arg0) {
				// TODO Auto-generated method stub
				return 0;
			}

			public View getView(int position, View convertView, ViewGroup parent) {
				if (ShowTexts[position] == null) {
					LayoutInflater inflater = LayoutInflater.from(Owner);
					View group = inflater.inflate(
							android.R.layout.simple_list_item_1, parent, false);

					ShowTexts[position] = (TextView) group
							.findViewById(android.R.id.text1);
					if (new File(FileNames.get(position)).isDirectory()) {
						ShowTexts[position].setTextColor(Color.GRAY);
					}
					ShowTexts[position].setText(ShowNames.get(position));
				}

				TextView tv = ShowTexts[position];// new
													// TextView(Owner);//(TextView)
													// group.findViewById(android.R.id.text1);//new
													// TextView(Owner);
				if (position == SelectedIndex) {
					tv.setBackgroundColor(Color.BLUE);
				} else {
					tv.setBackgroundColor(Color.TRANSPARENT);
				}

				return tv;
			}
		}

		private void refresh(String Name) {
			SelectedIndex = -1;
			FileName = null;
			FileNames.clear();
			ShowNames.clear();
			File file = new File(CurrentPath);
			if (file.getParent() != null) {
				String str = file.getParent();
				if (str.equals("/"))
					FileNames.add(str);
				else
					FileNames.add(str + "/");
				ShowNames.add("上一级");
			}

			File files[] = file.listFiles();
			if (files != null)
				for (int i = 0; i < files.length; i++) {
					if (files[i].isDirectory()) {
						FileNames.add(files[i].getPath() + "/");
						ShowNames.add(files[i].getName() + "/");
					} else {
						Matcher fMatcher = regMask.matcher(file.getName()
								.toLowerCase(Locale.US));
						if (fMatcher.matches()) {
							FileNames.add(CurrentPath + files[i].getName());
							ShowNames.add(files[i].getName());
							if (Name.equals(files[i].getName())) {
								FileName = CurrentPath + files[i].getName();
								SelectedIndex = FileNames.size() - 1;
							}
						}
					}
				}

			ShowTexts = new TextView[ShowNames.size()];
			String[] items = new String[ShowNames.size()];
			for (int i = 0; i < ShowNames.size(); ++i) {
				items[i] = ShowNames.get(i);
			}

			listView.setAdapter(new MyAdapter(Owner,
					android.R.layout.simple_list_item_1, items));
			// listView.setSelector(Color.TRANSPARENT);
			// listView.setBackgroundColor(Color.GRAY);
			// listView.setChoiceMode(CHOICE_MODE_SINGLE);
			if (SelectedIndex >= 0) {
				listView.setSelection(SelectedIndex);
				// listView.getChildAt(SelectedIndex).setBackgroundColor(Color.BLUE);
			}
			Title.setText(CurrentPath);
		}

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
								long id) {
			if (position >= FileNames.size())
				return;
			if (position < 0)
				return;
			if (new File(FileNames.get(position)).isDirectory()) {
				CurrentPath = FileNames.get(position);
				refresh("");
			} else {
				// if((SelectedIndex>=0) &&
				// (listView.getChildAt(SelectedIndex)!=null))listView.getChildAt(SelectedIndex).setBackgroundColor(Color.TRANSPARENT);
				if ((SelectedIndex >= 0) && (ShowTexts[SelectedIndex] != null))
					ShowTexts[SelectedIndex]
							.setBackgroundColor(Color.TRANSPARENT);
				SelectedIndex = position;
				FileName = FileNames.get(SelectedIndex);

				if (ShowTexts[SelectedIndex] != null)
					ShowTexts[SelectedIndex].setBackgroundColor(Color.BLUE);
				// listView.setSelection(SelectedIndex);
				// if(listView.getChildAt(SelectedIndex)!=null)listView.getChildAt(SelectedIndex).setBackgroundColor(Color.BLUE);
			}
		}
	}
	
	/**
	 * 写程序日志
	 * TODO(这里用一句话描述这个方法的作用)
	 * @Title:writeLog
	 * @param fileName
	 * @param info 
	 *
	 * @author yudapei
	 */
	public static void writeLog(String fileName, String info) {
		Date dt = new Date();
		fileName = fileName + "log_" + DateUtil.dateToStr(dt, "yyyyMMdd") + ".txt";
		info = DateUtil.dateToStr(dt, "HH:mm:ss.")
				+ String.format("%03d ", dt.getTime() % 1000) + info + "\r\n";
		
		FileUtil.writeFile(fileName, info, true, "GB2312");
	}

}

/*
 *  Lordroid One Deodexer To Rule Them All
 * 
 *  Copyright 2016 Rachid Boudjelida <rachidboudjelida@gmail.com>
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package deodex.tools;

import java.awt.Component;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import deodex.R;
import deodex.S;
import deodex.SessionCfg;
import deodex.controlers.LoggerPan;

/**
 * 
 * @author lord-ralf-adolf
 *
 */
public class FilesUtils {

	/**
	 * 
	 * @param input
	 *            the input file to be copied
	 * @param dest
	 *            the destination file
	 * @return true only if the file was copied
	 */
	public static boolean copyFile(File input, File dest) {
		// making sure the path is there and writable !
		dest.getParentFile().mkdirs();
		// dest.delete();
		Logger.appendLog("[FilesUtils][I] copying " + input.getAbsolutePath() + " to " + dest.getAbsolutePath());
		if (dest.getParentFile().exists()) { // if the parent doesn't exist then
												// don't bother copy

			try {

				dest.delete();

				InputStream is = new FileInputStream(input);
				OutputStream out = new FileOutputStream(dest);
				byte[] buffer = new byte[32768];
				int len;
				while ((len = is.read(buffer)) != -1) {
					out.write(buffer, 0, len);
				}
				is.close();
				out.close();

			} catch (FileNotFoundException e) {
				e.printStackTrace();
				Logger.appendLog("[FilesUtils][EX]" + e.getStackTrace());
			} catch (IOException e) {
				e.printStackTrace();
				Logger.appendLog("[FilesUtils][EX]" + e.getStackTrace());
			}
		} else {
			return false;
		}
		Logger.appendLog("[FilesUtils][I] copy of " + input.getAbsolutePath() + " to " + dest.getAbsolutePath()
				+ " successed ? " + dest.exists());
		return dest.exists();
	}

	/**
	 * 
	 * @param in
	 *            InputStream
	 * @param dest
	 *            destination file 
	 * @return true if the inputStream was saved to the file
	 */
	public static boolean copyFile(InputStream in, File dest) {
		dest.mkdirs();
		dest.delete();
		if (dest.getParentFile().exists()) {
			try {
				OutputStream out = new FileOutputStream(dest);
				byte[] buffer = new byte[32768];
				int len;
				while ((len = in.read(buffer)) != -1) {
					out.write(buffer, 0, len);
				}
				in.close();
				out.close();

			} catch (FileNotFoundException e) {
				e.printStackTrace();
				Logger.appendLog("[FilesUtils][EX]" + e.getStackTrace());
			} catch (IOException e) {
				e.printStackTrace();
				Logger.appendLog("[FilesUtils][EX]" + e.getStackTrace());
			}
		} else {
			return false;
		}
		return dest.exists();
	}

	public static boolean copyFileRecurcively(File in, File out) {
		boolean status = true;
		if (in.isDirectory()) {
			out.mkdir();
			File[] list = in.listFiles();
			for (File f : list) {
				status = status
						&& copyFileRecurcively(f, new File(out.getAbsolutePath() + File.separator + f.getName()));
			}
		} else {
			status = status && copyFile(in, out);
		}

		return out.exists() && status;
	}

	public static void deleteFiles(ArrayList<File> files) {
		if (files != null && files.size() > 0)
			for (File f : files) {
				if (f.isFile()) {
					Logger.appendLog("[FilesUtils][I] deleting " + f.getAbsolutePath());
					f.delete();
				}
			}

	}

	/**
	 * deletes recursively a folder (USE WITH CARE)
	 * 
	 * @param f
	 *            folder to be deleted
	 * @return true only if the folder and all it's content was deleted
	 */
	public static boolean deleteRecursively(File f) {
		boolean done = false;
		if (f.isFile()) {
			f.delete();
			return true;
		}
		if (f.isDirectory()) {
			File[] list = f.listFiles();
			if (list.length < 0) {
				Logger.appendLog("[FilesUtils][I] deleting " + f.getAbsolutePath());
				return f.delete();
			} else {

				for (File file : list) {
					deleteRecursively(file);
				}

				return f.delete();
			}

		}
		return done;
	}

	/**
	 * cleans the given folder from all it's unpty folders
	 * 
	 * @param folder
	 *            the folder to be cleaned
	 */
	public static void deleteUmptyFoldersInFolder(File folder) {
		if (folder.isFile())
			return;
		if (folder.listFiles() == null || folder.listFiles().length <= 0) {
			folder.delete();
		} else {
			for (File f : folder.listFiles()) {
				if (f.isDirectory())
					deleteUmptyFoldersInFolder(f);
			}
		}
		if (folder.listFiles() == null || folder.listFiles().length <= 0) {
			Logger.appendLog("[FilesUtils][I] deleting because it is empty " + folder.getAbsolutePath());
			folder.delete();
		}
	}

	/**
	 * 
	 * @param folder
	 *            the folder to be searched
	 * @return the count of odex files
	 */
	public static int getOdexCount(File folder) {
		int x = 0;
		if (folder.exists()) {
			x = ArrayUtils.deletedupricates(FilesUtils.searchrecursively(folder, S.ODEX_EXT)).size();
			x = x + ArrayUtils.deletedupricates(FilesUtils.searchrecursively(folder, S.COMP_ODEX_EXT)).size();
			x = x + ArrayUtils.deletedupricates(FilesUtils.searchrecursively(folder, S.COMP_GZ_ODEX_EXT)).size();
		}
		return x;
	}

	/**
	 * receives a systemFolder and determines it's arch
	 * 
	 * @param systemFolder
	 * @return a string with the arch name
	 */
	public static String getRomArch(File systemFolder) {
		File frameworkFolder = new File(systemFolder.getAbsolutePath() + File.separator + S.SYSTEM_FRAMEWORK);
		File[] list = frameworkFolder.listFiles();

		for (String str : S.ARCH) {
			for (File f : list) {
				if (f.isDirectory()) {
					if (str.equals(f.getName())) {
						return str;
					}
				}
			}

		}
		return "null";
	}

	/**
	 * Tests if the given folder is a valid system folder
	 * 
	 * @param systemFolder
	 *            the system folder to be tested
	 * @param log
	 *            the LoggerPan were all the logs will be sent
	 * @return true only if the system folder is a valid one
	 */
	public static boolean isAValideSystemDir(File systemFolder, LoggerPan log) {

		// first we check if the build.prop exists if not we can't determine sdk
		// we abort !
		if (!new File(systemFolder.getAbsolutePath() + File.separator + S.SYSTEM_BUILD_PROP).exists()) {
			log.addLog(R.getString(S.LOG_ERROR) + R.getString(S.LOG_NO_BUILD_PROP));
			return false;
		}

		int sdkLevel;

		try {
			sdkLevel = Integer.parseInt(PropReader.getProp(S.SDK_LEVEL_PROP,
					new File(systemFolder.getAbsolutePath() + File.separator + S.SYSTEM_BUILD_PROP)));
		} catch (Exception e) {
			for (StackTraceElement element : e.getStackTrace())
				Logger.appendLog("[FilesUtils][EX]" + element.toString());
			log.addLog(R.getString(S.LOG_ERROR) + R.getString(S.CANT_READ_SDK_LEVEL));
			return false;
		}

		boolean isapp = new File(systemFolder.getAbsolutePath() + File.separator + S.SYSTEM_APP).exists();
		boolean isprivApp = new File(systemFolder.getAbsolutePath() + File.separator + S.SYSTEM_PRIV_APP).exists();
		boolean isframwork = new File(systemFolder.getAbsolutePath() + File.separator + S.SYSTEM_FRAMEWORK).exists();
		if (!isapp && !isprivApp && !isframwork) {
			log.addLog(R.getString(S.LOG_ERROR) + R.getString(S.LOG_NOT_A_SYSTEM_FOLDER));
		}
		// is there /app
		if (isapp) {
			log.addLog(R.getString(S.LOG_INFO) + R.getString(S.LOG_SYSTEM_APP_FOUND));
		} else {
			log.addLog(R.getString(S.LOG_WARNING) + R.getString(S.LOG_SYSTEM_APP_NOT_FOUND));
		}
		// is there privz app api > 18 only
		if (sdkLevel > 18) {
			if (isprivApp) {
				log.addLog(R.getString(S.LOG_INFO) + R.getString("log.privapp.found"));
			} else {
				log.addLog(R.getString(S.LOG_WARNING) + R.getString("log.privapp.not.found"));
			}
		}

		if (isframwork) {

			log.addLog(R.getString(S.LOG_INFO) + R.getString("log.framework.found"));
		} else {
			log.addLog(R.getString(S.LOG_ERROR) + R.getString("log.framwork.not.found.error"));
			return false;

		}
		String arch = getRomArch(systemFolder);
		// is the odex have .xposed extention ? if there is we will rename all the odex files 
		// TODO may be there is more out there ?
		ArrayList<File> xposed = FilesUtils.searchrecursively(systemFolder, ".odex.xposed");
		if( !xposed.isEmpty()){
			for (File f : xposed){
				f.renameTo(new File(f.getAbsolutePath().substring(0, f.getAbsolutePath().lastIndexOf("."))));
			}
		}
		
		// can we detetect arch ?
		if (arch.equals("null") && sdkLevel > 20) {
			log.addLog(R.getString(S.LOG_ERROR) + R.getString("log.no.arch.detected"));
			int odexCount = getOdexCount(systemFolder);
			int bootcount = FilesUtils
					.searchExactFileNames(
							new File(systemFolder.getAbsolutePath() + File.separator + S.SYSTEM_FRAMEWORK), "boot.oat")
					.size();
			// To do externalize those
			try {
				if (odexCount <= 0) {
					// TODO is this good ? make some research !
					if (!log.getClass().equals(CmdLogger.class))
						JOptionPane.showMessageDialog((Component) log, R.getString("0000130"), R.getString("0000132"),
								JOptionPane.ERROR_MESSAGE);
					return false;
				} else if (bootcount <= 0) {
					// TODO is this good ? make some research !
					if (!log.getClass().equals(CmdLogger.class))
						JOptionPane.showMessageDialog((Component) log, R.getString("0000131"), R.getString("0000133"),
								JOptionPane.ERROR_MESSAGE);
					return false;
				}
			} catch (Exception e) {
				Logger.appendLog("[FilesUtils][EX]" + e.getStackTrace());
			}

		}

		// lets detect if the rom is have squashfs
		File appSquash = new File(systemFolder.getAbsolutePath() + File.separator + "odex.app.sqsh");
		File privAppSquash = new File(systemFolder.getAbsolutePath() + File.separator + "odex.priv-app.sqsh");
		File framSquash = new File(systemFolder.getAbsolutePath() + File.separator + "odex.framework.sqsh");

		boolean isSquash = false;
		if (appSquash.exists() || privAppSquash.exists() || framSquash.exists()) {
			log.addLog(R.getString(S.LOG_INFO) + R.getString("0000128"));
			isSquash = true;
			if (!UnsquashUtils.haveUnsquash()) {
				log.addLog(R.getString(S.LOG_ERROR) + R.getString("0000129"));
				return false;
			}
		}
		// is boot .oat there may be it's in squash file ? lets skip this check
		// if sqsh files were detected
		if (!framSquash.exists())
			if (sdkLevel > 20) {
				ArrayList<File> bootOat = FilesUtils.searchExactFileNames(
						new File(systemFolder.getAbsolutePath() + File.separator + S.SYSTEM_FRAMEWORK), "boot.oat");
				if (bootOat == null || bootOat.size() <= 0) {
					log.addLog(R.getString(S.LOG_ERROR) + R.getString("log.no.boot.oat"));
					return false;
				} else {
					SessionCfg.setBootOatFile(bootOat.get(0));
				}
			}
		// Session Settings set them
		SessionCfg.isSquash = isSquash;
		SessionCfg.setSdk(sdkLevel);
		log.addLog(R.getString(S.LOG_INFO) + R.getString("log.detected.sdk") + sdkLevel);
		SessionCfg.setArch(arch);
		if (sdkLevel > 20)
			log.addLog(R.getString(S.LOG_INFO) + R.getString("log.detected.arch") + arch);

		SessionCfg.setSystemFolder(systemFolder);
		// lets change the temp folder to the same location ,this way we can use
		// rename instead of copy/delete
		// means less IOs and less time NOTE: ROMs can have up to 2GO all apks
		// have to be copied from systemFolder
		// to temp and then copied back so that's 4GO at least !
		// lets assume
		// average users HDD speed is 50mb
		// 4*1024/50/60 = 1.365333333 minutes that's alot of time !
		S.setTempDir(systemFolder);
		log.addLog(R.getString(S.LOG_INFO) + R.getString("log.chosen.folder") + systemFolder);
		int apkCount = getOdexCount(new File(systemFolder.getAbsolutePath() + File.separator + S.SYSTEM_APP))
				+ getOdexCount(new File(systemFolder.getAbsolutePath() + File.separator + S.SYSTEM_PRIV_APP))
				+ (new File(systemFolder.getAbsolutePath() + "/" + "plugin").exists()
						? getOdexCount(new File(systemFolder.getAbsolutePath() + "/" + "plugin")) : 0)
				+ (new File(systemFolder.getAbsolutePath() + "/" + "vendor").exists()
						? getOdexCount(new File(systemFolder.getAbsolutePath() + "/" + "vendor")) : 0)
				+

		(new File(systemFolder.getAbsolutePath() + "/" + "data-app").exists()
				? getOdexCount(new File(systemFolder.getAbsolutePath() + "/" + "data-app")) : 0);
		int jarCounts = getOdexCount(new File(systemFolder.getAbsolutePath() + File.separator + S.SYSTEM_FRAMEWORK));
		if (jarCounts + apkCount <= 0 && !isSquash) {
			log.addLog(R.getString(S.LOG_INFO) + R.getString("no.odexFiles.wereFound"));
			return false;
		}
		if (!isSquash) {
			log.addLog(R.getString(S.LOG_INFO) + R.getString("log.there.is") + " " + apkCount + " apks "
					+ R.getString("log.to.be.deodexed"));
			log.addLog(R.getString(S.LOG_INFO) + R.getString("log.there.is") + " " + jarCounts + " jars "
					+ R.getString("log.to.be.deodexed"));
		} else {
			log.addLog(R.getString(S.LOG_INFO) + R.getString("0000127"));
			log.addLog(R.getString(S.LOG_INFO) + R.getString("0000126"));

		}
		if (new File(systemFolder.getAbsolutePath() + "/" + "plugin").exists()
				&& new File(systemFolder.getAbsolutePath() + "/" + "plugin").isDirectory()) {
			log.addLog(R.getString(S.LOG_INFO) + R.getString("0000125"));
		}
		if (new File(systemFolder.getAbsolutePath() + "/" + "vendor").exists()
				&& new File(systemFolder.getAbsolutePath() + "/" + "vendor").isDirectory()) {
			log.addLog(R.getString(S.LOG_INFO) + R.getString("0000124"));
		}
		if (new File(systemFolder.getAbsolutePath() + "/" + "data-app").exists()
				&& new File(systemFolder.getAbsolutePath() + "/" + "data-app").isDirectory()) {
			log.addLog(R.getString(S.LOG_INFO) + "/data-app folder detected it will be deodexed if necessary ...");
		}
		return true;
	}

	/**
	 * lists all files in a given folder
	 * 
	 * @param folder
	 *            the folder to list files from
	 * @return a list of all the files
	 */
	public static ArrayList<File> listAllFiles(File folder) {
		ArrayList<File> list = new ArrayList<File>();
		if (!folder.exists() || folder.listFiles() == null || folder.listFiles().length <= 0 || !folder.canRead()) {
			return list;
		}
		File[] listf = folder.listFiles();
		for (File f : listf) {
			if (f.isFile()) {
				list.add(f);
			} else {
				if (listAllFiles(f) != null)
					for (File f1 : listAllFiles(f)) {
						list.add(f1);
					}
			}
		}
		return list;
	}

	/**
	 * logs all the files from a given folder to stdIO and to the full main log
	 * file
	 * 
	 * @param folder
	 *            the folder to be logged
	 */
	public static void LogFilesListToFile(File folder) {
		String str = "System folder Files list :\n";
		File app = new File(folder.getAbsolutePath() + File.separator + "app");
		File privApp = new File(folder.getAbsolutePath() + File.separator + "priv-app");
		File framework = new File(folder.getAbsolutePath() + File.separator + "framework");

		if (app.exists())
			for (File f : listAllFiles(app)) {
				str = str + (f.getAbsolutePath().substring(app.getAbsolutePath().length() + 1)) + "\n";
			}
		if (privApp.exists())
			for (File f : listAllFiles(privApp)) {
				str = str + (f.getAbsolutePath().substring(privApp.getAbsolutePath().length() + 1)) + "\n";
			}
		if (framework.exists())
			for (File f : listAllFiles(framework)) {
				str = str + (f.getAbsolutePath().substring(framework.getAbsolutePath().length() + 1)) + "\n";
			}
		Logger.writeSystemFolderFiles("[FilesUtils][D]\n" + str);
	}

	public static boolean moveFile(File in, File dest) {
		boolean iscopied = copyFile(in, dest);
		if (!iscopied)
			return false;
		if (dest.exists())
			in.delete();

		return !in.exists();
	}

	/**
	 * search a folder for files with a given Name (full name match)
	 * 
	 * @param folder
	 *            the folder to be searched
	 * @param ext
	 *            the file name to be searched
	 * @return a list of all the matching files
	 */
	public static ArrayList<File> searchExactFileNames(File folder, String ext) {
		ArrayList<File> list = new ArrayList<File>();
		File[] files = folder.listFiles();
		for (File f : files) {
			if (f.isDirectory()) {
				for (File f1 : searchExactFileNames(f, ext)) {
					list.add(f1);
				}
			} else if (f.isFile() && f.getName().equals(ext)) {
				list.add(f);
			}
		}
		return list;
	}

	/**
	 * search a folder for files with a given file extension
	 * 
	 * @param folder
	 *            the folder to be searched
	 * @param ext
	 *            the extension query
	 * @return ArrayfilesList list of all the matching files
	 */
	public static ArrayList<File> searchrecursively(File folder, String ext) {
		ArrayList<File> list = new ArrayList<File>();
		File[] files = folder.listFiles();
		for (File f : files) {
			if (f.isDirectory()) {
				for (File f1 : searchrecursively(f, ext)) {
					list.add(f1);
				}
			} else if (f.isFile() && f.getName().endsWith(ext)) {
				list.add(f);
			}
		}

		return list;
	}
}

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

package deodex;

import java.io.File;
import java.util.ArrayList;

import deodex.tools.Logger;
import deodex.tools.Os;
import deodex.tools.PathUtils;
import deodex.tools.PropReader;
import deodex.tools.StringUtils;

/**
 * @author lord-ralf-adolf
 *
 */
public class Cfg {
	private static final String CFG_PATH = PathUtils.getExcutionPath() + File.separator + "cfg/app_config.rsx";
	private static final String LANG_FOLDER = PathUtils.getExcutionPath() + File.separator + "lang";
	private static final String SHOW_DEODEX_ALERT = "show.deodex.alert";
	private static final String SHOW_EXIT_ALERT = "show.exit.alert";
	private static final String SHOW_THREAD_ALERT = "show.thread.alert";
	private static final String MAX_JOBS_PROP = "max.job.prop";

	private static String currentLang;
	private static int showDeodexAlert = 1;
	private static int showExitAlert = 1;
	private static int showThreadAlert = 1;
	private static int maxJobs = Cfg.getIdealMaxThread();

	private static ArrayList<File> langFiles = new ArrayList<File>();
	private static ArrayList<String> availableLang = new ArrayList<String>();

	/**
	 * returns weither or not show the dialog to the user
	 * 
	 * @return showDeodexAlert
	 */
	public static boolean doShowDeodexAlert() {
		return showDeodexAlert == 1;
	}

	/**
	 * returns weither or not show the dialog to the user
	 * 
	 * @return showExitAlert
	 */
	public static boolean doShowExitAlert() {
		return showExitAlert == 1;
	}

	/**
	 * 
	 * @return doShowThreadAlert 
	 */
	public static boolean doShowThreadAlert() {
		return showThreadAlert == 1;
	}

	/**
	 * 
	 * @return availableLangs the available language files 
	 */
	public static ArrayList<String> getAvailableLaunguages() {
		File langFolder = new File(LANG_FOLDER);
		File lang[] = langFolder.listFiles();

		langFiles = new ArrayList<File>();
		for (File f : lang) {
			String ext;
			if (f.isFile()) {
				String name = f.getName();
				ext = StringUtils.getLastchars(name, 4);
				if (ext.equals("prop"))
					langFiles.add(f);
			}

		}
		availableLang = new ArrayList<String>();

		for (File f : langFiles) {
			availableLang.add(PropReader.getProp(S.LANGUAGE_PROP, f));
		}

		return availableLang;

	}

	/**
	 * @return currentLang the String of the name of the current Language
	 */
	public static String getCurrentLang() {
		return currentLang;
	}

	/**
	 * 
	 * @return langFile the lang file to use for strings 
	 */
	public static File getLangFile() {
		File langFolder = new File(LANG_FOLDER);
		File lang[] = langFolder.listFiles();
		langFiles = new ArrayList<File>();
		for (File f : lang) {
			String ext;
			if (f.isFile()) {
				String name = f.getName();
				ext = StringUtils.getLastchars(name, 4);
				if (ext.equals("prop"))
					langFiles.add(f);
			}

		}
		availableLang = new ArrayList<String>();

		for (File f : langFiles) {
			availableLang.add(PropReader.getProp(S.LANGUAGE_PROP, f));
		}

		File tmp = null;
		if (currentLang == null)
			return new File("lang/en.prop");
		for (int i = 0; i < availableLang.size(); i++) {
			if (availableLang.get(i).equals(currentLang)) {
				tmp = langFiles.get(i);
				break;
			}
		}
		return tmp;
	}

	/**
	 * 
	 * @return maxJobs the max jobs to use
	 */
	public static int getMaxJobs() {
		return Cfg.maxJobs;
	}

	/**
	 * 
	 * @return OsName os name 
	 */
	public static String getOs() {
		if (Os.isLinux()) {
			return S.LINUX;
		} else if (Os.isWindows()) {
			return S.WINDOWS;
		} else if (Os.isMac()) {
			return S.MAC;
		}
		return null;
	}

	/**
	 * 
	 * @return isfirstLaunch
	 * 						  returns the logical value of this question : is the application launching for the first time ?
	 */
	public static boolean isFirstLaunch() {
		return !new File(CFG_PATH).exists();
	}

	public static void readCfg() {
		currentLang = PropReader.getProp(S.CFG_CUR_LANG, new File(CFG_PATH));
		/**
		 * is previous version those values was not there don't force the user
		 * to use a new configuration instead lets try to catch prop not Found
		 * exception
		 */
		try {
			showDeodexAlert = Integer.parseInt(PropReader.getProp(SHOW_DEODEX_ALERT, new File(CFG_PATH)));
		} catch (Exception e) {
			Cfg.setShowDeodexAlert(true);
			Logger.writLog("[Cfg][EX]" + e.getStackTrace());
		}

		try {
			showExitAlert = Integer.parseInt(PropReader.getProp(SHOW_EXIT_ALERT, new File(CFG_PATH)));
		} catch (Exception e) {
			Cfg.setShowExitAlert(true);
			Logger.writLog("[Cfg][EX]" + e.getStackTrace());
		}
		try {
			showThreadAlert = Integer.parseInt(PropReader.getProp(SHOW_THREAD_ALERT, new File(CFG_PATH)));
		} catch (Exception e) {
			Cfg.setShowThreadAlert(true);
			Logger.writLog("[Cfg][EX]" + e.getStackTrace());
		}
		try {
			Cfg.maxJobs = Integer.parseInt(PropReader.getProp(MAX_JOBS_PROP, new File(CFG_PATH)));
		} catch (Exception e) {
			Cfg.setMaxJobs(Cfg.getIdealMaxThread());
			Logger.writLog("[Cfg][EX]" + e.getStackTrace());
		}
	}

	/**
	 * @param currentLang
	 *            the currentLang to set
	 */
	public static void setCurrentLang(String currentLang) {
		Cfg.currentLang = currentLang;
	}

	public static void setMaxJobs(int i) {
		Cfg.maxJobs = i;
		PropReader.writeProp(MAX_JOBS_PROP, "" + i, new File(CFG_PATH));
	}

	/**
	 * set weither or not show a dialog to the user
	 * 
	 * @param show
	 */
	public static void setShowDeodexAlert(boolean show) {
		if (show) {
			showDeodexAlert = 1;
		} else {
			showDeodexAlert = 0;
		}
		PropReader.writeProp(SHOW_DEODEX_ALERT, "" + showDeodexAlert, new File(Cfg.CFG_PATH));
	}

	/**
	 * set weither or not show a dialog to the user
	 * 
	 * @param show
	 */
	public static void setShowExitAlert(boolean show) {
		if (show) {
			showExitAlert = 1;
		} else {
			showExitAlert = 0;
		}
		PropReader.writeProp(SHOW_EXIT_ALERT, "" + showExitAlert, new File(Cfg.CFG_PATH));

	}

	/**
	 * set weither or not show a dialog to the user
	 * 
	 * @param show
	 */
	public static void setShowThreadAlert(boolean show) {
		if (show) {
			showThreadAlert = 1;
		} else {
			showThreadAlert = 0;
		}
		PropReader.writeProp(SHOW_THREAD_ALERT, "" + showThreadAlert, new File(Cfg.CFG_PATH));
	}

	/**
	 * 
	 * writes the cfg file to disque 
	 */

	public static void writeCfgFile() {
		PropReader.writeProp(S.CFG_CUR_LANG, currentLang, new File(CFG_PATH));
		PropReader.writeProp(S.CFG_HOST_OS, Cfg.getOs(), new File(CFG_PATH));
		PropReader.writeProp(SHOW_DEODEX_ALERT, "" + showDeodexAlert, new File(Cfg.CFG_PATH));
		PropReader.writeProp(MAX_JOBS_PROP, ""+Cfg.getMaxJobs(), new File(Cfg.CFG_PATH));
	}

	/**
	 * 
	 * @return defaultMaxThread max thread count 
	 */
	public static int getIdealMaxThread(){
		int max = 2;
		int cpu = 2;
		try {
			cpu = HostInfo.availableCpus();
		} catch (Exception e){
			cpu = 2;
		}
		
		if( cpu == 1){
			max = 1;
		} else if (cpu == 2){
			max = 2;
		} else if(cpu > 2){
			max = 4;
		}
		return max;
	}
}
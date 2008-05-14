package cz.polous.andaria;

import java.io.File;
import java.io.IOException;


/*******************************************************************************
 * Provide operating system specific methods
 * 
 * Changlog:
 *      [0.2]   unrarPatchItem added
 * 
 * @author  Martin Polehla (andaria_patcher@polous.cz)
 * @version 0.2
 ******************************************************************************/
abstract class OperatingSystem {

    private final String local_storage = System.getProperty("java.io.tmpdir") + File.separator + "AndariaPatcher";
    private final String remote_storage = "http://space.andaria.net/data/Andaria_Soubory";
    private final String about_url = "http://strazci.andaria.net/patcher/java.php";
    private final String news_url = "http://www.andaria.net/novinky_updater.php";
    private final String debug_log = "0";
    private final String filelist_url = "http://www.andaria.net/admin/patcher.txt";
    private final String[] unrarPatchItem = {"unrar.exe", "Unrar", "12.4.2008, 12:00", "15d03a204e1781629fdb463cb1f36a0d", "1", "117112", "313d", "Program pro rozbaleni rar archivu."};//private final String[]
    private static Log log;

    public String[] getUnrarPatchItem() {
        return unrarPatchItem;
    }

    public String getAbout_url() {
        return about_url;
    }

    public String getDebug_log() {
        return debug_log;
    }

    public String getFilelist_url() {
        return filelist_url;
    }

    public String getLocal_storage() {
        return local_storage;
    }

    public String getNews_url() {
        return news_url;
    }

    public String getRemote_storage() {
        return remote_storage;
    }
    //private static final String[] defaultSettings = {"", "unrar_command", "ultima_onine_path", "local_storage", "remote_storage", "about_url", "news_url", "debug_log", "filelist_url"};
    abstract String getRun_command();

    abstract String getUltima_online_path();

    abstract String getUnrar_path();

    abstract void downloadUnrar();

    /***************************************************************************
     * Creates a new instance of current operating system
     * @return OS object with current os specific methods
     **************************************************************************/
    static OperatingSystem createOperatingSystemInstance() {
        if (System.getProperty("os.name").contentEquals("Linux")) {
            return new LinuxOS();
        }
        return new WindowsOS();
    }

    /***************************************************************************
     * Creates a new instance of OperatingSystem
     **************************************************************************/
    public OperatingSystem() {
        log = new Log(this);
    }

    /***************************************************************************
     * and getFileExistingInstance(File f) wrapper
     * @return physical existing file instance
     * @see #getFileExistingInstance(File f)
     **************************************************************************/
    public static File getExistingFileInstance(String fn) {
        return getFileExistingInstance(new File(fn));
    }

    /***************************************************************************
     * Create a File instance and gurantee file existing
     * @return physical existing file instance
     **************************************************************************/
    public static File getFileExistingInstance(File f) {
        if (f.exists()) {
            return f;
        } else {
            try {
                if (f.isDirectory()) {
                    f.mkdirs();
                } else {
                    f.getParentFile().mkdirs();
                    f.createNewFile();
                }
            } catch (IOException e) {
                //log.addEx(e);
                log.addLine("Nemuzu vytvorit soubor (adresar): " + f.getAbsolutePath());
            }
        }
        return f;
    }

    public static void deleteUOFile(String subDir, String fileName) {

        File f = new File(Settings.getValue(Settings.ULTIMA_ONINE_PATH) + File.separator + subDir + File.separator + fileName);
        if (f.exists()) {
            f.delete();
        }

    }

    /***************************************************************************
     * @return XML cofiguration file patch
     **************************************************************************/
    abstract String getConfigPath();

    /***************************************************************************
     * @return os oriented patch script command
     **************************************************************************/
    abstract String[] getBatchExecCommand(File f);

    /***************************************************************************
     * TODO: Java unrar implementation (using unrar native library).
     * @param file to extract
     **************************************************************************/
    abstract void unrar(File file);

    /***************************************************************************
     * TODO: Unrar library inicialization
     **************************************************************************/
    abstract void unrarInit();
}
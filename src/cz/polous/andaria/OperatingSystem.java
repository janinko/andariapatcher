package cz.polous.andaria;

import java.io.File;
import java.io.IOException;

/*******************************************************************************
 * Provide operating system specific methods
 * 
 * @author  Martin Polehla (andaria_patcher@polous.cz)
 * @version 0.2
 ******************************************************************************/
abstract class OperatingSystem {

    private static Log log;

    abstract String getDefaultRunCommand();

    abstract String getDefaultRunCommand1();

    abstract String getDefaultRunCommand2();

    abstract String getUOPath();

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
    public File getExistingFileInstance(String fn) {
        return getFileExistingInstance(new File(fn));
    }

    /***************************************************************************
     * Create a File instance and gurantee file existing
     * @return physical existing file instance
     **************************************************************************/
    public File getFileExistingInstance(File f) {
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
                log.addLine("Nemůžu vytvořit soubor nebo adresář: " + f.getAbsolutePath());
            }
        }
        return f;
    }

    public void deleteUOFile(String subDir, String fileName) {

        File f = new File(Settings.getInstance().getValue(Settings.VALUES.ULTIMA_ONINE_PATH) + File.separator + subDir + File.separator + fileName);
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
}

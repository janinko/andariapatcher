package cz.polous.andaria;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.prefs.Preferences;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/*******************************************************************************
 *
 * @author p0l0us
 ******************************************************************************/
class WindowsOS extends OperatingSystem {

    private static final String unrar_path = "C:\\Program Files\\WinRAR\\unrar.exe";
//    private static final String ultima_online_path = getRegUoPath();
//    private static final String run_command = ultima_online_path + "\\AndariaClient.exe";
    private static final String regOriginFileName = "UO_Registry_Origin.reg";
    private static Log log;
    private String uoPath;

    static {
        log = new Log("WindowsOS");
    }
   // @Override
    @Override
    public String getRun_command() {
        return getUltima_online_path().concat("\\AndariaClient.exe");
    }
    //@Override
    @Override
    public String getUltima_online_path() {
        // If I had checked (or inicialized) uoPath before,
        // don't try again.
        if (uoPath != null) {
            return uoPath;
        }
        //   final int HKEY_LOCAL_MACHINE = 0x80000002;
        //   final int KEY_QUERY_VALUE = 1;
        final Preferences root = Preferences.systemRoot();
        final Class cl = root.getClass();
        //  final Method queryValue;
        final int KEY_READ = 0x20019;
        final String subKey = "SOFTWARE\\Origin Worlds Online\\Ultima Online\\1.0";

        // Class[] params = {int.class, byte[].class};


        try {

            Class[] parms1 = {byte[].class, int.class, int.class};
            final Method mOpenKey = cl.getDeclaredMethod("openKey", parms1);
            mOpenKey.setAccessible(true);

            Class[] parms2 = {int.class};
            final Method mCloseKey = cl.getDeclaredMethod("closeKey", parms2);
            mCloseKey.setAccessible(true);

            Class[] parms3 = {int.class, byte[].class};
            final Method mWinRegQueryValue = cl.getDeclaredMethod("WindowsRegQueryValueEx", parms3);
            mWinRegQueryValue.setAccessible(true);

            Object[] objects1 = {toByteArray(subKey), new Integer(KEY_READ), new Integer(KEY_READ)};
            Integer hSettings = (Integer) mOpenKey.invoke(root, objects1);

            Object[] objects2 = {hSettings, toByteArray("InstCDPath")};
            byte[] b = (byte[]) mWinRegQueryValue.invoke(root, objects2);
            String value = b != null ? new String(b).trim() : null;

            Object[] objects3 = {hSettings};
            mCloseKey.invoke(root, objects3);

            uoPath = value;
        } catch (InvocationTargetException e) {
            System.err.println(e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
        } catch (IllegalAccessException e) {
            System.err.println(e.getMessage());
        } catch (SecurityException e) {
            System.err.println(e.getMessage());
        } catch (NoSuchMethodException e) {
            System.err.println(e.getMessage());
        }


        if (uoPath == null || uoPath.isEmpty()) {
            Object[] opts = {"Obnovit", "Neobnovovat"};
            int obnov = JOptionPane.showOptionDialog(null, "Nemůžu najít registry UO Monday's Legacy v registrech windows.\nBud nemas nainstalovaou uo spravne, nebo je to rozbity. Zkus ultimu preinstalovat ultimu.\nPokud to nepomuze, napis p0l0usovi na foru andarie o pomoc. Přeješ si registry obnovit ručně ?", "Upozorneni !", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, opts, opts[0]);
            if (obnov == JOptionPane.YES_OPTION) {
                uoPath = Settings.getInstance().openFile("Vyber adresář s ultimou", "C:\\", JFileChooser.DIRECTORIES_ONLY);
                generateRegistryData(uoPath);
                return uoPath;
            }
            return "";
        }
        System.out.println("Rozpoznaný adresář s ultimou: ".concat(uoPath));
        return uoPath;
    }

    @Override
    String getUnrar_path() {
        return unrar_path;
    }

    @Override
    public void downloadUnrar() {
    }

    /***************************************************************************
     * Creates a new instance of WindowsOS
     **************************************************************************/
    WindowsOS() {
        super();
        System.err.println("OPERACNI SYSTEM WINDOWS");
    }

    /***************************************************************************
     * @return XML cofiguration file patch
     **************************************************************************/
    @Override
    public String getConfigPath() {
        return getUltima_online_path() + File.separator + "AndariaPatcherConfig.xml";
    }

    /***************************************************************************
     * @return operating system oriented patch script command
     **************************************************************************/
    @Override
    public String[] getBatchExecCommand(File f) {
        return new String[]{f.getAbsolutePath()};
    }

    /***************************************************************************
     * Convert a string to byte[] array.
     * @param str input string
     * @return byte array of a string
     **************************************************************************/
    private static byte[] toByteArray(String str) {
        byte[] result = new byte[str.length() + 1];
        for (int i = 0; i < str.length(); i++) {
            result[i] = (byte) str.charAt(i);
        }
        result[str.length()] = 0;
        return result;
    }

    public void renewWindowsRegistry() {
         String newpath = Settings.getInstance().openFile("Vyber adresář s ultimou", "C:\\", JFileChooser.DIRECTORIES_ONLY);
         if (newpath.isEmpty()) return;
         uoPath = newpath;
         generateRegistryData(uoPath);
    }
    /***************************************************************************
     * @return ultima online path from windows registers
     **************************************************************************/
    private String generateRegistryData(String uoPath) {
        String regData = ""; //new String("");

        regData = regData.concat("Windows Registry Editor Version 5.00\n\n");
        regData = regData.concat("[HKEY_LOCAL_MACHINE\\SOFTWARE\\Origin Worlds Online]\n");
        regData = regData.concat("\"HarvestStageNew\"=dword:00000000\n");
        regData = regData.concat("\"UniqueInstanceId\"=dword:49fec2ea\n\n");
        regData = regData.concat("[HKEY_LOCAL_MACHINE\\SOFTWARE\\Origin Worlds Online\\Ultima Online]\n\n");
        regData = regData.concat("[HKEY_LOCAL_MACHINE\\SOFTWARE\\Origin Worlds Online\\Ultima Online\\1.0]\n");
        regData = regData.concat("\"ExePath\"=\"").concat(uoPath.replace("\\", "\\\\")).concat("\\\\client.exe\"\n");
        regData = regData.concat("\"InstCDPath\"=\"").concat(uoPath.replace("\\", "\\\\")).concat("\"\n");
        regData = regData.concat("\"PatchExePath\"=\"").concat(uoPath.replace("\\", "\\\\")).concat("\\\\uopatch.exe\"\n");
        regData = regData.concat("\"StartExePath\"=\"").concat(uoPath.replace("\\", "\\\\")).concat("\\\\uo.exe\"\n");
        regData = regData.concat("\"Upgraded\"=\"Yes\"\n");

        regData = regData.concat("[HKEY_LOCAL_MACHINE\\SOFTWARE\\Origin Worlds Online\\Ultima Online Third Dawn]\n\n");

        regData = regData.concat("[HKEY_LOCAL_MACHINE\\SOFTWARE\\Origin Worlds Online\\Ultima Online Third Dawn\\1.0]\n");
        regData = regData.concat("\"ExePath\"=\"").concat(uoPath.replace("\\", "\\\\")).concat("\\\\client.exe\"\n");
        regData = regData.concat("\"InstCDPath\"=\"").concat(uoPath.replace("\\", "\\\\")).concat("\"\n");
        regData = regData.concat("\"PatchExePath\"=\"").concat(uoPath.replace("\\", "\\\\")).concat("\\\\uopatch.exe\"\n");
        regData = regData.concat("\"StartExePath\"=\"").concat(uoPath.replace("\\", "\\\\")).concat("\\\\uo.exe\"\n");
        regData = regData.concat("\"Upgraded\"=\"Yes\"\n");
        FileWriter fw;
        File f;
        try {
            f = getExistingFileInstance(uoPath.concat(File.separator).concat(regOriginFileName));
            if (f.exists() && f.canWrite()) {
                fw = new FileWriter(f);
                fw.write(regData);
                fw.close();

                try {
                    String[] cmd = {"cmd.exe", "/c", "regedit.exe /s \"".concat(f.getAbsolutePath()).concat("\"")};

                    log.addCmd(cmd);

                    Process proc = Runtime.getRuntime().exec(cmd);
                    try {
                        proc.waitFor();
                    } catch (InterruptedException ex) {
                        log.addEx(ex);
                    }
                    proc.destroy();
                } catch (IOException ex) {
                    log.addEx(ex);
                }
            }
        } catch (IOException ex) {
            log.addEx(ex);
        } finally {
            f = null;
        }

        return uoPath;

    }

    /***************************************************************************
     * 
     * @param file to extract
     **************************************************************************/
    @Override
    public void unrar(File file) {
        // TODO: Java unrar implementation (using unrar native library).
    }

    /***************************************************************************
     * TODO: Unrar library inicialization
     **************************************************************************/
    @Override
    public void unrarInit() {
        // TODO: Java unrar implementation (using unrar native library).
    }
}
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

    private static final String regOriginFileName = "UO_Registry_Origin.reg";
    private static final String regRazorFileName = "Razor_client.reg";
    private static final String CONFIG_FILENAME = "AndariaPatcherConfig.xml";
    private static Log log;
    private String uoPath;

    static {
        log = new Log("WindowsOS");
    }

    @Override
    public String getDefaultRunCommand() {
        return getUOPath().concat("\\AndariaClient.exe");
    }

    @Override
    public String getDefaultRunCommand1() {
        return "";
    }

    @Override
    public String getDefaultRunCommand2() {
        return "";
    }

    @Override
    public String getUOPath() {
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

        // ljk upravy 09.09.07
        if (uoPath == null || uoPath.isEmpty()) {
            //Object[] opts = {"Obnovit", "Neobnovovat","Ukázat patcheru cestu (nepracovat s registry)",};
            Object[] opts = {"Instalovat", "Opravit registry", "Nepracovat s registry",};
            int obnov = JOptionPane.showOptionDialog(null, "Nemůžu najít záznam UO Monday's Legacy v registrech windows.\nBuď nemáš UO ještě nainstalovanou nebo jen chybí v registrech. Nyní můžeš:\n1) spustit automatickou instalaci UO\n2) opravit registry tím, že mi řekneš kde UO máš\n3) pracovat bez registrů (vhodné pro Flash paměti).", "Upozornění !", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, opts, opts[0]);
            if (obnov == JOptionPane.YES_OPTION) {
                // this will enable autoinstall procedure.
                uoPath = Settings.getInstance().openFile("Vyber (vytvoř) adresář do kterého chceš UO instalovat.", "C:\\", JFileChooser.DIRECTORIES_ONLY);
                if (uoPath == null) {
                    return "";
                }
               
                Settings.setAutoInstall(Settings.AUTO_LEVELS.AUTO_INSTALL);
                return uoPath;
            }

            if (obnov == JOptionPane.NO_OPTION) {
                uoPath = Settings.getInstance().openFile("Vyber adresář s ultimou", "C:\\", JFileChooser.DIRECTORIES_ONLY);
                if (uoPath == null) {
                    return "";
                }
                generateRegistryData(uoPath);
                return uoPath;
            } else if (obnov == JOptionPane.CANCEL_OPTION) {
                uoPath = Settings.getInstance().openFile("Vyber adresář s ultimou", "C:\\", JFileChooser.DIRECTORIES_ONLY);
                if (uoPath == null) {
                    return "";
                }
                // don't ask when config exists.
                File fCnf = new File(uoPath + File.separator + CONFIG_FILENAME);
                if (!fCnf.exists()) {
                    Object[] opts2 = {"Ano", "Ne, do tempu s nimi",};

                    int temporaryFiles = JOptionPane.showOptionDialog(null, "Uložit dočasné soubory patcheru přímo do složky UO? (V opačném případě budou uloženy do TEMPu)", "Kam s nimi ?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, opts2, opts2[0]);
                    if (temporaryFiles == JOptionPane.YES_OPTION) {
                        String storage = uoPath + File.separator + "TempAndariaPatcher";
                        Settings.getInstance().setAlternate_storage(storage);
                    }
                }
                return uoPath;
            }
            return "";
        }

        System.out.println("Rozpoznaný adresář s ultimou: ".concat(uoPath));
        return uoPath;
    }

    /***************************************************************************
     * Creates a new instance of WindowsOS
     **************************************************************************/
    WindowsOS() {
        super();
    }

    /***************************************************************************
     * @return XML cofiguration file patch
     **************************************************************************/
    @Override
    public String getConfigPath() {
        return getUOPath() + File.separator + CONFIG_FILENAME;
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
        if (newpath.isEmpty()) {
            return;
        }
        uoPath = newpath;
        generateRegistryData(uoPath);
    }

    /***************************************************************************
     * @return ultima online path from windows registers
     **************************************************************************/
    public String generateRegistryData(String uoPath) {
        
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

      public String generateRazorData(String uoPath) {
        String regData = ""; //new String("");

        regData = regData.concat("Windows Registry Editor Version 5.00\n\n");
        regData = regData.concat("[HKEY_CURRENT_USER\\Software\\Razor]\n");
        regData = regData.concat("\"Client1\"=\"C:\\\\uo\\\\AndariaClient.exe\"");
        regData = regData.concat("\"DefClient\"=\"2\"");
 
        FileWriter fw;
        File f;
        try {
            f = getExistingFileInstance(uoPath.concat(File.separator).concat(regRazorFileName));
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
}

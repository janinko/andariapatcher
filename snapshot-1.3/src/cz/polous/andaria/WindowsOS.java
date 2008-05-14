package cz.polous.andaria;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.prefs.Preferences;
import javax.swing.JOptionPane;

/*******************************************************************************
 *
 * @author p0l0us
 ******************************************************************************/
class WindowsOS extends OperatingSystem {

    private final String unrar_path = "C:\\Program Files\\WinRAR\\unrar.exe";
    private final String ultima_online_path = getRegUoPath();
    private final String run_command = ultima_online_path + "\\AndariaClient.exe";

    public String getRun_command() {
        return run_command;
    }

    String getUltima_online_path() {
        return ultima_online_path;
    }

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
    public String getConfigPath() {
        return getRegUoPath() + File.separator + "AndariaPatcherConfig.xml";
    }

    /***************************************************************************
     * @return operating system oriented patch script command
     **************************************************************************/
    public String[] getBatchExecCommand(File f) {
        return new String[] { f.getAbsolutePath() };
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

    /***************************************************************************
     * @return ultima online path from windows registers
     **************************************************************************/
    private static String getRegUoPath() {
        String uopath = null;

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

            uopath = value;
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


        if (uopath == null) {
            JOptionPane.showMessageDialog(null, "Nemuzu najit zaznam o ultime v registrech.\nBud nemas nainstalovaou uo spravne, nebo je to rozbity. Zkus ultimu preinstalovat ultimu.\nPokud to nepomuze, napis p0l0usovi na foru andarie o pomoc.", "Upozorneni !", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        System.out.println("Rozpoznany adresar s ultimou: ".concat(uopath));
        return uopath;
    }

    /***************************************************************************
     * TODO: Java unrar implementation (using unrar native library).
     * @param file to extract
     **************************************************************************/
    public void unrar(File file) {
    }

    /***************************************************************************
     * TODO: Unrar library inicialization
     **************************************************************************/
    public void unrarInit() {
    }
}
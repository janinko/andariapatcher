package cz.polous.andaria;

import java.io.File;

/*******************************************************************************
 *
 * @author p0l0us
 ******************************************************************************/
class LinuxOS extends OperatingSystem {

  
    private final String ultima_online_path = System.getProperty("user.home") + "/.wine/drive_c/Program Files/EA Games/Ultima Online Mondain's Legacy";
    private final String run_command = "wine " + ultima_online_path + "/AndariaClient.exe";

    @Override
    public String getRun_command() {
        return run_command;
    }

    @Override
    String getUltima_online_path() {
        return ultima_online_path;
    }

    /** Creates a new instance of LinuxOS
     **************************************************************************/
    public LinuxOS() {
        super();
    }

    /***************************************************************************
     * @return XML cofiguration file patch
     **************************************************************************/
    @Override
    public String getConfigPath() {
        return System.getProperty("user.home") + "/.AndariaPatcherConfig.xml";
    }

    /***************************************************************************
     * @return os oriented patch script command
     **************************************************************************/
    @Override
    public String[] getBatchExecCommand(File f) {
        return new String[]{"wine", "cmd", "/C ", f.getName()};
    }
}
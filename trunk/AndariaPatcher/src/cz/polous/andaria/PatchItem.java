package cz.polous.andaria;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.Date;
import java.text.ParseException;
import javax.swing.JPanel;
import java.io.File;
import java.security.MessageDigest;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.security.NoSuchAlgorithmException;
import org.jdom.Element;

/*******************************************************************************
 * PatchItem: Object of one patch file and procedures to control it.
 * 
 * 
 * ChangLog:
 *      [0.2] osetreni chyby s hlasenim nestazeneho soubor presunutim odchytuti
 *              vyjimky checkHash funkce mimo funkci.
 *            pridana funkce isPacked(), ktere urci, jestli je soubor zabaleny
 * 
 * 
 * @author  Martin Polehla (andaria_patcher@polous.cz)
 * @version 0.2
 ******************************************************************************/
class PatchItem {

    private String name;                    // patch name2
    private BigInteger hash;                // MD5 hash
    private String version;                 // latest version
    private String currentVersion;          // installed version
    private Date date;                      // date of release
    private Date currentDate;               // date of installed patch
    private Boolean requiredFlag;           // reqired patch (by server)
    private Boolean installFlag;            // should be installed ?
    private Boolean autoInstallFlag;        // if user want auto-install this patch (initial value is requiredFlag or from config file - if stored)
    private Boolean installed;              // is installed
    private String description;             // patch description
    private String fileName;                // file name
    private long size;                      // file size
    private Boolean downloaded;             // is file downloaded and ready for instalation ?
    private static Log log;
    public PatchPanel panel;
    public final DateFormat dateFormat = new SimpleDateFormat("dd.mm.yyyy, hh:mm");

    /***************************************************************************
     * Creates a new instance of PatchItem
     **************************************************************************/
    public PatchItem(String[] data) {
        log = new Log(this);
        name = data[1];
        hash = new BigInteger(data[3].trim(), 16);
        version = data[6];
        currentVersion = data[6];
        try {
            date = dateFormat.parse(data[2]);
        } catch (ParseException e) {
            log.addLine("Chyba rozpoznani datumu patche: ".concat(e.getMessage()));
        }

        description = data[7];
        fileName = data[0];
        size = Long.parseLong(data[5]);
        downloaded = false;

        Element el = Settings.getPatchData(getFileName());

        if (el != null) {
            try {
                autoInstallFlag = (el.getAttributeValue("auto_install").equalsIgnoreCase("1") ? true : false);
                // determinate if patch allready installed.
                installed = el.getText().equalsIgnoreCase(getHash()) & (el.getAttributeValue("installed").equalsIgnoreCase("1") ? true : false);
                currentDate = dateFormat.parse(el.getAttributeValue("date"));
                currentVersion = el.getAttributeValue("version");
            } catch (Exception e) {
            }
        } else {
            installed = false;
        }

        if (autoInstallFlag == null) {
            autoInstallFlag = (data[4].equalsIgnoreCase("1") ? true : false);
        }

        if (installed == null) {
            installed = false;
        }

        requiredFlag = ((data[4].equalsIgnoreCase("1") ? true : false)); //& ! installed );

        installFlag = autoInstallFlag & !installed;

        panel = new PatchPanel(this);
    }

    public String getInLine() {
        return name;
    }

    public JPanel getInFrame() {
        return panel;
    }

    public String getLocalFileName() {
        return Settings.getValue(Settings.LOCAL_STORAGE) + File.separator + fileName;
    }

    public String getRemoteFileName() {
        return Settings.getValue(Settings.REMOTE_STORAGE) + "/" + fileName;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getDates() {
        String cd;
        try {
            cd = dateFormat.format(currentDate);
        } catch (NullPointerException e) {
            cd = "-";
        }
        return (dateFormat.format(date)).concat(" (").concat(cd).concat(")");
    }

    public String getVersion() {
        return version;
    }

    public String getVersions() {
        return version.concat(" (").concat((currentVersion == null ? "-" : currentVersion)).concat(")");
    }

    public String getHash() {
        return hash.toString(16);
    }

    public String getFileName() {
        return fileName;
    }

    public long getSize() {
        return size;
    }

    public boolean isInstalled() {
        return installed;
    }

    public boolean isPacked() {
        return fileName.endsWith(".rar");
    }

    public boolean isRequired() {
        return requiredFlag;
    }

    public boolean getInstallFlag() {
        return installFlag;
    }

    public boolean getAutoInstallFlag() {
        return autoInstallFlag;
    }

    public void setDownloaded(boolean b) {
        downloaded = b;
    }

    public void setAutoInstallFlag(boolean b) {
        autoInstallFlag = b;
        Settings.savePatchItem(this);
    }

    public void switchInstallFlag() {
        installFlag = !installFlag;
        panel.refresh();
    }

    /***************************************************************************
     * Control local if local file hash (md5) is rigth
     * @return If hash check passed. If an error ocures, returned value will be false too.
     **************************************************************************/
    synchronized public boolean checkHash() throws IOException {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            File f = new File(getLocalFileName());
            InputStream is = new FileInputStream(f);

            byte[] buffer = new byte[8192];
            int read = 0;
            while ((read = is.read(buffer)) > 0) {
                digest.update(buffer, 0, read);
            }
            is.close();

            byte[] md5sum = digest.digest();
            BigInteger bigInt = new BigInteger(1, md5sum);
            //String md5 = bigInt.toString(16);
            if (0 != bigInt.compareTo(hash)) {
                throw new IOException("Spatny hash stazeneho souboru.");
            }

            return (0 == bigInt.compareTo(hash));
        } catch (NoSuchAlgorithmException e) {
            log.addEx(e);
        }
        return false;

    }

    /***************************************************************************
     * Set patch Item as intalled
     * - uncheck in patchlist
     * - set to settings
     **************************************************************************/
    public void setInstalled() {
        installed = true;
        installFlag = false;
        panel.refresh();
        Settings.savePatchItem(this);
    }
}

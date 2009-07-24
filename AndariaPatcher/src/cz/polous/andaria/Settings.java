package cz.polous.andaria;

import java.io.File;
import java.io.FileWriter;
import org.jdom.output.XMLOutputter;
import java.io.IOException;
import java.util.Date;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import org.jdom.Document;
import org.jdom.Element;

/*******************************************************************************
 * Settings: Trida se statickyma metodama pro pristup k nastaveni a ukladani 
 * nastaveni.
 * 
 * @author  Martin Polehla (andaria_patcher@polous.cz)
 ******************************************************************************/
class Settings {

    private static final Settings INSTANCE = new Settings();
    private Document doc; // XML settings document
    private Element root; // root element of document
    private Element settings; // settings of patcher
    private Element patches; // content of local stored patchlist
    private OperatingSystem os;

    /**
     * Adresy URL
     */
    private final String local_storage = System.getProperty("java.io.tmpdir") + File.separator + "AndariaPatcher";
    private final String remote_storage = "http://space.andaria.net/data/Andaria_Soubory_7zip";
    private final String about_url = "http://strazci.andaria.net/patcher/java.php";
    private final String news_url = "http://www.andaria.net/novinky_updater.php";
    private final String debug_log = "0";
    private final String counter_url = "http://strazci.andaria.net/patcher/beta/counter.php";
    private final String filelist_url = "http://www.andaria.net/admin/patcher.csv";
    
    private static Log log;

    private Settings() {
        os = OperatingSystem.createOperatingSystemInstance();
        log = new Log("Settings");
    }

    public static Settings getInstance() {
        return INSTANCE;
    }
    
    public OperatingSystem getOs() {
        return os;
    }
    
    /*
     * Get URL's method
     */
    
    public String getCounter_url() {
        return counter_url;
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
    
    /***************************************************************************
     * Get configuration value specified by config name.
     * @param item  requested settings item sub-element
     * @return      Required item (string)
     **************************************************************************/
    public String getValue(int item) {
        String result = null;
        try {
            result = settings.getChildText(getSettingName(item));
        } catch (IllegalArgumentException e) {
            //System.err.println(e);
        } catch (NullPointerException e) {
            //System.err.println(e);
        } finally {
            if (result == null) {
                result = getDefaultValue(item);
            }
        }
        return result;
    }

    /***************************************************************************
     * setValue(String item, String val) wrapper
     * @param item  element to set
     * @param val   New value of item
     **************************************************************************/
    public void setValue(int item, String val) {
        setValue(getSettingName(item), val);
    }

    /***************************************************************************
     * Set a configuration settings (not save).
     * @param item  Name of element to set
     * @param val   New value of item
     **************************************************************************/
    public void setValue(String item, String val) {
        if (settings == null) {
            return;
        }
        try {
            settings.getChild(item).setText(val);
        } catch (NullPointerException e) {
            settings.addContent(new Element(item).setText(val));
        }
    }

    /***************************************************************************
     * Save a PatchItem object state into XML setting file.
     * @param p PatchItem to save.
     **************************************************************************/
    public void savePatchItem(PatchItem p) {
        Element ch = patches.getChild(p.getFileName());
        if (ch == null) {
            ch = new Element(p.getFileName());
            patches.addContent(ch);
        }
        ch.setText(p.getHash());
        ch.setAttribute("version", p.getVersion());
        ch.setAttribute("date", p.getDateFormat().format(new Date()));
        ch.setAttribute("auto_install", p.getAutoInstallFlag() ? "1" : "0");
        ch.setAttribute("installed", p.isInstalled() ? "1" : "0");
        save();
    }

    /***************************************************************************
     * @param el    Element where to look for item
     * @param item  Name of sub-element
     * @return      Return required element or new empty one.
     **************************************************************************/
    private Element getExistingElement(Element el, String item) {
        List list;
        try {
            list = el.getChildren(item);
            if (list.size() > 0) {
                return (Element) list.get(0);
            } else {
                Element newElement = new Element(item);
                el.addContent(newElement);
                return newElement;
            }
        } catch (NullPointerException e) {
            // return new Element(item);
        }
        return new Element(item);
    }

    /***************************************************************************
     * @return PatchItem data Element
     **************************************************************************/
    public Element getPatchData(String item) {
        return patches.getChild(item);
    }

    /***************************************************************************
     * Open file dialog openner.
     * @param title Title of JFileCooser
     * @param defPath Default path of JFileChooser
     * @param ft Selection mode (ie. JFileCooser.DIRECTORY)
     **************************************************************************/
    public String openFile(String title, String defPath, int ft) {
        final JFileChooser fc = new JFileChooser();
        fc.setDialogTitle(title);
        fc.setCurrentDirectory(new File(defPath).getParentFile());
        fc.setFileSelectionMode(ft);
        fc.setFileHidingEnabled(false);

        if (fc.showOpenDialog(FrontEnd.getInstance()) == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            return file.getAbsolutePath();
        }
        return defPath;
    }

    /***************************************************************************
     * Load settings, doc, and paches objects from XML file
     **************************************************************************/
    public void load() {
        File f = os.getExistingFileInstance(os.getConfigPath());
        //Document newdoc = null;
        if (f.exists()) {
            SAXBuilder parser = new SAXBuilder();
            try {
                doc = parser.build(f);
                root = doc.getRootElement();
                settings = getExistingElement(root, "settings");
                patches = getExistingElement(root, "patchlist");
            } catch (JDOMException e) {
                System.out.println(e.getMessage());
            } catch (IOException e) {
                System.err.println(e);
            }
        }
        if (doc == null || root == null) {
            JOptionPane.showMessageDialog(null, "Nejspíš jsi patcher pustil poprvé, takže nebyl nalezen konfigurační soubor (" + os.getConfigPath() + ").\nNež budeš patchovat, tak si zkontroluj a ulož svoje nastavení AndariaPatcheru.", "Upozornění !", JOptionPane.WARNING_MESSAGE);
            root = new Element("main");
            settings = new Element("settings");
            patches = new Element("patchlist");

            root.addContent(settings);
            root.addContent(patches);
            doc = new Document(root);
        }
    }

    /***************************************************************************
     * Save settings and paches objects into XML file
     **************************************************************************/
    public void save() {
        if (doc == null) {
            return;
        }
        //  SAXBuilder parser = new SAXBuilder();
        File f = os.getExistingFileInstance(os.getConfigPath());

        try {
            XMLOutputter out = new XMLOutputter();
            FileWriter fw = new FileWriter(f);
            out.output(doc, fw);
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    /***************************************************************************
     * Determinate if program is in debug mode.
     * @return true if debug mode, false if not.
     **************************************************************************/
    public boolean debugMode() {
        return getValue(DEBUG_MODE).equalsIgnoreCase("1") ? true : false;
    }

    public static String getSettingName(int val) {
        return settingList[val];
    }
    public static final int RUN_COMMAND = 0;
    public static final int UNRAR_PATH = 1;
    public static final int ULTIMA_ONINE_PATH = 2;
    public static final int LOCAL_STORAGE = 3;
    public static final int REMOTE_STORAGE = 4;
    public static final int ABOUT_URL = 5;
    public static final int NEWS_URL = 6;
    public static final int DEBUG_MODE = 7;
    public static final int FILE_LIST_URL = 8;
    private static final String[] settingList = {"run_command", "unrar_path", "ultima_online_path", "local_storage", "remote_storage", "about_url", "news_url", "debug_log", "filelist_url"};

    private String getDefaultValue(int item) {

        switch (item) {

            case 0:
                return os.getRun_command();
            case 1:
//                return os.getUnrar_path();
            case 2:
                return os.getUltima_online_path();
            case 3:
                return getLocal_storage();
            case 4:
                return getRemote_storage();
            case 5:
                return getAbout_url();
            case 6:
                return getNews_url();
            case 7:
                return getDebug_log();
            case 8:
                return getFilelist_url();
            default:
                return "";
        }
    }

    public boolean RenewWindowsRegistry() {

        return true;
    }
}
package cz.polous.andaria;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import org.jdom.output.XMLOutputter;
import java.io.IOException;
import java.util.Date;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
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
    private final String remote_storage = "http://space.andaria.net/data/andaria_soubory_7zip";
    private final String about_url = "http://patcher.andaria.net/java.php";
    private final String news_url = "http://www.andaria.net/novinky_updater.php";
    private final String debug_log = "0";
    private final String counter_url = "http://patcher.andaria.net/counter.php";
    private final String filelist_url = "http://www.andaria.net/admin/patcher.csv";
    private final String uomlRemotePath = "http://patcher.andaria.net/7z/";
    private final String uomlPatchItemName = "UOML";
    private final String razorPatchFileName = "razor.7z";
    private final String uoamPatchFileName = "uoam.7z";
    private final String razorPath = "razor\\razor.exe";
    private final String uoamPath = "uoam\\uoam.exe -q";
    private final String[] uomlPatchItem = {"uoml_win32_6-0-14-2_Andaria.7z", uomlPatchItemName, "8.9.2009, 18:51", "3874f382e20355ba29f9ecc6aff445d7", "0", "645048128", "6.0.14.2", "Předinstalovaná ultima online."};
    //private final String[] uomlPatchItem = {"uoml_win32_6-0-14-2_ConfigOnly.7z", uomlPatchItemName, "8.9.2009, 18:51", "183e6e68922c3ff9b9bddb2e34632bde", "0", "1013", "6.0.14.2", "Předinstalovaná ultima online - jenom config pro testovani."};
    //private final String[] uomlPatchItem = {"uoml_win32_6-0-14-2_ConfigOnlyNoLogin.7z", uomlPatchItemName, "8.9.2009, 18:51", "346083434d0142bb7aec9e96e0b364e7", "0", "987", "6.0.14.2", "Předinstalovaná ultima online - jenom config pro testovani bez login patche."};
    private static int autoInstall = AUTO_LEVELS.MANUAL;
    private static Log log;
    private String alternate_storage;

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
        // ljk upravy 09.09.07
        if (alternate_storage != null) {
            return alternate_storage;
        } else {
            return local_storage;
        }
    }

    public String getRazorPath() {
        return razorPath;
    }

    public String getUoamPath() {
        return uoamPath;
    }

    public String getRazorPatchFileName() {
        return razorPatchFileName;
    }

    public String getUoamPatchFileName() {
        return uoamPatchFileName;
    }

    public static int getAutoInstall() {
        return autoInstall;
    }

    public static void setAutoInstall(int autoInstall) {
        Settings.autoInstall = autoInstall;
    }

    public String getNews_url() {
        return news_url;
    }

    public String getRemote_storage() {
        return remote_storage;
    }

    public void setAlternate_storage(String storage) {
        alternate_storage = storage;
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
        //return null;
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
            if (autoInstall == AUTO_LEVELS.MANUAL) {
                JOptionPane.showMessageDialog(null, "Nejspíš jsi patcher pustil poprvé, takže nebyl nalezen konfigurační soubor (" + os.getConfigPath() + ").\nNež budeš patchovat, tak si zkontroluj a ulož svoje nastavení AndariaPatcheru.", "Upozornění !", JOptionPane.WARNING_MESSAGE);
            }

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
        return getValue(VALUES.DEBUG_MODE).equalsIgnoreCase("1") ? true : false;
    }

    public static String getSettingName(int val) {
        return settingList[val];
    }

    class AUTO_LEVELS {

        public static final int MANUAL = 0;
        //NOTE: use this first
        public static final int AUTO_INSTALL = 1;
        public static final int AUTO_UPDATE = 2;
        public static final int AUTO_CLOSE = 3;
    }

    class VALUES {

        public static final int RUN_COMMAND = 0;
        //NOTE: use this first
        public static final int RUN_COMMAND1 = 1;
        public static final int ULTIMA_ONINE_PATH = 2;
        public static final int LOCAL_STORAGE = 3;
        public static final int REMOTE_STORAGE = 4;
        public static final int ABOUT_URL = 5;
        public static final int NEWS_URL = 6;
        public static final int DEBUG_MODE = 7;
        public static final int FILE_LIST_URL = 8;
        public static final int RUN_COMMAND2 = 9;
    }
    private static final String[] settingList = {"run_command", "run_command1", "ultima_online_path", "local_storage", "remote_storage", "about_url", "news_url", "debug_log", "filelist_url", "run_command2"};

    private String getDefaultValue(int item) {
        switch (item) {
            case 0:
                return os.getDefaultRunCommand();
            case 1:
                return os.getDefaultRunCommand1();
            case 2:
                return os.getUOPath();
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
            case 9:
                return os.getDefaultRunCommand2();
            default:
                return "";
        }
    }

    public boolean RenewWindowsRegistry() {

        return true;
    }

    public PatchItem getUomlPatchItem() {
        return new PatchItem(uomlPatchItem);
    }

    public String getUomlPatchItemName() {
        return uomlPatchItemName;
    }
    /*
     * Funkce urcena ke zruseni ve verzi 1.7 ci pozdeji.
     */

    @Deprecated
    public static void removeRarFiles() {
        String dir = Settings.getInstance().getValue(Settings.VALUES.LOCAL_STORAGE);

        FilenameFilter rarFilter = new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                if (name.endsWith(".rar")) {
                    return true;
                } else {
                    return false;
                }
            }
        };
        File tmpDir = new File(dir);
        String[] fileList = tmpDir.list(rarFilter);
        File file;
        if (fileList != null) {
            for (int i = 0; i < fileList.length; i++) {
                file = new File(tmpDir.getAbsolutePath().concat(File.separator).concat(fileList[i]));
                if (file.delete()) {
                    log.addLine("Smazal jsem soubor: ".concat(file.getAbsolutePath()));
                } else {
                    log.addErr("Nepodařilo se smazat soubor: ".concat(file.getAbsolutePath()));
                }
            }
        }
    }

    public String getUomlRemotePath() {
        return uomlRemotePath;
    }

    public void updateTempSize() {
        long size = 0;
        String dir = Settings.getInstance().getValue(Settings.VALUES.LOCAL_STORAGE);
        File tmpDir = new File(dir);
        File[] files = tmpDir.listFiles();

        for (int i = 0; i < files.length; i++) {
            size += files[i].length();
        }
        JLabel label = FrontEnd.getInstance().getjLabel(this, FrontEnd.LABEL_TYPES.TEMP_SIZE);
        label.setText(Long.toString(Math.round(size / 1024)).concat(" MB"));
    }

    public void addAutorun(String name, String path) {
        addAutorun(name, path, name);
    }

    public void addAutorun(String name, String path, String alterName) {
        log.addLine("Nastavuji ".concat(name).concat(" aby se automaticky spouštěl po zavření AndariaPatheru."));
        if (getValue(VALUES.RUN_COMMAND).toLowerCase().contains(alterName) || getValue(VALUES.RUN_COMMAND).toLowerCase().contains(name)) {
            setValue(VALUES.RUN_COMMAND, path);
        } else {
            if (getValue(VALUES.RUN_COMMAND1).toLowerCase().contains(alterName) || getValue(VALUES.RUN_COMMAND).toLowerCase().contains(name)) {
                setValue(VALUES.RUN_COMMAND1, path);
            } else {
                if (getValue(VALUES.RUN_COMMAND2).toLowerCase().contains(alterName) || getValue(VALUES.RUN_COMMAND).toLowerCase().contains(name)) {
                    setValue(VALUES.RUN_COMMAND2, path);
                } else {
                    if (getValue(VALUES.RUN_COMMAND).isEmpty()) {
                        setValue(VALUES.RUN_COMMAND, path);
                    } else {
                        if (getValue(VALUES.RUN_COMMAND1).isEmpty()) {
                            setValue(VALUES.RUN_COMMAND1, path);
                        } else {
                            if (getValue(VALUES.RUN_COMMAND2).isEmpty()) {
                                setValue(VALUES.RUN_COMMAND2, path);
                            } else {
                                log.addLine("Nevím do které spouštěcí řádky ".concat(name).concat(" zapsat. Musíš to udělat ručně."));
                            }
                        }
                    }
                }
            }
        }
        FrontEnd.getInstance().updatejTConfRunCommands();
    }
}

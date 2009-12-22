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
    private final String local_storage = System.getProperty("java.io.tmpdir") + File.separator + "AndariaPatcher";

    class CONST {

        /**
         * Adresy URL
         */
        private static final String remote_storage = "http://space.andaria.net/data/andaria_soubory_7zip";
        private static final String about_url = "http://patcher.andaria.net/java.php";
        private static final String news_url = "http://www.andaria.net/novinky_updater.php";
        private static final String debug_log = "0";
        private static final String counter_url = "http://patcher.andaria.net/counter.php";
        private static final String filelist_url = "http://www.andaria.net/admin/patcher.csv";
        private static final String distlist_url = "http://patcher.andaria.net/7z/versions.csv";
        private static final String dist_storage = "http://patcher.andaria.net/7z";
        private static final String razorPatchFileName = "razor.7z";
        private static final String uoamPatchFileName = "uoam.7z";
        private static final String razorPath = "razor\\razor.exe";
        private static final String uoamPath = "uoam\\uoam.exe";
//        private static final String uomlPatchItemName = "UOML";
//        private static final String dotnetPatchItemName = "DotNetFx";
    }
    //public static final String[] uomlPatchItem = {"uoml_win32_6-0-14-3_Andaria.7z", CONST.uomlPatchItemName, "8.9.2009, 18:51", "428174eb1856187be42b4e4630b9bace", "0", "556111101", "6.0.14.2", "Předinstalovaná ultima online."};
    //public static final String[] uomlPatchItem = {"uoml_win32_6-0-14-2_Andaria.7z", CONST.uomlPatchItemName, "8.9.2009, 18:51", "3874f382e20355ba29f9ecc6aff445d7", "0", "645048128", "6.0.14.2", "Předinstalovaná ultima online."};
    //public staticfinal String[] uomlPatchItem = {"uoml_win32_6-0-14-2_ConfigOnly.7z", uomlPatchItemName, "8.9.2009, 18:51", "183e6e68922c3ff9b9bddb2e34632bde", "0", "1013", "6.0.14.2", "Předinstalovaná ultima online - jenom config pro testovani."};
    //public staticfinal String[] uomlPatchItem = {"uoml_win32_6-0-14-2_ConfigOnlyNoLogin.7z", uomlPatchItemName, "8.9.2009, 18:51", "346083434d0142bb7aec9e96e0b364e7", "0", "987", "6.0.14.2", "Předinstalovaná ultima online - jenom config pro testovani bez login patche."};
//    public static final String[] dotnetPatchItem = {"dotNetFx35setup.exe", CONST.dotnetPatchItemName, "1.10.2009, 12:00", "269f314b87e6222a20e5f745b6b89783", "0", "2869264", "3.0", "Oficialni instalacni soubor pro DotNetFx 3.5"};
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

    public String getLocal_storage() {
        // ljk upravy 09.09.07
        if (alternate_storage != null) {
            return alternate_storage;
        } else {
            return local_storage;
        }
    }

    public static int getAutoInstall() {
        return autoInstall;
    }

    public static void setAutoInstall(int autoInstall) {
        Settings.autoInstall = autoInstall;
    }

    public void setAlternate_storage(String storage) {
        alternate_storage = storage;
    }

    public String getUomlParams() {
        log.addDebug("-q -c " + getValue(VALUES.UOAM_SERVER) +
                "-p " + getValue(VALUES.UOAM_PORT) +
                "-n " + getValue(VALUES.UOAM_NAME) +
                "-pw " + getValue(VALUES.UOAM_PASSWORD));

        if (getValue(VALUES.UOAM_SERVER).isEmpty() ||
                getValue(VALUES.UOAM_PORT).isEmpty() ||
                getValue(VALUES.UOAM_NAME).isEmpty() ||
                getValue(VALUES.UOAM_PASSWORD).isEmpty()) {
            return " -q ";
        } else {
            return (" -q -c " + getValue(VALUES.UOAM_SERVER) +
                    " -p " + getValue(VALUES.UOAM_PORT) +
                    " -n " + getValue(VALUES.UOAM_NAME) +
                    " -pw " + getValue(VALUES.UOAM_PASSWORD));
        }
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
        public static final int RUN_COMMAND1 = 1;
        public static final int ULTIMA_ONINE_PATH = 2;
        public static final int LOCAL_STORAGE = 3;
        public static final int REMOTE_STORAGE = 4;
        public static final int ABOUT_URL = 5;
        public static final int NEWS_URL = 6;
        public static final int DEBUG_MODE = 7;
        public static final int FILE_LIST_URL = 8;
        public static final int RUN_COMMAND2 = 9;
        public static final int UOAM_SERVER = 10;
        public static final int UOAM_PORT = 11;
        public static final int UOAM_PASSWORD = 12;
        public static final int UOAM_NAME = 13;
        public static final int DIST_STORAGE = 14;
        public static final int DIST_LIST_URL = 15;
        public static final int COUNTER_URL = 16;
        public static final int RAZOR_PATCH_NAME = 17;
        public static final int RAZOR_INSTALL_PATH= 18;
        public static final int UOAM_PATCH_NAME = 19;
        public static final int UOAM_INSTALL_PATH = 20;
//        public static final int = 21;
//        public static final int = 22;

    }
    // !! NEVER CHANGE THIS STRINGS
    private static final String[] settingList =
        {"run_command", "run_command1", "ultima_online_path", "local_storage", "remote_storage",
         "about_url", "news_url", "debug_log", "filelist_url", "run_command2",
         "uoam_server", "uoam_port", "uoam_password", "uoam_name", "dist_storage",
         "distlist_url", "counter_url", "razor_patch_name", "razor_path", "uoam_patch_name",
         "uoam_install_path"
        };

    /* private Map<Integer, String> settingsList = new HashMap<Integer, String>();
    settingsList.put(VALUES.RUN_COMMAND, "run_command");
    settingsList.put(VALUES.RUN_COMMAND1, "run_command1");
    settingsList.put(VALUES.RUN_COMMAND2, "run_command2");
    settingsList.put(VALUES.ULTIMA_ONINE_PATH, "ultima_online_path");
    settingsList.put(VALUES.LOCAL_STORAGE, "local_storage");
    settingsList.put(VALUES.REMOTE_STORAGE, "remote_storage");
    settingsList.put(VALUES.ABOUT_URL, "about_url");
    settingsList.put(VALUES.NEWS_URL, "news_url");
    settingsList.put(VALUES.DEBUG_MODE, "debug_log");
    settingsList.put(VALUES.FILE_LIST_URL, "filelist_url");

    settingsList.put(VALUES.UOAM_SERVER, "uoam_server");
    settingsList.put(VALUES.UOAM_PORT, "uoam_port");
    settingsList.put(VALUES.UOAM_PASSWORD, "uoam_password");
    settingsList.put(VALUES.UOAM_NAME, "uoam_name");
    settingsList.put(VALUES.DIST_LIST_URL, "distlist_url");
   */
    private String getDefaultValue(int item) {
        switch (item) {
            case VALUES.RUN_COMMAND:
                return os.getDefaultRunCommand();
            case VALUES.RUN_COMMAND1:
                return os.getDefaultRunCommand1();
            case VALUES.ULTIMA_ONINE_PATH:
                return os.getUOPath();
            case VALUES.LOCAL_STORAGE:
                return getLocal_storage();
            case VALUES.REMOTE_STORAGE:
                return CONST.remote_storage;
            case VALUES.ABOUT_URL:
                return CONST.about_url;
            case VALUES.NEWS_URL:
                return CONST.news_url;
            case VALUES.DEBUG_MODE:
                return CONST.debug_log;
            case VALUES.FILE_LIST_URL:
                return CONST.filelist_url;
            case VALUES.RUN_COMMAND2:
                return os.getDefaultRunCommand2();
            case VALUES.UOAM_SERVER:
            case VALUES.UOAM_PORT:
            case VALUES.UOAM_PASSWORD:
            case VALUES.UOAM_NAME:
                return "";
            case VALUES.DIST_STORAGE:
                return CONST.dist_storage;
            case VALUES.DIST_LIST_URL:
                return CONST.distlist_url;
            case VALUES.COUNTER_URL:
                return CONST.counter_url;
            case VALUES.RAZOR_PATCH_NAME:
                return CONST.razorPatchFileName;
            case VALUES.RAZOR_INSTALL_PATH:
                return CONST.razorPath;
            case VALUES.UOAM_PATCH_NAME:
                return CONST.uoamPatchFileName;
            case VALUES.UOAM_INSTALL_PATH:
                return CONST.uoamPath;

            default:
                return "";
        }
    }

  /*  public PatchItem getUomlPatchItem() {
        return new PatchItem(uomlPatchItem);
    }*/
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

package cz.polous.andaria;

import java.io.IOException;
import java.awt.*;
import javax.swing.*;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.DefaultCaret;
import org.lobobrowser.html.gui.HtmlPanel;

/*******************************************************************************
 * Predni panel aplikace - rozhrani mezi uzivatelem a logikou aplikace.
 * Hlavni spustitelna trida, ktera ve svem kontruktoru inicializuje program.
 * 
 * @author Martin Polehla (andaria_patcher@polous.cz)
 ******************************************************************************/
public class FrontEnd extends JFrame {

    private Log log;
    private static final FrontEnd INSTANCE = new FrontEnd(); //representation of main class (this)
    private int defaultToolTipSpeed;
    //  private PatchList patchList; // representation of patchlist, patch procedure control object    private final Settings settings = ;

    public static FrontEnd getInstance() {
        return INSTANCE;
    }

    public static class LABEL_TYPES {

        public static final int TEXT = 0;
        public static final int SPEED = 1;
        public static final int TEMP_SIZE = 2;
    }

    /***************************************************************************
     * Creates new form FrontEnd and call pl inicialization
     **************************************************************************/
    public FrontEnd() {
//        instance = this;

        initComponents();
        Log.logArea = jTLog;

        log = new Log(this);

        loadSettings();
        defaultToolTipSpeed = ToolTipManager.sharedInstance().getInitialDelay();
        jSPPatchList.getVerticalScrollBar().setUnitIncrement(17);

        // HTMLDocument doc = new HTMLDocument();
        // doc.gethtmlre
        // JEditorPane ta = new JEditorPane();
        //  JScrollPane jsp = new JScrollPane(ta);


        // try {
        //     ta.setPage(Settings.getInstance().getValue(Settings.NEWS_URL));
        // } catch (IOException ex) {
        //     log.addEx(ex);
        // }
//Font font = new Font ("Serif", Font.ITALIC, 40);
        // ta.setFont(font);
        //JScrollPane jsp = new JScrollPane(ta);
        // jTPMain.insertTab(null, null, jsp, "Tady se nachází novinky nejen ze světa...", 0);

        /*  FileReader fr;
        File f;
        try {
        f = new File("http://ip.katka.biz");
        fr = new FileReader(f);
        ta.read(fr, null);
        fr.close();
        } catch (IOException ex) {
        log.addEx(ex);
        }
        // Browser2 newsPanel = new Browser2(Settings.getInstance().getValue(Settings.NEWS_URL));
        //*/

        HtmlPanel htmlPNews = new HtmlPanel();
        jPNews1.add(htmlPNews);

        //jTPMain.insertTab(null, null, htmlPNews, "Tady se nachází novinky nejen ze světa...", 0);
        //jTPMain.setTitleAt(0, "Novinky");
        final Browser news = new Browser(htmlPNews, Settings.getInstance().getValue(Settings.VALUES.NEWS_URL));

        HtmlPanel htmlPAbout = new HtmlPanel();
        jTPMain.addTab(null, null, htmlPAbout, "Taky něco o programu samotném.");
        jTPMain.setTitleAt(jTPMain.getComponentCount() - 1, "O programu");
        final Browser about = new Browser(htmlPAbout, Settings.getInstance().getValue(Settings.VALUES.ABOUT_URL));

        // handle TAB changes
        jTPMain.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent changeEvent) {
                //log.addDebug("Change of about changed.");
                if (jTPMain.getSelectedIndex() == jTPMain.getComponentCount() - 1) {
                    // TODO: change about page to static
                    about.reload();
                }
                // tooltip speed manager
                if (jTPMain.getSelectedIndex() == jTPMain.getComponentCount() - 3) {
                    ToolTipManager.sharedInstance().setInitialDelay(0);
                } else {
                    ToolTipManager.sharedInstance().setInitialDelay(defaultToolTipSpeed);
                }
                if (jTPMain.getSelectedIndex() == jTPMain.getComponentCount() - 2) {
                    Settings.getInstance().updateTempSize();
                }


            }
        });

        jTPMain.setSelectedIndex(0);

        //  patchList = PatchList.getInstance();

        if (Settings.getInstance().debugMode()) {
            log.addDebug(System.getProperty("os.name"));
            log.addDebug(System.getProperty("user.home"));
            log.addDebug(System.getProperty("java.io.tmpdir"));
        }
        PatchList.getInstance().reload();
        callCounter();
//        Settings.getInstance().updateTempSize();
    }

    /***************************************************************************
     * Application runner
     * @param args the command line arguments
     **************************************************************************/
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                INSTANCE.setVisible(true);
                // ProgressBars and status fields reset
                Settings.getInstance().updateTempSize();
                Downloader.getInstance().reset();
                Installer.getInstance().reset();
            }
        });
    }

    /***************************************************************************
     * PatchList pl object inicialization, display list of patches at jPList panel.
     **************************************************************************/
    private void reloadPatchList() {
        PatchList patchList = PatchList.getInstance();
        if (patchList.inProgress()) {
            patchList.cancel();
        }

        patchList.reload();
    }

    /***************************************************************************
     * start automatic uo install process
     **************************************************************************/
    public void installUO() {
        disableButtons();
        PatchList.getInstance().downloadUOML();
        // updateButtons();
        jTPMain.setSelectedIndex(1);
    }

    /***************************************************************************
     * start update process
     **************************************************************************/
    public void installPatches() {
        disableButtons();
        // TODO: version 1.7 and later: remove this line
        Settings.removeRarFiles();
        PatchList.getInstance().download();
        //updateButtons();

        jTPMain.setSelectedIndex(1);
    }

    private void disableButtons() {
        setJBInstall(false);
        jBCancel.setEnabled(true);
        jBClose.setEnabled(false);
        jBDownloadUO.setEnabled(false);
        setJBPatchListEnabled(false);
        jBPachAndPlay.setEnabled(false);
    }

    public void setJBInstall(boolean state) {
        jBInstallSelection.setEnabled(state);
        jBInstall.setEnabled(state);
    }

    public void setJBPatchListEnabled(boolean state) {
        jBRefreshPatchList.setEnabled(state);
        jBInstallSelectAll.setEnabled(state);
        jBInstallSelectNone.setEnabled(state);
    }

    private void callCounter() {
        URLConnection conn = null;
        InputStream in = null;

        try {
            String uri = Settings.getInstance().getCounter_url();

            URL url = new URL(uri);
            conn = url.openConnection();
            in = conn.getInputStream();
            log.addDebug("Zavolal jsem počítadlo spuštění.");
            /*            byte[] buff = new byte[2048];
            int numRead;
            while ((numRead = in.read(buff)) != -1) {
            out.write(buff, 0, numRead);
            }*/
        } catch (Exception e) {
            log.addEx(e);
            log.addErr("Došlo k chybě při volání počítadla spuštění.");
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                log.addEx(e);
            }
        }
    }

    /***************************************************************************
     * Standard application exit
     * execute autoRun commands and quit
     **************************************************************************/
    public void closeMe() {
        boolean error = false;
        if (!Settings.getInstance().getValue(Settings.VALUES.RUN_COMMAND).isEmpty()) {
            error = (error || runApp(Settings.getInstance().getValue(Settings.VALUES.RUN_COMMAND)));
        }
        if (!Settings.getInstance().getValue(Settings.VALUES.RUN_COMMAND1).isEmpty()) {
            error = (error || runApp(Settings.getInstance().getValue(Settings.VALUES.RUN_COMMAND1)));
        }
        if (!Settings.getInstance().getValue(Settings.VALUES.RUN_COMMAND2).isEmpty()) {
            error = (error || runApp(Settings.getInstance().getValue(Settings.VALUES.RUN_COMMAND2)));
        }
        if (error == false) {
            System.exit(0);
            Runtime.getRuntime().exit(0);
        } else {
            log.addErr("Při spouštění některého z externích programů došlo k chybě. Oprav prosím nastavení spouštěných programů nebo zavři patcher \"křížkem\".");
        }
    }

    private boolean runApp(String command) {
        if (command.toLowerCase().contains("uoam")) {
            command = command.concat(Settings.getInstance().getUomlParams());
        }
        try {
            log.addDebug(command);
            Runtime.getRuntime().exec(command.split(" "), null, new File(Settings.getInstance().getValue(Settings.VALUES.ULTIMA_ONINE_PATH)));
        } catch (IOException ex) {
            log.addErr("Chyba při spouštění externího programu !");
            log.addDebug(command.split("").toString());
            ex.printStackTrace();
            return true;
        }
        return false;
    }

    /***************************************************************************
     * Inicialize application settings and settings form.
     **************************************************************************/
    public void loadSettings() {
        Settings set = Settings.getInstance();
        set.load();
        updatejTConfRunCommands();

        jTConfUltimaOnlinePath.setText(set.getValue(Settings.VALUES.ULTIMA_ONINE_PATH));
        jTConfTempPath.setText(set.getValue(Settings.VALUES.LOCAL_STORAGE));
        jChDebug.setSelected(set.debugMode());

        jTConfUoamServer.setText(set.getValue(Settings.VALUES.UOAM_SERVER));
        jTConfUoamPort.setText(set.getValue(Settings.VALUES.UOAM_PORT));
        jTpwConfUoamPassword.setText(set.getValue(Settings.VALUES.UOAM_PASSWORD));
        jTConfUoamName.setText(set.getValue(Settings.VALUES.UOAM_NAME));
    }

    public void updatejTConfRunCommands() {
        jTConfRunCommand.setText(Settings.getInstance().getValue(Settings.VALUES.RUN_COMMAND));
        jTConfRunCommand1.setText(Settings.getInstance().getValue(Settings.VALUES.RUN_COMMAND1));
        jTConfRunCommand2.setText(Settings.getInstance().getValue(Settings.VALUES.RUN_COMMAND2));
    }

    /***************************************************************************
     * Store application Settings.
     **************************************************************************/
    public void saveSettings() {
        Settings set = Settings.getInstance();
        set.setValue(Settings.VALUES.RUN_COMMAND, jTConfRunCommand.getText());
        set.setValue(Settings.VALUES.RUN_COMMAND1, jTConfRunCommand1.getText());
        set.setValue(Settings.VALUES.RUN_COMMAND2, jTConfRunCommand2.getText());

        set.setValue(Settings.VALUES.ULTIMA_ONINE_PATH, jTConfUltimaOnlinePath.getText());
        set.setValue(Settings.VALUES.LOCAL_STORAGE, jTConfTempPath.getText());
        set.setValue(Settings.VALUES.DEBUG_MODE, jChDebug.isSelected() ? "1" : "0");

        set.setValue(Settings.VALUES.UOAM_SERVER, jTConfUoamServer.getText());
        set.setValue(Settings.VALUES.UOAM_PORT, jTConfUoamPort.getText());
        set.setValue(Settings.VALUES.UOAM_NAME, jTConfUoamName.getText());
        set.setValue(Settings.VALUES.UOAM_PASSWORD, new String(jTpwConfUoamPassword.getPassword()));

        set.save();
    }

    private Image getIcon(String fileName) {
        URL res = getClass().getResource(fileName);
        return res == null ? null : Toolkit.getDefaultToolkit().getImage(res);
    }

    public JProgressBar getjPBDownloadSingle() {
        return jPBDownloadSingle;
    }

    public JProgressBar getjPBTotal(Object cls) {
        if (cls.getClass() == Downloader.class) {
            return getjPBDownloadTotal();
        } else {
            return getjPBInstallTotal();
        }
    }

    public JProgressBar getjPBSingle(Object cls) {
        if (cls.getClass() == Downloader.class) {
            return getjPBDownloadSingle();
        } else {
            return getjPBInstallSingle();
        }
    }

    public JLabel getjLabel(Object cls, int ltype) {
        switch (ltype) {
            case LABEL_TYPES.TEXT:
                if (cls.getClass() == Downloader.class) {
                    return jLDownload;
                } else {
                    return jLInstall;
                }
            case LABEL_TYPES.SPEED:
                if (cls.getClass() == Downloader.class) {
                    return jLDownloadSpeed;
                } else {
                    return jLInstallSpeed;
                }
            case LABEL_TYPES.TEMP_SIZE: {
                return jLConfTempSize;
            }
        }
        return null;
    }

    public JProgressBar getjPBDownloadTotal() {
        return jPBDownloadTotal;
    }

    public JProgressBar getjPBInstallSingle() {
        return jPBInstallSingle;
    }

    public JProgressBar getjPBInstallTotal() {
        return jPBInstallTotal;
    }

    @Override
    public void pack() {
        updateButtons();
        super.pack();
    }

    public javax.swing.JPanel getJPPatchList() {
        return jPPatchList;
    }

    /***************************************************************************
     * Update FrontEnd buttons state (enabled or disabled).
     * Usualy called when downloader or installer progress state may be changed.
     * @see cz.polous.andaria.PatchList#isWorking()
     * @see cz.polous.andaria.Downloader#inProgress()
     * @see cz.polous.andaria.Installer#inProgress()
     **************************************************************************/
    public void updateButtons() {
        try {
            if (PatchList.getInstance().inProgress()) {
                disableButtons();
            } else {
                setJBInstall(true);
                jBCancel.setEnabled(false);
                jBClose.setEnabled(true);
                setJBPatchListEnabled(true);
                jBDownloadUO.setEnabled(true);
                jBPachAndPlay.setEnabled(true);
            }

        } catch (NullPointerException e) {
            log.addEx(e);
        }

    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTPMain = new javax.swing.JTabbedPane();
        jPNews = new javax.swing.JPanel();
        jPNews1 = new javax.swing.JPanel();
        jBPachAndPlay = new javax.swing.JButton();
        jPControlsTab = new javax.swing.JPanel();
        jPButtons = new javax.swing.JPanel();
        jBRefreshPatchList = new javax.swing.JButton();
        jBInstall = new javax.swing.JButton();
        jBCancel = new javax.swing.JButton();
        jBClose = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jSPLog = new javax.swing.JScrollPane();
        jTLog = new javax.swing.JTextArea();
        jSeparator2 = new javax.swing.JSeparator();
        jPDownloadProgress = new javax.swing.JPanel();
        jPDownloadProgressText = new javax.swing.JPanel();
        jLDownload = new javax.swing.JLabel();
        jLDownloadSpeed = new javax.swing.JLabel();
        jSeparator7 = new javax.swing.JSeparator();
        jPBDownloadSingle = new javax.swing.JProgressBar();
        jSeparator6 = new javax.swing.JSeparator();
        jPBDownloadTotal = new javax.swing.JProgressBar();
        jSeparator3 = new javax.swing.JSeparator();
        jPInstallProgress = new javax.swing.JPanel();
        jPInstallProgressText = new javax.swing.JPanel();
        jLInstall = new javax.swing.JLabel();
        jLInstallSpeed = new javax.swing.JLabel();
        jSeparator8 = new javax.swing.JSeparator();
        jPBInstallSingle = new javax.swing.JProgressBar();
        jSeparator5 = new javax.swing.JSeparator();
        jPBInstallTotal = new javax.swing.JProgressBar();
        jSeparator4 = new javax.swing.JSeparator();
        jPPatchListTab = new javax.swing.JPanel();
        jSPPatchList = new javax.swing.JScrollPane();
        jPPatchList = new javax.swing.JPanel();
        jBInstallSelection = new javax.swing.JButton();
        jBInstallSelectNone = new javax.swing.JButton();
        jBInstallSelectAll = new javax.swing.JButton();
        jPSettingsTab = new javax.swing.JPanel();
        jLConfUltimaOnlinePath = new javax.swing.JLabel();
        jTConfUltimaOnlinePath = new javax.swing.JTextField();
        jBConfBrowseUltimaOnlinePath = new javax.swing.JButton();
        jLConfTempPath = new javax.swing.JLabel();
        jTConfTempPath = new javax.swing.JTextField();
        jBConfBrowseTempPath = new javax.swing.JButton();
        jLConfRunCommand = new javax.swing.JLabel();
        jTConfRunCommand = new javax.swing.JTextField();
        jBConfBrowseRunCommand = new javax.swing.JButton();
        jBConfLoad = new javax.swing.JButton();
        jBConfSave = new javax.swing.JButton();
        jChDebug = new javax.swing.JCheckBox();
        jBSetAllInstalled = new javax.swing.JButton();
        jLVersion = new javax.swing.JLabel();
        jBDeleteNWB = new javax.swing.JButton();
        jBDeleteIntro = new javax.swing.JButton();
        jBRenewRegistry = new javax.swing.JButton();
        jBRemoveTempFiles = new javax.swing.JButton();
        jLConfTempPath1 = new javax.swing.JLabel();
        jLConfTempSize = new javax.swing.JLabel();
        jBDownloadUO = new javax.swing.JButton();
        jLConfRunCommand1 = new javax.swing.JLabel();
        jTConfRunCommand1 = new javax.swing.JTextField();
        jBConfBrowseRunCommand1 = new javax.swing.JButton();
        jLConfRunCommand2 = new javax.swing.JLabel();
        jTConfRunCommand2 = new javax.swing.JTextField();
        jBConfBrowseRunCommand2 = new javax.swing.JButton();
        jPUoam = new javax.swing.JPanel();
        jLConfUoamServer = new javax.swing.JLabel();
        jLConfUoamPort = new javax.swing.JLabel();
        jLConfUoamName = new javax.swing.JLabel();
        jLConfUoamPassword = new javax.swing.JLabel();
        jTConfUoamServer = new javax.swing.JTextField();
        jTConfUoamPort = new javax.swing.JTextField();
        jTConfUoamName = new javax.swing.JTextField();
        jTpwConfUoamPassword = new javax.swing.JPasswordField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Andaria Patcher");
        setBackground(java.awt.Color.white);
        setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        setForeground(new java.awt.Color(163, 125, 86));
        setIconImage(getIcon("andaria.png"));
        setLocationByPlatform(true);
        setMaximizedBounds(new java.awt.Rectangle(0, 0, 2147483647, 2147483647));
        setName("AndariaPatcher"); // NOI18N
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.LINE_AXIS));

        jTPMain.setBackground(getBackground());
        jTPMain.setForeground(getForeground());
        jTPMain.setToolTipText("");
        jTPMain.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jTPMain.setMinimumSize(new java.awt.Dimension(727, 481));
        jTPMain.setName(""); // NOI18N
        jTPMain.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jTPMainStateChanged(evt);
            }
        });

        jPNews.setBackground(getBackground());
        jPNews.setForeground(getForeground());
        jPNews.setToolTipText("Klikni pro zobrazení nastavení programu nebo informacích o programu.");
        jPNews.setMinimumSize(new java.awt.Dimension(722, 452));

        jPNews1.setBackground(getBackground());
        jPNews1.setForeground(getForeground());
        jPNews1.setToolTipText("Klikni pro zobrazení nastavení programu nebo informacích o programu.");
        jPNews1.setMinimumSize(new java.awt.Dimension(722, 452));
        jPNews1.setLayout(new javax.swing.BoxLayout(jPNews1, javax.swing.BoxLayout.PAGE_AXIS));

        jBPachAndPlay.setBackground(new java.awt.Color(204, 255, 204));
        jBPachAndPlay.setForeground(getForeground());
        jBPachAndPlay.setText("Opatchuj (je-li co) a spusť hru");
        jBPachAndPlay.setToolTipText("Nastaví všechy patche v seznamu patchů jako nainstalované. Použij, pokud jsi spustil AndariaPatcher poprvé a máš již Ultimu plně opatchovanou souborama za shardu Andarie.");
        jBPachAndPlay.setFocusable(false);
        jBPachAndPlay.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jBPachAndPlay.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jBPachAndPlay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBPachAndPlayActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPNewsLayout = new org.jdesktop.layout.GroupLayout(jPNews);
        jPNews.setLayout(jPNewsLayout);
        jPNewsLayout.setHorizontalGroup(
            jPNewsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPNews1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 722, Short.MAX_VALUE)
            .add(jBPachAndPlay, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 722, Short.MAX_VALUE)
        );
        jPNewsLayout.setVerticalGroup(
            jPNewsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPNewsLayout.createSequentialGroup()
                .add(jPNews1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 423, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jBPachAndPlay))
        );

        jTPMain.addTab("Novinky", null, jPNews, "Tady se nachází novinky nejen ze světa...");

        jPControlsTab.setBackground(getBackground());
        jPControlsTab.setForeground(getForeground());
        jPControlsTab.setMinimumSize(new java.awt.Dimension(722, 452));
        jPControlsTab.setPreferredSize(new java.awt.Dimension(722, 452));
        jPControlsTab.setLayout(new javax.swing.BoxLayout(jPControlsTab, javax.swing.BoxLayout.Y_AXIS));

        jPButtons.setBackground(getBackground());
        jPButtons.setForeground(getForeground());
        jPButtons.setMaximumSize(new java.awt.Dimension(32767, 25));
        jPButtons.setMinimumSize(new java.awt.Dimension(400, 25));
        jPButtons.setOpaque(false);
        jPButtons.setPreferredSize(new java.awt.Dimension(400, 25));
        jPButtons.setLayout(new java.awt.GridLayout(1, 0));

        jBRefreshPatchList.setBackground(getBackground());
        jBRefreshPatchList.setForeground(getForeground());
        jBRefreshPatchList.setText("Obnovit seznam");
        jBRefreshPatchList.setToolTipText("Obnoví seznam souborů ze serveru a vybere soubory doporučené ke stažení.");
        jBRefreshPatchList.setEnabled(false);
        jBRefreshPatchList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBRefreshPatchListActionPerformed(evt);
            }
        });
        jPButtons.add(jBRefreshPatchList);

        jBInstall.setBackground(getBackground());
        jBInstall.setForeground(getForeground());
        jBInstall.setText("Stáhnout a instalovat");
        jBInstall.setEnabled(false);
        jBInstall.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBInstallActionPerformed(evt);
            }
        });
        jPButtons.add(jBInstall);

        jBCancel.setBackground(getBackground());
        jBCancel.setForeground(getForeground());
        jBCancel.setText("Zrušit akce");
        jBCancel.setEnabled(false);
        jBCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBCancelActionPerformed(evt);
            }
        });
        jPButtons.add(jBCancel);

        jBClose.setBackground(getBackground());
        jBClose.setForeground(getForeground());
        jBClose.setText("Zavřít program");
        jBClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBCloseActionPerformed(evt);
            }
        });
        jPButtons.add(jBClose);

        jPControlsTab.add(jPButtons);

        jSeparator1.setBackground(getBackground());
        jSeparator1.setForeground(getForeground());
        jSeparator1.setMaximumSize(new java.awt.Dimension(32767, 5));
        jSeparator1.setMinimumSize(new java.awt.Dimension(0, 5));
        jSeparator1.setPreferredSize(new java.awt.Dimension(50, 5));
        jPControlsTab.add(jSeparator1);

        jSPLog.setBackground(getBackground());
        jSPLog.setForeground(getForeground());

        jTLog.setBackground(getBackground());
        jTLog.setColumns(20);
        jTLog.setForeground(getForeground());
        jTLog.setRows(5);
        DefaultCaret caret = (DefaultCaret) jTLog.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        jSPLog.setViewportView(jTLog);

        jPControlsTab.add(jSPLog);

        jSeparator2.setBackground(getBackground());
        jSeparator2.setForeground(getForeground());
        jSeparator2.setMaximumSize(new java.awt.Dimension(32767, 10));
        jSeparator2.setMinimumSize(new java.awt.Dimension(0, 10));
        jSeparator2.setPreferredSize(new java.awt.Dimension(50, 10));
        jPControlsTab.add(jSeparator2);

        jPDownloadProgress.setBackground(getBackground());
        jPDownloadProgress.setForeground(getForeground());
        jPDownloadProgress.setLayout(new javax.swing.BoxLayout(jPDownloadProgress, javax.swing.BoxLayout.Y_AXIS));

        jPDownloadProgressText.setBackground(getBackground());
        jPDownloadProgressText.setLayout(new javax.swing.BoxLayout(jPDownloadProgressText, javax.swing.BoxLayout.X_AXIS));

        jLDownload.setBackground(getBackground());
        jLDownload.setForeground(getForeground());
        jLDownload.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLDownload.setLabelFor(jPBDownloadSingle);
        jLDownload.setText("Nic nestahuju");
        jLDownload.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jLDownload.setMaximumSize(new java.awt.Dimension(99999, 13));
        jLDownload.setMinimumSize(new java.awt.Dimension(400, 13));
        jLDownload.setPreferredSize(new java.awt.Dimension(400, 13));
        jPDownloadProgressText.add(jLDownload);

        jLDownloadSpeed.setBackground(getBackground());
        jLDownloadSpeed.setForeground(getForeground());
        jLDownloadSpeed.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLDownloadSpeed.setText("0 kbps");
        jLDownloadSpeed.setToolTipText("Aktuální rychlost stahování souboru.");
        jLDownloadSpeed.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jLDownloadSpeed.setMaximumSize(new java.awt.Dimension(50, 14));
        jPDownloadProgressText.add(jLDownloadSpeed);

        jPDownloadProgress.add(jPDownloadProgressText);

        jSeparator7.setBackground(getBackground());
        jSeparator7.setForeground(getForeground());
        jSeparator7.setMaximumSize(new java.awt.Dimension(32767, 5));
        jSeparator7.setMinimumSize(new java.awt.Dimension(0, 5));
        jSeparator7.setPreferredSize(new java.awt.Dimension(50, 5));
        jPDownloadProgress.add(jSeparator7);

        jPBDownloadSingle.setBackground(getBackground());
        jPBDownloadSingle.setForeground(getForeground());
        jPBDownloadSingle.setToolTipText("Průběh stahování aktuálního souboru.");
        jPBDownloadSingle.setBorder(null);
        jPBDownloadSingle.setStringPainted(true);
        jPDownloadProgress.add(jPBDownloadSingle);

        jSeparator6.setBackground(getBackground());
        jSeparator6.setForeground(getForeground());
        jSeparator6.setMaximumSize(new java.awt.Dimension(32767, 5));
        jSeparator6.setMinimumSize(new java.awt.Dimension(0, 5));
        jSeparator6.setPreferredSize(new java.awt.Dimension(50, 5));
        jPDownloadProgress.add(jSeparator6);

        jPBDownloadTotal.setBackground(getBackground());
        jPBDownloadTotal.setForeground(getForeground());
        jPBDownloadTotal.setToolTipText("Průběh celkového stahování vybraných updatů.");
        jPBDownloadTotal.setBorder(null);
        jPBDownloadTotal.setStringPainted(true);
        jPDownloadProgress.add(jPBDownloadTotal);

        jPControlsTab.add(jPDownloadProgress);

        jSeparator3.setBackground(getBackground());
        jSeparator3.setForeground(getForeground());
        jSeparator3.setMaximumSize(new java.awt.Dimension(32767, 10));
        jSeparator3.setMinimumSize(new java.awt.Dimension(0, 10));
        jSeparator3.setPreferredSize(new java.awt.Dimension(50, 10));
        jPControlsTab.add(jSeparator3);

        jPInstallProgress.setBackground(getBackground());
        jPInstallProgress.setForeground(getForeground());
        jPInstallProgress.setLayout(new javax.swing.BoxLayout(jPInstallProgress, javax.swing.BoxLayout.Y_AXIS));

        jPInstallProgressText.setBackground(getBackground());
        jPInstallProgressText.setLayout(new javax.swing.BoxLayout(jPInstallProgressText, javax.swing.BoxLayout.X_AXIS));

        jLInstall.setBackground(getBackground());
        jLInstall.setForeground(getForeground());
        jLInstall.setLabelFor(jPBInstallSingle);
        jLInstall.setText("Nic neinstaluju");
        jLInstall.setMaximumSize(new java.awt.Dimension(99999, 13));
        jLInstall.setMinimumSize(new java.awt.Dimension(400, 13));
        jLInstall.setPreferredSize(new java.awt.Dimension(400, 13));
        jPInstallProgressText.add(jLInstall);

        jLInstallSpeed.setBackground(getBackground());
        jLInstallSpeed.setForeground(getForeground());
        jLInstallSpeed.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLInstallSpeed.setText("0 kbps");
        jLInstallSpeed.setToolTipText("Aktuální rychlost rozbalování souboru. Čím rychlejší, tím máš výkonější počítač.");
        jLInstallSpeed.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jLInstallSpeed.setMaximumSize(new java.awt.Dimension(50, 14));
        jPInstallProgressText.add(jLInstallSpeed);

        jPInstallProgress.add(jPInstallProgressText);

        jSeparator8.setBackground(getBackground());
        jSeparator8.setForeground(getForeground());
        jSeparator8.setMaximumSize(new java.awt.Dimension(32767, 5));
        jSeparator8.setMinimumSize(new java.awt.Dimension(0, 5));
        jSeparator8.setPreferredSize(new java.awt.Dimension(50, 5));
        jPInstallProgress.add(jSeparator8);

        jPBInstallSingle.setBackground(getBackground());
        jPBInstallSingle.setForeground(getForeground());
        jPBInstallSingle.setToolTipText("Průběh instalace aktuálního patche.");
        jPBInstallSingle.setBorder(null);
        jPBInstallSingle.setStringPainted(true);
        jPInstallProgress.add(jPBInstallSingle);

        jSeparator5.setBackground(getBackground());
        jSeparator5.setForeground(getForeground());
        jSeparator5.setMaximumSize(new java.awt.Dimension(32767, 5));
        jSeparator5.setMinimumSize(new java.awt.Dimension(0, 5));
        jSeparator5.setPreferredSize(new java.awt.Dimension(50, 5));
        jPInstallProgress.add(jSeparator5);

        jPBInstallTotal.setBackground(getBackground());
        jPBInstallTotal.setForeground(getForeground());
        jPBInstallTotal.setToolTipText("Celkový průběh instalace vybraných updatů.");
        jPBInstallTotal.setBorder(null);
        jPBInstallTotal.setStringPainted(true);
        jPInstallProgress.add(jPBInstallTotal);

        jPControlsTab.add(jPInstallProgress);

        jSeparator4.setBackground(getBackground());
        jSeparator4.setForeground(getForeground());
        jSeparator4.setMaximumSize(new java.awt.Dimension(32767, 5));
        jSeparator4.setMinimumSize(new java.awt.Dimension(0, 5));
        jSeparator4.setPreferredSize(new java.awt.Dimension(50, 5));
        jPControlsTab.add(jSeparator4);

        jTPMain.addTab("Kontrolní panel", null, jPControlsTab, "Klikni zde a můžeš řídit činnost programu či sledovat průběh instalace...");

        jPPatchListTab.setBackground(getBackground());
        jPPatchListTab.setForeground(getForeground());

        jSPPatchList.setBackground(getBackground());
        jSPPatchList.setForeground(getForeground());
        jSPPatchList.setMinimumSize(new java.awt.Dimension(722, 452));
        jSPPatchList.setPreferredSize(new java.awt.Dimension(722, 452));

        jPPatchList.setBackground(getBackground());
        jPPatchList.setForeground(getForeground());
        jPPatchList.setLayout(new java.awt.GridLayout(1, 0));
        jSPPatchList.setViewportView(jPPatchList);

        jBInstallSelection.setBackground(getBackground());
        jBInstallSelection.setForeground(getForeground());
        jBInstallSelection.setText("Stáhni a instaluj vybrané soubory");
        jBInstallSelection.setEnabled(jBInstall.isEnabled());
        jBInstallSelection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBInstallActionPerformed(evt);
            }
        });

        jBInstallSelectNone.setBackground(getBackground());
        jBInstallSelectNone.setForeground(getForeground());
        jBInstallSelectNone.setText("Zruš výběr všech");
        jBInstallSelectNone.setToolTipText("Odškrtne všechny zaškrtlé patche.");
        jBInstallSelectNone.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBInstallSelectNonejBInstallActionPerformed(evt);
            }
        });

        jBInstallSelectAll.setBackground(getBackground());
        jBInstallSelectAll.setForeground(getForeground());
        jBInstallSelectAll.setText("Vyber všechny doporučené");
        jBInstallSelectAll.setToolTipText("Vybere všechny neaktuální doporučené patche a patche nastavené k automatické instalaci.");
        jBInstallSelectAll.setEnabled(false);
        jBInstallSelectAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBInstallSelectAlljBInstallActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPPatchListTabLayout = new org.jdesktop.layout.GroupLayout(jPPatchListTab);
        jPPatchListTab.setLayout(jPPatchListTabLayout);
        jPPatchListTabLayout.setHorizontalGroup(
            jPPatchListTabLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPPatchListTabLayout.createSequentialGroup()
                .add(jBInstallSelectAll, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 216, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jBInstallSelection, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jBInstallSelectNone, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 216, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(2, 2, 2))
            .add(jSPPatchList, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 722, Short.MAX_VALUE)
        );

        jPPatchListTabLayout.linkSize(new java.awt.Component[] {jBInstallSelectAll, jBInstallSelectNone}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        jPPatchListTabLayout.setVerticalGroup(
            jPPatchListTabLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPPatchListTabLayout.createSequentialGroup()
                .add(jSPPatchList, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 427, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPPatchListTabLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jBInstallSelection, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jBInstallSelectNone, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jBInstallSelectAll, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
        );

        jPPatchListTabLayout.linkSize(new java.awt.Component[] {jBInstallSelectAll, jBInstallSelectNone}, org.jdesktop.layout.GroupLayout.VERTICAL);

        jTPMain.addTab("Výběr souborů", null, jPPatchListTab, "Tady si můžeš vybrat jaké soubory se mají instalovat a jaké ne...");

        jPSettingsTab.setBackground(getBackground());
        jPSettingsTab.setForeground(getForeground());
        jPSettingsTab.setToolTipText("Klikni pro zobrazení nastavení programu nebo informacích o programu.");
        jPSettingsTab.setMinimumSize(new java.awt.Dimension(722, 452));

        jLConfUltimaOnlinePath.setBackground(getBackground());
        jLConfUltimaOnlinePath.setForeground(getForeground());
        jLConfUltimaOnlinePath.setLabelFor(jTConfUltimaOnlinePath);
        jLConfUltimaOnlinePath.setText("Adresář ultimy");
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("cz/polous/andaria/FrontEnd"); // NOI18N
        jLConfUltimaOnlinePath.setToolTipText(bundle.getString("UOPathTootlTip")); // NOI18N

        jTConfUltimaOnlinePath.setBackground(getBackground());
        jTConfUltimaOnlinePath.setColumns(30);
        jTConfUltimaOnlinePath.setForeground(getForeground());
        jTConfUltimaOnlinePath.setToolTipText(bundle.getString("UOPathTootlTip")); // NOI18N
        jTConfUltimaOnlinePath.setMinimumSize(new java.awt.Dimension(20, 19));

        jBConfBrowseUltimaOnlinePath.setBackground(getBackground());
        jBConfBrowseUltimaOnlinePath.setForeground(getForeground());
        jBConfBrowseUltimaOnlinePath.setText("Procházet");
        jBConfBrowseUltimaOnlinePath.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBConfBrowseUltimaOnlinePathActionPerformed(evt);
            }
        });

        jLConfTempPath.setBackground(getBackground());
        jLConfTempPath.setForeground(getForeground());
        jLConfTempPath.setLabelFor(jTConfTempPath);
        jLConfTempPath.setText("Dočasné úložiště:");
        jLConfTempPath.setToolTipText(bundle.getString("DownloadTempToolTip")); // NOI18N

        jTConfTempPath.setBackground(getBackground());
        jTConfTempPath.setColumns(30);
        jTConfTempPath.setForeground(getForeground());
        jTConfTempPath.setToolTipText(bundle.getString("DownloadTempToolTip")); // NOI18N
        jTConfTempPath.setMinimumSize(new java.awt.Dimension(200, 19));

        jBConfBrowseTempPath.setBackground(getBackground());
        jBConfBrowseTempPath.setForeground(getForeground());
        jBConfBrowseTempPath.setText("Procházet");
        jBConfBrowseTempPath.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBConfBrowseTempPathActionPerformed(evt);
            }
        });

        jLConfRunCommand.setBackground(getBackground());
        jLConfRunCommand.setForeground(getForeground());
        jLConfRunCommand.setLabelFor(jTConfRunCommand);
        jLConfRunCommand.setText("Spuštěný program 1");
        jLConfRunCommand.setToolTipText(bundle.getString("AutoRunToolTip")); // NOI18N

        jTConfRunCommand.setBackground(getBackground());
        jTConfRunCommand.setColumns(30);
        jTConfRunCommand.setForeground(getForeground());
        jTConfRunCommand.setToolTipText(bundle.getString("AutoRunToolTip")); // NOI18N
        jTConfRunCommand.setMinimumSize(new java.awt.Dimension(20, 19));

        jBConfBrowseRunCommand.setBackground(getBackground());
        jBConfBrowseRunCommand.setForeground(getForeground());
        jBConfBrowseRunCommand.setText("Procházet");
        jBConfBrowseRunCommand.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBConfBrowseRunCommandActionPerformed(evt);
            }
        });

        jBConfLoad.setBackground(getBackground());
        jBConfLoad.setForeground(getForeground());
        jBConfLoad.setText("Načti nastavení");
        jBConfLoad.setToolTipText(bundle.getString("ReadSettingsToolTip")); // NOI18N
        jBConfLoad.setMaximumSize(new java.awt.Dimension(300, 25));
        jBConfLoad.setMinimumSize(new java.awt.Dimension(100, 25));
        jBConfLoad.setPreferredSize(new java.awt.Dimension(130, 25));
        jBConfLoad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBConfLoadActionPerformed(evt);
            }
        });

        jBConfSave.setBackground(getBackground());
        jBConfSave.setForeground(getForeground());
        jBConfSave.setText("Ulož nastavení");
        jBConfSave.setToolTipText(bundle.getString("SaveSettingsToolTip")); // NOI18N
        jBConfSave.setMaximumSize(new java.awt.Dimension(300, 25));
        jBConfSave.setMinimumSize(new java.awt.Dimension(100, 25));
        jBConfSave.setPreferredSize(new java.awt.Dimension(130, 25));
        jBConfSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBConfSaveActionPerformed(evt);
            }
        });

        jChDebug.setBackground(getBackground());
        jChDebug.setForeground(getForeground());
        jChDebug.setText(bundle.getString("DebugModeToolTip")); // NOI18N
        jChDebug.setToolTipText("Po zaškrtnutí bude patcher zobrazovat detailní informace o své činnosti. Pokud chcete nahlásit chybu, zapněte tuto možnost a spolu s popisem chyby zašlete i výpis v okně Logu.");
        jChDebug.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jChDebug.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jChDebugStateChanged(evt);
            }
        });
        jChDebug.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jChDebugActionPerformed(evt);
            }
        });

        jBSetAllInstalled.setBackground(getBackground());
        jBSetAllInstalled.setForeground(getForeground());
        jBSetAllInstalled.setText("Nastavit všechny patche jako nainstalované");
        jBSetAllInstalled.setToolTipText(bundle.getString("SetAllInstalledToolTip")); // NOI18N
        jBSetAllInstalled.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBSetAllInstalledActionPerformed(evt);
            }
        });

        jLVersion.setBackground(getBackground());
        jLVersion.setForeground(getForeground());
        jLVersion.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLVersion.setText("Verze programu: 1.6beta1");
        jLVersion.setToolTipText(bundle.getString("VersionToolTip")); // NOI18N

        jBDeleteNWB.setBackground(getBackground());
        jBDeleteNWB.setForeground(getForeground());
        jBDeleteNWB.setText("Odstranit soubor desktop.nwb (použij, pokud máš problém se spuštěním UO)");
        jBDeleteNWB.setToolTipText(bundle.getString("RemoveDesktipNwbToolTip")); // NOI18N
        jBDeleteNWB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBDeleteNWBActionPerformed(evt);
            }
        });

        jBDeleteIntro.setBackground(getBackground());
        jBDeleteIntro.setForeground(getForeground());
        jBDeleteIntro.setText("Odstranit intro hry (úvodní videa)");
        jBDeleteIntro.setToolTipText(bundle.getString("RemoveIntroToolTip")); // NOI18N
        jBDeleteIntro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBDeleteIntroActionPerformed(evt);
            }
        });

        jBRenewRegistry.setBackground(getBackground());
        jBRenewRegistry.setForeground(getForeground());
        jBRenewRegistry.setText("Obnovit registry windows (vybereš adresář ve kterém je ultima nainstalovaná)");
        jBRenewRegistry.setToolTipText(bundle.getString("RenewRegistryToolTip")); // NOI18N
        jBRenewRegistry.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBRenewRegistryActionPerformed(evt);
            }
        });
        if (Settings.getInstance().getOs().getClass().toString().endsWith("LinuxOS"))
        jBRenewRegistry.setVisible(false);
        else jBRenewRegistry.setVisible(true);

        jBRemoveTempFiles.setBackground(getBackground());
        jBRemoveTempFiles.setForeground(getForeground());
        jBRemoveTempFiles.setText("Smazat z dočasného úložiště všechny stažené soubory.");
        jBRemoveTempFiles.setToolTipText(bundle.getString("DownloadTempEraseToolTip")); // NOI18N
        jBRemoveTempFiles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBRemoveTempFilesActionPerformed(evt);
            }
        });
        if (Settings.getInstance().getOs().getClass().toString().endsWith("LinuxOS"))
        jBRenewRegistry.setVisible(false);
        else jBRenewRegistry.setVisible(true);

        jLConfTempPath1.setBackground(getBackground());
        jLConfTempPath1.setForeground(getForeground());
        jLConfTempPath1.setLabelFor(jTConfTempPath);
        jLConfTempPath1.setText("Úložiště obsahuje:");
        jLConfTempPath1.setToolTipText(bundle.getString("DownloadTempSizeToolTip")); // NOI18N

        jLConfTempSize.setBackground(getBackground());
        jLConfTempSize.setForeground(getForeground());
        jLConfTempSize.setLabelFor(jTConfTempPath);
        jLConfTempSize.setText("0kB");
        jLConfTempSize.setToolTipText(bundle.getString("DownloadTempSizeToolTip")); // NOI18N

        jBDownloadUO.setBackground(getBackground());
        jBDownloadUO.setForeground(getForeground());
        jBDownloadUO.setText("Instalovat Ultimu Online Monday's Legacy pro shard Andaria.net jedním klikem");
        jBDownloadUO.setToolTipText(bundle.getString("InstallUomlToolTip")); // NOI18N
        jBDownloadUO.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBDownloadUOActionPerformed(evt);
            }
        });

        jLConfRunCommand1.setBackground(getBackground());
        jLConfRunCommand1.setForeground(getForeground());
        jLConfRunCommand1.setLabelFor(jTConfRunCommand);
        jLConfRunCommand1.setText("Spuštěný program 2");
        jLConfRunCommand1.setToolTipText(bundle.getString("AutoRunToolTip")); // NOI18N

        jTConfRunCommand1.setBackground(getBackground());
        jTConfRunCommand1.setColumns(30);
        jTConfRunCommand1.setForeground(getForeground());
        jTConfRunCommand1.setToolTipText(bundle.getString("AutoRunToolTip")); // NOI18N
        jTConfRunCommand1.setMinimumSize(new java.awt.Dimension(20, 19));

        jBConfBrowseRunCommand1.setBackground(getBackground());
        jBConfBrowseRunCommand1.setForeground(getForeground());
        jBConfBrowseRunCommand1.setText("Procházet");
        jBConfBrowseRunCommand1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBConfBrowseRunCommand1ActionPerformed(evt);
            }
        });

        jLConfRunCommand2.setBackground(getBackground());
        jLConfRunCommand2.setForeground(getForeground());
        jLConfRunCommand2.setLabelFor(jTConfRunCommand);
        jLConfRunCommand2.setText("Spuštěný program 3");
        jLConfRunCommand2.setToolTipText(bundle.getString("AutoRunToolTip")); // NOI18N

        jTConfRunCommand2.setBackground(getBackground());
        jTConfRunCommand2.setColumns(30);
        jTConfRunCommand2.setForeground(getForeground());
        jTConfRunCommand2.setToolTipText(bundle.getString("AutoRunToolTip")); // NOI18N
        jTConfRunCommand2.setMinimumSize(new java.awt.Dimension(20, 19));

        jBConfBrowseRunCommand2.setBackground(getBackground());
        jBConfBrowseRunCommand2.setForeground(getForeground());
        jBConfBrowseRunCommand2.setText("Procházet");
        jBConfBrowseRunCommand2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBConfBrowseRunCommand2ActionPerformed(evt);
            }
        });

        jPUoam.setBackground(getBackground());
        jPUoam.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Nastavení UOAM", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), getForeground())); // NOI18N
        jPUoam.setForeground(getForeground());
        jPUoam.setToolTipText(bundle.getString("UoamTootlTip")); // NOI18N

        jLConfUoamServer.setBackground(getBackground());
        jLConfUoamServer.setForeground(getForeground());
        jLConfUoamServer.setLabelFor(jTConfUltimaOnlinePath);
        jLConfUoamServer.setText("Server:");
        jLConfUoamServer.setToolTipText(jTConfUoamServer.getToolTipText());
        jLConfUoamServer.setEnabled(jPUoam.isEnabled());

        jLConfUoamPort.setBackground(getBackground());
        jLConfUoamPort.setForeground(getForeground());
        jLConfUoamPort.setLabelFor(jTConfUltimaOnlinePath);
        jLConfUoamPort.setText("Port:");
        jLConfUoamPort.setToolTipText(jTConfUoamPort.getToolTipText());
        jLConfUoamPort.setEnabled(jPUoam.isEnabled());

        jLConfUoamName.setBackground(getBackground());
        jLConfUoamName.setForeground(getForeground());
        jLConfUoamName.setLabelFor(jTConfUltimaOnlinePath);
        jLConfUoamName.setText("Jméno:");
        jLConfUoamName.setToolTipText(jTConfUoamName.getToolTipText());
        jLConfUoamName.setEnabled(jPUoam.isEnabled());

        jLConfUoamPassword.setBackground(getBackground());
        jLConfUoamPassword.setForeground(getForeground());
        jLConfUoamPassword.setLabelFor(jTConfUltimaOnlinePath);
        jLConfUoamPassword.setText("Heslo:");
        jLConfUoamPassword.setToolTipText(bundle.getString("UoamPasswordTootlTip")); // NOI18N
        jLConfUoamPassword.setEnabled(jPUoam.isEnabled());

        jTConfUoamServer.setBackground(getBackground());
        jTConfUoamServer.setColumns(30);
        jTConfUoamServer.setForeground(getForeground());
        jTConfUoamServer.setToolTipText(bundle.getString("UoamServerTootlTip")); // NOI18N
        jTConfUoamServer.setEnabled(jPUoam.isEnabled());
        jTConfUoamServer.setMinimumSize(new java.awt.Dimension(200, 19));

        jTConfUoamPort.setBackground(getBackground());
        jTConfUoamPort.setColumns(30);
        jTConfUoamPort.setForeground(getForeground());
        jTConfUoamPort.setToolTipText(bundle.getString("UoamPortTootlTip")); // NOI18N
        jTConfUoamPort.setEnabled(jPUoam.isEnabled());
        jTConfUoamPort.setMinimumSize(new java.awt.Dimension(200, 19));

        jTConfUoamName.setBackground(getBackground());
        jTConfUoamName.setColumns(30);
        jTConfUoamName.setForeground(getForeground());
        jTConfUoamName.setToolTipText(bundle.getString("UoamNameTootlTip")); // NOI18N
        jTConfUoamName.setEnabled(jPUoam.isEnabled());
        jTConfUoamName.setMinimumSize(new java.awt.Dimension(200, 19));

        jTpwConfUoamPassword.setBackground(getBackground());
        jTpwConfUoamPassword.setForeground(getForeground());
        jTpwConfUoamPassword.setText("jPasswordField1");
        jTpwConfUoamPassword.setToolTipText(bundle.getString("UoamPasswordTootlTip")); // NOI18N
        jTpwConfUoamPassword.setEnabled(jPUoam.isEnabled());

        org.jdesktop.layout.GroupLayout jPUoamLayout = new org.jdesktop.layout.GroupLayout(jPUoam);
        jPUoam.setLayout(jPUoamLayout);
        jPUoamLayout.setHorizontalGroup(
            jPUoamLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPUoamLayout.createSequentialGroup()
                .addContainerGap()
                .add(jLConfUoamServer)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jTConfUoamServer, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 144, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jLConfUoamPort)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jTConfUoamPort, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 70, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jLConfUoamName)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jTConfUoamName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 169, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jLConfUoamPassword)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jTpwConfUoamPassword, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 122, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPUoamLayout.setVerticalGroup(
            jPUoamLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPUoamLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(jLConfUoamServer)
                .add(jTConfUoamServer, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(jTConfUoamPort, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(jLConfUoamName)
                .add(jLConfUoamPort)
                .add(jTConfUoamName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(jLConfUoamPassword)
                .add(jTpwConfUoamPassword, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        org.jdesktop.layout.GroupLayout jPSettingsTabLayout = new org.jdesktop.layout.GroupLayout(jPSettingsTab);
        jPSettingsTab.setLayout(jPSettingsTabLayout);
        jPSettingsTabLayout.setHorizontalGroup(
            jPSettingsTabLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPSettingsTabLayout.createSequentialGroup()
                .addContainerGap()
                .add(jPSettingsTabLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPSettingsTabLayout.createSequentialGroup()
                        .add(jPUoam, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .add(jPSettingsTabLayout.createSequentialGroup()
                        .add(jPSettingsTabLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jBRenewRegistry, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 708, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jBSetAllInstalled, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 708, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jBDownloadUO, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 708, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jPSettingsTabLayout.createSequentialGroup()
                                .add(jPSettingsTabLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(jLConfTempPath, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 88, Short.MAX_VALUE)
                                    .add(jLConfTempPath1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jPSettingsTabLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPSettingsTabLayout.createSequentialGroup()
                                        .add(jLConfTempSize, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(jBRemoveTempFiles, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 499, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPSettingsTabLayout.createSequentialGroup()
                                        .add(jTConfTempPath, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 525, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(jBConfBrowseTempPath))))
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jChDebug)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jBDeleteIntro, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 708, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jBDeleteNWB, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 708, Short.MAX_VALUE)
                            .add(jPSettingsTabLayout.createSequentialGroup()
                                .add(jPSettingsTabLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(jPSettingsTabLayout.createSequentialGroup()
                                        .add(jPSettingsTabLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                            .add(jLConfUltimaOnlinePath)
                                            .add(jLConfRunCommand, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 98, Short.MAX_VALUE))
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED))
                                    .add(jLConfRunCommand1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 102, Short.MAX_VALUE)
                                    .add(jLConfRunCommand2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 102, Short.MAX_VALUE))
                                .add(jPSettingsTabLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPSettingsTabLayout.createSequentialGroup()
                                        .add(jPSettingsTabLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                            .add(jTConfRunCommand2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 517, Short.MAX_VALUE)
                                            .add(jTConfRunCommand1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 517, Short.MAX_VALUE))
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(jPSettingsTabLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                            .add(jBConfBrowseRunCommand2)
                                            .add(jBConfBrowseRunCommand1)))
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPSettingsTabLayout.createSequentialGroup()
                                        .add(jTConfRunCommand, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 517, Short.MAX_VALUE)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(jBConfBrowseRunCommand))
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPSettingsTabLayout.createSequentialGroup()
                                        .add(jTConfUltimaOnlinePath, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 517, Short.MAX_VALUE)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(jBConfBrowseUltimaOnlinePath)))))
                        .add(10, 10, 10))
                    .add(jPSettingsTabLayout.createSequentialGroup()
                        .add(jBConfLoad, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 185, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLVersion, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 355, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jBConfSave, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 160, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );
        jPSettingsTabLayout.setVerticalGroup(
            jPSettingsTabLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPSettingsTabLayout.createSequentialGroup()
                .addContainerGap()
                .add(jPSettingsTabLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jBConfBrowseUltimaOnlinePath)
                    .add(jLConfUltimaOnlinePath)
                    .add(jTConfUltimaOnlinePath, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPSettingsTabLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jTConfRunCommand, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLConfRunCommand)
                    .add(jBConfBrowseRunCommand))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPSettingsTabLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jTConfRunCommand1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLConfRunCommand1)
                    .add(jBConfBrowseRunCommand1))
                .add(jPSettingsTabLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPSettingsTabLayout.createSequentialGroup()
                        .add(9, 9, 9)
                        .add(jLConfRunCommand2))
                    .add(jPSettingsTabLayout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPSettingsTabLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jTConfRunCommand2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jBConfBrowseRunCommand2))))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jBDeleteIntro)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jBDeleteNWB)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPSettingsTabLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLConfTempPath)
                    .add(jTConfTempPath, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jBConfBrowseTempPath))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPSettingsTabLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLConfTempPath1)
                    .add(jLConfTempSize)
                    .add(jBRemoveTempFiles, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 23, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jChDebug)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jBDownloadUO)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jBSetAllInstalled)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jBRenewRegistry)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jPUoam, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(11, 11, 11)
                .add(jPSettingsTabLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jBConfLoad, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jBConfSave, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLVersion))
                .add(23, 23, 23))
        );

        jBRemoveTempFiles.getAccessibleContext().setAccessibleDescription("Pomůže uvolnit nějaké to místo na disku. ");
        jBDownloadUO.getAccessibleContext().setAccessibleDescription("Nainstaluje Ultimu Online a vše potřebné pro hraní na Shardu Andaria.net do adresáře, který jsi zvolil při spuštění AndariaPatcheru. Složku můžeš změnit zde v nastavení - položka \"Adresář ultimy\".");

        jTPMain.addTab("Nastavení", null, jPSettingsTab, "Klikni pro zobrazení panelu s nastavením Andaria Patcheru...");

        getContentPane().add(jTPMain);

        getAccessibleContext().setAccessibleDescription("Instalator souboru potrebnych pro hrani na Ultima Online RP Free Shradu Adaria");

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void jBSetAllInstalledActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBSetAllInstalledActionPerformed
        if (0 == JOptionPane.showConfirmDialog(null, "Chceš opravdu nastavit všechny soubory jako nainstalované ?", "Zásadní otázka...", JOptionPane.YES_NO_OPTION)) {
            PatchList.getInstance().setAllInstalled();
        }
    }//GEN-LAST:event_jBSetAllInstalledActionPerformed

    private void jChDebugStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jChDebugStateChanged
        Settings.getInstance().setValue("debug_log", jChDebug.isSelected() ? "1" : "0");
    }//GEN-LAST:event_jChDebugStateChanged

    private void jBCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBCloseActionPerformed
        closeMe();
    }//GEN-LAST:event_jBCloseActionPerformed

    private void jBCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBCancelActionPerformed
        PatchList.getInstance().cancel();
        // updateButtons();
    }//GEN-LAST:event_jBCancelActionPerformed

    private void jBConfLoadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBConfLoadActionPerformed
        loadSettings();
    }//GEN-LAST:event_jBConfLoadActionPerformed

    private void jBConfSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBConfSaveActionPerformed
        saveSettings();
    }//GEN-LAST:event_jBConfSaveActionPerformed

    private void jBConfBrowseRunCommandActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBConfBrowseRunCommandActionPerformed
        String tmp = Settings.getInstance().openFile("Vyber první program, který mám spustit po ukončení patcheru tlačítkem zavřít", jTConfRunCommand.getText(), JFileChooser.FILES_ONLY);
        if (tmp != null) {
            jTConfRunCommand.setText(tmp);
        }
    }//GEN-LAST:event_jBConfBrowseRunCommandActionPerformed

    private void jBConfBrowseTempPathActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBConfBrowseTempPathActionPerformed
        String tmp = Settings.getInstance().openFile("Vyber adresář kam stahovat soubory", jTConfTempPath.getText(), JFileChooser.DIRECTORIES_ONLY);
        if (tmp != null) {
            jTConfTempPath.setText(tmp);
        }
    }//GEN-LAST:event_jBConfBrowseTempPathActionPerformed

    private void jBConfBrowseUltimaOnlinePathActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBConfBrowseUltimaOnlinePathActionPerformed
        String tmp = Settings.getInstance().openFile("Vyber adresář s Ultimou", jTConfUltimaOnlinePath.getText(), JFileChooser.DIRECTORIES_ONLY);
        if (tmp != null) {
            jTConfUltimaOnlinePath.setText(tmp);
        }
    }//GEN-LAST:event_jBConfBrowseUltimaOnlinePathActionPerformed

    private void jBInstallActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBInstallActionPerformed
        installPatches();
    }//GEN-LAST:event_jBInstallActionPerformed

    private void jBRefreshPatchListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBRefreshPatchListActionPerformed
        reloadPatchList();
    }//GEN-LAST:event_jBRefreshPatchListActionPerformed

    private void jBDeleteIntroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBDeleteIntroActionPerformed
        if (0 == JOptionPane.showConfirmDialog(null, "Chceš opravdu smazat soubory s úvodníma videama ?", "Zásadní otázka...", JOptionPane.YES_NO_OPTION)) {
            Settings.getInstance().getOs().deleteUOFile("Music", "Intro.bik");
            Settings.getInstance().getOs().deleteUOFile("Music", "ealogo.bik");
            Settings.getInstance().getOs().deleteUOFile("Music", "osilogo.bik");
        }
    }//GEN-LAST:event_jBDeleteIntroActionPerformed

    private void jBDeleteNWBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBDeleteNWBActionPerformed
        if (0 == JOptionPane.showConfirmDialog(null, "Chceš opravdu smazat soubor desktop.nwb ?", "Zásadní otázka...", JOptionPane.YES_NO_OPTION)) {
            Settings.getInstance().getOs().deleteUOFile(".", "desktop.nwb");
        }
}//GEN-LAST:event_jBDeleteNWBActionPerformed

    private void jChDebugActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jChDebugActionPerformed
    }//GEN-LAST:event_jChDebugActionPerformed

    private void jBRenewRegistryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBRenewRegistryActionPerformed
        // button can't be visible on linux machines
        WindowsOS winos = (WindowsOS) Settings.getInstance().getOs();
        winos.renewWindowsRegistry();
        loadSettings();
}//GEN-LAST:event_jBRenewRegistryActionPerformed

    private void jBRemoveTempFilesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBRemoveTempFilesActionPerformed
        String dir = Settings.getInstance().getValue(Settings.VALUES.LOCAL_STORAGE);
        Object[] opts = {"Smaž to všechno", "Rozmyslel jsem si to"};
        int potvrzeni = JOptionPane.showOptionDialog(null, "Opravdu si přeješ smazat obsah adresáře \"".concat(dir).concat("\" ?"), "Otázečka...", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, opts, opts[0]);
        if (potvrzeni == JOptionPane.YES_OPTION) {

            File tmpDir = new File(dir);
            String[] fileList = tmpDir.list();
            File file;

            for (int i = 0; i < fileList.length; i++) {
                file = new File(tmpDir.getAbsolutePath().concat(File.separator).concat(fileList[i]));
                if (file.delete()) {
                    log.addLine("Smazal jsem soubor: ".concat(file.getAbsolutePath()));
                } else {
                    log.addErr("Nepodařilo se smazat soubor: ".concat(file.getAbsolutePath()));
                }
                Settings.getInstance().updateTempSize();
            }
        }
}//GEN-LAST:event_jBRemoveTempFilesActionPerformed

    private void jTPMainStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jTPMainStateChanged
    }//GEN-LAST:event_jTPMainStateChanged

    private void jBInstallSelectNonejBInstallActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBInstallSelectNonejBInstallActionPerformed
        PatchList.getInstance().selectNone();
}//GEN-LAST:event_jBInstallSelectNonejBInstallActionPerformed

    private void jBInstallSelectAlljBInstallActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBInstallSelectAlljBInstallActionPerformed
        PatchList.getInstance().selectAll();
}//GEN-LAST:event_jBInstallSelectAlljBInstallActionPerformed

    private void jBDownloadUOActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBDownloadUOActionPerformed
        if (0 == JOptionPane.showConfirmDialog(null,
                "Jsi si opravdu jistý, že chceš stáhnout a nainstalovat " +
                "\nUO Monday's Legacy do složky zvolené v nastavení (Adresář Ultimy) ?" +
                "\n\nPro tuto akci budeš potřebovat cca 700MB místa na disku kde jsou Windows" +
                "\na cca 1,7GB na mítě kam plánuješ uo nainstalovat. Tedy celkem počítej 2,4GB." +
                "\n(těch 700 pak můžeš smazat v nastavení AndariaPatcheru).", "Velmi zásadní otázka...", JOptionPane.YES_NO_OPTION)) {
            Settings.setAutoInstall(Settings.AUTO_LEVELS.AUTO_UPDATE);
            installUO();
        }
    }//GEN-LAST:event_jBDownloadUOActionPerformed

    private void jBConfBrowseRunCommand1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBConfBrowseRunCommand1ActionPerformed
        String tmp = Settings.getInstance().openFile("Vyber druhý program, který mám spustit po ukončení patcheru tlačítkem zavřít", jTConfRunCommand.getText(), JFileChooser.FILES_ONLY);
        if (tmp != null) {
            jTConfRunCommand1.setText(tmp);
        }
    }//GEN-LAST:event_jBConfBrowseRunCommand1ActionPerformed

    private void jBConfBrowseRunCommand2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBConfBrowseRunCommand2ActionPerformed
        String tmp = Settings.getInstance().openFile("Vyber třetí program, který mám spustit po ukončení patcheru tlačítkem zavřít", jTConfRunCommand.getText(), JFileChooser.FILES_ONLY);
        if (tmp != null) {
            jTConfRunCommand2.setText(tmp);
        }
    }//GEN-LAST:event_jBConfBrowseRunCommand2ActionPerformed

    private void jBPachAndPlayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBPachAndPlayActionPerformed
        Settings.setAutoInstall(Settings.AUTO_LEVELS.AUTO_CLOSE);
        installPatches();
}//GEN-LAST:event_jBPachAndPlayActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jBCancel;
    private javax.swing.JButton jBClose;
    private javax.swing.JButton jBConfBrowseRunCommand;
    private javax.swing.JButton jBConfBrowseRunCommand1;
    private javax.swing.JButton jBConfBrowseRunCommand2;
    private javax.swing.JButton jBConfBrowseTempPath;
    private javax.swing.JButton jBConfBrowseUltimaOnlinePath;
    private javax.swing.JButton jBConfLoad;
    private javax.swing.JButton jBConfSave;
    private javax.swing.JButton jBDeleteIntro;
    private javax.swing.JButton jBDeleteNWB;
    private javax.swing.JButton jBDownloadUO;
    private javax.swing.JButton jBInstall;
    private javax.swing.JButton jBInstallSelectAll;
    private javax.swing.JButton jBInstallSelectNone;
    private javax.swing.JButton jBInstallSelection;
    private javax.swing.JButton jBPachAndPlay;
    private javax.swing.JButton jBRefreshPatchList;
    private javax.swing.JButton jBRemoveTempFiles;
    private javax.swing.JButton jBRenewRegistry;
    private javax.swing.JButton jBSetAllInstalled;
    private javax.swing.JCheckBox jChDebug;
    private javax.swing.JLabel jLConfRunCommand;
    private javax.swing.JLabel jLConfRunCommand1;
    private javax.swing.JLabel jLConfRunCommand2;
    private javax.swing.JLabel jLConfTempPath;
    private javax.swing.JLabel jLConfTempPath1;
    public javax.swing.JLabel jLConfTempSize;
    private javax.swing.JLabel jLConfUltimaOnlinePath;
    private javax.swing.JLabel jLConfUoamName;
    private javax.swing.JLabel jLConfUoamPassword;
    private javax.swing.JLabel jLConfUoamPort;
    private javax.swing.JLabel jLConfUoamServer;
    private javax.swing.JLabel jLDownload;
    private javax.swing.JLabel jLDownloadSpeed;
    private javax.swing.JLabel jLInstall;
    private javax.swing.JLabel jLInstallSpeed;
    private javax.swing.JLabel jLVersion;
    private javax.swing.JProgressBar jPBDownloadSingle;
    private javax.swing.JProgressBar jPBDownloadTotal;
    private javax.swing.JProgressBar jPBInstallSingle;
    private javax.swing.JProgressBar jPBInstallTotal;
    private javax.swing.JPanel jPButtons;
    private javax.swing.JPanel jPControlsTab;
    private javax.swing.JPanel jPDownloadProgress;
    private javax.swing.JPanel jPDownloadProgressText;
    private javax.swing.JPanel jPInstallProgress;
    private javax.swing.JPanel jPInstallProgressText;
    private javax.swing.JPanel jPNews;
    private javax.swing.JPanel jPNews1;
    private javax.swing.JPanel jPPatchList;
    private javax.swing.JPanel jPPatchListTab;
    private javax.swing.JPanel jPSettingsTab;
    private javax.swing.JPanel jPUoam;
    private javax.swing.JScrollPane jSPLog;
    private javax.swing.JScrollPane jSPPatchList;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JSeparator jSeparator8;
    private javax.swing.JTextField jTConfRunCommand;
    private javax.swing.JTextField jTConfRunCommand1;
    private javax.swing.JTextField jTConfRunCommand2;
    private javax.swing.JTextField jTConfTempPath;
    private javax.swing.JTextField jTConfUltimaOnlinePath;
    private javax.swing.JTextField jTConfUoamName;
    private javax.swing.JTextField jTConfUoamPort;
    private javax.swing.JTextField jTConfUoamServer;
    private javax.swing.JTextArea jTLog;
    private javax.swing.JTabbedPane jTPMain;
    private javax.swing.JPasswordField jTpwConfUoamPassword;
    // End of variables declaration//GEN-END:variables
}

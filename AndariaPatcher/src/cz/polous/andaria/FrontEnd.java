package cz.polous.andaria;

import java.io.IOException;
import java.awt.*;
import javax.swing.*;
import java.io.File;
import java.net.URL;
import java.util.Vector;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
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
    private PatchList patchList; // representation of patchlist, patch procedure control object    private final Settings settings = ;

    public static FrontEnd getInstance() {
        return INSTANCE;
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

        jSPPatchList.getVerticalScrollBar().setUnitIncrement(17);

        HtmlPanel htmlPNews = new HtmlPanel();
        jTPMain.insertTab(null, null, htmlPNews, "Tady se nachází novinky nejen ze světa...", 0);
        jTPMain.setTitleAt(0, "Novinky");
        Browser news = new Browser(htmlPNews, Settings.getInstance().getValue(Settings.NEWS_URL));

        HtmlPanel htmlPAbout = new HtmlPanel();
        jTPMain.addTab(null, null, htmlPAbout, "Taky něco o programu samotném.");
        jTPMain.setTitleAt(jTPMain.getComponentCount() - 1, "O programu");
        final Browser about = new Browser(htmlPAbout, Settings.getInstance().getValue(Settings.ABOUT_URL));

        jTPMain.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent changeEvent) {
                //log.addDebug("Change of about changed.");
                if (jTPMain.getSelectedIndex() == jTPMain.getComponentCount() - 1) {
                    about.reload();
                }
            }
        });

        jTPMain.setSelectedIndex(0);

        patchList = PatchList.getInstance();

        if (Settings.getInstance().debugMode()) {
            log.addDebug(System.getProperty("os.name"));
            log.addDebug(System.getProperty("user.home"));
            log.addDebug(System.getProperty("java.io.tmpdir"));
        }
    }

    /***************************************************************************
     * Application runner
     * @param args the command line arguments
     **************************************************************************/
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {

            public void run() {
                new FrontEnd().setVisible(true);
            }
        });
    }

    /***************************************************************************
     * PatchList pl object inicialization, display list of patches at jPList panel.
     **************************************************************************/
    private void reloadPatchList() {
        if (patchList.inProgress()) {
            patchList.cancel();
        }
        jBInstallSelection.setEnabled(false);
        jBInstall.setEnabled(false);
        patchList.reload();
    }

    /***************************************************************************
     * Inicialize application settings and settings form.
     **************************************************************************/
    private void loadSettings() {
        Settings.getInstance().load();

        jTConfRunCommand.setText(Settings.getInstance().getValue(Settings.RUN_COMMAND));
        jTConfUnRARCommand.setText(Settings.getInstance().getValue(Settings.UNRAR_PATH));
        jTConfUltimaOnlinePath.setText(Settings.getInstance().getValue(Settings.ULTIMA_ONINE_PATH));
        jTConfTempPath.setText(Settings.getInstance().getValue(Settings.LOCAL_STORAGE));
        jChDebug.setSelected(Settings.getInstance().debugMode());
    }

    /***************************************************************************
     * Store application Settings.getInstance().
     **************************************************************************/
    private void saveSettings() {
        Settings.getInstance().setValue(Settings.RUN_COMMAND, jTConfRunCommand.getText());
        Settings.getInstance().setValue(Settings.UNRAR_PATH, jTConfUnRARCommand.getText());
        Settings.getInstance().setValue(Settings.ULTIMA_ONINE_PATH, jTConfUltimaOnlinePath.getText());
        Settings.getInstance().setValue(Settings.LOCAL_STORAGE, jTConfTempPath.getText());
        Settings.getInstance().setValue(Settings.DEBUG_MODE, jChDebug.isSelected() ? "1" : "0");

        Settings.getInstance().save();
    }

    private Image getIcon(String fileName) {
        URL res = getClass().getResource(fileName);
        return res == null ? null : Toolkit.getDefaultToolkit().getImage(res);
    }

    public JProgressBar getjPBDownloadSingle() {
        return jPBDownloadSingle;
    }

    public JProgressBar getjPBDownloadTotal() {
        return jPBDownloadTotal;
    }

    public JLabel getjLDownload() {
        return jLDownload;
    }

    public JProgressBar getjPBInstall() {
        return jPBInstall;
    }

    public JProgressBar getjPBTotal() {
        return jPBTotal;
    }

    public JLabel getjLInstall() {
        return jLInstall;
    }

    /***************************************************************************
     * Refresh patchlist tab content
     *
     * @link cz.polous.andaria.PatchList.getInaPanel()
     **************************************************************************/
    public void refreshPlPane() {
        Vector jPlist = patchList.getInPanel();
        jPPatchList.removeAll();
        int i;
        for (i = 0; i < jPlist.size(); i++) {
            jPPatchList.add((JPanel) jPlist.get(i));
        }
        if (i > 0) {
            jPPatchList.setLayout(new GridLayout(patchList.getCount(), 0));
        }
        updateButtons();
        pack();
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
            if (patchList.inProgress()) {
                jBInstall.setEnabled(false);
                jBInstallSelection.setEnabled(false);
                jBCancel.setEnabled(true);
                jBClose.setEnabled(false);

                jBRefreshPatchList.setEnabled(false);
            } else {
                jBInstall.setEnabled(true);
                jBInstallSelection.setEnabled(true);
                jBCancel.setEnabled(false);
                jBClose.setEnabled(true);
                jBRefreshPatchList.setEnabled(true);
            }
        } catch (NullPointerException e) {
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTPMain = new javax.swing.JTabbedPane();
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
        jLDownload = new javax.swing.JLabel();
        jSeparator7 = new javax.swing.JSeparator();
        jPBDownloadSingle = new javax.swing.JProgressBar();
        jSeparator6 = new javax.swing.JSeparator();
        jPBDownloadTotal = new javax.swing.JProgressBar();
        jSeparator3 = new javax.swing.JSeparator();
        jPInstallProgress = new javax.swing.JPanel();
        jLInstall = new javax.swing.JLabel();
        jSeparator8 = new javax.swing.JSeparator();
        jPBInstall = new javax.swing.JProgressBar();
        jSeparator5 = new javax.swing.JSeparator();
        jPBTotal = new javax.swing.JProgressBar();
        jSeparator4 = new javax.swing.JSeparator();
        jPPatchListTab = new javax.swing.JPanel();
        jSPPatchList = new javax.swing.JScrollPane();
        jPPatchList = new javax.swing.JPanel();
        jBInstallSelection = new javax.swing.JButton();
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
        jLConfUnRARCommand = new javax.swing.JLabel();
        jTConfUnRARCommand = new javax.swing.JTextField();
        jBConfBrowseUnRARCommand = new javax.swing.JButton();
        jBConfLoad = new javax.swing.JButton();
        jBConfSave = new javax.swing.JButton();
        jSeparator9 = new javax.swing.JSeparator();
        jChDebug = new javax.swing.JCheckBox();
        jBSetAllInstalled = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jSeparator10 = new javax.swing.JSeparator();
        jBDeleteNWB = new javax.swing.JButton();
        jBDeleteIntro = new javax.swing.JButton();
        jBConfBrowseUnRARCommand1 = new javax.swing.JButton();
        jBRenewRegistry = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Andaria Patcher");
        setBackground(java.awt.Color.white);
        setFont(new java.awt.Font("Verdana", 1, 12));
        setForeground(new java.awt.Color(163, 125, 86));
        setIconImage(getIcon("andaria.png"));
        setLocationByPlatform(true);
        setMaximizedBounds(new java.awt.Rectangle(0, 0, 2147483647, 2147483647));
        setName("AndariaPatcher"); // NOI18N
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.LINE_AXIS));

        jTPMain.setBackground(getBackground());
        jTPMain.setForeground(getForeground());
        jTPMain.setToolTipText("");
        jTPMain.setFont(new java.awt.Font("Verdana", 1, 14));
        jTPMain.setMinimumSize(new java.awt.Dimension(727, 481));
        jTPMain.setName(""); // NOI18N

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

        jLDownload.setBackground(getBackground());
        jLDownload.setForeground(getForeground());
        jLDownload.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLDownload.setLabelFor(jPBDownloadSingle);
        jLDownload.setText("Nic nestahuju");
        jLDownload.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jLDownload.setMaximumSize(new java.awt.Dimension(99999, 13));
        jLDownload.setMinimumSize(new java.awt.Dimension(400, 13));
        jLDownload.setPreferredSize(new java.awt.Dimension(400, 13));
        jPDownloadProgress.add(jLDownload);

        jSeparator7.setBackground(getBackground());
        jSeparator7.setForeground(getForeground());
        jSeparator7.setMaximumSize(new java.awt.Dimension(32767, 5));
        jSeparator7.setMinimumSize(new java.awt.Dimension(0, 5));
        jSeparator7.setPreferredSize(new java.awt.Dimension(50, 5));
        jPDownloadProgress.add(jSeparator7);

        jPBDownloadSingle.setBackground(getBackground());
        jPBDownloadSingle.setForeground(getForeground());
        jPBDownloadSingle.setToolTipText("Prubeh stahovani aktualniho souboru.");
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
        jPBDownloadTotal.setToolTipText("Prubeh stahovani aktualniho souboru.");
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

        jLInstall.setBackground(getBackground());
        jLInstall.setForeground(getForeground());
        jLInstall.setLabelFor(jPBInstall);
        jLInstall.setText("Nic neinstaluju");
        jLInstall.setMaximumSize(new java.awt.Dimension(99999, 13));
        jLInstall.setMinimumSize(new java.awt.Dimension(400, 13));
        jLInstall.setPreferredSize(new java.awt.Dimension(400, 13));
        jPInstallProgress.add(jLInstall);

        jSeparator8.setBackground(getBackground());
        jSeparator8.setForeground(getForeground());
        jSeparator8.setMaximumSize(new java.awt.Dimension(32767, 5));
        jSeparator8.setMinimumSize(new java.awt.Dimension(0, 5));
        jSeparator8.setPreferredSize(new java.awt.Dimension(50, 5));
        jPInstallProgress.add(jSeparator8);

        jPBInstall.setBackground(getBackground());
        jPBInstall.setForeground(getForeground());
        jPBInstall.setToolTipText("Prubeh stahovani aktualniho souboru.");
        jPBInstall.setBorder(null);
        jPBInstall.setStringPainted(true);
        jPInstallProgress.add(jPBInstall);

        jSeparator5.setBackground(getBackground());
        jSeparator5.setForeground(getForeground());
        jSeparator5.setMaximumSize(new java.awt.Dimension(32767, 5));
        jSeparator5.setMinimumSize(new java.awt.Dimension(0, 5));
        jSeparator5.setPreferredSize(new java.awt.Dimension(50, 5));
        jPInstallProgress.add(jSeparator5);

        jPBTotal.setBackground(getBackground());
        jPBTotal.setForeground(getForeground());
        jPBTotal.setToolTipText("Prubeh stahovani aktualniho souboru.");
        jPBTotal.setBorder(null);
        jPBTotal.setStringPainted(true);
        jPInstallProgress.add(jPBTotal);

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

        org.jdesktop.layout.GroupLayout jPPatchListTabLayout = new org.jdesktop.layout.GroupLayout(jPPatchListTab);
        jPPatchListTab.setLayout(jPPatchListTabLayout);
        jPPatchListTabLayout.setHorizontalGroup(
            jPPatchListTabLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jBInstallSelection, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 722, Short.MAX_VALUE)
            .add(jSPPatchList, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 722, Short.MAX_VALUE)
        );
        jPPatchListTabLayout.setVerticalGroup(
            jPPatchListTabLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPPatchListTabLayout.createSequentialGroup()
                .add(jSPPatchList, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 427, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jBInstallSelection, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        jTPMain.addTab("Výběr souborů", null, jPPatchListTab, "Tady si můžeš vybrat jaké soubory se mají instalovat a jaké ne...");

        jPSettingsTab.setBackground(getBackground());
        jPSettingsTab.setForeground(getForeground());
        jPSettingsTab.setToolTipText("Klikni pro zobrazení nastavení programu nebo informacích o programu.");
        jPSettingsTab.setMinimumSize(new java.awt.Dimension(722, 452));

        jLConfUltimaOnlinePath.setBackground(getBackground());
        jLConfUltimaOnlinePath.setForeground(getForeground());
        jLConfUltimaOnlinePath.setLabelFor(jTConfUltimaOnlinePath);
        jLConfUltimaOnlinePath.setText("Adresář ultimy");
        jLConfUltimaOnlinePath.setToolTipText("Adresář, kde je nainstalovaná ultima online. Patcher tam bude instalovat soubory potřebné pro hraní na Andarii.");

        jTConfUltimaOnlinePath.setBackground(getBackground());
        jTConfUltimaOnlinePath.setColumns(30);
        jTConfUltimaOnlinePath.setForeground(getForeground());
        jTConfUltimaOnlinePath.setToolTipText("Adresář, kde je nainstalovaná ultima online. Patcher tam bude instalovat soubory potřebné pro hraní na Andarii.");
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
        jLConfTempPath.setText("Úložiště souborů");
        jLConfTempPath.setToolTipText("Místo kam bude AndariaPatcher stahovat soubory z internetu. Je také možné tam nahrát již stažené soubory, aby je AndariaPatcher nemusel stahovat.");

        jTConfTempPath.setBackground(getBackground());
        jTConfTempPath.setColumns(30);
        jTConfTempPath.setForeground(getForeground());
        jTConfTempPath.setToolTipText("Místo kam bude AndariaPatcher stahovat soubory z internetu. Je také možné tam nahrát již stažené soubory, aby je AndariaPatcher nemusel stahovat.");
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
        jLConfRunCommand.setText("Spuštěný program");
        jLConfRunCommand.setToolTipText("Program, který bude spuštěn po ukončení AndariaPatcheru. Pokud nechceš spouštět žádný program, nech řádku prázdnou.");

        jTConfRunCommand.setBackground(getBackground());
        jTConfRunCommand.setColumns(30);
        jTConfRunCommand.setForeground(getForeground());
        jTConfRunCommand.setToolTipText("Program, který bude spuštěn po ukončení AndariaPatcheru. Pokud nechceš spouštět žádný program, nech řádku prázdnou.");
        jTConfRunCommand.setMinimumSize(new java.awt.Dimension(20, 19));

        jBConfBrowseRunCommand.setBackground(getBackground());
        jBConfBrowseRunCommand.setForeground(getForeground());
        jBConfBrowseRunCommand.setText("Procházet");
        jBConfBrowseRunCommand.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBConfBrowseRunCommandActionPerformed(evt);
            }
        });

        jLConfUnRARCommand.setBackground(getBackground());
        jLConfUnRARCommand.setForeground(getForeground());
        jLConfUnRARCommand.setLabelFor(jTConfUnRARCommand);
        jLConfUnRARCommand.setText("Cesta k unrar.exe");
        jLConfUnRARCommand.setToolTipText("Příkaz, kterým AndariaPatcher bude rozbalovat .rar soubory. Program musí být nastaven tak aby rozbalil soubory do aktuálního adresáře. Jméno souboru k rozbalení nahraď slovem %rar% (včetně znaků %). Běžně používaný unrar má parametry: -o+ e %rar%.");

        jTConfUnRARCommand.setBackground(getBackground());
        jTConfUnRARCommand.setColumns(30);
        jTConfUnRARCommand.setForeground(getForeground());
        jTConfUnRARCommand.setToolTipText("Příkaz, kterým AndariaPatcher bude rozbalovat .rar soubory. Program musí být nastaven tak aby rozbalil soubory do aktuálního adresáře. Jméno souboru k rozbalení nahraď slovem %rar% (včetně znaků %). Běžně používaný unrar má parametry: -o+ e %rar%.");
        jTConfUnRARCommand.setMinimumSize(new java.awt.Dimension(20, 19));

        jBConfBrowseUnRARCommand.setBackground(getBackground());
        jBConfBrowseUnRARCommand.setForeground(getForeground());
        jBConfBrowseUnRARCommand.setText("Procházet");
        jBConfBrowseUnRARCommand.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBConfBrowseUnRARCommandActionPerformed(evt);
            }
        });

        jBConfLoad.setBackground(getBackground());
        jBConfLoad.setForeground(getForeground());
        jBConfLoad.setText("Načti nastavení");
        jBConfLoad.setToolTipText("Klikni pro načtení nastavení ze souboru s nastavením.");
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
        jBConfSave.setToolTipText("Klikni pro uložení nastavení do souboru s nastavením.");
        jBConfSave.setMaximumSize(new java.awt.Dimension(300, 25));
        jBConfSave.setMinimumSize(new java.awt.Dimension(100, 25));
        jBConfSave.setPreferredSize(new java.awt.Dimension(130, 25));
        jBConfSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBConfSaveActionPerformed(evt);
            }
        });

        jSeparator9.setBackground(getBackground());
        jSeparator9.setForeground(getBackground());
        jSeparator9.setEnabled(false);
        jSeparator9.setMinimumSize(new java.awt.Dimension(10, 30));

        jChDebug.setBackground(getBackground());
        jChDebug.setForeground(getForeground());
        jChDebug.setText("Zobrazovat informace užitečné pro ladění Andaria Patcheru a odstraňování problémů (debug režim)");
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
        jBSetAllInstalled.setToolTipText("Nastaví všechy patche v seznamu patchů jako nainstalované. Použij, pokud jsi spustil AndariaPatcher poprvé a máš již Ultimu plně opatchovanou souborama za shardu Andarie.");
        jBSetAllInstalled.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBSetAllInstalledActionPerformed(evt);
            }
        });

        jLabel1.setBackground(getBackground());
        jLabel1.setForeground(getForeground());
        jLabel1.setText("Verze programu: 1.3");

        jSeparator10.setBackground(getBackground());
        jSeparator10.setForeground(getBackground());
        jSeparator10.setEnabled(false);
        jSeparator10.setMinimumSize(new java.awt.Dimension(10, 30));

        jBDeleteNWB.setBackground(getBackground());
        jBDeleteNWB.setForeground(getForeground());
        jBDeleteNWB.setText("Odstranit soubor desktop.nwb (použij, pokud máš problém se spuštěním UO)");
        jBDeleteNWB.setToolTipText("Opravuje chybu spouštění ultimy online, při které hra spadne hned po spuštění.");
        jBDeleteNWB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBDeleteNWBActionPerformed(evt);
            }
        });

        jBDeleteIntro.setBackground(getBackground());
        jBDeleteIntro.setForeground(getForeground());
        jBDeleteIntro.setText("Odstranit intro hry (úvodní videa)");
        jBDeleteIntro.setToolTipText("Smaže úvodní videa hry. Tím se zrychlí start a udělá trošku místa na disku.");
        jBDeleteIntro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBDeleteIntroActionPerformed(evt);
            }
        });

        jBConfBrowseUnRARCommand1.setBackground(getBackground());
        jBConfBrowseUnRARCommand1.setForeground(getForeground());
        jBConfBrowseUnRARCommand1.setText("Stáhnout");
        jBConfBrowseUnRARCommand1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBConfBrowseUnRARCommand1ActionPerformed(evt);
            }
        });

        jBRenewRegistry.setBackground(getBackground());
        jBRenewRegistry.setForeground(getForeground());
        jBRenewRegistry.setText("Obnovit registry windows (vybereš adresář ve kterém je ultima nainstalovaná)");
        jBRenewRegistry.setToolTipText("Opravuje chybu spouštění ultimy online, při které hra spadne hned po spuštění.");
        jBRenewRegistry.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBRenewRegistryActionPerformed(evt);
            }
        });
        if (Settings.getInstance().getOs().getClass().toString().endsWith("LinuxOS"))
        jBRenewRegistry.setVisible(false);
        else jBRenewRegistry.setVisible(true);

        org.jdesktop.layout.GroupLayout jPSettingsTabLayout = new org.jdesktop.layout.GroupLayout(jPSettingsTab);
        jPSettingsTab.setLayout(jPSettingsTabLayout);
        jPSettingsTabLayout.setHorizontalGroup(
            jPSettingsTabLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPSettingsTabLayout.createSequentialGroup()
                .addContainerGap()
                .add(jPSettingsTabLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPSettingsTabLayout.createSequentialGroup()
                        .add(jBConfLoad, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 185, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jSeparator10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 86, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel1)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jSeparator9, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 152, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jBConfSave, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 160, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPSettingsTabLayout.createSequentialGroup()
                        .add(jPSettingsTabLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(jLConfRunCommand, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(jLConfUltimaOnlinePath, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(jLConfTempPath, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(jLConfUnRARCommand))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPSettingsTabLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPSettingsTabLayout.createSequentialGroup()
                                .add(jTConfTempPath, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 521, Short.MAX_VALUE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jBConfBrowseTempPath))
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPSettingsTabLayout.createSequentialGroup()
                                .add(jTConfUltimaOnlinePath, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 521, Short.MAX_VALUE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jBConfBrowseUltimaOnlinePath))
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPSettingsTabLayout.createSequentialGroup()
                                .add(jPSettingsTabLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(jTConfRunCommand, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 521, Short.MAX_VALUE)
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPSettingsTabLayout.createSequentialGroup()
                                        .add(jTConfUnRARCommand, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 436, Short.MAX_VALUE)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(jBConfBrowseUnRARCommand1)))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jPSettingsTabLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jBConfBrowseUnRARCommand)
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jBConfBrowseRunCommand)))))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jBDeleteIntro, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 702, Short.MAX_VALUE)
                    .add(jBDeleteNWB, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 702, Short.MAX_VALUE)
                    .add(jChDebug)
                    .add(jBSetAllInstalled, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 702, Short.MAX_VALUE)
                    .add(jBRenewRegistry, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 702, Short.MAX_VALUE))
                .addContainerGap())
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
                    .add(jBConfBrowseTempPath)
                    .add(jLConfTempPath)
                    .add(jTConfTempPath, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPSettingsTabLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jBConfBrowseUnRARCommand)
                    .add(jLConfUnRARCommand)
                    .add(jBConfBrowseUnRARCommand1)
                    .add(jTConfUnRARCommand, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPSettingsTabLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jBConfBrowseRunCommand)
                    .add(jLConfRunCommand)
                    .add(jTConfRunCommand, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jChDebug)
                .add(12, 12, 12)
                .add(jBSetAllInstalled)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jBDeleteIntro)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jBDeleteNWB)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jBRenewRegistry)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 152, Short.MAX_VALUE)
                .add(jPSettingsTabLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jPSettingsTabLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(jBConfLoad, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(jBConfSave, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPSettingsTabLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(jLabel1)
                        .add(jPSettingsTabLayout.createSequentialGroup()
                            .add(5, 5, 5)
                            .add(jSeparator9, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(jSeparator10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jLConfUnRARCommand.getAccessibleContext().setAccessibleDescription("Umístění souboru unrar.exe. Používá se k rozbalení .rar souborů. Najdete ho ke stažení na http://www.winrar.cz/download.php?area=rarother");
        jTConfUnRARCommand.getAccessibleContext().setAccessibleDescription("Umístění souboru unrar.exe. Používá se k rozbalení .rar souborů. Najdete ho ke stažení na http://www.winrar.cz/download.php?area=rarother");

        jTPMain.addTab("Nastavení", null, jPSettingsTab, "Klikni pro zobrazení panelu s nastavením Andaria Patcheru...");

        getContentPane().add(jTPMain);

        getAccessibleContext().setAccessibleDescription("Instalator souboru potrebnych pro hrani na Ultima Online RP Free Shradu Adaria");

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void jBSetAllInstalledActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBSetAllInstalledActionPerformed
        if (0 == JOptionPane.showConfirmDialog(null, "Chceš opravdu nastavit všechny soubory jako nainastalové ?", "Zásadní otázka...", JOptionPane.YES_NO_OPTION)) {
            patchList.setAllInstalled();
        }
    }//GEN-LAST:event_jBSetAllInstalledActionPerformed

    private void jChDebugStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jChDebugStateChanged
        Settings.getInstance().setValue("debug_log", jChDebug.isSelected() ? "1" : "0");
    }//GEN-LAST:event_jChDebugStateChanged

    private void jBCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBCloseActionPerformed
        try {
            Runtime.getRuntime().exec(Settings.getInstance().getValue(Settings.RUN_COMMAND).split(" "), null, new File(Settings.getInstance().getValue(Settings.ULTIMA_ONINE_PATH)));
        } catch (IOException ex) {
            log.addErr("Chyba při spouštění externího programu !");
            ex.printStackTrace();
        }

        System.exit(0);
    }//GEN-LAST:event_jBCloseActionPerformed

    private void jBCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBCancelActionPerformed
        patchList.cancel();
    // updateButtons();
    }//GEN-LAST:event_jBCancelActionPerformed

    private void jBConfBrowseUnRARCommandActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBConfBrowseUnRARCommandActionPerformed
        jTConfUnRARCommand.setText(Settings.getInstance().openFile("Vyber unrar", jTConfUnRARCommand.getText(), JFileChooser.FILES_ONLY));
    }//GEN-LAST:event_jBConfBrowseUnRARCommandActionPerformed

    private void jBConfLoadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBConfLoadActionPerformed
        loadSettings();
    }//GEN-LAST:event_jBConfLoadActionPerformed

    private void jBConfSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBConfSaveActionPerformed
        saveSettings();
    }//GEN-LAST:event_jBConfSaveActionPerformed

    private void jBConfBrowseRunCommandActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBConfBrowseRunCommandActionPerformed
        jTConfRunCommand.setText(Settings.getInstance().openFile("Vyber soubor ktery mam spustit po ukonceni", jTConfRunCommand.getText(), JFileChooser.FILES_ONLY));
    }//GEN-LAST:event_jBConfBrowseRunCommandActionPerformed

    private void jBConfBrowseTempPathActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBConfBrowseTempPathActionPerformed
        jTConfTempPath.setText(Settings.getInstance().openFile("Vyber adresar kam stahovat soubory", jTConfTempPath.getText(), JFileChooser.DIRECTORIES_ONLY));
    }//GEN-LAST:event_jBConfBrowseTempPathActionPerformed

    private void jBConfBrowseUltimaOnlinePathActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBConfBrowseUltimaOnlinePathActionPerformed
        jTConfUltimaOnlinePath.setText(Settings.getInstance().openFile("Vyber adresar s Ultimou", jTConfUltimaOnlinePath.getText(), JFileChooser.DIRECTORIES_ONLY));
    }//GEN-LAST:event_jBConfBrowseUltimaOnlinePathActionPerformed

    private void jBInstallActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBInstallActionPerformed
        patchList.download();
        updateButtons();
        jTPMain.setSelectedIndex(1);
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
    // TODO add your handling code here:
    }//GEN-LAST:event_jChDebugActionPerformed

    private void jBConfBrowseUnRARCommand1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBConfBrowseUnRARCommand1ActionPerformed

        
        if (Settings.getInstance().getOs().getClass().toString().endsWith("LinuxOS")) {

            JOptionPane.showMessageDialog(null, "Unrar si na linuxu musis zaridit sam :-P", "Chybka lenochu !", JOptionPane.WARNING_MESSAGE);
        } else {
            PatchItem patchItem = new PatchItem(Settings.getInstance().getOs().getUnrarPatchItem());

            patchList.downloadOther(patchItem);

            jTConfUnRARCommand.setText(Settings.getInstance().getOs().getUltima_online_path() + File.separator + patchItem.getFileName());
            
            saveSettings();
        }
        
    }//GEN-LAST:event_jBConfBrowseUnRARCommand1ActionPerformed

    private void jBRenewRegistryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBRenewRegistryActionPerformed
        // button can't be visible on linux machines
        WindowsOS winos = (WindowsOS) Settings.getInstance().getOs();
        winos.renewWindowsRegistry();
        loadSettings();
}//GEN-LAST:event_jBRenewRegistryActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jBCancel;
    private javax.swing.JButton jBClose;
    private javax.swing.JButton jBConfBrowseRunCommand;
    private javax.swing.JButton jBConfBrowseTempPath;
    private javax.swing.JButton jBConfBrowseUltimaOnlinePath;
    private javax.swing.JButton jBConfBrowseUnRARCommand;
    private javax.swing.JButton jBConfBrowseUnRARCommand1;
    private javax.swing.JButton jBConfLoad;
    private javax.swing.JButton jBConfSave;
    private javax.swing.JButton jBDeleteIntro;
    private javax.swing.JButton jBDeleteNWB;
    private javax.swing.JButton jBInstall;
    private javax.swing.JButton jBInstallSelection;
    private javax.swing.JButton jBRefreshPatchList;
    private javax.swing.JButton jBRenewRegistry;
    private javax.swing.JButton jBSetAllInstalled;
    private javax.swing.JCheckBox jChDebug;
    private javax.swing.JLabel jLConfRunCommand;
    private javax.swing.JLabel jLConfTempPath;
    private javax.swing.JLabel jLConfUltimaOnlinePath;
    private javax.swing.JLabel jLConfUnRARCommand;
    private javax.swing.JLabel jLDownload;
    private javax.swing.JLabel jLInstall;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JProgressBar jPBDownloadSingle;
    private javax.swing.JProgressBar jPBDownloadTotal;
    private javax.swing.JProgressBar jPBInstall;
    private javax.swing.JProgressBar jPBTotal;
    private javax.swing.JPanel jPButtons;
    private javax.swing.JPanel jPControlsTab;
    private javax.swing.JPanel jPDownloadProgress;
    private javax.swing.JPanel jPInstallProgress;
    private javax.swing.JPanel jPPatchList;
    private javax.swing.JPanel jPPatchListTab;
    private javax.swing.JPanel jPSettingsTab;
    private javax.swing.JScrollPane jSPLog;
    private javax.swing.JScrollPane jSPPatchList;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator10;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JSeparator jSeparator8;
    private javax.swing.JSeparator jSeparator9;
    private javax.swing.JTextField jTConfRunCommand;
    private javax.swing.JTextField jTConfTempPath;
    private javax.swing.JTextField jTConfUltimaOnlinePath;
    private javax.swing.JTextField jTConfUnRARCommand;
    private javax.swing.JTextArea jTLog;
    private javax.swing.JTabbedPane jTPMain;
    // End of variables declaration//GEN-END:variables
}

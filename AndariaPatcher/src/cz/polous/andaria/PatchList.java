package cz.polous.andaria;

import java.awt.GridLayout;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.BufferedReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Vector;
import java.io.IOException;
import javax.swing.JPanel;

/*******************************************************************************
 * installatortchList: List of installatortches, offers download start procedure.
 * @author  Martin Polehla (andaria_patcher@polous.cz)
 * @version 0.1
 *******************************************************************************/
class PatchList {

    private static final PatchList INSTANCE = new PatchList();
    private Vector patchData; //= new Vector();
    private Thread t;
    private static Log log;
    private Thread installThread;
    private Thread downloadThread;

    /***************************************************************************
     * Creates a new instance of installatortchList
     **************************************************************************/
    private PatchList() {
        log = new Log(this);

        installThread = new Thread(Installer.getInstance());
        downloadThread = new Thread(Downloader.getInstance());
        downloadThread.start();
        installThread.start();
        // reload();
    }

    public static PatchList getInstance() {
        return INSTANCE;
    }

    public Thread getDownloadThread() {
        return downloadThread;
    }

    public Thread getInstallThread() {
        return installThread;
    }

    /***************************************************************************
     * Return count of installatortches
     * @return amount of installatortchItem in list
     **************************************************************************/
    public int getCount() {
        return patchData.size();
    }

    /***************************************************************************
     * @return amount of patches to install
     **************************************************************************/
    public int getInstallCount() {
        int result = 0;
        PatchItem patchItem;
        for (int i = 0; i < patchData.size(); i++) {
            patchItem = (PatchItem) patchData.get(i);
            if (patchItem.getInstallFlag()) {
                result++;
            }
        }
        return result;
    }

    /***************************************************************************
     * Read patchList from remote storage and fill data  vector
     * (list of pachItems).
     **************************************************************************/
    public synchronized void reload() {
        t = new Thread() {

            public boolean canceled = false;
            private Log log = new Log("ReaderThread");

            @Override
            public void run() {
                String uri = Settings.getInstance().getValue(Settings.VALUES.FILE_LIST_URL);
                try {
                    log.addLine("Zacinam stahovat seznam patchu z internetu.");
                    FrontEnd.getInstance().setJBPatchListEnabled(false);
                    FrontEnd.getInstance().setJBInstall(false);

                    URL url = new URL(uri);
                    URLConnection connection = url.openConnection();
                    InputStream in = connection.getInputStream();
                    Reader reader = new InputStreamReader(in, "UTF-8");
                    BufferedReader br = new BufferedReader(reader);

                    String sLine; // Line buffer
                    String[] sItems; // Item Buffer
                    patchData = new Vector(); //reset patchdata
                    JPanel jPPatchList = FrontEnd.getInstance().getJPPatchList();
                    jPPatchList.removeAll(); // clean frontend patchlist
                    PatchItem patchItem;
                    //CSVReade
                    for (int i = 0; br.ready(); i++) {
                        if (canceled) {
                            reader.close();
                            FrontEnd.getInstance().setJBPatchListEnabled(true);
                            return;
                        }
                        sLine = br.readLine();
                        log.addDebug(sLine);
                        sItems = sLine.split(";");
                        for (int k = 0; k < sItems.length; k++) {
                            sItems[k] = sItems[k].substring(1, sItems[k].length() - 1);
                        }
                        patchItem = new PatchItem(sItems);
                        patchData.add(patchItem);
                        jPPatchList.add(patchItem.getInFrame());
                    }
                    jPPatchList.setLayout(new GridLayout(patchData.size(), 0));
                    reader.close();
                    if (patchData.size() == 0) {
                        log.addErr("Z nějakého důvodu se nepodařilo nahrát seznam patchů z internetu. Velmi častou příčinou býva nastaveni firewallu či antiviru. Raději to překontroluj. Možností je také je nedostupnost serveru Andaria. Zkus stahnout soubor: " + uri + ". Pokud seš si jistý, že je vše v pořádku, napiš o pomoc na fórum Andarie.");
                    } else {
                        log.addLine("Seznam patchu byl nahran z internetu.");
                    }
                } catch (IOException e) {
                    log.addErr("Z nějakého důvodu se nepodařilo nahrát seznam patchů z internetu. Velmi častou příčinou býva nastaveni firewallu či antiviru. Raději to překontroluj. Možností je také je nedostupnost serveru Andaria. Zkus stahnout soubor: " + uri + ". Pokud seš si jistý, že je vše v pořádku, napiš o pomoc na fórum Andarie.");
                    log.addEx(e);
                } finally {
                    FrontEnd.getInstance().setJBPatchListEnabled(true);
                    FrontEnd.getInstance().setJBInstall(true);
                    FrontEnd.getInstance().updateButtons();

                    if (Settings.getAutoInstall() == Settings.AUTO_LEVELS.AUTO_UPDATE) {
                        Settings.setAutoInstall(Settings.AUTO_LEVELS.MANUAL);
                        FrontEnd.getInstance().installPatches();
                    }
                    if (Settings.getAutoInstall() == Settings.AUTO_LEVELS.AUTO_INSTALL) {
                        Settings.setAutoInstall(Settings.AUTO_LEVELS.AUTO_UPDATE);
                        FrontEnd.getInstance().installUO();
                    }

                }
            }
        };
        t.start();
    }

    /***************************************************************************
     * Determinate if a job is in progress (downloader or installer)
     * @return state of patch process
     **************************************************************************/
    public boolean inProgress() {
        return Downloader.getInstance().inProgress() | Installer.getInstance().inProgress();
    }

    /***************************************************************************
     * Cancel downloads and installations
     **************************************************************************/
    public void cancel() {
        Downloader.getInstance().cancel();
        Installer.getInstance().cancel();
    }

    /***************************************************************************
     * Download other than patch item
     *
     * used for unrar download, not used now.
     **************************************************************************/
    @Deprecated
    public synchronized void downloadOnly(PatchItem patchItem) {
        Downloader downloader = Downloader.getInstance();
        downloader.reset();
        Installer.getInstance().reset();
        downloader.addPatchItem(patchItem);
        downloader.startSafe();
    }

    /***************************************************************************
     * Start downloads and installations
     **************************************************************************/
    public synchronized void downloadUOML() {
        Downloader downloader = Downloader.getInstance();
        downloader.reset();
        Installer.getInstance().reset();
        downloader.addPatchItem(Settings.getInstance().getUomlPatchItem());

        if (downloader.patchQueue.size() > 0) {
            downloader.startSafe();
        }
    }

    /***************************************************************************
     * Start downloads and installations
     **************************************************************************/
    public synchronized void download() {
        Downloader downloader = Downloader.getInstance();
        PatchItem patchItem;
        downloader.reset();
        Installer.getInstance().reset();
        for (int i = 0; i < patchData.size(); i++) {
            patchItem = (PatchItem) patchData.get(i);
            if (patchItem.getInstallFlag()) {
                downloader.addPatchItem(patchItem);
            }
        }
        if (downloader.patchQueue.size() > 0) {
            downloader.startSafe();
        } else {
            if (Settings.getAutoInstall() == Settings.AUTO_LEVELS.AUTO_CLOSE) {
                FrontEnd.getInstance().closeMe();
            }
            FrontEnd.getInstance().updateButtons();
        }
    }

    /***************************************************************************
     * Set all pachtes in list installed and store it to config file.
     * Usualy used when user has some of files installed by his own.
     **************************************************************************/
    public void setAllInstalled() {
        PatchItem patchItem;
        for (int i = 0; i < patchData.size(); i++) {
            patchItem = (PatchItem) patchData.get(i);
            patchItem.setInstalled();
        }
    }

    public void selectNone() {
        PatchItem patchItem;
        for (int i = 0; i < patchData.size(); i++) {
            patchItem = (PatchItem) patchData.get(i);
            patchItem.setInstallFlag(false);
        }
    }

    public void selectAll() {
        PatchItem patchItem;
        for (int i = 0; i < patchData.size(); i++) {
            patchItem = (PatchItem) patchData.get(i);
            if (patchItem.getAutoInstallFlag()) {
                patchItem.setInstallFlag(true);
            } else {
                patchItem.setInstallFlag(false);
            }
        }

    }
}

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

    /***************************************************************************
     * Creates a new instance of installatortchList
     **************************************************************************/
    private PatchList() {
        log = new Log(this);

        Thread installThread = new Thread(Installer.getInstance());
        Thread downloadThread = new Thread(Downloader.getInstance());
        downloadThread.start();
        installThread.start();
    // reload();
    }

    public static PatchList getInstance() {
        return INSTANCE;
    }

    /***************************************************************************
     * Return count of installatortches
     * @return amount of installatortchItem in list
     **************************************************************************/
    public int getCount() {
        return patchData.size();
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
                try {
                    log.addLine("Zacinam stahovat seznam patchu z internetu.");
                    URL url = new URL(Settings.getInstance().getValue(Settings.FILE_LIST_URL));
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
                    for (int i = 0; br.ready(); i++) {
                        if (canceled) {
                            reader.close();
                            return;
                        }
                        sLine = br.readLine();
                        log.addDebug(sLine);
                        sItems = sLine.split(";");
                        patchItem = new PatchItem(sItems);
                        patchData.add(patchItem);
                        jPPatchList.add(patchItem.getInFrame());
                    }
                    jPPatchList.setLayout(new GridLayout(patchData.size(), 0));
                    FrontEnd.getInstance().pack();
                    reader.close();
                    log.addLine("Seznam patchu byl nahran z internetu.");
                } catch (IOException e) {
                    log.addEx(e);
                }
            }
        };
        t.start();
    }

    /***************************************************************************
     * Determinate if a job is in progress (downloader or installer)
     * @return wokring state of patchlist
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
     **************************************************************************/
    public synchronized void downloadOnly(PatchItem patchItem) {
        Downloader downloader = Downloader.getInstance();
        downloader.reset();
        downloader.addPatchItem(patchItem);
        downloader.startSafe();
    }

    /***************************************************************************
     * Start downloads and installations
     **************************************************************************/
    public synchronized void download() {
        Downloader downloader = Downloader.getInstance();
        PatchItem patchItem;
        downloader.reset();
        for (int i = 0; i < patchData.size(); i++) {
            patchItem = (PatchItem) patchData.get(i);
            if (patchItem.getInstallFlag()) {
                downloader.addPatchItem(patchItem);
            }
        }

        downloader.startSafe();
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
}
package cz.polous.andaria;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.BufferedReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Vector;
import java.io.IOException;

/*******************************************************************************
 * installatortchList: List of installatortches, offers download start procedure.
 * @author  Martin Polehla (andaria_patcher@polous.cz)
 * @version 0.1
 *******************************************************************************/
class PatchList  {

    private Vector data = new Vector();
    private Downloader downloader; // downloader object
    private Installer installator;
 
        
    //   private Thread downloadThread, installThread;
    private Thread t;

         private static Log log;

    /***************************************************************************
     * Creates a new instance of installatortchList
     **************************************************************************/
    PatchList() {
        log = new Log(this);
        installator = new Installer();
        downloader = new Downloader(installator);

        Thread installThread = new Thread(installator);
        Thread downloadThread = new Thread(downloader);

        downloader.setSingleBP(FrontEnd.instance.getjPBDownloadSingle());
        downloader.setTotalBP(FrontEnd.instance.getjPBDownloadTotal());
        downloader.setLabel(FrontEnd.instance.getjLDownload());

        installator.setSingleBP(FrontEnd.instance.getjPBInstall());
        installator.setTotalBP(FrontEnd.instance.getjPBTotal());
        installator.setLabel(FrontEnd.instance.getjLInstall());

        read();

        downloadThread.start();
        installThread.start();
    }

    /***************************************************************************
     * Return count of installatortches
     * @return amount of installatortchItem in list
     **************************************************************************/
    public int getCount() {
        return data.size();
    }

    /***************************************************************************
     * Read patchList from remote storage and fill data  vector
     * (list of pachItems).
     **************************************************************************/
    private void read() {
        t = new Thread() {

            public boolean canceled = false;
            private Log log = new Log("ReaderThread");

            public void run() {
                try {
                    log.addLine("Zacinam stahovat seznam patchu z internetu.");
                    URL url = new URL(Settings.getValue(Settings.FILE_LIST_URL));
                    URLConnection connection = url.openConnection();
                    InputStream in = connection.getInputStream();
                    Reader reader = new InputStreamReader(in, "UTF-8");
                    BufferedReader br = new BufferedReader(reader);

                    String sLine; // Line buffer
                    String[] sItems; // Item Buffer
                    for (int i = 0; br.ready(); i++) {
                        if (canceled) {
                            reader.close();
                            return;
                        }
                        sLine = br.readLine();
                        sItems = sLine.split(";");
                        data.add(new PatchItem(sItems));
                    }
                    reader.close();
                    FrontEnd.instance.refreshPlPane();
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
    public boolean isWorking() {
        return downloader.inProgress() | installator.inProgress();
    }

    /***************************************************************************
     * Create panel for FrontEnd containig patch files informations.
     * @return vector of jPanels
     **************************************************************************/
    public Vector getInPanel() {
        PatchItem patchItem;
        Vector result = new Vector();

        for (int i = 0; i < data.size(); i++) {
            patchItem = (PatchItem) data.get(i);
            result.add(patchItem.getInFrame());
        }
        return result;
    }

    /***************************************************************************
     * Cancel downloads and installations
     **************************************************************************/
    public void cancel() {
        downloader.cancel();
        installator.cancel();
    }

     /***************************************************************************
     * Download other than patch item
     **************************************************************************/
    public synchronized void downloadOther(PatchItem patchItem) {
        downloader.reset();
        downloader.addPatchItem(patchItem);
        

        downloader.safeWork();
    }
    /***************************************************************************
     * Start downloads and installations
     **************************************************************************/
    public synchronized void download() {
        PatchItem patchItem;
        downloader.reset();
        for (int i = 0; i < data.size(); i++) {
            patchItem = (PatchItem) data.get(i);
            if (patchItem.getInstallFlag()) {
                downloader.addPatchItem(patchItem);
            }
        }

        downloader.safeWork();
    }

    /***************************************************************************
     * Set all pachtes in list installed and store it to config file.
     * Usualy used when user has some of files installed by his own.
     **************************************************************************/
    public void setAllInstalled() {
        PatchItem patchItem;
        for (int i = 0; i < data.size(); i++) {
            patchItem = (PatchItem) data.get(i);
            patchItem.setInstalled();
        }
    }
}
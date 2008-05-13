package cz.polous.andaria;

import java.io.OutputStream;
import java.net.URLConnection;
import java.io.InputStream;
import java.net.URL;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Downloader: Downloade files and start instalator when file ready.
 * When file don't exists on localhost, download it. After correct file found on
 * local temp storage, add patchitem to Installer object (installator) and
 * run it.
 * 
 * @author Martin Polehla (andaria_patcher@polous.cz)
 * 
 */
class Downloader extends PatcherQueue {

    Installer installator;

    /***************************************************************************
     * Creates a new instance of Downloader
     ***************************************************************************/
    public Downloader(Installer inst) {
        super();
        installator = inst;
    }

    /***************************************************************************
     * Main download procedure
     *  - SetTotal amount of install object to same like this (It suppose, user want install all files)
     *  - Set progress
     *  - Check if file exists at local storage (if yes, finish downloading)
     *  - Downlaod file
     *  - Check if file downloaded correct (if yes, finish downloading, else remove file from queue and print error message)
     **************************************************************************/
    synchronized void executeNext() {
        //setInProgress();
        //  - SetTotal amount of install object to same like this (It suppose, user want install all files)
        PatchItem p = getFirstItem();
        installator.setTotalAmount(getTotalAmount());

        // - Set progress
        resetSingleDone((double) p.getSize());

        // - Check if file exists at local storage (if yes, finish downloading)
        String fileName = p.getLocalFileName();
        try {
            p.checkHash();
            setLabelText("Kontroluju soubor: " + p.getFileName());
            log.addLine("Soubor: " + p.getFileName() + " uz je stazeny.");
            startInstaller(p);
            return;
        } catch (IOException e) {
        }

        setLabelText("Stahuju soubor: " + p.getFileName());
        log.addLine("Stahuju soubor: " + p.getFileName());
        // - Downlaod file
        OutputStream out = null;
        URLConnection conn = null;
        InputStream in = null;


        try {
            String uri = p.getRemoteFileName();

            URL url = new URL(uri);
            out = new BufferedOutputStream(new FileOutputStream(OperatingSystem.getExistingFileInstance(fileName)));
            conn = url.openConnection();
            in = conn.getInputStream();

            // long length = conn.getContentLength();
            //long done = 0;



            byte[] buff = new byte[2048];
            int numRead;
            while ((numRead = in.read(buff)) != -1) {
                if (canceled()) {
                    //singleDone((double) 0);
                    return;
                }
                //done += numRead;
                addSingleDone((double) numRead);
                //singleDone((double) done);
                out.write(buff, 0, numRead);
            }

        } catch (FileNotFoundException e) {
            log.addErr("Soubor nebyl na serveru nalezen. Prosim kontaktuj administratory.\n (" + e + ")");
        } catch (Exception e) {
            log.addEx(e);
            log.addErr("Doslo k chybe pri stahovani souboru: " + p.getRemoteFileName() + ". Soubor vynechavam, zkuste to znova.");
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                log.addEx(e);
            } finally {

                // - Check if file downloaded correct (if yes, finish downloading, else remove file from queue and print error message)
                setLabelText("Kontroluju soubor: " + p.getFileName());
                try {
                    p.checkHash();
                    startInstaller(p);
                } catch (IOException e) {
                    // p.checkHash() throws an exception.
                    log.addEx(e);
                    log.addErr("Nemuzu otevrit soubor nebo je spatne stazeny ! Zkus to pozdeji znova.");
                    installator.removeTotalAmount(p.getSize());
                    singleDone((double) p.getSize());
                    removeFirst();
                }
            }
        }
    //resetInProgress();
    }

    /***************************************************************************
     * Downlaod finish procedure
     *  update download status
     *  add item to installer queue and run pi using safeWork();
     *  remove downlaoded file from download queue
     **************************************************************************/
    private void startInstaller(PatchItem p) {
        p.setDownloaded(true);
        singleDone((double) p.getSize());
        installator.addPatchItem(p);
        removeFirst();
        installator.safeWork();
    }
}
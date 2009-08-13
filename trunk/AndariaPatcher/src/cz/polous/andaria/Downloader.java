package cz.polous.andaria;

import java.io.OutputStream;
import java.net.URLConnection;
import java.io.InputStream;
import java.net.URL;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 * Downloader se stara o stahovani souboru. Po uspesnem stazeni presune soubor
 * do fronty installeru a spusti installer.
 *
 * Trida umoznuje pouze jednu instanci (singleton)
 *
 * Class is singleton.
 * 
 * @author Martin Polehla (andaria_patcher@polous.cz)
 * 
 */
class Downloader extends PatcherQueue {

    private static final Downloader INSTANCE = new Downloader();

    /***************************************************************************
     * Creates a new instance of Downloader
     ***************************************************************************/
    private Downloader() {
        super();
    }

    public static Downloader getInstance() {
        return INSTANCE;
    }

    /***************************************************************************
     * Main download procedure
     *  - Set total size of install object to same like download size.
     *      (I suppose, user want install all downloaded files)
     *  - Update progress during downloading.
     *  - Check if file exists at local storage (if exists, try continue downloading).
     *  - Download file.
     *  - Check if file downloaded correct.
     *  - Aftre a file correctly downloaded, move it to Installer queue.
     **************************************************************************/
    @Override
    synchronized void executeNext() {
        PatchItem p = getFirstItem();
        // ProgressBars - for single file
        resetProgressBar(BARS.SINGLE, p.getSize());

        // Check if wanted file exists at local storage
        String fileName = p.getLocalFileName();
        try {
            p.checkHash();
            setLabelText("Kontroluju soubor: " + p.getFileName());
            log.addLine("Soubor: " + p.getFileName() + " je už stažený.");
            finishDownload(p); // correct file found, then finish action
            return; // file exists, we don't need download it
        } catch (FileNotFoundException e) {
            log.addDebug("Soubor ".concat(p.getFileName()).concat(" není ještě stažený."));
        } catch (IOException e) {
            log.addEx(e);
        } catch (Exception e) {
            log.addLine(e.getMessage().concat(" Zkusím to znova."));
        }

        setLabelText("Stahuji soubor: " + p.getFileName());
        log.addLine("Stahuji soubor: " + p.getFileName());

        // start download
        OutputStream out = null;
        URLConnection conn = null;
        InputStream in = null;

        try {
            String uri = p.getRemoteFileName();

            URL url = new URL(uri);
            out = new BufferedOutputStream(new FileOutputStream(Settings.getInstance().getOs().getExistingFileInstance(fileName)));
            conn = url.openConnection();
            in = conn.getInputStream();

            byte[] buff = new byte[2048];
            int numRead;
            Date start = new Date();
            long size = 0;
            while ((numRead = in.read(buff)) != -1 && !canceled()) {
                out.write(buff, 0, numRead);
                //addToSingleProgress((double) numRead);
                size += numRead;
                setSingleProgress(size);
                try {
                    setLabelSpeed(1.00 * size / ((new Date()).getTime() - start.getTime()));
                    //log.addDebug(Double.toString((new Date()).getTime() - start.getTime()));
                } catch (ArithmeticException e) {
                }
            }
        } catch (FileNotFoundException e) {
            log.addErr("Soubor jsem na serveru Andarie nenašel. Zřejmě problém seznamu patchů. Prosím kontaktuj admina Andarie.\n (" + e + ")");
        } catch (Exception e) {
            log.addEx(e);
            log.addErr("Došlo k chybě při stahování souboru: " + p.getRemoteFileName() + ". Soubor vynechávám, zkus to znova nebo požádej o pomoc na fóru Andarie.");
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
                if (canceled()) {
                    setLabelText("Stahování bylo přerušeno.");
                    return;
                }
                setLabelText("Kontroluji soubor: " + p.getFileName());
                try {
                    p.checkHash();
                    finishDownload(p); // success
                } catch (IOException e) { // file not found or read errors
                    log.addEx(e);
                    removeFirst(); // finally I won't download it again.
                } catch (Exception e) { // file is corrupted or old
                    log.addErr(e.getMessage().concat(" Zkus to znova a pokud se ani pak nezadaří, napiš to prosím na fórum andarie."));
                    removeFirst(); // finally I won't download it again.
                } finally {
                    Settings.getInstance().updateTempSize();
                }
            }
        }
    }

    /***************************************************************************
     * Finish download procedure
     *  - update download status
     *  - add item to install queue and run Installer
     *  - remove downlaoded file from download queue
     **************************************************************************/
    private void finishDownload(PatchItem p) {
        p.setDownloaded(true);
        setSingleProgressPercents(100);
        Installer.getInstance().addPatchItem(p);
        removeFirst(); // finally I won't download it again.
    }

    /***************************************************************************
     * Add new PatchItem to queue.
     * Overriding patchQueue method add progressbar max size counting funcion.
     *
     * @param p item to add
     * @see cz.polous.andaria.Downloader#executeNext
     **************************************************************************/
    @Override
    protected void addPatchItem(PatchItem p) {
        super.addPatchItem(p);
        setTotalMax(getTotalMax() + p.getSize());
        Installer.getInstance().setTotalMax(this.getTotalMax());
        log.addDebug("TotalMax = ".concat(Double.toString(getTotalMax())));
    }
}

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
        // ProgressBars

        resetSingleProgress((double) p.getSize());

        // Check if wanted file exists at local storage
        String fileName = p.getLocalFileName();
        try {
            p.checkHash();
            setLabelText("Kontroluju soubor: " + p.getFileName());
            log.addLine("Soubor: " + p.getFileName() + " je už stažený.");
            startInstaller(p);
            return; // file exists, don't need download
        } catch (FileNotFoundException e) {
            log.addDebug("Soubor ".concat(p.getFileName()).concat(" není ještě stažený."));
        } catch (IOException e) {
            log.addEx(e);
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
            while ((numRead = in.read(buff)) != -1) {
                if (canceled()) {
                    return;
                }
                out.write(buff, 0, numRead);
                //addToSingleProgress((double) numRead);
                size += numRead;
                setSingleProgress(size);
                try {
                    setLabelSpeed(0.50 * size / ((new Date()).getTime() - start.getTime()));
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
                setLabelText("Kontroluji soubor: " + p.getFileName());
                try {
                    p.checkHash();
                    startInstaller(p);
                } catch (IOException e) {

                    log.addEx(e);
                    log.addErr("Nemůžu otevřít soubor nebo je špatně stažený ! Zkus to znova nebo požádej o pomoc na fóru Andarie.");
                    Installer.getInstance().removeFromTotalProgress(p.getSize());
                    setSingleProgressPercents(100);
                    removeFirst();
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
    private void startInstaller(PatchItem p) {
        p.setDownloaded(true);
        setSingleProgressPercents(100);
        Installer.getInstance().addPatchItem(p);
        removeFirst();
        Installer.getInstance().startSafe();
    }
}

package cz.polous.andaria;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * Installer se stara o rozbalovani (nebo jen kopirovani) souboru do adresare
 * s ultimou. Po rozbaleni souboru do adresare s uo se koukne po instalacnich
 * skriptech start_a.bat nebo start_b.bat a kdyz je najde tak je spusti.
 * 
 * Po uspesnem nainstalovani oznaci soubor jako nainstalovany a odstrani
 * z fornty pro instalaci
 *
 * Class is singleton.
 * 
 * @author Martin Polehla (andaria_patcher@polous.cz)
 */
class Installer extends PatcherQueue {

    private static final Installer INSTANCE = new Installer();
    private boolean failed;
    private double extractTotalSize;
    private double extractProgressPart;

    //public static Log log;
    public void setFailed(boolean failed) {
        this.failed = failed;
    }

    /***************************************************************************
     * Creates a new instance of Runner
     **************************************************************************/
    private Installer() {
        super();
    }

    public static Installer getInstance() {
        return INSTANCE;
    }

    /***************************************************************************
     * Add new PatchItem to queue.
     * Override PatchQueue object method to remove totalAmount (file size)
     * adding. totalAmount variable is set by Downloader.
     * @param p item to add
     * @see cz.polous.andaria.Downloader#executeNext
     **************************************************************************/
    @Override
    protected void addPatchItem(PatchItem p) {
        patchQueue.add(p);
    }

    /**
     * Run file in separated thread and pause installator thread using wait()
     * method. Installer thread is resumed using work() method.
     *  Note: this is reason of existing safeWork()
     *
     * @param command Command to execute
     */
    private synchronized void exec(final String[] command) {
        log.addCmd(command);

        Thread t = new Thread() {

            @Override
            public void run() {
                try {
                    log.addDebug("Před spuštěním");
                    Process proc = Runtime.getRuntime().exec(command, null, new File(Settings.getInstance().getValue(Settings.ULTIMA_ONINE_PATH)));
                    log.addDebug("Výstup procesu:");
                    String line;

                    BufferedReader input = new BufferedReader(new InputStreamReader(proc.getInputStream()));
                    while ((line = input.readLine()) != null) {
                        log.addDebug("[" + command[0] + "]: " + line);
                    }
                    input.close();
                    log.addDebug("Čekám na dokončení.");
                    proc.waitFor();

                } catch (InterruptedException e) {
                    setFailed(true);
                    log.addEx(e);
                } catch (IOException e) {
                    setFailed(true);
                    log.addEx(e);
                } finally {
                    log.addDebug("Špuštěný program skončil.");
                    //--PatchList.getInstance().getInstallThread().notify();
                    Installer.getInstance().start();
                }
            }
        };
        t.start();
        try {
            wait();
        } catch (InterruptedException e) {
            log.addEx(e);
        } finally {
            t = null;
        }
    }

    /***************************************************************************
     * Main install procedure
     *  - (re)set progress indicator (for fun of users, generated random value .-) )
     *  - Unpack patch files
     *  - if exists start_a.bat, execute it
     *  - if exists start_g.bat, execute it
     *  - finish install procedure and remove file from queue
     **************************************************************************/
    @Override
    synchronized void executeNext() {
        failed = false;
        PatchItem patchItem = getFirstItem();

        // progress will be counted by 65% for extracting files
        // and  35% for installing patches.
        resetSingleProgress(patchItem.getSize());
        extractProgressPart = patchItem.getSize() * 65 / 100.00;

        log.addDebug("Pracuju se souborem: " + patchItem.getLocalFileName());
        if (patchItem.isPacked()) {
            final String uopath = Settings.getInstance().getValue(Settings.ULTIMA_ONINE_PATH);

            setLabelText("Rozbaluju patch: " + patchItem.getFileName());
            J7zipBinding sevenZip = new J7zipBinding();
            try {
                sevenZip.unpack(patchItem.getLocalFileName());
            } catch (IOException ex) {
                log.addEx(ex);
            } catch (Exception ex) {
                log.addEx(ex);
            }
            
            setLabelSpeed(0);
            log.addDebug("Soubor je rozbalený, hledám instalační skripty.");
            File f = new File(uopath + File.separator + "start_a.bat");
            log.addDebug("Hledám:" + f.getAbsolutePath());
            if (f.exists()) {
                log.addDebug("Našel jsem start_a.bat.");
                setLabelText("Instaluju patch (start_a.bat): " + patchItem.getFileName());

                exec(Settings.getInstance().getOs().getBatchExecCommand(f));
                f.delete();
                setSingleProgressPercents(82);
            }
            // - if exist start_g.bat, execute it
            f = new File(uopath + File.separator + "start_g.bat");
            setSingleProgressPercents(87);
            log.addDebug("Hledám:" + f.getAbsolutePath());
            if (f.exists()) {
                log.addDebug("Našel jsem start_g.bat.");
                setLabelText("Instaluju patch (start_g.bat): " + patchItem.getFileName());
                exec(Settings.getInstance().getOs().getBatchExecCommand(f));
                f.delete();
                setSingleProgressPercents(97);
            }

            setLabelText("Práce dokončena (" + patchItem.getFileName() + ").");
            log.addDebug("Instalace patche " + patchItem.getFileName() + " dokončena.");
           // setSingleProgressPercents(100);
        } else {
            // copy raw downloaded file into game directory
            // probably not used, after unrar - 7zip migration.
            extractProgressPart = patchItem.getSize();
            setLabelText("Kopíruju soubor: " + patchItem.getFileName() + " do adresáře s UO.");

            final String uopath = Settings.getInstance().getValue(Settings.ULTIMA_ONINE_PATH);
            File inFile = new File(patchItem.getLocalFileName());
            File outFile = new File(uopath + File.separator + patchItem.getFileName());

            try {
                FileChannel inChannel = new FileInputStream(inFile).getChannel();
                FileChannel outChannel = new FileOutputStream(outFile).getChannel();
                try {
                    // magic number for Windows, 64Mb - 32Kb)
                    int maxCount = (64 * 1024 * 1024) - (32 * 1024);
                    setExtractedTotal(inChannel.size());

                    long size = inChannel.size();
                    long position = 0;
                    while (position < size) {
                        position += inChannel.transferTo(position, maxCount, outChannel);
                        setExtractedProgress(size);
                    }
                } catch (IOException e) {
                    throw e;
                } finally {
                    if (inChannel != null) {
                        inChannel.close();
                    }
                    if (outChannel != null) {
                        outChannel.close();
                    }
                }
            } catch (IOException e) {
                log.addEx(e);
            }
        }

        if (!failed) {
            patchItem.setInstalled();
        }
        setSingleProgressPercents(100);
        removeFirst();
    }

    /***************************************************************************
     * Part of progressbar belogns to file extraction and rest of bar belongs
     * to patch instalation process.
     *
     * setExtractedProgress count and set progressbar while extracting patch
     * files.
     **************************************************************************/
    public void setExtractedProgress(long value) {
        setSingleProgress(1.00 * value / extractTotalSize * extractProgressPart);
    }

    /***************************************************************************
     * TotalExtractedSize hold total size of files to extract. This entry is
     * used to calculate extract progress bar part [setExtractedProgress()].
     **************************************************************************/
    public void setExtractedTotal(long value) {
        extractTotalSize = value;
    }
}

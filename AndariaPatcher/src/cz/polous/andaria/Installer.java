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
 * Trida umoznuje pouze jednu instanci (singleton)
 * 
 * @author Martin Polehla (andaria_patcher@polous.cz)
 */
class Installer extends PatcherQueue {

    private static final Installer INSTANCE = new Installer();
    private boolean failed;
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
                    start();
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

        // - reset progress
        resetSingleDone(patchItem.getSize());

        log.addDebug("Pracuju se souborem: " + patchItem.getLocalFileName());
        if (patchItem.isPacked()) {
            // - Unpack patch files
            setLabelText("Rozbaluju patch: " + patchItem.getFileName());
            final String uopath = Settings.getInstance().getValue(Settings.ULTIMA_ONINE_PATH);
            singleDone(((double) patchItem.getSize()) / (5 + Math.random() * 10));

            exec(new String[]{Settings.getInstance().getValue(Settings.UNRAR_PATH), "-o+", "e", patchItem.getLocalFileName()});

            singleDone(((double) patchItem.getSize()) / 2);
            log.addDebug("Soubor je rozbalený, hledám instalační skripty.");
            // - if exist start_a.bat, execute it

            File f = new File(uopath + File.separator + "start_a.bat");
            log.addDebug("hledám:" + f.getAbsolutePath());
            if (f.exists()) {
                log.addDebug("Našel jsem start_a.bat.");
                setLabelText("Instaluju patch: " + patchItem.getFileName());

                exec(Settings.getInstance().getOs().getBatchExecCommand(f));
                f.delete();
            }
            // - if exist start_g.bat, execute it
            f = new File(uopath + File.separator + "start_g.bat");
            log.addDebug("hledám:" + f.getAbsolutePath());
            if (f.exists()) {
                log.addDebug("Našel jsem start_g.bat.");
                setLabelText("Instaluju patch: " + patchItem.getFileName());
                exec(Settings.getInstance().getOs().getBatchExecCommand(f));
                f.delete();
            }
            // - finish install procedure and remove file from queue
            // Fake progress ? :-) .. maybe later, I don't wanna think about it now :-P
            // singleDone(  ( (double) patchItem.getSize() )/(Math.random()*5));
            // wait(300);
            setLabelText("Práce dokončena (" + patchItem.getFileName() + ").");
            singleDone((double) patchItem.getSize());
            log.addDebug("Instalace patche " + patchItem.getFileName() + " dokončena.");
        } else {
            // copy files into game directory
            setLabelText("Kopiruju soubor: " + patchItem.getFileName() + " do adresáře s UO.");

            final String uopath = Settings.getInstance().getValue(Settings.ULTIMA_ONINE_PATH);
            File inFile = new File(patchItem.getLocalFileName());
            File outFile = new File(uopath + File.separator + patchItem.getFileName());

            try {
                FileChannel inChannel = new FileInputStream(inFile).getChannel();
                FileChannel outChannel = new FileOutputStream(outFile).getChannel();
                try {
                    // magic number for Windows, 64Mb - 32Kb)
                    int maxCount = (64 * 1024 * 1024) - (32 * 1024);
                    long size = inChannel.size();
                    long position = 0;
                    while (position < size) {
                        position +=
                                inChannel.transferTo(position, maxCount, outChannel);
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
        removeFirst();
    }
}

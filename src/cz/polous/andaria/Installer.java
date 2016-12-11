package cz.polous.andaria;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.channels.FileChannel;
import cz.polous.andaria.Settings.VALUES;

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
    private long extractTotalSize;
    private long extractProgressPart;
    long totalsize = 0;
    long totalsizeEnd = 0;
    long singlediv = 0;

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
     * Override PatchQueue method - Installer try start automaticly after new
     * file added to queue.
     *
     * @param p item to add
     * @see cz.polous.andaria.Downloader#executeNext
     **************************************************************************/
    @Override
    protected void addPatchItem(PatchItem p) {
        super.addPatchItem(p);
        startSafe();
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
                    Process proc = Runtime.getRuntime().exec(command, null, new File(Settings.getInstance().getValue(VALUES.ULTIMA_ONINE_PATH)));
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
        File f;
        final Settings settings = Settings.getInstance();
        final String uopath = settings.getValue(VALUES.ULTIMA_ONINE_PATH);

        // progress will be counted by 65% for extracting files
        // and  35% for installing patches expect for uoml file.
        // ProgressBars - for single file
        resetProgressBar(BARS.SINGLE, patchItem.getSize());
        if (patchItem.getStorage() == VALUES.DIST_STORAGE) {
           // extractProgressPart = patchItem.getSize();
            // - if exist start_g.bat, execute it
            f = new File(settings.getOs().getConfigPath());
            log.addDebug("Hledám:" + f.getAbsolutePath());
            if (canceled()) {
                return;
            }
            if (f.exists()) {
                log.addDebug("Našel jsem config - mazu ho.");
                f.delete();
            }
        } //else {
            extractProgressPart = Math.round(patchItem.getSize() * 65 / 100.00);
       // }

        log.addDebug("Pracuju se souborem: ".concat(patchItem.getLocalFileName()));
        log.addDebug("...jeho velikost je: ".concat(Double.toString(patchItem.getSize())));
        totalsize += patchItem.getSize();

        if (patchItem.isPacked()) {
            setLabelText("Rozbaluju soubor: " + patchItem.getFileName());
            J7zipBinding sevenZip = new J7zipBinding();
            try {
                sevenZip.unpack(patchItem.getLocalFileName());
            } catch (Exception ex) { // unzip errors
                log.addErr("Chyba při rozbalování souboru");
                log.addEx(ex);
                setLabelSpeed(0);
                removeFirst();
                return;
            }

            // setLabelSpeed(0);
            log.addDebug("Soubor je rozbalený, hledám instalační skripty.");
            f = new File(uopath + File.separator + "start_a.bat");
            log.addDebug("Hledám:" + f.getAbsolutePath());
            if (canceled()) {
                return;
            }
            if (f.exists()) {
                log.addDebug("Našel jsem start_a.bat.");
                setLabelText("Instaluju patch (start_a.bat): " + patchItem.getFileName());

                exec(settings.getOs().getBatchExecCommand(f));
                f.delete();
                setSingleProgressPercents(68);
            }
            // - if exist start_g.bat, execute it
            f = new File(uopath + File.separator + "start_g.bat");
            setSingleProgressPercents(72);
            log.addDebug("Hledám:" + f.getAbsolutePath());
            if (canceled()) {
                return;
            }
            if (f.exists()) {
                log.addDebug("Našel jsem start_g.bat.");
                setLabelText("Instaluju patch (start_g.bat): " + patchItem.getFileName());
                exec(settings.getOs().getBatchExecCommand(f));
                f.delete();
                setSingleProgressPercents(81);
            }

            // - if exist start_g.bat, execute it
            f = new File(uopath + File.separator + "install.bat");
            setSingleProgressPercents(88);
            log.addDebug("Hledám:" + f.getAbsolutePath());
            if (canceled()) {
                return;
            }
            if (f.exists()) {
                log.addDebug("Našel jsem install.bat.");
                setLabelText("Instaluju patch (install.bat): " + patchItem.getFileName());
                exec(settings.getOs().getBatchExecCommand(f));
                f.delete();
                setSingleProgressPercents(96);
            }

            if (patchItem.getStorage() == (VALUES.DIST_STORAGE)) {
                if (settings.getOs().getClass() == WindowsOS.class) {
                    WindowsOS os = (WindowsOS) settings.getOs();
                    os.generateRegistryData(uopath);
                }
                // a strange hack, but it works :-)
                // this should merge settings in memory and .xml data.
                FrontEnd.getInstance().loadSettings();
                FrontEnd.getInstance().saveSettings();
                PatchList.getInstance().reload();
            }
            if (patchItem.getFileName().equals(settings.getValue(VALUES.RAZOR_PATCH_NAME))) {
                Settings.getInstance().addAutorun("razor", uopath + File.separator + settings.getValue(VALUES.RAZOR_INSTALL_PATH), "client");
                if (settings.getOs().getClass() == WindowsOS.class) {
                    WindowsOS os = (WindowsOS) settings.getOs();
                    os.generateRazorData(uopath);
                }
            }
            if (patchItem.getFileName().equals(settings.getValue(VALUES.UOAM_PATCH_NAME))) {
                Settings.getInstance().addAutorun("uoam", uopath + File.separator + settings.getValue(VALUES.UOAM_INSTALL_PATH));
            }
            setLabelText("Práce dokončena (" + patchItem.getFileName() + ").");
            log.addDebug("Instalace patche " + patchItem.getFileName() + " dokončena.");

        } else {
            // copy raw downloaded file into game directory
            // probably not used, after unrar - 7zip migration.
            extractProgressPart = patchItem.getSize();
            setLabelText("Kopíruju soubor: " + patchItem.getFileName() + " do adresáře s UO.");


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
        //TODO: odstranit tyhle kontroly velikosti a jejich promenne. Jsou celkem zbytecne.
        log.addDebug(
                "Spočítaná velikost doinstalovaného souboru: ".concat(Double.toString(getSingleProgress())));
        //   totalsizeEnd += patchItem.getSize();
        //  if (totalsizeEnd != totalsize) {
        //       log.addErr("Nevychází velikost celkově nainstalovaných patchů na začátku a konci instalačního procesu.");
        //  }
        //  if (getTotalProgress() != totalsize) {
        //      log.addErr("Nevychází velikost celkově nainstalovaných patchů a stropu progressBaru.");
        // }



        if (getTotalMax() == getTotalProgress()) {
            log.addDebug("Podle progressbaru jsou všechny patche jsou nainstalované.");
        }

        removeFirst();
        // Autoclose flag set, all work finished, no problems
        if (Settings.getAutoInstall() == Settings.AUTO_LEVELS.AUTO_CLOSE && !failed && (Downloader.getInstance().patchQueue.size() + this.patchQueue.size() == 0)) {
            FrontEnd.getInstance().closeMe();
        }
        updateProgressBar(BARS.TOTAL);
    }

    /***************************************************************************
     * Part of progressbar belogns to file extraction and rest of bar belongs
     * to patch instalation process.
     *
     * setExtractedProgress count and set progressbar while extracting patch
     * files.
     **************************************************************************/
    public void setExtractedProgress(long value) {
        setSingleProgress(Math.round(value * extractProgressPart / extractTotalSize));
    }

    /***************************************************************************
     * TotalExtractedSize hold total size of files to extract. This entry is
     * used to calculate extract progress bar part [setExtractedProgress()].
     **************************************************************************/
    public void setExtractedTotal(long value) {
        extractTotalSize = value;
    }
}

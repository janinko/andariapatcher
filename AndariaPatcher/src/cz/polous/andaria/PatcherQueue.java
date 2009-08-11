package cz.polous.andaria;

/*******************************************************************************
 * Supertrida, ktera se stara o frontu patchu a probihajici proces. Dedi ji
 * tridy Downloader a Installer, ktere zajisti konkretni zpracovani patchu
 * ve fronte.
 * 
 * Statusbar updates and counting moved to ProgressBar class.
 *
 * Class is singleton.
 * @author  Martin Polehla (andaria_patcher@polous.cz)
 ******************************************************************************/
import java.util.Vector;

abstract class PatcherQueue extends ProgressBar implements Runnable  {

    protected Vector patchQueue = new Vector();
    private boolean inProgress;
    private boolean cancel = false;
    protected Log log;

    /***************************************************************************
     * (abstract) Queue item processing method.
     **************************************************************************/
    abstract void executeNext();

    /***************************************************************************
     * Creates a new instance of Patcher thread
     **************************************************************************/
    protected PatcherQueue() {
        log = new Log(this);
    }

    /***************************************************************************
     * Start Patcher thread
     **************************************************************************/
    @Override
    public void run() {
       // reset();
        execute();
    }

    public boolean isFinished() {
        return !inProgress;
    }

    public boolean inProgress() {
        return inProgress;
    }

    /***************************************************************************
     * Main patch queue process method. It wait for notify() message and call
     * executeNext() if any items found in queue.
     **************************************************************************/
    private synchronized void execute() {
        try {
            while (true) {
                //cancel = false;
                log.addDebug("Čekám...");

                inProgress = false;
                FrontEnd.getInstance().updateButtons();

                // wait for start() or startSafe() invocation
                wait();
                cancel = false;
                inProgress = true;
                FrontEnd.getInstance().updateButtons();

                while (patchQueue.size() > 0 && !cancel) {
                    log.addDebug("Volání executeNext. Ve frontš je ještě: " + patchQueue.size() + " souborů.");
                    executeNext();
                }
                this.inProgress = false;
                if (cancel) {
                   // reset();
                    setLabelText("Činnost byla přerušena, vyčkávám na další příkazy...");
                    if (!Installer.getInstance().inProgress() && Downloader.getInstance().inProgress())
                        log.addLine("Činnost byla pozastavena.");
                } else {
                    setLabelText("Vyčkávám na další příkazy...");
                    if (!Installer.getInstance().inProgress() && Downloader.getInstance().inProgress())
                        log.addLine("Činnost byla dokončena.");

                }
            }
        } catch (InterruptedException e) {
            log.addEx(e);
        }
    }

    /***************************************************************************
     * Reset queue state
     **************************************************************************/
    void reset() {
        this.patchQueue.removeAllElements();
        resetProgressBar(BARS.SINGLE, 0);
        resetProgressBar(BARS.TOTAL, 0);

    }

    /***************************************************************************
     * Pause queue thread.
     **************************************************************************/
    public synchronized void pause() {
        try {
            wait();
        } catch (InterruptedException e) {
            log.addEx(e);
        }
    }

    /***************************************************************************
     * Resume paused queue thread.
     **************************************************************************/
    public synchronized void start() {
        log.addDebug("Startuj thread. inProgress = " + this.inProgress + ".");
        //this.wait();
        notifyAll();
    }

    /**
     * Resume queue thread only when executing process is not in progress (variable inProgres == false).
     *  Used to normal start of queue processing when execute method get object to wait() state.
     *  @see cz.polous.andaria.Installer#exec
     **************************************************************************/
    public void startSafe() {
        //log.addDebug("Pracuje thread: (" + this.inProgress + ") ?");
        if (!inProgress) {
            //reset();
            start();
        }
    }

   

    /***************************************************************************
     * Add new PatchItem to queue.
     * @param p item
     **************************************************************************/
    protected void addPatchItem(PatchItem p) {
        patchQueue.add(p);
    }

    /***************************************************************************
     * Get item to pricess
     * @return first item in queue
     **************************************************************************/
    PatchItem getFirstItem() {
        return (PatchItem) patchQueue.get(0);
    }
 
    boolean notCanceled() {
        return !cancel;
    }

    boolean canceled() {
        return cancel;
    }

    void removeFirst() {
        patchQueue.remove(0);
        
    }

    void cancel() {
        cancel = true;
        log.addLine("Čekám na dokončení nepřerušitelných akcí.");
        setLabelText("Akce bude co nejdříve přerušena.");
    }


   
}
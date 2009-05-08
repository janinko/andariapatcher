package cz.polous.andaria;

/*******************************************************************************
 * Supertrida, ktera se stara o frontu patchu a probihajici proces. Dedi ji
 * tridy Downloader a Installer, ktere zajisti konkretni zpracovani patchu
 * ve fronte.
 * 
 * Resi update statusbaru, monitorovani thready a pod.
 * 
 * @author  Martin Polehla (andaria_patcher@polous.cz)
 ******************************************************************************/
import java.util.Vector;

abstract class PatcherQueue implements Runnable {

    protected Vector patchQueue;
    private double totalDone;
    private double totalAmount;
    private double singleDone;
    private double singleAmount;
    private boolean inProgress;
    private boolean cancel;
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
    public void run() {
        reset();
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

                wait();

                inProgress = true;
                FrontEnd.getInstance().updateButtons();

                updateSingleBar();
                updateTotalBar();

                while (patchQueue.size() > 0 && !cancel) {
                    log.addDebug("Volání executeNext. Ve frontš je ještě: " + patchQueue.size() + " souborů.");
                    executeNext();
                }
                if (cancel) {
                    setLabelText("Všechny akce byly zrušeny, vyčkávám na další příkazy...");
                    reset();
                } else {
                    setLabelText("Vyčkávám na další příkazy...");
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
        setTotalDone(0);
        setTotalAmount(0);
        cancel = false;
        inProgress = false;
        patchQueue = new Vector();
        updateSingleBar();
        updateTotalBar();
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
        // TODO: Control this method and callers for bugs
        //log.addLine(" -- prikaz k praci");
        //if (getState() == State.WAITING)
        notifyAll();
    //this.inProgress = true;
    }

    /**
     * Resume queue thread only when executing process is not in progress (variable inProgres == false).
     *  Used to normal start of queue processing when execute method get object to wait() state.
     *  @see cz.polous.andaria.Installer#exec
     **************************************************************************/
    public void startSafe() {
        //log.addLine(">> bezpecne pracuj (" + this.inProgress + ")");
        if (!inProgress) {
            start();
        }
    }

    double getTotalDone() {
        return totalDone;
    }

    double getTotalAmount() {
        return totalAmount;
    }

    double getSingleDone() {
        return singleDone;
    }

    double getSingleAmount() {
        return singleAmount;
    }

    /***************************************************************************
     * Add number to single file length
     * @param i (double) addition to singleDone amount.
     **************************************************************************/
    protected void addSingleDone(double i) {
        setSingleDone(singleDone + i);
        setTotalDone(totalDone + i);
        updateSingleBar();
        updateTotalBar();
    }

    /***************************************************************************
     * Substract number from total length of all files in queue.
     * @param i (double) substract amount
     **************************************************************************/
    public void removeTotalAmount(double i) {
        setTotalAmount(totalAmount - i);
        updateTotalBar();
    }

    /***************************************************************************
     * Set single file done amount (progress done) to a value
     * (i shouldn't be more than singlDoneAmount). Also correct totalDone value.
     * @param i (double) value
     **************************************************************************/
    void singleDone(double i) {
        setTotalDone(totalDone - singleDone + i);
        setSingleDone(i);
        updateTotalBar();
        updateSingleBar();
    }

    /***************************************************************************
     * Reset current file progress and set SingleDone to a value.
     * @param i (double) value
     **************************************************************************/
    void resetSingleDone(double i) {
        setSingleAmount(i);
        resetSingleDone();
    }

    /***************************************************************************
     * Reset current file progress.
     **************************************************************************/
    void resetSingleDone() {
        setSingleDone(0);
        updateSingleBar();
    }

    public void setTotalAmount(double i) {
        totalAmount = i;
    }

    private void setTotalDone(double i) {
        totalDone = i;
    }

    public void setSingleAmount(double i) {
        singleAmount = i;
    }

    private void setSingleDone(double i) {
        singleDone = i;
    }

    /***************************************************************************
     * Update total progressbar value
     * (count from totalDone / totalAmount percent).
     **************************************************************************/
    protected void updateTotalBar() {
        try {
            try {
                double total = totalDone / totalAmount * 100;
                FrontEnd.getInstance().getjPBDownloadTotal().setValue((int) total);
            } catch (ArithmeticException e) {
                FrontEnd.getInstance().getjPBDownloadTotal().setValue(0);
            }
            FrontEnd.getInstance().getjPBDownloadTotal().repaint();
        } catch (NullPointerException e) {
            log.addEx(e);
        }
    }

    /***************************************************************************
     * Update single progressbar value
     * (count from totalDone / totalAmount percent).
     **************************************************************************/
    protected void updateSingleBar() {
        try {
            try {
                double total = singleDone / singleAmount * 100;
                FrontEnd.getInstance().getjPBDownloadSingle().setValue((int) total);
            } catch (ArithmeticException e) {
                FrontEnd.getInstance().getjPBDownloadSingle().setValue(0);
            }
        } catch (NullPointerException e) {
            log.addEx(e);
        }
    }

    /***************************************************************************
     * Set process Label
     * @param s (String) New text
     **************************************************************************/
    void setLabelText(String s) {
        try {
            FrontEnd.getInstance().getjLDownload().setText(s);
        } catch (Exception e) {
        }
    }

    /***************************************************************************
     * Add new PatchItem to queue.
     * @param p item
     **************************************************************************/
    protected void addPatchItem(PatchItem p) {
        patchQueue.add(p);
        setTotalAmount(totalAmount + p.getSize());
    }

    /***************************************************************************
     * Get item to pricess
     * @return first item in queue
     **************************************************************************/
    PatchItem getFirstItem() {
        return (PatchItem) patchQueue.get(0);
    }

    //  void setInProgress() {
    //      inProgress = true;
    //       FrontEnd.getInstance().updateButtons();
    // }

    // void resetInProgress() {
    //    inProgress = false;
    //     FrontEnd.getInstance().updateButtons();
    // }
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
    }
}
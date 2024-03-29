package cz.polous.andaria;

import java.text.NumberFormat;
import javax.swing.JProgressBar;

/**
 * Take cares about progress bar and labels.
 * Universal class used by Installer and Downloader.
 *
 * @author Martin Polehla
 */
public class ProgressBar {

    private Log log;
    private long totalProgress;
    private long totalMax;
    private long singleProgress;
    private long singleMax;
    private NumberFormat speedFormat;

    public static class BARS {
        static final int SINGLE = 0;
        static final int TOTAL = 1;
    }

    /***************************************************************************
     * Update progressbar component and repaint it.
     *
     * (count from totalDone / totalAmount percent).
     **************************************************************************/
    protected void updateProgressBar(int bar) {
        JProgressBar pb;
        double total;
        try {
            switch (bar) {
                case 0:
                    pb = FrontEnd.getInstance().getjPBSingle(this);
                    total = 100.00 * singleProgress / singleMax;
                    break;
                case 1:
                    pb = FrontEnd.getInstance().getjPBTotal(this);
                    total = 100.00 * totalProgress / totalMax;
                    break;
                default:
                    log.addErr("Neznámý parametr funkce.");
                    return;
            }
            pb.setValue((int) total);
            pb.repaint();
        } catch (ArithmeticException e) {
        } catch (NullPointerException e) {
            log.addEx(e);
        }

    }

    void setLabelText(String s) {
        try {
            FrontEnd.getInstance().getjLabel(this, FrontEnd.LABEL_TYPES.TEXT).setText(s);
        } catch (Exception e) {
        }
    }

    String getSpeedLabelText() {
        String text = null;
        try {
            text = FrontEnd.getInstance().getjLabel(this, FrontEnd.LABEL_TYPES.SPEED).getText();
        } catch (Exception e) {
        } finally {
            return text;
        }
    }
    /***************************************************************************
     * Set patchers speed label
     * 
     * @param i (double) speed in bytes per second
     **************************************************************************/
    void setLabelSpeed(double speed) {
        String s = speedFormat.format(speed).concat(" kB/s");
        try {
            FrontEnd.getInstance().getjLabel(this, FrontEnd.LABEL_TYPES.SPEED).setText(s);
        } catch (Exception e) {
        }
    }

    /***************************************************************************
     * Set single file progress to a value
     * (it shouldn't be more than singlDoneMax).
     *
     * Also updates totalProgress value.
     *
     * @param i (double) value
     **************************************************************************/
    public void setSingleProgress(long i) {
        if (i > singleMax) {
            i = singleMax;
            log.addErr("Chyba počítání velikosti souborů.");
        }

        setTotalProgress(totalProgress - singleProgress + i);
        singleProgress = i;
        updateProgressBar(BARS.SINGLE);
    }

    void resetProgressBar(int bar, long max) {
        switch (bar) {
            case BARS.SINGLE:
                log.addDebug("Resetuji Single progressbar.");
                singleMax = max;
                break;
            case BARS.TOTAL:
                log.addDebug("Resetuji Total progressbar.");
                totalMax = max;
                break;
            default:
                log.addErr("Pokus o resetovani neexistujiciho progressBaru");
        }
        resetProgressBar(bar);
    }

    void resetProgressBar(int bar) {
        switch (bar) {
            case BARS.SINGLE:
                singleProgress = 0;
                break;
            case BARS.TOTAL:
                totalProgress = 0;
                break;
            default:
                log.addErr("Pokus o resetovani neexistujiciho progressBaru");
        }
        setLabelSpeed(0);
        updateProgressBar(bar);
    }

    public void removeFromTotalProgress(long i) {
        setTotalMax(totalMax - i);
    }

    /***************************************************************************
     * COMMON CLASSES
     **************************************************************************/
    /***************************************************************************
     * Constructor
     **************************************************************************/
    protected ProgressBar() {
        log = new Log(this);
        speedFormat = NumberFormat.getInstance();
        speedFormat.setMaximumFractionDigits(2);
        speedFormat.setMinimumFractionDigits(2);
    }

    long getTotalProgress() {
        return totalProgress;
    }

    long getTotalMax() {
        return totalMax;
    }

    long getSingleProgress() {
        return singleProgress;
    }

    long getSingleMax() {
        return singleMax;
    }

    public void setTotalProgress(long i) {
        totalProgress = i;
        updateProgressBar(BARS.TOTAL);
    }

    public void setTotalMax(long i) {
        totalMax = i;
        updateProgressBar(BARS.TOTAL);
    }

    public void setSingleMax(long i) {
        singleMax = i;
        updateProgressBar(BARS.SINGLE);
    }

    void setSingleProgressPercents(int perc) {
        setSingleProgress(singleMax * perc / 100);
    }
}

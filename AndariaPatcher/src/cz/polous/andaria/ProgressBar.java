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
    private double totalProgress;
    private double totalMax;
    private double singleProgress;
    private double singleMax;
    private NumberFormat speedFormat;


    public static class Bars {
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
            FrontEnd.getInstance().getjLabel(this, FrontEnd.LType.TEXT).setText(s);
        } catch (Exception e) {
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
            FrontEnd.getInstance().getjLabel(this, FrontEnd.LType.SPEED).setText(s);
        } catch (Exception e) {
        }
    }

    @Deprecated
    protected void addToSingleProgress(double i) {
        setSingleProgress(singleProgress + i);
    }

    /***************************************************************************
     * Set single file progress to a value
     * (it shouldn't be more than singlDoneMax).
     *
     * Also updates totalProgress value.
     *
     * @param i (double) value
     **************************************************************************/
    public void setSingleProgress(double i) {
        if (i > singleMax) {
            i = singleMax;
        }

        setTotalProgress(totalProgress - singleProgress + i);
        singleProgress = i;
        updateProgressBar(Bars.SINGLE);
    }

    void resetSingleProgress(double i) {
        setSingleMax(i);
        resetSingleProgress();
    }

    void resetSingleProgress() {
        setLabelSpeed(0);
        singleProgress = 0;
        updateProgressBar(Bars.SINGLE);
    }

    public void removeFromTotalProgress(double i) {
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

    double getTotalProgress() {
        return totalProgress;
    }

    double getTotalMax() {
        return totalMax;
    }

    double getSingleProgress() {
        return singleProgress;
    }

    double getSingleMax() {
        return singleMax;
    }

    public void setTotalProgress(double i) {
        totalProgress = i;
        updateProgressBar(Bars.TOTAL);
    }

    public void setTotalMax(double i) {
        totalMax = i;
        updateProgressBar(Bars.TOTAL);
    }

    public void setSingleMax(double i) {
        singleMax = i;
        updateProgressBar(Bars.SINGLE);
    }

    void setSingleProgressPercents(int perc) {
        setSingleProgress(singleMax * perc / 100);
    }
}

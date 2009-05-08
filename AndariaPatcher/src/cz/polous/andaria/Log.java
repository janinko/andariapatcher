package cz.polous.andaria;

import javax.swing.JTextArea;

/*******************************************************************************
 *
 * @author p0l0us
 ******************************************************************************/
class Log {

    protected static JTextArea logArea;
    private String logOwnerClass; // name of object owning instance of log

    /** Creates a new instance of Log */
    public Log(Object ob) {
        logOwnerClass = ob.getClass().toString();
    }

    public Log(String obName) {
        logOwnerClass = obName;
    }

    public void addDebug(String debugLine) {
        if (Settings.getInstance().debugMode()) {
            System.out.println(debugLine);
            add("DEBUG: " + debugLine);
        }
    }

    public void addErr(String errLine) {
        if (Settings.getInstance().debugMode()) {
            System.err.println(errLine);
        }

        add("CHYBA: " + errLine + "!");
    }

    public void addEx(Exception e) {
        if (Settings.getInstance().debugMode()) {
            e.printStackTrace();
            add(e.getStackTrace().toString());
        }
        add("VNITRNI CHYBA: " + e.getMessage() + "!!!!!!!");

    }

    public void addLine(String line) {
        if (Settings.getInstance().debugMode()) {
            System.out.println(line);
        }

        add(line);

    }

    private void add(String line) {
        if (Settings.getInstance().debugMode()) {
            logArea.insert("[" + logOwnerClass + "]  " + line + "\n", 0);
        } else {
            logArea.insert(line + "\n", 0);
        }
    }

    public void addCmd(String[] cmd) {
        String debugLine = new String();

        for (int i = 0; i < cmd.length; i++) {
            debugLine += cmd[i] + " ";
        }

        addLine("Spoustim prikaz: " + debugLine);
    }
}

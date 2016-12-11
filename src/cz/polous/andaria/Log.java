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

        add("CHYBKA: " + errLine + "!");
    }

    public void addEx(Exception e) {
        if (Settings.getInstance().debugMode()) {
            e.printStackTrace();
            add(e.getStackTrace().toString());
        }
        add("VNITŘNÍ CHYBA: " + e.getMessage() + "! Není třeba panikařit! Nahlašte však prosím danou chybu na fóru andaria.cz a vývojaři se ji pokusí co nejdříve vyřešit.");

    }

    public void addLine(String line) {
        if (Settings.getInstance().debugMode()) {
            System.out.println(line);
        }

        add(line);

    }

    private void add(String line) {
        if (Settings.getInstance().debugMode()) {
            logArea.append("[" + logOwnerClass + "]  " + line + "\n");
        } else {
            logArea.append(line + "\n");
        }
    }

    public void addCmd(String[] cmd) {
        String debugLine = new String();

        for (int i = 0; i < cmd.length; i++) {
            debugLine += cmd[i] + " ";
        }

        addLine("Spouštím příkaz: " + debugLine);
    }
}

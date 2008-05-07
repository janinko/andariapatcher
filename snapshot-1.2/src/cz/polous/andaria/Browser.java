/*
 * Browser.java
 *
 * Created on 3. říjen 2007, 23:46
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package cz.polous.andaria;

import java.awt.Dimension;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;
import org.lobobrowser.html.HtmlRendererContext;
import org.lobobrowser.html.gui.HtmlPanel;
import org.lobobrowser.html.parser.DocumentBuilderImpl;
import org.lobobrowser.html.parser.InputSourceImpl;
import org.lobobrowser.html.test.SimpleHtmlRendererContext;
import org.xml.sax.InputSource;


/**
 *
 * @author p0l0us
 */
class Browser {

    HtmlPanel htmlPanel;
    String uri;
    private static Log log;

    /** Creates a new instance of Browser */
    public Browser(HtmlPanel p, String url) {
        log = new Log(this);
        uri = url;
        htmlPanel = p;
        try {
            openUrl(url);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

/**
     * Custom hyperlink listener. Used by jTBrowser JTextArea.
     *
     * @see cz.polous.andaria.Browser#openUrl
     */
    private class hyperlink implements HyperlinkListener {

        public void hyperlinkUpdate(HyperlinkEvent e) {
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                JEditorPane pane = (JEditorPane) e.getSource();
                if (e instanceof HTMLFrameHyperlinkEvent) {
                    HTMLFrameHyperlinkEvent evt = (HTMLFrameHyperlinkEvent) e;
                    HTMLDocument doc = (HTMLDocument) pane.getDocument();
                    doc.processHTMLFrameHyperlinkEvent(evt);
                } else {
                    try {
                        pane.setPage(e.getURL());
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * @param uri Url to display
     */
    private void openUrl(String uri) throws Exception {
        URL url = new URL(uri);
        URLConnection connection = url.openConnection();
        InputStream in = connection.getInputStream();
        Reader reader = new InputStreamReader(in, "windows-1250");
        InputSource is = new InputSourceImpl(reader, uri);

        HtmlRendererContext rendererContext = new LocalHtmlRendererContext(htmlPanel);
        //htmlPanel.setPreferredWidth(400);
        htmlPanel.setPreferredSize(new Dimension(400, 400));

        DocumentBuilderImpl builder = new DocumentBuilderImpl(rendererContext.getUserAgentContext(), rendererContext);
        org.w3c.dom.Document document = builder.parse(is);
        in.close();
        htmlPanel.setDocument(document, rendererContext);
    }

/**
     *
     */
    private static class LocalHtmlRendererContext extends SimpleHtmlRendererContext {

        // O1verride methods here to implement browser functionality
        public LocalHtmlRendererContext(HtmlPanel contextComponent) {
            super(contextComponent);
        }
    }

    public void reload() {
        try {
            openUrl(uri);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
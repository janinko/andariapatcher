package cz.polous.andaria;

import java.awt.Dimension;
import java.io.IOException;
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
import org.lobobrowser.html.FormInput;
import org.lobobrowser.html.HtmlRendererContext;
import org.lobobrowser.html.UserAgentContext;
import org.lobobrowser.html.gui.HtmlPanel;
import org.lobobrowser.html.gui.SelectionChangeEvent;
import org.lobobrowser.html.gui.SelectionChangeListener;
import org.lobobrowser.html.parser.DocumentBuilderImpl;
import org.lobobrowser.html.parser.InputSourceImpl;
import org.lobobrowser.html.test.SimpleHtmlRendererContext;
import org.lobobrowser.html.test.SimpleUserAgentContext;
import org.xml.sax.InputSource;

/**
 * Trida ktera zobrazi stranku v HtmlPanelu.
 * 
 * nepouzivano - misto toho se puziva knihovna Cobra.
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

        p.addSelectionChangeListener(new SelectionListener());
        try {
            openUrl(url);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private class SelectionListener implements SelectionChangeListener {

        @Override
        public void selectionChanged(SelectionChangeEvent arg0) {
            // do nothing
        }
    }

    /**
     * Custom hyperlink listener. Used by jTBrowser JTextArea.
     *
     * useful for Browser2 implementation
     *
     * @see cz.polous.andaria.Browser#openUrl
     */
    @Deprecated
    private class hyperlink implements HyperlinkListener {

        @Override
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

    private static class LocalUserAgentContext
            extends SimpleUserAgentContext {
        // Override methods from SimpleUserAgentContext to
        // provide more accurate information about application.

        public LocalUserAgentContext() {
        }

        @Override
        public String getAppMinorVersion() {
            return "6";
        }

        @Override
        public String getAppName() {
            return "AndariaPatcher";
        }

        @Override
        public String getAppVersion() {
            return "1";
        }

        @Override
        public String getUserAgent() {
            return "Mozilla/4.0 (compatible;) AndariaPatcher/cobra";
        }
    }

    /**
     * @param uri Url to display
     */
    private void openUrl(String uri) throws Exception {
        URL url = new URL(uri);
        URLConnection connection = url.openConnection();
        InputStream in = connection.getInputStream();
        Reader reader = new InputStreamReader(in, "utf-8");
        InputSource is = new InputSourceImpl(reader, uri);

        UserAgentContext ucontext = new LocalUserAgentContext();
        HtmlRendererContext rendererContext = new LocalHtmlRendererContext(htmlPanel, ucontext);
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

        HtmlPanel htmlPanel;

        public LocalHtmlRendererContext(HtmlPanel contextComponent) {
            super(contextComponent);
            this.htmlPanel = contextComponent;
        }

        public LocalHtmlRendererContext(HtmlPanel contextComponent,
                UserAgentContext ucontext) {
            super(contextComponent, ucontext);
        }

        @Override
        protected String getDocumentCharset(URLConnection connection) {
            return "ISO-8859-2";
        }

        /**
         * Submits a form and/or navigates by making
         * a <i>synchronous</i> request. This method is invoked
         * by {@link #submitForm(String, URL, String, String, FormInput[])}.
         * @param method The request method.
         * @param action The action URL.
         * @param target The target identifier.
         * @param enctype The encoding type.
         * @param formInputs The form inputs.
         * @throws IOException
         * @throws org.xml.sax.SAXException
         * @see #submitForm(String, URL, String, String, FormInput[])
         */
        @Override
        protected void submitFormSync(final String method, final java.net.URL action, final String target, String enctype, final FormInput[] formInputs) throws IOException, org.xml.sax.SAXException {
            URL url = action;
            URLConnection connection = url.openConnection();
            InputStream in = connection.getInputStream();
            Reader reader = new InputStreamReader(in, "utf-8");
            InputSource is = new InputSourceImpl(reader, action.toString());

            HtmlRendererContext rendererContext = new LocalHtmlRendererContext(htmlPanel);

            DocumentBuilderImpl builder = new DocumentBuilderImpl(rendererContext.getUserAgentContext(), rendererContext);
            org.w3c.dom.Document document = builder.parse(is);
            in.close();
            htmlPanel.setDocument(document, rendererContext);
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

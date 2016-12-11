package cz.polous.andaria;

import SevenZip.Archive.IArchiveExtractCallback;
import SevenZip.Archive.IInArchive;
import SevenZip.Archive.SevenZip.Handler;
import SevenZip.Archive.SevenZipEntry;
import SevenZip.HRESULT;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

/**
 *
 * @author Martin Polehla
 */
public class J7zipBinding {

    private static Log log;

    public J7zipBinding() {
        log = new Log(this);
        log.addDebug("\nJ7zip 4.43 ALPHA 2 (" + Runtime.getRuntime().availableProcessors() + " CPUs)");
    }

    public void unpack(String fileName) throws IOException, Exception {
        Vector<String> listOfNames = new Vector<String>();
        J7zipRandomAccessFile istream = new J7zipRandomAccessFile(fileName, "r");
        IInArchive archive = new Handler();
        int ret = archive.Open(istream);
        if (ret != 0) {
            System.out.println("ERROR !");
            return;
        }
        //Installer.getInstance().setExtractedTotal( getExtractedSize(archive));
        log.addLine("Rozbaluji soubor: ".concat(new File(fileName).getName()));
        testOrExtract(archive, listOfNames, IInArchive.NExtract_NAskMode_kExtract);
        archive.close();

    }

    static void testOrExtract(IInArchive archive, Vector<String> listOfNames, int mode) throws Exception {

        J7zipArchiveExtractCallback extractCallbackSpec = new J7zipArchiveExtractCallback();
        IArchiveExtractCallback extractCallback = extractCallbackSpec;
        extractCallbackSpec.Init(archive);
        extractCallbackSpec.PasswordIsDefined = false;

        try {
            int len = 0;
            int arrays[] = null;

            if (listOfNames.size() >= 1) {
                arrays = new int[listOfNames.size()];
                for (int i = 0; i < archive.size(); i++) {
                    if (listOfNames.contains(archive.getEntry(i).getName())) {
                        arrays[len++] = i;
                    }
                }
            }

            int res;

            if (len == 0) {
                res = archive.Extract(null, -1, mode, extractCallback);
            } else {
                res = archive.Extract(arrays, len, mode, extractCallback);
            }

            if (res == HRESULT.S_OK) {
                if (extractCallbackSpec.NumErrors == 0) {
                    System.out.println("Ok Done");
                } else {
                    System.out.println(" " + extractCallbackSpec.NumErrors + " errors");
                }
            } else {
                System.out.println("ERROR !!");
            }
        } catch (java.io.IOException e) {
            System.out.println("IO error : " + e.getLocalizedMessage());
        }
    }

    static long getExtractedSize(IInArchive archive) {
        long size = 0;
        log.addDebug("Obsah archivu:");

        for (int i = 0; i < archive.size(); i++) {
            SevenZipEntry item = archive.getEntry(i);
            log.addDebug(item.getName().concat(" (").concat(String.format("%13d", item.getSize())).concat(")"));
            size += item.getSize();
        }
        log.addDebug("Potřebné místo: ".concat(String.format("%13d", size)));
        return size;
    }

}

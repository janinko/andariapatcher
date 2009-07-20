package cz.polous.andaria;

import SevenZip.Archive.IArchiveExtractCallback;
import SevenZip.Archive.IInArchive;
import SevenZip.Archive.SevenZipEntry;



import SevenZip.HRESULT;
import java.io.File;
import java.util.Date;

public class J7zipArchiveExtractCallback implements IArchiveExtractCallback // , ICryptoGetTextPassword,
{

    private Log log;
    private String printLine = new String();
    SevenZip.Archive.IInArchive _archiveHandler;  // IInArchive
    String _filePath;       // name inside arcvhive
    String _diskFilePath;   // full path to file on disk
    public long NumErrors;
    boolean PasswordIsDefined;
    String Password;
    boolean _extractMode;
    boolean isDirectory;
    Date starTime = new Date();

    public J7zipArchiveExtractCallback() {
        log = new Log(this);
        PasswordIsDefined = false;
        _filePath = Settings.getInstance().getValue(Settings.ULTIMA_ONINE_PATH);
    }

    class OutputStream extends java.io.OutputStream {

        java.io.RandomAccessFile file;

        public OutputStream(java.io.RandomAccessFile f) {
            file = f;
        }

        @Override
        public void close() throws java.io.IOException {
            file.close();
            file = null;
        }
        /*
        public void flush()  throws java.io.IOException {
        file.flush();
        }
         */

        @Override
        public void write(byte[] b) throws java.io.IOException {
            file.write(b);
        }

        @Override
        public void write(byte[] b, int off, int len) throws java.io.IOException {
            file.write(b, off, len);
        }

        @Override
        public void write(int b) throws java.io.IOException {
            file.write(b);
        }
    }

    @Override
    public int SetTotal(long size) {
        Installer.getInstance().setExtractedTotal(size);
        //log.addDebug(new Long(size).toString());
        starTime = new Date();
        return HRESULT.S_OK;
    }

    @Override
    public int SetCompleted(long completeValue) {
        //log.addDebug(new Long(completeValue).toString());
        Installer inst = Installer.getInstance();
        inst.setExtractedProgress(completeValue);
        inst.setLabelSpeed(1.00 * completeValue / ((new Date()).getTime() - starTime.getTime()));
        return HRESULT.S_OK;
    }

    public void PrintString(String str) {
        log.addDebug(str);
        printLine.concat(str);
    }

    public void PrintNewLine() {
        log.addLine(printLine);
        printLine = new String();
    }

    @Override
    public int PrepareOperation(int askExtractMode) {
        _extractMode = false;
        switch (askExtractMode) {
            case IInArchive.NExtract_NAskMode_kExtract:
                _extractMode = true;
        }
        ;
        switch (askExtractMode) {
            case IInArchive.NExtract_NAskMode_kExtract:
                PrintString("Extracting  ");
                break;
            case IInArchive.NExtract_NAskMode_kTest:
                PrintString("Testing     ");
                break;
            case IInArchive.NExtract_NAskMode_kSkip:
                PrintString("Skipping    ");
                break;
        }
        ;
        PrintString(_filePath);
        return HRESULT.S_OK;
    }

    @Override
    public int SetOperationResult(int operationResult) throws java.io.IOException {
        switch (operationResult) {
            case IInArchive.NExtract_NOperationResult_kOK:
                break;
            default: {
                NumErrors++;
                PrintString("     ");
                switch (operationResult) {
                    case IInArchive.NExtract_NOperationResult_kUnSupportedMethod:
                        PrintString("Unsupported Method");
                        break;
                    case IInArchive.NExtract_NOperationResult_kCRCError:
                        PrintString("CRC Failed");
                        break;
                    case IInArchive.NExtract_NOperationResult_kDataError:
                        PrintString("Data Error");
                        break;
                    default:
                        PrintString("Unknown Error");
                }
            }
        }
        /*
        if(_outFileStream != null && _processedFileInfo.UTCLastWriteTimeIsDefined)
        _outFileStreamSpec->File.SetLastWriteTime(&_processedFileInfo.UTCLastWriteTime);
         */
        if (_outFileStream != null) {
            _outFileStream.close(); // _outFileStream.Release();
        }            /*
        if (_extractMode && _processedFileInfo.AttributesAreDefined)
        NFile::NDirectory::MySetFileAttributes(_diskFilePath, _processedFileInfo.Attributes);
         */
        PrintNewLine();
        return HRESULT.S_OK;
    }
    java.io.OutputStream _outFileStream;

    @Override
    public int GetStream(int index,
            java.io.OutputStream[] outStream,
            int askExtractMode) throws java.io.IOException {

        outStream[0] = null;

        SevenZipEntry item = _archiveHandler.getEntry(index);
        _filePath = item.getName();

        File file = new File(_filePath);

        switch (askExtractMode) {
            case IInArchive.NExtract_NAskMode_kTest:
                return HRESULT.S_OK;

            case IInArchive.NExtract_NAskMode_kExtract:

                try {
                    isDirectory = item.isDirectory();

                    if (isDirectory) {
                        if (file.isDirectory()) {
                            return HRESULT.S_OK;
                        }
                        if (file.mkdirs()) {
                            return HRESULT.S_OK;
                        } else {
                            return HRESULT.S_FALSE;
                        }
                    }


                    File dirs = file.getParentFile();
                    if (dirs != null) {
                        if (!dirs.isDirectory()) {
                            if (!dirs.mkdirs()) {
                                return HRESULT.S_FALSE;
                            }
                        }
                    }

                    long pos = item.getPosition();
                    if (pos == -1) {
                        file.delete();
                    }

                    java.io.RandomAccessFile outStr = new java.io.RandomAccessFile(_filePath, "rw");

                    if (pos != -1) {
                        outStr.seek(pos);
                    }

                    outStream[0] = new OutputStream(outStr);
                } catch (java.io.IOException e) {
                    return HRESULT.S_FALSE;
                }

                return HRESULT.S_OK;

        }
        // other case : skip ...
        return HRESULT.S_OK;
    }

    public void Init(SevenZip.Archive.IInArchive archiveHandler) {
        NumErrors = 0;
        _archiveHandler = archiveHandler;
    }
}

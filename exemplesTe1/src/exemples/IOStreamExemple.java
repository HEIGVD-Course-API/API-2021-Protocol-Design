
package exemples;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;

public class IOStreamExemple {
    String parentPath, inFilePath, outFilePath;

    public IOStreamExemple() {
        //get absolute path of top folder (.../API-20201-Protocol-Design
        parentPath = new File("").getAbsolutePath();
        inFilePath = parentPath + "/exemplesTe1/src/in.txt";
        outFilePath = parentPath + "/exemplesTe1/src/out.txt";
    }

    /**
     * Example on reading and writing bite per byte from one file to the an other.
     * Warning : Doesn't show proper exception handling (.close() must be in final)
     */
    public void rwByte() {

        try {
            // Read file stream throws FileNotFoundException
            FileInputStream fis = new FileInputStream(inFilePath);

            // Write file stream throws FileNotFoundException
            FileOutputStream fos = new FileOutputStream(outFilePath);

            int b; // The values are 0-255 (byte)
            // -1 indicates that we are at the end of the stream
            while ((b = fis.read()) != -1) {
                // blocking call throws io IOException (bytes not necessarily written after this call)
                fos.write(b);
                try {
                    Thread.sleep(500);
                }catch (InterruptedException ex){
                    System.out.println(ex.getMessage());
                }
            }
            // With fos.write(b), the os decides when to write, flush forces any unwritten data to be written.
            fos.flush();
            fos.close();
            fis.close();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Example on read and writing chars from one file to another
     * Warning : Doesn't show proper exception handling (.close() must be in final)
     */
    public void rwChar() {
        try {
            // Read file stream throws FileNotFoundException
            FileReader fr = new FileReader(inFilePath);

            // Write file stream throws FileNotFoundException
            FileWriter fw = new FileWriter(outFilePath);
            int c; // the values are 0-65535 (char)
            while((c = fr.read()) != -1) {
                fw.write(c);
            }
            fw.flush();
            fw.close();
            fr.close();

        } catch (IOException e) { // FileNotFoundException is included in IOException
            System.out.println(e.getMessage());
        }
    }

    /**
     * Example on reading and writing blocks of data from one file to another.
     * Implementation using FileReader / Writer in comment
     * Warning : Doesn't show proper exception handling (.close() must be in final)
     */
    public void rwByteBlocks() {
        try {
            int BUFFER_SIZE = 255;
            FileInputStream fis = new FileInputStream(inFilePath);
            FileOutputStream fos = new FileOutputStream(outFilePath);
            byte[] buffer = new byte[BUFFER_SIZE];

            int numberOfBytes;
            // The read method fills the buffer with data
            while ((numberOfBytes = fis.read(buffer)) != -1)
                    // write(buffer) writes the total content off buffer even NULLS if  file data < than buffer size
                    fos.write(buffer, 0, numberOfBytes);

            fos.flush();
            fos.close();
            fis.close();

            // --------- Accordingly with FileReader / Writer ---------

            FileReader fr = new FileReader(inFilePath);
            FileWriter fw = new FileWriter(outFilePath);
            char[] cBuffer = new char[BUFFER_SIZE];

            int numberOfChars;
            // The read method fills the buffer with data
            while ((numberOfChars = fr.read(cBuffer)) != -1)
                fw.write(cBuffer, 0, numberOfChars);

            fw.flush();
            fw.close();
            fr.close();

        }catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Example of read write using bufferedInput / output
     * Warning : Doesn't show proper exception handling (.close() must be in final)
     */
    public void bufferedRw() {
        try {
            BufferedInputStream bufRead = new BufferedInputStream(new FileInputStream(inFilePath));
            BufferedOutputStream bufWrite = new BufferedOutputStream(new FileOutputStream(outFilePath));
            int c;
            while ((c = bufRead.read()) != -1)
                bufWrite.write(c);

            bufWrite.flush();
            bufWrite.close();
            bufRead.close();

            // --------- Accordingly with FileReader / Writer ---------

            BufferedReader bufFr = new BufferedReader(new FileReader(inFilePath));
            BufferedWriter bufFw = new BufferedWriter(new FileWriter(outFilePath));
            int c2;
            while ((c2 = bufFr.read()) != -1)
                bufFw.write(c2);

            bufFw.flush();
            bufFw.close();
            bufFr.close();
        }catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Example of InputStreamReader / Writer with one of many possible combinations.
     * The InputStreamReader / Writer wrapper lets us specify the charset / encoding.
     * this final example also shows proper exception handling (.close() must be in final)
     */
    public void BufferedInputStreamReaderRW() {

        InputStreamReader isr = null;
        OutputStreamWriter osw = null;

        try {
            isr = new InputStreamReader(new BufferedInputStream(new FileInputStream(inFilePath)), StandardCharsets.UTF_8);
            // Or without the buffer
            // InputStreamReader isr = new InputStreamReader(new FileInputStream(inFilePath), StandardCharsets.UTF_8);
            osw = new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(outFilePath)), StandardCharsets.UTF_8);

            int c;
            while ((c = isr.read()) != -1)
                osw.write(c);
            osw.flush();

        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (isr != null) isr.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
            try {
                if (osw != null) osw.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}

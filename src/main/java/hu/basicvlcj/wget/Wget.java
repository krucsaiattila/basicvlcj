package hu.basicvlcj.wget;

import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.utils.IOUtils;

import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Paths;

/**
 * A class that manages the downloading from the web. It is used to download subtitle files.
 */
public class Wget {

    /**
     * A staic method that is used to download a file. It also unarchives the file.
     * @param urlOfFile the URL of the file that should be downloaded
     * @param actualFilePath the path of the actual file that is imported into the media player
     */
    public static void wGet(String urlOfFile, String actualFilePath) {
        try {
            URL url = new URL(urlOfFile);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            InputStream is = connection.getInputStream();
            ArchiveInputStream inputStream = new ArchiveStreamFactory().createArchiveInputStream("zip", is);
            ZipArchiveEntry entry = (ZipArchiveEntry) inputStream.getNextEntry();
            OutputStream outputStream = new FileOutputStream(new File(Paths.get(actualFilePath).toAbsolutePath().getParent().toString(), entry.getName()));
            IOUtils.copy(inputStream, outputStream);
            outputStream.close();
            inputStream.close();

            JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "Subtitle successfully downloaded to " + Paths.get(actualFilePath).toAbsolutePath().getParent());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "Failed to download subtitle file", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

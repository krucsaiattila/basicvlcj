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

public class Wget {

    public static void wGet(String urlOfFile, String directory) {
        try {
            URL url = new URL(urlOfFile);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            InputStream is = connection.getInputStream();
            ArchiveInputStream inputStream = new ArchiveStreamFactory().createArchiveInputStream("zip", is);
            ZipArchiveEntry entry = (ZipArchiveEntry) inputStream.getNextEntry();
            OutputStream outputStream = new FileOutputStream(new File(Paths.get(directory).toAbsolutePath().getParent().toString(), entry.getName()));
            IOUtils.copy(inputStream, outputStream);
            outputStream.close();
            inputStream.close();

            JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "Subtitle successfully downloaded to " + Paths.get(directory).toAbsolutePath().getParent());
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "Failed to download subtitle file", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

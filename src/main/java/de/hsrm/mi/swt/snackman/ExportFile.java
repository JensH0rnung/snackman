package de.hsrm.mi.swt.snackman;

import java.io.*;
import java.nio.file.*;
import java.net.URL;

public class ExportFile {

    public static void exportFile(String path, String filename) throws Exception {
        // Ressource aus der JAR laden
        URL resourceUrl = ExportFile.class.getClassLoader().getResource(path);
        if (resourceUrl == null) {
            System.err.println("Ressource nicht gefunden: " + path);
            return;
        }

        // Extrahiere die Datei zur Laufzeit
        try (InputStream inputStream = resourceUrl.openStream()) {
            // Temporäre Datei im Dateisystem erstellen
            File tempFile = File.createTempFile(filename.split(".")[0], filename.split(".")[1]);

            // Die Ressource in die temporäre Datei mit Files.copy kopieren
            Path targetPath = tempFile.toPath();
            Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);

            System.out.println("Datei wurde extrahiert nach: " + tempFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

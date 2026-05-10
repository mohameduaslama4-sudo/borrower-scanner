package be.esi.prj.service;

import com.google.zxing.*;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Service responsible for generating, storing, and exporting QR codes.
 * <p>
 * Uses ZXing library to generate QR codes and manages file operations
 * related to QR code storage and download.
 */
public class QRService {

    private static final String FOLDER = "external-data/qrcodes";

    /**
     * Generates a QR code image from the given content and stores it locally.
     *
     * @param content the data to encode into the QR code
     * @throws IllegalStateException if QR generation fails
     */
    public void generateQRCode(String content) {

        try {
            String filePath = FOLDER + File.separator + content + ".png";
            Path qrPath = new File(filePath).toPath();

            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix matrix = writer.encode(content, BarcodeFormat.QR_CODE, 300, 300);
            MatrixToImageWriter.writeToPath(matrix, "PNG", qrPath);

        } catch (WriterException | IOException e) {
            throw new IllegalStateException("Erreur génération QR code", e);
        }
    }

    /**
     * Copies a QR code file to a user-selected destination.
     *
     * @param sourcePath path of the existing QR code file
     * @param destination target destination path
     * @throws IllegalStateException if the file cannot be copied
     */
    public void downloadQRCode(String sourcePath, Path destination) {

        try {
            Files.copy(Path.of(sourcePath), destination);
        } catch (IOException e) {
            throw new IllegalStateException("Chemin vers QR code introuvable", e);
        }
    }

    // =====================================================
    // FOLDER MANAGEMENT
    // =====================================================

    /**
     * Opens a file chooser dialog to select a destination for downloading a QR code.
     *
     * @param defaultName default file name without extension
     * @param window the JavaFX window used as dialog owner
     * @return selected file path or null if cancelled
     */
    public Path chooseDownloadDestination(String defaultName, Window window) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Télécharger QR Code");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PNG Image", "*.png")
        );
        fileChooser.setInitialFileName(defaultName + ".png");

        File file = fileChooser.showSaveDialog(window);
        return file != null ? file.toPath() : null;
    }

}
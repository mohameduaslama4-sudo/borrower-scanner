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

public class QRService {

    private static final String FOLDER = "external-data/qrcodes";

    // =====================================================
    // GENERATE QR
    // =====================================================
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

    // =====================================================
    // DOWNLOAD QR
    // =====================================================
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
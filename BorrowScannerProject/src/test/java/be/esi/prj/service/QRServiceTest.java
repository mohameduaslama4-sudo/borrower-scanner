package be.esi.prj.service;

import be.esi.prj.testsupport.TestDatabase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class QRServiceTest {

    private QRService qrService;

    @BeforeEach
    void setUp() {
        TestDatabase.reset();
        qrService = new QRService();
    }

    // =====================================================
    // GENERATE QR
    // =====================================================
    @Test
    void generateQrCodeCreatesPngInQrFolder() throws IOException {
        String content = "ITEM-123";

        qrService.generateQRCode(content);

        Path generated = Path.of("external-data", "qrcodes", content + ".png");

        assertTrue(Files.exists(generated));
        assertTrue(Files.size(generated) > 0);
    }

    // =====================================================
    // DOWNLOAD QR
    // =====================================================
    @Test
    void downloadQrCodeCopiesFileCorrectly() throws Exception {

        Path source = Files.createTempFile("qr-source", ".png");
        Files.write(source, new byte[]{1, 2, 3, 4}); // fake binary

        Path destination = Files.createTempFile("qr-dest", ".png");
        Files.delete(destination);

        qrService.downloadQRCode(source.toString(), destination);

        assertTrue(Files.exists(destination));
        assertArrayEquals(
                Files.readAllBytes(source),
                Files.readAllBytes(destination)
        );
    }

    // =====================================================
    // ERROR HANDLING
    // =====================================================
    @Test
    void downloadQrCodeThrowsWhenSourceMissing() {

        Path destination = Path.of("external-data", "qrcodes", "missing.png");

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> qrService.downloadQRCode("does-not-exist.png", destination)
        );

        assertTrue(ex.getMessage().contains("introuvable")
                || ex.getMessage().contains("Chemin"));
    }
}
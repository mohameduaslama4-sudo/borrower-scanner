package be.esi.prj.service;

import be.esi.prj.testsupport.TestDatabase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

    @Test
    void generateQrCodeCreatesPngInQrFolder() {
        qrService.generateQRCode("ITEM-123");

        Path generated = Path.of("external-data", "qrcodes", "ITEM-123.png");
        assertTrue(Files.exists(generated));
        assertTrue(generated.toFile().length() > 0);
    }

    @Test
    void downloadQrCodeCopiesSourceToDestination() throws Exception {
        Path source = Files.createTempFile("source-qr", ".png");
        Files.writeString(source, "png-ish");
        Path destination = Files.createTempFile("dest-qr", ".png");
        Files.delete(destination);

        qrService.downloadQRCode(source.toString(), destination);

        assertEquals("png-ish", Files.readString(destination));
    }

    @Test
    void downloadQrCodeWrapsMissingSourceAsIllegalState() {
        Path destination = Path.of("external-data", "qrcodes", "missing-copy.png");

        IllegalStateException error = assertThrows(IllegalStateException.class,
                () -> qrService.downloadQRCode("does-not-exist.png", destination));

        assertTrue(error.getMessage().contains("Chemin vers QR code"));
    }
}

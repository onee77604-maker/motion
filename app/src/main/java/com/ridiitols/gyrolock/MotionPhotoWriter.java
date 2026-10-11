package com.ridiitols.gyrolock;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/** Writes the widely used Google Camera Motion Photo layout: JPEG + XMP APP1 + MP4 tail. */
final class MotionPhotoWriter {
    private MotionPhotoWriter() {}

    static void write(File jpeg, File mp4, File output) throws IOException {
        if (!jpeg.isFile() || jpeg.length() < 4) throw new IOException("Foto JPEG tidak valid");
        if (!mp4.isFile() || mp4.length() < 1024) throw new IOException("Video Motion Photo tidak valid");
        long videoLength = mp4.length();
        String xmp = "http://ns.adobe.com/xap/1.0/\0" +
                "<x:xmpmeta xmlns:x='adobe:ns:meta/'>" +
                "<rdf:RDF xmlns:rdf='http://www.w3.org/1999/02/22-rdf-syntax-ns#'>" +
                "<rdf:Description rdf:about='' xmlns:GCamera='http://ns.google.com/photos/1.0/camera/' " +
                "GCamera:MotionPhoto='1' GCamera:MotionPhotoVersion='1' " +
                "GCamera:MicroVideo='1' GCamera:MicroVideoVersion='1' GCamera:MicroVideoOffset='" + videoLength + "'/>" +
                "</rdf:RDF></x:xmpmeta>";
        byte[] xmpBytes = xmp.getBytes(StandardCharsets.UTF_8);
        int segmentLength = xmpBytes.length + 2;
        if (segmentLength > 65535) throw new IOException("Metadata XMP terlalu besar");
        try (FileInputStream in = new FileInputStream(jpeg); FileOutputStream out = new FileOutputStream(output)) {
            int a = in.read(), b = in.read();
            if (a != 0xFF || b != 0xD8) throw new IOException("Header JPEG tidak valid");
            out.write(a); out.write(b);
            out.write(0xFF); out.write(0xE1);
            out.write((segmentLength >> 8) & 0xFF); out.write(segmentLength & 0xFF);
            out.write(xmpBytes);
            byte[] buffer = new byte[64 * 1024]; int n;
            while ((n = in.read(buffer)) != -1) out.write(buffer, 0, n);
            try (FileInputStream videoIn = new FileInputStream(mp4)) {
                while ((n = videoIn.read(buffer)) != -1) out.write(buffer, 0, n);
            }
            out.flush();
        }
    }
}

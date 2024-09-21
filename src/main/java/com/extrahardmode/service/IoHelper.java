package com.extrahardmode.service;

import java.io.*;
import java.nio.channels.FileChannel;

import com.google.common.io.Files;
import java.nio.charset.StandardCharsets;

/**
 * Helper Functions for general IoStuff
 *
 * @author Diemex
 */
public class IoHelper {
    /**
     * Copy contents of one File to another
     *
     * @param sourceFile to copy from
     * @param destFile   to copy to
     * @param append     append the content of the the sourcefile to the destination
     *                   file
     *
     * @throws IOException if bad stuff happens
     */
    public static void copyFile(File sourceFile, File destFile, boolean append) throws IOException {
        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        try (FileChannel source = new FileInputStream(sourceFile).getChannel();
             FileChannel destination = new FileOutputStream(destFile, append).getChannel()) {
            destination.transferFrom(source, 0L, source.size());
        }
    }

    /**
     * Platform independent rename function
     *
     * @param currentFile file to rename
     * @param newName     rename to this (same directory)
     */
    public static void renameTo(File currentFile, String newName) {
        try {
            File newFile = new File(newName);
            Files.move(currentFile, newFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeHeader(File input, ByteArrayOutputStream headerStream) {
        try {
            // Read original file contents -> memstream
            BufferedReader br = new BufferedReader(new FileReader(input));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null)
                sb.append(line + String.format("%n"));
            br.close();

            // Header -> original file
            FileOutputStream outFileStream = new FileOutputStream(input);

            // header + original file
            OutputStreamWriter memWriter = new OutputStreamWriter(headerStream, StandardCharsets.UTF_8.newEncoder());
            memWriter.write(sb.toString());
            memWriter.close();
            headerStream.writeTo(outFileStream);

            headerStream.close();
            outFileStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

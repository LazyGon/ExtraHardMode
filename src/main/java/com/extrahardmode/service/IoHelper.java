package com.extrahardmode.service;

import com.google.common.io.Files;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.channels.FileChannel;
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
     * @throws IOException if bad stuff happens
     */
    public static void copyFile(File sourceFile, File destFile, boolean append) throws IOException {
        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        try (FileInputStream fileIn = new FileInputStream(sourceFile);
             FileOutputStream fileOut = new FileOutputStream(destFile, append);
             FileChannel source = fileIn.getChannel();
             FileChannel destination = fileOut.getChannel()) {
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
            Files.move(currentFile, new File(newName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeHeader(File input, ByteArrayOutputStream headerStream) {
        try (BufferedReader br = new BufferedReader(new FileReader(input));
             FileOutputStream outFileStream = new FileOutputStream(input);
             OutputStreamWriter memWriter = new OutputStreamWriter(headerStream,
                     StandardCharsets.UTF_8.newEncoder())) {

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line + String.format("%n"));
            }

            memWriter.write(sb.toString());
            headerStream.writeTo(outFileStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

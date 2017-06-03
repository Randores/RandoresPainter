/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2015 socraticphoenix@gmail.com
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * @author Socratic_Phoenix (socraticphoenix@gmail.com)
 */
package com.gmail.socraticphoenix.forge.randores.painter.util;

import com.gmail.socraticphoenix.collect.Items;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class FileUtil {

    public static List<String> readLines(String file) throws IOException {
        return FileUtil.readLines(new File(file));
    }

    public static InputStream getResource(String className) {
        return FileUtil.class.getClassLoader().getResourceAsStream(className);
    }


    public static List<String> readLines(File file) throws IOException {
        List<String> lines = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        while ((line = reader.readLine()) != null) {
            lines.add(line);
        }

        return lines;
    }

    public static void copy(String file, String target) throws IOException {
        copy(file, target, f -> false);
    }

    public static void copy(File file, File target) throws IOException {
        copy(file, target, f -> false, e -> false);
    }

    public static void copy(String file, String target, Predicate<File> shouldExclude) throws IOException {
        copy(new File(file), new File(target), shouldExclude, e -> false);
    }

    public static void copy(File file, File target, Predicate<File> shouldExclude, Predicate<IOException> ignore) throws IOException {
        try {
            if (!shouldExclude.test(file)) {
                if (file.isDirectory()) {
                    target.mkdir();
                    for (File f : file.listFiles()) {
                        copy(f, new File(target, f.getName()), shouldExclude, ignore);
                    }
                } else {
                    Files.copy(file.toPath(), target.toPath());
                }
            }
        } catch (IOException e) {
            if(!ignore.test(e)) {
                throw e;
            }
        }
    }

    public static void deleteDirectory(File dir) throws IOException {
        if (dir.isDirectory()) {
            for (File f : dir.listFiles()) {
                deleteDirectory(f);
            }
        }
        Files.delete(dir.toPath());
    }

    public static List<File> getAllFilesInDirectory(String dir) {
        return getAllFilesInDirectory(new File(dir));
    }

    public static List<File> getAllFilesInDirectory(File dir) {
        List<File> files = new ArrayList<File>();

        files.add(dir);

        if (dir.isDirectory()) {
            loopThroughDir(dir, files);
        }

        return Items.reversed(files);
    }


    private static void loopThroughDir(File dir, List<File> holder) {
        if (dir.isDirectory()) {
            for (File f : dir.listFiles()) {
                holder.add(f);
                if (f.isDirectory()) {
                    loopThroughDir(f, holder);
                }
            }
        }
    }

    public static void downloadFile(String url, String filename)
            throws IOException {
        BufferedInputStream in = null;
        FileOutputStream fout = null;
        try {
            in = new BufferedInputStream(new URL(url).openStream());
            fout = new FileOutputStream(filename);

            final byte data[] = new byte[1024];
            int count;
            while ((count = in.read(data, 0, 1024)) != -1) {
                fout.write(data, 0, count);
            }
        } finally {
            if (in != null) {
                in.close();
            }
            if (fout != null) {
                fout.close();
            }
        }
    }

    public static void zipFile(File source, File targetZip) throws IOException {
        targetZip.mkdirs();
        targetZip.delete();
        targetZip.createNewFile();
        FileOutputStream fos = new FileOutputStream(targetZip);
        ZipOutputStream zos = new ZipOutputStream(fos);
        FileUtil.recurse(zos, source, source);
        zos.close();
    }

    private static void recurse(ZipOutputStream stream, File file, File source) throws IOException {
        if(file.isDirectory()) {
            for(File sub : file.listFiles()) {
                FileUtil.recurse(stream, sub, source);
            }
        } else {
            byte[] buffer = new byte[1024];
            ZipEntry entry = new ZipEntry(FileUtil.name(file, source));
            stream.putNextEntry(entry);
            FileInputStream in = new FileInputStream(file);
            int len;
            while ((len = in.read(buffer)) > 0) {
                stream.write(buffer, 0, len);
            }
            in.close();
            stream.closeEntry();
        }
    }

    private static String name(File file, File source) {
        return file.getAbsolutePath().substring(source.getAbsolutePath().length() + 1, file.getAbsolutePath().length()).replace(File.separator, "/").replace("\\", "/");
    }

    public static void unZipFile(File zip, File dir)
            throws IOException {
        if (!dir.exists() || !dir.isDirectory()) {
            dir.mkdirs();
        }
        // Open the zip file
        ZipFile zipFile = new ZipFile(zip);
        Enumeration<?> enu = zipFile.entries();
        while (enu.hasMoreElements()) {
            ZipEntry zipEntry = (ZipEntry) enu.nextElement();
            String name = zipEntry.getName();
            // Do we need to create a directory ?
            File file = new File(dir, name);
            if (name.endsWith("/")) {
                file.mkdirs();
                continue;
            }

            File parent = file.getParentFile();
            if (parent != null) {
                parent.mkdirs();
            }

            // Extract the file
            InputStream is = zipFile.getInputStream(zipEntry);
            FileOutputStream fos = new FileOutputStream(file);
            byte[] bytes = new byte[1024];
            int length;
            while ((length = is.read(bytes)) >= 0) {
                fos.write(bytes, 0, length);

            }
            is.close();
            fos.close();

        }
        zipFile.close();
    }
}

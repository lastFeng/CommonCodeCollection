/*
 * Copyright 2001-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.learn.platformutil.io;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.learn.platformutil.base.PlatformsUtil;
import com.sun.istack.internal.NotNull;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import javax.annotation.processing.FilerException;
import java.io.*;
import java.util.Iterator;
import java.util.List;

/**
 * <p> Title: </p>
 *
 * <p> Description: </p>
 *
 * @author: Guo Weifeng
 * @version: 1.0
 * @create: 2019/10/23 11:13
 * 关于文件工具类：
 * 1.文件读写
 * 2.文件及目录操作
 */
public class FileUtil {
    /**
     * 文件读写
     * */
    public static byte[] toByteArray(final File file) throws IOException {
        return Files.toByteArray(file);
    }

    public static String toString(final File file) throws IOException {
        return Files.toString(file, Charsets.UTF_8);
    }

    public static List<String> toLines(final File file) throws IOException {
        return Files.readLines(file, Charsets.UTF_8);
    }

    public static void write(final CharSequence data, final File file) throws IOException {
        Files.write(data, file, Charsets.UTF_8);
    }

    public static void append(final CharSequence from, final File to) throws IOException {
        Files.append(from, to, Charsets.UTF_8);
    }

    /**
     * @since 1.6+
     * */
    public static InputStream asInputStream(String fileName) throws IOException {
        File filePath = getFileByPath(fileName);
        if (filePath == null) {
            throw new FilerException(fileName);
        }
        return new FileInputStream(filePath);
    }

    public static InputStream asInputStream(File file) throws IOException {
        return new FileInputStream(file);
    }

    /**
     * @since 1.6+
     * */
    public static OutputStream asOutputStream(String fileName) throws IOException {
        File filePath = getFileByPath(fileName);
        if (filePath == null) {
            throw new FilerException(fileName);
        }
        return new FileOutputStream(filePath);
    }

    public static OutputStream asOutputStream(File file) throws IOException {
        return new FileOutputStream(file);
    }

    public static BufferedReader asBufferedReader(String fileName) throws IOException {
        if (StringUtils.isNotBlank(fileName)) {
            return Files.newReader(getFileByPath(fileName), Charsets.UTF_8);
        } else {
            return null;
        }
    }

    public static BufferedWriter asBufferedWriter(String fileName) throws IOException {
        if (StringUtils.isNotBlank(fileName)) {
            return Files.newWriter(getFileByPath(fileName), Charsets.UTF_8);
        } else {
            return null;
        }
    }

    /**
     * 文件操作
     * */
    public static void copy(@NotNull File from, @NotNull File to) throws IOException {
        Validate.notNull(from);
        Validate.notNull(to);

        if (from.isDirectory()) {
            // 拷贝文件夹
            copyDir(from, to);
        } else {
            // 拷贝文件
            copyFile(from, to);
        }
    }

    public static void copyFile(@NotNull File from, @NotNull File to) throws IOException {
        Validate.isTrue(isFileExists(from), from + " is not exist or not a file");
        Validate.notNull(to);

        Validate.isTrue(!isDirExists(to), to + " is exist but it is a dir");
        Files.copy(from, to);
    }

    public static void copyDir(@NotNull File from, @NotNull File to) throws IOException {
        Validate.isTrue(isDirExists(from), from + " is not exist or not a dir");
        Validate.notNull(to);

        if (to.exists()) {
            Validate.isTrue(!to.isFile(), to + " is exist but it is a file");
        } else {
            to.mkdirs();
        }

        File[] files = from.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                String name = files[i].getName();
                if (".".equals(name) || "..".equals(name)) {
                    continue;
                }
                copy(files[i], new File(to, name));
            }
        }
    }

    public static void moveFile(@NotNull File from, @NotNull File to) throws IOException {
        Validate.isTrue(isFileExists(from), from + " is not exist or not a file");
        Validate.notNull(to);
        Validate.isTrue(!isFileExists(to), to + " is exist but it is a file");

        final boolean rename = from.renameTo(to);

        if (!rename) {
            if (to.getCanonicalPath().startsWith(from.getCanonicalPath() + File.separator)) {
                throw new IOException("Cannot move directory: " + from + " to a subdirectory of itself: " + to);
            }
            copyDir(from, to);
            deleteDir(from);
            if (from.exists()) {
                throw new IOException("Failed to delete original directory '" + from + "' after copy to '" + to + "'");
            }
        }
    }


    public static void touch(String filePath) throws IOException {
        if (StringUtils.isNotBlank(filePath)) {
            Files.touch(getFileByPath(filePath));
        }
    }

    public static void touch(File file) throws IOException {
        Files.touch(file);
    }

    public static void deleteFile(@NotNull File file) throws IOException {
        Validate.isTrue(isFileExists(file), file + " is not exist or not a file");
        file.delete();
    }

    public static void deleteDir(File dir) {
        Validate.isTrue(isDirExists(dir), dir + " is not exist or not a dir");

        // 后序遍历，先删掉子目录中的文件/目录
        Iterator<File> iterator = Files.fileTreeTraverser().postOrderTraversal(dir).iterator();
        while (iterator.hasNext()) {
            iterator.next().delete();
        }
    }

    public static boolean isDirExists(String dirPath) {
        return isDirExists(getFileByPath(dirPath));
    }

    public static boolean isDirExists(File dir) {
        if (dir == null) {
            return false;
        }
        return dir.exists() && dir.isDirectory();
    }

    public static void makesureDirExists(File file) throws IOException {
        Validate.notNull(file);

        if (file.exists()) {
            if (!file.isDirectory()) {
                throw new IOException("There is a file exists " + file);
            }
        } else {
            file.mkdirs();
        }
    }

    public static void makesureParentDirExists(File file) throws IOException {
        Files.createParentDirs(file);
    }

    public static boolean isFileExists(String fileName) {
        return isFileExists(getFileByPath(fileName));
    }

    public static boolean isFileExists(File file) {
        if (file == null) {
            return false;
        }
        return file.exists() && file.isFile();
    }

    public static File createTempDir() {
        return Files.createTempDir();
    }

    public static File createTempFile() throws IOException {
        return File.createTempFile("tmp-", ".tmp");
    }

    public static File createTempFile(String prefix, String suffix) throws IOException {
        return File.createTempFile(prefix, suffix);
    }

    private static File getFileByPath(String filePath) {
        return StringUtils.isBlank(filePath) ? null : new File(filePath);
    }

    public static String getFileName(@NotNull String fullName) {
        Validate.notEmpty(fullName);
        int last = fullName.lastIndexOf(PlatformsUtil.FILE_PATH_SEPARATOR_CHAR);
        return fullName.substring(last + 1);
    }

    public static String getFileExtension(File file) {
        return Files.getFileExtension(file.getName());
    }

    public static String getFileExtension(String fullName) {
        return Files.getFileExtension(fullName);
    }
}
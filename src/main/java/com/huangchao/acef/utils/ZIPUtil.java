package com.huangchao.acef.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZIPUtil {

    public static void main(String[] args) {
        System.out.println(compress("E:\\file\\img\\aaip\\0ea68a01-09b8-4c8a-afea-3d209f172120.jpg"));
    }

    /**
     * 压缩文件
     *
     * @param filePath 压缩路径
     */

    public static String compress(String filePath) {
        //生成新路径
        String newFilePath = filePath.substring(0, filePath.lastIndexOf(".")) + ".zip";

        try {

            ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(newFilePath));
            File file = new File(filePath);
            ZipEntry zipEntry = new ZipEntry(file.getName());
            zipOutputStream.putNextEntry(zipEntry);
            FileInputStream fileInputStream = new FileInputStream(filePath);

            byte[] b = new byte[1024 * 1024 * 5];
            int length = 0;
            while ((length = fileInputStream.read(b)) != -1) {
                zipOutputStream.write(b, 0, length);
            }

            fileInputStream.close();
            zipOutputStream.closeEntry();

            return newFilePath;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
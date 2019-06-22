package com.huangchao.acef.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZIPUtil {


    /**
     * 压缩文件
     *
     * @param filePath 压缩路径
     */

    public static void compress(String filePath) {
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

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
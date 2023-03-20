package com.mail.tools;

import com.creek.common.CreekPath;
import com.creek.router.annotation.CreekMethod;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class FileTool {

    /**
     * 读取指定文件的中内容
     */
    public static String readFile(File file) {
        if (!file.exists()) {
            return null;
        }
        InputStream in = null;
        try {
            in = new FileInputStream(file);
            InputStreamReader isr= new InputStreamReader(in, "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            StringBuffer sb = new StringBuffer();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line.trim()).append("\n");
            }
            return sb.toString();
        } catch (Exception e) {
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }


    @CreekMethod(path = CreekPath.Tools_Save_File_To_SdCard)
    public static String saveFile(byte[] data, File file) {
        if (file.exists()) {
            return file.getAbsolutePath();
        }

        new File(file.getParent()).mkdirs();
        FileOutputStream os = null;
        BufferedOutputStream bos = null;
        try {
            file.createNewFile();
            os = new FileOutputStream(file);
            bos = new BufferedOutputStream(os);
            bos.write(data);
            bos.flush();
        } catch (Exception e) {
            return "";
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    return "";
                }
            }
            if (bos != null) {
                try {
                    bos.close();
                } catch (Exception e2) {
                    return "";
                }
            }
        }
        return file.getAbsolutePath();
    }


    /**
     * 获得指定文件的byte数组
     */
    public static byte[] getBytes(String filePath) {
        byte[] buffer = null;
        try {
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (Exception e) {

        }
        return buffer;
    }

    /**
     * 将字符串写入到文件
     *
     * @param text 要写入的字符串
     */
    public static void writeFile(String text, File file) {
        try {
            new File(file.getParent()).mkdirs();
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream os = new FileOutputStream(file);
            os.write(text.getBytes());
            os.close();
        } catch (Exception e) {

        }
    }


    public static int getFileLogo(String filename) {
        String name = filename.toUpperCase().trim();

        if (name.endsWith(".DOC") || name.endsWith(".DOT") || name.endsWith(".DOTX") || name.endsWith(".DOCM")
                || name.endsWith(".DOTM") || name.endsWith(".DOCX") || name.endsWith(".WPT"))
            return R.mipmap.icon_doc;
        if (name.endsWith(".TXT"))
            return R.mipmap.icon_txt;
        if (name.endsWith(".PDF"))
            return R.mipmap.icon_pdf;
        if (name.endsWith(".JPG") || name.endsWith(".JPEG") || name.endsWith(".PNG") || name.endsWith(".GIF"))
            return R.mipmap.icon_png;
        if (name.endsWith(".PPT") || name.endsWith(".POT") || name.endsWith(".PPS") || name.endsWith(".PPTM")
                || name.endsWith(".POTX") || name.endsWith(".POTM") || name.endsWith(".PPSX")
                || name.endsWith(".PPSM") || name.endsWith(".PPTX"))
            return R.mipmap.icon_ppt;
        if (name.endsWith(".XLS") || name.endsWith(".XLSX") || name.endsWith(".XLT") || name.endsWith(".XLSM"))
            return R.mipmap.icon_xls;
        if (name.endsWith(".ZIP") || name.endsWith(".RAR") || name.endsWith(".7Z"))
            return R.mipmap.icon_zip;
        else {
            return R.mipmap.icon_other;
        }
    }
}

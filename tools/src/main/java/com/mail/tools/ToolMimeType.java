package com.mail.tools;

import com.creek.common.constant.ConstFileType;

import org.apache.tika.metadata.HttpHeaders;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaMetadataKeys;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.xml.sax.helpers.DefaultHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;

public class ToolMimeType {

    public static ConstFileType getFileType(String path) {
        File file = new File(path);
        String mime = getMimeType(file).toLowerCase(Locale.ROOT);
        if (mime.contains("android.package")) {
            return ConstFileType.Apk;
        }
        if (mime.contains("officedocument")) {
            return ConstFileType.Office;
        }

        if (mime.startsWith("image")) {
            return ConstFileType.Image;
        }
        if (mime.startsWith("text") || mime.contains("html") || mime.contains("xml")) {
            return ConstFileType.Text;
        }

        if (mime.startsWith("video")) {
            return ConstFileType.Video;
        }

        if (mime.contains("rar") || mime.contains("zip")) {
            return ConstFileType.Zip;
        }

        return ConstFileType.Other;
    }


    /**
     * 获取文件类型
     *
     * @param file
     * @return
     */
    private static String getMimeType(File file) {
        if (!file.exists() || file.isDirectory()) {
            return "";
        }

        AutoDetectParser parser = new AutoDetectParser();
        parser.setParsers(new HashMap<MediaType, Parser>());

        Metadata metadata = new Metadata();
        metadata.add(TikaMetadataKeys.RESOURCE_NAME_KEY, file.getName());

        try {
            InputStream stream = new FileInputStream(file);
            parser.parse(stream, new DefaultHandler(), metadata, new ParseContext());
            stream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return metadata.get(HttpHeaders.CONTENT_TYPE);
    }

}

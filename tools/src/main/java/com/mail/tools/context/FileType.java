package com.mail.tools.context;

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

public class FileType {

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

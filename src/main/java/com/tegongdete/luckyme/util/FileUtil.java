package com.tegongdete.luckyme.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.nio.file.Files;

public class FileUtil {
    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);
    public static String loadFile(String name) {
        try {
            File file = ResourceUtils.getFile(name);
            String content = new String(Files.readAllBytes(file.toPath()));
            return content;
        }
        catch (Exception e) {
            logger.error("Read File failed: " + e.getMessage());
            return "";
        }
    }
}

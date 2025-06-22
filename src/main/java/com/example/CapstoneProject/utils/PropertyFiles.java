package com.example.CapstoneProject.utils;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Properties;
public class PropertyFiles {
    public static HashMap<String,String> readPropertyFile(String propertyFileName) {
        HashMap<String,String> hashMap=new HashMap<>();
        Properties prop = new Properties();
        try {
            InputStream in = new BufferedInputStream(Files.newInputStream(Paths.get(propertyFileName)));
            prop.load(in);
            for (String key : prop.stringPropertyNames()) {
                hashMap.put(key, prop.getProperty(key));
            }
            in.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return hashMap;
    }

    public static String getRunValue(String key){
        HashMap<String, String> hashMap = PropertyFiles.readPropertyFile("./src/main/resources/RunSettings.properties");
        String value;
        value=hashMap.get(key);
        return value;
    }
}

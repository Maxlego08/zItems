package fr.maxlego08.items.api.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public record ItemFile(List<ItemFile> itemFiles, List<String> files, String name) {

    public static ItemFile fromFolder(File folder) {
        if (!folder.isDirectory()) {
            throw new IllegalArgumentException("File is not a folder : " + folder.getAbsolutePath());
        }

        List<ItemFile> subFolders = new ArrayList<>();
        List<String> files = new ArrayList<>();

        File[] entries = folder.listFiles();
        if (entries != null) {
            for (File entry : entries) {
                if (entry.isDirectory()) {
                    subFolders.add(fromFolder(entry));
                } else if (entry.getName().endsWith(".yml")) {
                    files.add(entry.getName().replace(".yml", ""));
                }
            }
        }

        return new ItemFile(subFolders, files, folder.getName());
    }

}

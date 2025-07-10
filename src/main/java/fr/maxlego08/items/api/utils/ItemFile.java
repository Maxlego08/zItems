package fr.maxlego08.items.api.utils;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public record ItemFile(List<ItemFile> itemFiles, List<String> files, String name, Material displayMaterial,
                       int displayModelId) {

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

        Material displayMaterial = Material.PAPER;
        int displayModelId = 0;

        File file = new File(folder, ".folder-info.yml");
        if (file.exists()) {
            YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
            displayMaterial = Material.valueOf(configuration.getString("material", Material.PAPER.name()).toUpperCase());
            displayModelId = configuration.getInt("model-id", 0);
        }

        return new ItemFile(subFolders, files, folder.getName(), displayMaterial, displayModelId);
    }

}

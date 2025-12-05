package fr.traqueur.items.api.registries;

import fr.traqueur.items.api.ItemsPlugin;
import fr.traqueur.items.api.Logger;
import fr.traqueur.items.api.models.Folder;
import org.bukkit.Material;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;

/**
 * A registry that loads its items from files in a specified folder structure.
 *
 * @param <ID> the type of the identifier for the items
 * @param <T>  the type of the items being registered
 */
public abstract class FileBasedRegistry<ID, T> implements Registry<ID, T> {

    /** The plugin instance */
    protected final ItemsPlugin plugin;
    /** The storage for registered items */
    protected final Map<ID, T> storage;
    /** The resource folder containing example files */
    private final String resourceFolder;
    /** The name used in logging messages */
    private final String logName;

    /** The root folder of the loaded folder structure */
    private Folder<T> rootFolder;

    /**
     * Constructs a FileBasedRegistry.
     *
     * @param plugin         the ItemsPlugin instance
     * @param resourceFolder the folder in resources containing example files
     * @param logName        the name used in logging messages
     */
    protected FileBasedRegistry(ItemsPlugin plugin, String resourceFolder, String logName) {
        this.plugin = plugin;
        this.storage = new HashMap<>();
        this.resourceFolder = resourceFolder;
        this.logName = logName;
    }

    /**
     * Loads items from the specified folder, building the folder structure.
     */
    public void loadFromFolder() {
        Path folder = this.plugin.getDataFolder().toPath().resolve(this.resourceFolder);
        if (!ensureFolderExists(folder)) {
            return;
        }

        this.storage.clear();
        this.rootFolder = buildFolderStructure(folder, folder);

        Logger.info("Loaded {} {}(s) in folder structure", this.storage.size(), logName);
    }

    /**
     * Recursively builds the folder structure from the given path.
     *
     * @param currentPath the current directory path being processed
     * @param rootPath    the root directory path for reference
     * @return the constructed Folder object representing the directory
     */
    private Folder<T> buildFolderStructure(Path currentPath, Path rootPath) {
        List<Folder<T>> subFolders = new ArrayList<>();
        List<T> elements = new ArrayList<>();

        String folderName = currentPath.equals(rootPath) ? "root" : currentPath.getFileName().toString();
        String displayName = folderName;
        Material material = Material.CHEST;
        int modelId = -1;

        try (Stream<Path> paths = Files.list(currentPath)) {
            paths.forEach(path -> {
                if (Files.isDirectory(path)) {
                    Folder<T> sub = buildFolderStructure(path, rootPath);
                    if (!sub.isEmpty()) {
                        subFolders.add(sub);
                    }
                } else if (isYamlFile(path)) {
                    T element = loadFile(path);
                    if (element != null) {
                        elements.add(element);
                    }
                }
            });
        } catch (IOException e) {
            Logger.severe("Failed to read directory: " + currentPath, e);
        }

        // Lecture des propriétés facultatives
        Path folderPropertiesPath = currentPath.resolve("folder.properties");
        if (Files.exists(folderPropertiesPath)) {
            Properties properties = new Properties();
            try (InputStream in = Files.newInputStream(folderPropertiesPath)) {
                properties.load(in);
            } catch (IOException e) {
                Logger.severe("Failed to load folder properties from: " + folderPropertiesPath, e);
            }

            String matStr = properties.getProperty("material");
            String modelStr = properties.getProperty("model-id", "-1");
            displayName = properties.getProperty("display-name", folderName);

            if (matStr != null) {
                Material m = Material.matchMaterial(matStr);
                if (m != null) material = m;
            }

            try {
                modelId = Integer.parseInt(modelStr);
            } catch (NumberFormatException e) {
                Logger.warning("Invalid model-id '{}' in folder properties at {}. Using default -1.",
                        modelStr, folderPropertiesPath);
            }
        }

        return new Folder<>(folderName, displayName, material, modelId, subFolders, elements);
    }

    /**
     * Ensures that the specified folder exists, creating it if necessary.
     *
     * @param folder the path to the folder
     * @return true if the folder exists or was created successfully, false otherwise
     */
    protected boolean ensureFolderExists(Path folder) {
        if (!Files.exists(folder)) {
            try {
                Files.createDirectories(folder);
                Logger.info("Created " + logName + " folder: " + folder);
                copyExampleFiles();
                return true;
            } catch (IOException e) {
                Logger.severe("Failed to create " + logName + " folder: " + folder, e);
                return false;
            }
        }
        return true;
    }

    /** Checks if the given path points to a YAML file.
     *
     * @param path the path to check
     * @return true if the file is a YAML file, false otherwise
     */
    private boolean isYamlFile(Path path) {
        String name = path.toString().toLowerCase();
        return name.endsWith(".yml") || name.endsWith(".yaml");
    }

    /** Copies example files from the resource folder to the plugin's data folder. */
    private void copyExampleFiles() {
        Logger.info("Copying example " + logName + " files...");
        List<String> copiedFiles = new ArrayList<>();

        try {
            List<String> resourceFiles = listResourceFiles(resourceFolder);

            for (String relativePath : resourceFiles) {
                if (isFile(relativePath)) {
                    try {
                        plugin.saveResource(resourceFolder + "/" + relativePath, false);
                        copiedFiles.add(relativePath);
                        Logger.debug("Copied example file: " + relativePath);
                    } catch (Exception e) {
                        Logger.warning("Failed to copy example file: " + relativePath + " - " + e.getMessage());
                    }
                }
            }

            Logger.info("Copied " + copiedFiles.size() + " example " + logName + " file(s)");
        } catch (Exception e) {
            Logger.severe("Failed to copy example " + logName + " files", e);
        }
    }

    /** Lists all resource files in the specified folder within the plugin's JAR.
     *
     * @param folder the resource folder path
     * @return a list of file paths relative to the specified folder
     * @throws IOException if an I/O error occurs
     */
    private List<String> listResourceFiles(String folder) throws IOException {
        List<String> filePaths = new ArrayList<>();
        ClassLoader classLoader = plugin.getClass().getClassLoader();
        URI uri;
        try {
            uri = Objects.requireNonNull(classLoader.getResource(folder)).toURI();
        } catch (Exception e) {
            Logger.warning("Could not find resource folder: " + folder);
            return filePaths;
        }

        Path resourcePath;
        FileSystem fileSystem = null;
        boolean needsClose = false;

        try {
            if ("jar".equals(uri.getScheme())) {
                try {
                    fileSystem = FileSystems.getFileSystem(uri);
                } catch (FileSystemNotFoundException e) {
                    fileSystem = FileSystems.newFileSystem(uri, Collections.emptyMap());
                    needsClose = true;
                }
                resourcePath = fileSystem.getPath("/" + folder);
            } else {
                resourcePath = Paths.get(uri);
            }

            try (Stream<Path> paths = Files.walk(resourcePath)) {
                paths.filter(Files::isRegularFile)
                        .forEach(path -> {
                            Path relative = resourcePath.relativize(path);
                            filePaths.add(relative.toString().replace("\\", "/"));
                        });
            }
        } finally {
            if (needsClose && fileSystem != null) {
                try {
                    fileSystem.close();
                } catch (IOException e) {
                    Logger.debug("Error closing file system: " + e.getMessage());
                }
            }
        }

        return filePaths;
    }

    /** Checks if the given file name corresponds to a supported file type.
     *
     * @param fileName the file name to check
     * @return true if the file is supported, false otherwise
     */
    private boolean isFile(String fileName) {
        String lower = fileName.toLowerCase();
        return lower.endsWith(".yml") || lower.endsWith(".yaml") || lower.endsWith(".properties");
    }

    /** Loads an item from the specified file.
     *
     * @param file the path to the file
     * @return the loaded item, or null if loading failed
     */
    protected abstract T loadFile(Path file);

    @Override
    public void register(ID id, T value) {
        storage.put(id, value);
    }

    @Override
    public T getById(ID id) {
        return storage.get(id);
    }

    @Override
    public Collection<T> getAll() {
        return storage.values();
    }

    @Override
    public void clear() {
        storage.clear();
    }

    /**
     * Gets the root folder of the loaded folder structure.
     *
     * @return the root Folder object
     */
    public Folder<T> getRootFolder() {
        return rootFolder != null
                ? rootFolder
                : new Folder<>("root", "root", Material.CHEST, -1, List.of(), List.of());
    }
}

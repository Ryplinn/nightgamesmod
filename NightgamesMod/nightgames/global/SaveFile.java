package nightgames.global;

import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;
import nightgames.gui.GUI;
import nightgames.json.JsonUtils;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

/**
 * Read and write SaveData to and from save files.
 */
public class SaveFile {
    private static File lastFile;
    static void autoSave() {
        if (Flag.checkFlag(Flag.autosave)) {
            save(new File("./auto.ngs"));
        }
    }

    public static void saveWithDialog() {
        Optional<File> file = GUI.gui.askForSaveFile(lastFile);
        file.ifPresent(SaveFile::save);
    }

    public static void save(File file) {
        save(file, new SaveData(GameState.getGameState()));
    }

    public static void save(File file, SaveData data) {
        // Backup if we're saving over a legacy save file
        if (Flag.checkFlag(Flag.LegacyCharAvailableSave) && file.exists() && file.getName().endsWith(".ngs")) {
            String stripExtension = file.getName().replace(".ngs", "");
            Path legacyPath = file.getParentFile().toPath().resolve(stripExtension + "_old.ngs");
            try {
                Files.copy(file.toPath(), legacyPath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                System.err.println("Could not back up legacy save " + file.getName() + " to " + legacyPath);
                e.printStackTrace();
            }
        }

        JsonObject saveJson = data.toJson();
        try (JsonWriter saver = new JsonWriter(new FileWriter(file))) {
            saver.setIndent("  ");
            JsonUtils.getGson().toJson(saveJson, saver);
            Flag.unflag(Flag.LegacyCharAvailableSave);
        } catch (IOException | JsonIOException e) {
            System.err.println("Could not save file " + file + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Optional<GameState> loadWithDialog() {
        File file = getSaveFileFromDialog();
        if (file == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(new GameState(load(file)));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SaveData.SaveDataException e) {
            String message = "Could not load save file " + file.getPath();
            System.err.println(message);
            JOptionPane.showMessageDialog(GUI.gui, message, "Save file not loaded",
                            JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public static File getSaveFileFromDialog() {
        JFileChooser dialog = new JFileChooser("./");
        FileFilter savesFilter = new FileNameExtensionFilter("Nightgame Saves", "ngs");
        dialog.addChoosableFileFilter(savesFilter);
        dialog.setFileFilter(savesFilter);
        dialog.setMultiSelectionEnabled(false);
        int rv = dialog.showOpenDialog(GUI.gui);
        if (rv != JFileChooser.APPROVE_OPTION) {
            return null;
        }
        File file = dialog.getSelectedFile();
        if (!file.isFile()) {
            file = new File(dialog.getSelectedFile().getAbsolutePath() + ".ngs");
            if (!file.isFile()) {
                // not a valid save, abort
                JOptionPane.showMessageDialog(GUI.gui, "Nightgames save file not found", "File not found",
                                JOptionPane.ERROR_MESSAGE);
                return null;
            }
        }
        return file;
    }

    public static SaveData load(File file) throws IOException, SaveData.SaveDataException {
        JsonObject object;
        try (Reader loader = new InputStreamReader(new FileInputStream(file))) {
            object = new JsonParser().parse(loader).getAsJsonObject();
        }
        System.out.println(String.format("Loaded game data from file %s", file.getName()));
        lastFile = file;
        return new SaveData(object);
    }

    static File getLastFile() {
        return lastFile;
    }

    public static class SaveFileException extends Throwable {
        private static final long serialVersionUID = 8659364881416302395L;

        public SaveFileException(String message) {
            super(message);
        }
    }
}

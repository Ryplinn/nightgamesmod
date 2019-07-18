package nightgames.global;

import nightgames.Resources.ResourceLoader;
import nightgames.characters.trait.TraitTree;
import nightgames.gui.GUI;
import nightgames.gui.HeadlessGui;
import nightgames.json.JsonUtils;
import nightgames.requirements.TraitRequirement;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Program entry point
 */
public class Main {
    private static volatile boolean exit = false;
    volatile static GameState gameState;

    public static void main(String[] args) {
        Map<Boolean, List<String>> splitArgs =
                        Stream.of(args).collect(Collectors.partitioningBy(arg -> arg.startsWith("DEBUG_")));
        List<String> debugArgs = splitArgs.get(true);
        List<String> otherArgs = splitArgs.get(false);
        Optional<Path> saveFile = parseArgs(otherArgs);
        new Logwriter();
        Logwriter.makeLogger(new Date());
        try {
            DebugFlags.parseDebugFlags(debugArgs);
        } catch (DebugFlags.UnknownDebugFlagException e) {
            // bad debug flags may be user error, not programmer error, so don't crash
            System.err.println(e.getMessage());
            displayHelp();
        }
        initialize();
        makeGUI();
        if (saveFile.isPresent()) {
            try {
                GameState loadedGame = new GameState(SaveFile.load(saveFile.get().toFile()));
                GUI.gui.load(loadedGame);
            } catch (SaveData.SaveDataException | IOException e) {
                e.printStackTrace();
            }
        } else {
            GUI.gui.showGameCreation();
        }
        while (!exit) {
            // TODO: Make sure this captures and logs useful information on errors
            try {
                run();
            } catch (RuntimeException e) {
                System.err.println("An error we didn't expect occurred:");
                e.printStackTrace();
            } catch (InterruptedException e) {
                System.err.println("Game closed by interrupt not caught by main loop:");
                e.printStackTrace();
            }
            if (!exit) {
                GUI.gui.showGameCreation();
            }
        }
    }

    private static void displayHelp() {
        String message = "Nightgamesmod is a text-based battlefuck game. User discretion is advised.\n"
                        + "Usage:\n"
                        + "\t$ nightgamesmod -help\n"
                        + "\t\tShow this help message.\n\n"
                        + "\t$ nightgamesmod [DEBUG_VAR1 [DEBUG_VAR2 ...]] [FILENAME]\n"
                        + "\t\tRun nightgamesmod with optional debug flags and save file.\n"
                        + "\t\tDebug flags are listed in the DebugFlags class. Providing a save file will\n"
                        + "\t\tlaunch directly into that game, skipping the game creation screen.\n";
        System.err.print(message);
    }

    private static Optional<Path> parseArgs(List<String> otherArgs) {
        if (otherArgs.size() > 0) {
            if (otherArgs.stream().anyMatch(arg -> arg.matches("-{1,2}help"))) {
                displayHelp();
            } else {
                return Optional.of(Paths.get(otherArgs.get(0)));
            }
        }
        return Optional.empty();
    }

    private static void run() throws InterruptedException {
        try {
            // Blocks until a game state is loaded into the GUI
            GameState.setGameState(GUI.gui.getGameState());
            GameState.getGameState().gameLoop();
        } finally {
            GameState.closeCurrentGame();
            GUI.gui.purgeGameState();
        }
    }

    private static void makeGUI() {
        GUI gui;
        if (DebugFlags.isDebugOn(DebugFlags.DEBUG_TURN_OFF_GUI)) {
            gui = new HeadlessGui();
        } else {
            gui = new GUI();
        }
        gui.addWindowListener(new CloseListener());
        GUI.gui = gui;
    }

    static void initialize() {
        TraitRequirement.setTraitRequirements(new TraitTree(ResourceLoader.getFileResourceAsStream("data/TraitRequirements.xml")));
        Map<String, Boolean> configurationFlags = JsonUtils.mapFromJson(JsonUtils.rootJson(
                        new InputStreamReader(ResourceLoader.getFileResourceAsStream("data/globalflags.json")))
                        .getAsJsonObject(), String.class, Boolean.class);
        configurationFlags.forEach(Flag::setFlag);
    }

    private static class CloseListener extends WindowAdapter {
        @Override public void windowClosing(WindowEvent e) {
            exit = true;
        }
    }
}


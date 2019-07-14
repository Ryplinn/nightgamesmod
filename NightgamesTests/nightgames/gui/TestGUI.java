package nightgames.gui;

import java.io.File;
import java.util.Optional;

public class TestGUI extends GUI {
    /**
     * 
     */
    private static final long serialVersionUID = 1739250786661411957L;

    public TestGUI() {
        super();
        GUI.gui = this;
    }

    @Override public void setVisible(boolean visible) {
        // pass
    }

    // Don't use save dialog in tests
    @Override public Optional<File> askForSaveFile(File lastFile) {
        return Optional.empty();
    }

    @Override public void refresh() {
        // pass
    }
}

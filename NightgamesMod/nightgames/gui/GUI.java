package nightgames.gui;

import nightgames.Resources.ResourceLoader;
import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Meter;
import nightgames.characters.Player;
import nightgames.combat.Combat;
import nightgames.daytime.Daytime;
import nightgames.debug.DebugGUIPanel;
import nightgames.global.Random;
import nightgames.global.*;
import nightgames.items.Item;
import nightgames.skills.Skill;
import nightgames.skills.TacticGroup;
import nightgames.utilities.DebugHelper;
import nightgames.utilities.MathUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class GUI extends JFrame implements Observer {
    private static final long serialVersionUID = 451431916952047183L;
    public static GUI gui;
    private static CompletableFuture currentPrompt = null;
    public Combat combat;
    private Map<TacticGroup, List<SkillButton>> skills;
    private CompletableFuture<Skill> chosenSkill;
    private TacticGroup currentTactics;
    public CommandPanel commandPanel;
    private JTextPane textPane;
    private JLabel stamina;
    private JLabel arousal;
    private JLabel mojo;
    private JLabel willpower;
    private JLabel lvl;
    private JLabel xp;
    private JProgressBar staminaBar;
    private JProgressBar arousalBar;
    private JProgressBar mojoBar;
    private JProgressBar willpowerBar;
    private JPanel topPanel;
    private JLabel loclbl;
    private JLabel timeLabel;
    private JLabel cashLabel;
    private Panel panel0;
    protected CreationGUI creation;
    private JScrollPane textScroll;
    private JPanel gamePanel;
    private JToggleButton stsbtn;
    private JPanel statusPanel;
    private JPanel mainPanel;
    private JPanel clothesPanel;
    private JPanel optionsPanel;
    private JPanel portraitPanel;
    private JPanel centerPanel;
    private JLabel portrait;
    private JComponent map;
    private JPanel imgPanel;
    private JLabel imgLabel;
    private JRadioButton rdnormal;
    private JRadioButton rddumb;
    private JRadioButton rdeasy;
    private JRadioButton rdhard;
    private JRadioButton rdMsgOn;
    private JRadioButton rdMsgOff;
    private JRadioButton rdAutoNextOn;
    private JRadioButton rdAutoNextOff;
    private JRadioButton rdautosaveon;
    private JRadioButton rdautosaveoff;
    private JRadioButton rdporon;
    private JRadioButton rdporoff;
    private JRadioButton rdimgon;
    private JRadioButton rdimgoff;
    private JButton rdfntnorm;
    private JButton rdnfntlrg;
    private JSlider malePrefSlider;
    private int width;
    private int height;
    public int fontsize;
    public JMenuItem mntmQuitMatch;
    public boolean skippedFeat;
    public NgsChooser saveFileChooser;
    private Box groupBox;
	private JFrame inventoryFrame;
    private JMenuItem mntmOptions;

    private Player populatedPlayer;

    private final static String USE_PORTRAIT = "PORTRAIT";
    private final static String USE_MAP = "MAP";
    private final static String USE_NONE = "NONE";
    private static final String USE_MAIN_TEXT_UI = "MAIN_TEXT";
    private static final String USE_CLOSET_UI = "CLOSET";
    volatile BlockingQueue<GameState> loadedState = new ArrayBlockingQueue<>(1);
    private volatile boolean refreshRequested = false;

    public GUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                        | UnsupportedLookAndFeelException e1) {
            System.err.println("Unable to set look-and-feel");
            e1.printStackTrace();
        }
        
        // frame title
        setTitle("NightGames Mod");
        setBackground(GUIColors.bgDark);
        // closing operation
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // resolution resolver

        height = (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() * 0.85);
        width = (int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth() * 0.85);

        setPreferredSize(new Dimension(width, height));

        // center the window on the monitor

        int y = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
        int x = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();

        int x1 = x / 2 - width / 2;
        int y1 = y / 2 - height / 2;

        this.setLocation(x1, y1);

        // menu bar

        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        // menu bar - new game

        JMenuItem mntmNewgame = new JMenuItem("New Game");

        //mntmNewgame.setForeground(Color.WHITE);
        //mntmNewgame.setBackground(GUIColors.bgGrey);
        mntmNewgame.setHorizontalAlignment(SwingConstants.CENTER);

        mntmNewgame.addActionListener(arg0 -> {
            if (GameState.inGame()) {
                int result = JOptionPane.showConfirmDialog(this,
                                "Do you want to start a new game? You'll lose any unsaved progress.", "Start new game?",
                                JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
                if (result == JOptionPane.OK_OPTION) {
                    GameState.closeCurrentGame();
                }
            }
        });

        menuBar.add(mntmNewgame);

        // menu bar - load game - can't change because can't figure out where
        // the frame is with swing

        JMenuItem mntmLoad = new JMenuItem("Load"); // Initializer

        //mntmLoad.setForeground(Color.WHITE); // Formatting
        //mntmLoad.setBackground(GUIColors.bgGrey);
        mntmLoad.setHorizontalAlignment(SwingConstants.CENTER);

        mntmLoad.addActionListener(arg0 -> {
            Optional<GameState> gameState = SaveFile.loadWithDialog();
            if (gameState.isPresent()) {
                try {
                    loadedState.clear();
                    loadedState.put(gameState.get());
                    GameState.closeCurrentGame();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        menuBar.add(mntmLoad);

        // menu bar - options

        mntmOptions = new JMenuItem("Options");
        //mntmOptions.setForeground(Color.WHITE);
        //mntmOptions.setBackground(GUIColors.bgGrey);

        menuBar.add(mntmOptions);

        // options submenu creator

        optionsPanel = new JPanel();
        optionsPanel.setLayout(new GridLayout(0, 3, 0, 0));

        // AILabel - options submenu - visible

        JLabel AILabel = new JLabel("AI Mode");
        ButtonGroup ai = new ButtonGroup();
        rdnormal = new JRadioButton("Normal");
        rddumb = new JRadioButton("Easier");
        ai.add(rdnormal);
        ai.add(rddumb);
        optionsPanel.add(AILabel);
        optionsPanel.add(rdnormal);
        optionsPanel.add(rddumb);

        // difficultyLabel - options submenu - visible

        JLabel difficultyLabel = new JLabel("NPC Bonuses (Mainly XP)");
        ButtonGroup diff = new ButtonGroup();
        rdeasy = new JRadioButton("Off");
        rdhard = new JRadioButton("On");
        diff.add(rdeasy);
        diff.add(rdhard);
        optionsPanel.add(difficultyLabel);
        optionsPanel.add(rdeasy);
        optionsPanel.add(rdhard);

        // systemMessageLabel - options submenu - visible

        JLabel systemMessageLabel = new JLabel("System Messages");
        ButtonGroup sysMsgG = new ButtonGroup();
        rdMsgOn = new JRadioButton("On");
        rdMsgOff = new JRadioButton("Off");
        sysMsgG.add(rdMsgOn);
        sysMsgG.add(rdMsgOff);
        optionsPanel.add(systemMessageLabel);
        optionsPanel.add(rdMsgOn);
        optionsPanel.add(rdMsgOff);

        JLabel autoNextLabel = new JLabel("Fast Combat Display");
        ButtonGroup autoNextG = new ButtonGroup();
        rdAutoNextOn = new JRadioButton("On");
        rdAutoNextOff = new JRadioButton("Off");
        autoNextG.add(rdAutoNextOn);
        autoNextG.add(rdAutoNextOff);
        optionsPanel.add(autoNextLabel);
        optionsPanel.add(rdAutoNextOn);
        optionsPanel.add(rdAutoNextOff);

        // autosave - options submenu - visible -(not currently working?)

        JLabel lblauto = new JLabel("Autosave (saves to auto.ngs)");
        ButtonGroup auto = new ButtonGroup();
        rdautosaveon = new JRadioButton("on");
        rdautosaveoff = new JRadioButton("off");
        auto.add(rdautosaveon);
        auto.add(rdautosaveoff);
        optionsPanel.add(lblauto);
        optionsPanel.add(rdautosaveon);
        optionsPanel.add(rdautosaveoff);

        // portraitsLabel - options submenu - visible

        JLabel portraitsLabel = new JLabel("Portraits");

        // portraits - options submenu - visible

        ButtonGroup portraitsButton = new ButtonGroup();

        // rdpron / rdporoff - options submenu - visible

        rdporon = new JRadioButton("on");
        rdporoff = new JRadioButton("off");
        portraitsButton.add(rdporon);
        portraitsButton.add(rdporoff);
        optionsPanel.add(portraitsLabel);
        optionsPanel.add(rdporon);
        optionsPanel.add(rdporoff);

        // imageLabel - options submenu - visible
        JLabel imageLabel = new JLabel("Images");
        ButtonGroup image = new ButtonGroup();
        rdimgon = new JRadioButton("on");
        rdimgoff = new JRadioButton("off");
        image.add(rdimgon);
        image.add(rdimgoff);
        optionsPanel.add(imageLabel);
        optionsPanel.add(rdimgon);
        optionsPanel.add(rdimgoff);

        // fontSizeLabel - options submenu - visible
        JLabel fontSizeLabel = new JLabel("Font Size");
        ButtonGroup size = new ButtonGroup();
        rdfntnorm = new JButton("Smaller");
        rdfntnorm.addActionListener(a -> {
            fontsize = MathUtils.clamp(fontsize - 1, 1, 7);
            gui.message("Text Size changed to " + fontsize);
        });
        rdnfntlrg = new JButton("Larger");
        rdnfntlrg.addActionListener(a -> {
            fontsize = MathUtils.clamp(fontsize + 1, 1, 7);
            gui.message("Text Size changed to " + fontsize);
        });
        size.add(rdfntnorm);
        size.add(rdnfntlrg);

        optionsPanel.add(fontSizeLabel);
        optionsPanel.add(rdfntnorm);
        optionsPanel.add(rdnfntlrg);
        
        JLabel pronounLabel = new JLabel("Human Pronoun Usage");
        ButtonGroup pronoun = new ButtonGroup();
        JRadioButton rdPronounBody = new JRadioButton("Based on Anatomy");
        JRadioButton rdPronounFemale = new JRadioButton("Always Female");
        pronoun.add(rdPronounBody);
        pronoun.add(rdPronounFemale);
        optionsPanel.add(pronounLabel);
        optionsPanel.add(rdPronounBody);
        optionsPanel.add(rdPronounFemale);

        JLabel npcPronounLabel = new JLabel("NPC Pronoun Usage");
        ButtonGroup npcPronoun = new ButtonGroup();
        JRadioButton rdNPCPronounBody = new JRadioButton("Based on Anatomy");
        JRadioButton rdNPCPronounFemale = new JRadioButton("Always Female");
        npcPronoun.add(rdNPCPronounBody);
        npcPronoun.add(rdNPCPronounFemale);
        optionsPanel.add(npcPronounLabel);
        optionsPanel.add(rdNPCPronounBody);
        optionsPanel.add(rdNPCPronounFemale);

        // m/f preference (no (other) males in the games yet... good for
        // modders?)

        // malePrefLabel - options submenu - visible
        JLabel malePrefLabel = new JLabel("Female vs. Male Preference");
        optionsPanel.add(malePrefLabel);
        malePrefSlider = new JSlider(SwingConstants.HORIZONTAL, 0, 10, 1);
        malePrefSlider.setMajorTickSpacing(5);
        malePrefSlider.setMinorTickSpacing(1);
        malePrefSlider.setPaintTicks(true);
        malePrefSlider.setPaintLabels(true);
        malePrefSlider.setLabelTable(new Hashtable<Integer, JLabel>() {
            /**
             * 
             */
            private static final long serialVersionUID = -4212836698571224221L;
            {
                put(0, new JLabel("Female"));
                put(5, new JLabel("Mixed"));
                put(10, new JLabel("Male"));
            }
        });
        malePrefSlider.setValue(Math.round(Flag.getValue(Flag.malePref)));
        malePrefSlider.setToolTipText("This setting affects the gender your opponents will gravitate towards once that"
                        + " option becomes available.");
        malePrefSlider.addChangeListener(e -> Flag.setCounter(Flag.malePref, malePrefSlider.getValue()));

        // malePrefPanel - options submenu - visible
        optionsPanel.add(malePrefSlider);
        mntmOptions.addActionListener(arg0 -> {
            if (Flag.checkFlag(Flag.systemMessages)) {
                rdMsgOn.setSelected(true);
            } else {
                rdMsgOff.setSelected(true);
            }

            if (Flag.checkFlag(Flag.AutoNext)) {
                rdAutoNextOn.setSelected(true);
            } else {
                rdAutoNextOff.setSelected(true);
            }

            if (Flag.checkFlag(Flag.hardmode)) {
                rdhard.setSelected(true);
            } else {
                rdeasy.setSelected(true);
            }

            if (Flag.checkFlag(Flag.dumbmode)) {
                rddumb.setSelected(true);
            } else {
                rdnormal.setSelected(true);
            }
            if (Flag.checkFlag(Flag.autosave)) {
                rdautosaveon.setSelected(true);
            } else {
                rdautosaveoff.setSelected(true);
            }
            if (Flag.checkFlag(Flag.noportraits)) {
                rdporoff.setSelected(true);
            } else {
                rdporon.setSelected(true);
            }
            if (Flag.checkFlag(Flag.noimage)) {
                rdimgoff.setSelected(true);
            } else {
                rdimgon.setSelected(true);
            }
            if (Flag.checkFlag(Flag.largefonts)) {
                rdnfntlrg.setSelected(true);
            } else {
                rdfntnorm.setSelected(true);
            }
            if (Flag.checkFlag(Flag.NPCFemalePronounsOnly)) {
                rdNPCPronounFemale.setSelected(true);
            } else {
                rdNPCPronounBody.setSelected(true);
            }
            if (Flag.checkFlag(Flag.PCFemalePronounsOnly)) {
                rdPronounFemale.setSelected(true);
            } else {
                rdPronounBody.setSelected(true);
            }
            malePrefSlider.setValue(Math.round(Flag.getValue(Flag.malePref)));
            int result = JOptionPane.showConfirmDialog(this, optionsPanel, "Options", JOptionPane.OK_CANCEL_OPTION,
                            JOptionPane.INFORMATION_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                Flag.setFlag(Flag.systemMessages, rdMsgOn.isSelected());
                Flag.setFlag(Flag.AutoNext, rdAutoNextOn.isSelected());
                Flag.setFlag(Flag.dumbmode, !rdnormal.isSelected());
                Flag.setFlag(Flag.hardmode, rdhard.isSelected());
                Flag.setFlag(Flag.autosave, rdautosaveon.isSelected());
                Flag.setFlag(Flag.noportraits, rdporoff.isSelected());
                Flag.setFlag(Flag.NPCFemalePronounsOnly, rdNPCPronounFemale.isSelected());
                Flag.setFlag(Flag.PCFemalePronounsOnly, rdPronounFemale.isSelected());
                if (!rdporon.isSelected()) {
                    showNone();
                }
                if (rdimgon.isSelected()) {
                    Flag.unflag(Flag.noimage);
                } else {
                    Flag.flag(Flag.noimage);
                    if (imgLabel != null) {
                        imgPanel.remove(imgLabel);
                    }
                    imgPanel.repaint();
                }
            }
        });

        // menu bar - credits

        JMenuItem mntmCredits = new JMenuItem("Credits");
        //mntmCredits.setForeground(Color.WHITE);
        //mntmCredits.setBackground(GUIColors.bgGrey);
        menuBar.add(mntmCredits);

        // menu bar - quit match

        mntmQuitMatch = new JMenuItem("Quit Match");
        mntmQuitMatch.setEnabled(false);
        //mntmQuitMatch.setForeground(Color.WHITE);
        //mntmQuitMatch.setBackground(GUIColors.bgGrey);
        mntmQuitMatch.addActionListener(arg0 -> {
            int result = JOptionPane.showConfirmDialog(this,
                            "Do you want to quit for the night? Your opponents will continue to fight and gain exp.",
                            "Retire early?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                Match.getMatch().quit();
            }
        });
        menuBar.add(mntmQuitMatch);
        mntmCredits.addActionListener(arg0 -> {
            JPanel panel = new JPanel();
            panel.add(new JLabel("<html>Night Games created by The Silver Bard<br/>"
                            + "Reyka and Samantha and a whole lot of stuff created by DNDW<br/>" + "Upgraded Strapon created by MotoKuchoma<br/>"
                            + "Strapon victory scenes created by Legion<br/>" + "Advanced AI by Jos<br/>"
                            + "Magic Training scenes by Legion<br/>" + "Jewel 2nd Victory scene by Legion<br/>"
                            + "Video Games scenes 1-9 by Onyxdime<br/>"
                            + "Kat Penetration Victory and Defeat scenes by Onyxdime<br/>"
                            + "Kat Non-Penetration Draw scene by Onyxdime<br/>"
                            + "Mara/Angel threesome scene by Onyxdime<br/>"
                            + "Footfetish expansion scenes by Sakruff<br/>"
                            + "Mod by Nergantre<br/>"
                            + "A ton of testing by Bronzechair</html>"));
            Object[] debugOptions = {"OK", "DEBUG"};
            Object[] okOnly = {"OK"};
            Object[] creditsOptions;
            if (GameState.inGame()) {
                creditsOptions = debugOptions;
            } else {
                creditsOptions = okOnly;
            }
            int results = JOptionPane.showOptionDialog(this, panel, "Credits", JOptionPane.DEFAULT_OPTION,
                            JOptionPane.INFORMATION_MESSAGE, null, creditsOptions, creditsOptions[0]);
            if (results == 1 && GameState.inGame()) {
                JPanel debugPanel = new DebugGUIPanel();
                JOptionPane.showOptionDialog(this, debugPanel, "Debug", JOptionPane.DEFAULT_OPTION,
                                JOptionPane.PLAIN_MESSAGE, null, okOnly, okOnly[0]);
            } else if (results == 1) {
                JOptionPane.showOptionDialog(this, "Not in game", "Debug", JOptionPane.DEFAULT_OPTION,
                                JOptionPane.INFORMATION_MESSAGE, null, okOnly, okOnly[0]);
            }
        });

        // panel layouts

        // gamePanel - everything is contained within it

        gamePanel = new JPanel();
        getContentPane().add(gamePanel);
        gamePanel.setLayout(new BoxLayout(gamePanel, BoxLayout.Y_AXIS));

        // panel0 - invisible, only handles topPanel

        panel0 = new Panel();
        gamePanel.add(panel0);
        panel0.setLayout(new BoxLayout(panel0, BoxLayout.X_AXIS));

        // topPanel - invisible, menus

        topPanel = new JPanel();
        panel0.add(topPanel);
        topPanel.setLayout(new GridLayout(0, 1, 0, 0));

        // mainPanel - body of GUI (not including the top bar and such)

        mainPanel = new JPanel();
        gamePanel.add(mainPanel);
        mainPanel.setLayout(new BorderLayout(0, 0));


        // statusPanel - visible, character status

        statusPanel = new JPanel();
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));

        // portraitPanel - invisible, contains imgPanel, west panel

        portraitPanel = new JPanel();
        mainPanel.add(portraitPanel, BorderLayout.WEST);

        portraitPanel.setLayout(new ShrinkingCardLayout());

        portraitPanel.setBackground(GUIColors.bgDark);
        portrait = new JLabel("");
        portrait.setVerticalAlignment(SwingConstants.TOP);
        portraitPanel.add(portrait, USE_PORTRAIT);

        map = new MapComponent();
        portraitPanel.add(map, USE_MAP);
        portraitPanel.add(Box.createGlue(), USE_NONE);

        // centerPanel, a CardLayout that will flip between the main text and different UIs
        centerPanel = new JPanel(new ShrinkingCardLayout());
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // textScroll
        textScroll = new JScrollPane();

        // textPane
        textPane = new JTextPane();
        DefaultCaret caret = (DefaultCaret) textPane.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        textPane.setForeground(GUIColors.textColorLight);
        textPane.setBackground(GUIColors.bgLight);
        textPane.setPreferredSize(new Dimension(width, 400));
        textPane.setEditable(false);
        textPane.setContentType("text/html");
        textScroll.setViewportView(textPane);
        fontsize = 5;

        // imgPanel - visible, contains imgLabel
        imgPanel = new JPanel();

        // imgLabel - probably contains the in-battle images
        imgLabel = new JLabel();
        imgPanel.add(imgLabel, BorderLayout.NORTH);
        imgLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        // textAreaPanel - the area with the main text window and the in-battle stance image if active.
        JPanel textAreaPanel = new JPanel();
        textAreaPanel.setLayout(new BoxLayout(textAreaPanel, BoxLayout.PAGE_AXIS));
        textAreaPanel.add(imgLabel);
        textAreaPanel.add(textScroll);
        textAreaPanel.setBackground(GUIColors.bgDark);

        centerPanel.add(textAreaPanel, USE_MAIN_TEXT_UI);

        // clothesPanel - used for closet ui
        clothesPanel = new JPanel();
        clothesPanel.setLayout(new GridLayout(0, 1));
        clothesPanel.setBackground(new Color(25, 25, 50));
        centerPanel.add(clothesPanel, USE_CLOSET_UI);

        // commandPanel - visible, contains the player's command buttons
        groupBox = Box.createHorizontalBox();
        groupBox.setBackground(GUIColors.bgDark);
        groupBox.setBorder(new CompoundBorder());
        JPanel groupPanel = new JPanel();
        gamePanel.add(groupPanel);

        commandPanel = new CommandPanel(width);
        groupPanel.add(groupBox);
        groupPanel.add(commandPanel.getPanel());
        gamePanel.add(groupPanel);
        groupPanel.setBackground(GUIColors.bgDark);
        groupPanel.setBorder(new CompoundBorder());

        skills = new HashMap<>();
        clearCommand();
        currentTactics = TacticGroup.all;
        setVisible(true);
        pack();
        JPanel panel = (JPanel) getContentPane();
        panel.setFocusable(true);
        // TODO: It's easy to lose focus and make hotkeys stop working.
        panel.addKeyListener(new KeyListener() {
            /**
             * Space bar will select the first option, unless they are in the default actions list.
             */
            @Override
            public void keyReleased(KeyEvent e) {
                Optional<KeyableButton> buttonOptional = commandPanel.getButtonForHotkey(e.getKeyChar());
                buttonOptional.ifPresent(KeyableButton::call);
            }

            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {}
        });

        // Use this for making save dialogs
        saveFileChooser = new NgsChooser(this);

    }

    public void load(GameState gameState) {
        try {
            loadedState.clear();
            loadedState.put(gameState);
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public Optional<File> askForSaveFile() {
        return saveFileChooser.askForSaveFile();
    }

    // image loader
    public static void setUIFont (javax.swing.plaf.FontUIResource f){
        Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
          Object key = keys.nextElement();
          Object value = UIManager.get (key);
          if (value instanceof javax.swing.plaf.FontUIResource)
            UIManager.put (key, f);
          }
    }

    public void displayImage(String path, String artist) {
        if (Flag.checkFlag(Flag.noimage)){
            return;
        }
        if (DebugFlags.isDebugOn(DebugFlags.DEBUG_GUI)) {
            System.out.println("Display image: " + path);
        }
        if (!(new File("assets/"+path).canRead())) {
            return;
        }
        BufferedImage pic = null;
        try {
            pic = ImageIO.read(ResourceLoader.getFileResourceAsStream("assets/" + path));
        } catch (IOException | IllegalArgumentException e) {
            e.printStackTrace();
        }
        clearImage();
        if (pic != null) {
            imgLabel.setIcon(new ImageIcon(pic));
            imgLabel.setHorizontalAlignment(SwingConstants.CENTER);
            imgLabel.setToolTipText(artist);
        }
    }

    // image unloader
    public void clearImage() {
        if (DebugFlags.isDebugOn(DebugFlags.DEBUG_GUI)) {
            System.out.println("Reset image");
        }
        imgLabel.setIcon(null);
    }
    public void clearPortrait() {
        portrait.setIcon(null);
    }
    public void loadPortrait(String imagepath) {
        if (imagepath != null && new File("assets/"+imagepath).canRead()) {
            BufferedImage face = null;
            try {
                face = ImageIO.read(ResourceLoader.getFileResourceAsStream("assets/" + imagepath));
            } catch (IOException | IllegalArgumentException e) {
                e.printStackTrace();
            }
            if (face != null) {
                if (DebugFlags.isDebugOn(DebugFlags.DEBUG_IMAGES)) {
                    System.out.println("Loading Portrait " + imagepath + " \n");
                }
                portrait.setIcon(null);

                if (width > 720) {
                    portrait.setIcon(new ImageIcon(face));
                    portrait.setVerticalAlignment(SwingConstants.TOP);
                } else {
                    Image scaledFace = face.getScaledInstance(width / 6, height / 4, Image.SCALE_SMOOTH);
                    portrait.setIcon(new ImageIcon(scaledFace));
                    portrait.setVerticalAlignment(SwingConstants.TOP);
                    System.out.println("Portrait resizing active.");
                }
            }
        }
    }

    // portrait loader
    public void loadPortrait(Combat c, Character player, Character enemy) {
        if (!Flag.checkFlag(Flag.noportraits) && c != null && c.isBeingObserved()) {
            if (DebugFlags.isDebugOn(DebugFlags.DEBUG_GUI)) {
                System.out.println("Load portraits");
            }
            String imagepath = null;
            if (!player.human()) {
                imagepath = player.getPortrait(c);
            } else if (!enemy.human()) {
                imagepath = enemy.getPortrait(c);
            }
            loadPortrait(imagepath);
        } else {
            clearPortrait();
            if (DebugFlags.isDebugOn(DebugFlags.DEBUG_GUI)) {
                System.out.println("No portraits");
            }
        }
    }

    public void showMap() {
        if (DebugFlags.isDebugOn(DebugFlags.DEBUG_GUI)) {
            System.out.println("Show map");
        }
        map.setPreferredSize(new Dimension(300, 385));
        CardLayout portraitLayout = (CardLayout) (portraitPanel.getLayout());
        portraitLayout.show(portraitPanel, USE_MAP);
    }

    public void showPortrait() {
        if (DebugFlags.isDebugOn(DebugFlags.DEBUG_GUI)) {
            System.out.println("Show portrait");
        }
        CardLayout portraitLayout = (CardLayout) (portraitPanel.getLayout());
        portraitLayout.show(portraitPanel, USE_PORTRAIT);
    }

    public void showNone() {
        if (DebugFlags.isDebugOn(DebugFlags.DEBUG_GUI)) {
            System.out.println("Show none");
        }
        CardLayout portraitLayout = (CardLayout) (portraitPanel.getLayout());
        portraitLayout.show(portraitPanel, USE_NONE);
    }

    // getLabelString - handles all the meters (bars)
    private String getLabelString(Meter meter) {
        if (meter.getOverflow() > 0) {
            return "(" + (meter.get() + meter.getOverflow()) + ")/" + meter.max();
        }
        return meter.get() + "/" + meter.max();
    }

    private void populateGameState(GameState gameState) {
        getContentPane().remove(creation);
        getContentPane().add(gamePanel);
        getContentPane().validate();

        Player player = gameState.characterPool.getPlayer();
        populatedPlayer = player;
        mntmOptions.setEnabled(true);
        player.gui = this;
        player.addObserver(this);
        JPanel meter = new JPanel();
        meter.setBackground(GUIColors.bgDark);
        topPanel.add(meter);
        meter.setLayout(new GridLayout(0, 4, 0, 0));

        stamina = new JLabel("Stamina: " + getLabelString(player.getStamina()));
        stamina.setFont(new Font("Sylfaen", Font.BOLD, 15));
        stamina.setHorizontalAlignment(SwingConstants.CENTER);
        stamina.setForeground(GUIColors.STAMINA_FOREGROUND);
        stamina.setToolTipText(
                        "Stamina represents your endurance and ability to keep fighting. If it drops to zero, you'll be temporarily stunned.");
        meter.add(stamina);

        arousal = new JLabel("Arousal: " + getLabelString(player.getArousal()));
        arousal.setFont(new Font("Sylfaen", Font.BOLD, 15));
        arousal.setHorizontalAlignment(SwingConstants.CENTER);
        arousal.setForeground(GUIColors.AROUSAL_FOREGROUND);
        arousal.setToolTipText(
                        "Arousal is raised when your opponent pleasures or seduces you. If it hits your max, you'll orgasm and lose the fight.");
        meter.add(arousal);

        mojo = new JLabel("Mojo: " + getLabelString(player.getMojo()));
        mojo.setFont(new Font("Sylfaen", Font.BOLD, 15));
        mojo.setHorizontalAlignment(SwingConstants.CENTER);
        mojo.setForeground(GUIColors.MOJO_FOREGROUND);
        mojo.setToolTipText(
                        "Mojo is the abstract representation of your momentum and style. It increases with normal techniques and is used to power special moves");
        meter.add(mojo);

        willpower = new JLabel("Willpower: " + getLabelString(player.getWillpower()));
        willpower.setFont(new Font("Sylfaen", Font.BOLD, 15));
        willpower.setHorizontalAlignment(SwingConstants.CENTER);
        willpower.setForeground(GUIColors.WILLPOWER_FOREGROUND);
        willpower.setToolTipText("Willpower is a representation of your will to fight. When this reaches 0, you lose.");
        meter.add(willpower);
        try {
            // on macs, the aqua look and feel does not have colored progress bars.
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                        | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        staminaBar = new JProgressBar();
        staminaBar.setBorder(new SoftBevelBorder(1, null, null, null, null));
        staminaBar.setForeground(GUIColors.STAMINA_FOREGROUND);
        staminaBar.setBackground(GUIColors.METER_BACKGROUND);
        meter.add(staminaBar);
        staminaBar.setMaximum(player.getStamina().max());
        staminaBar.setValue(player.getStamina().get());

        arousalBar = new JProgressBar();
        arousalBar.setBorder(new SoftBevelBorder(1, null, null, null, null));
        arousalBar.setForeground(GUIColors.AROUSAL_FOREGROUND);
        arousalBar.setBackground(GUIColors.METER_BACKGROUND);
        meter.add(arousalBar);
        arousalBar.setMaximum(player.getArousal().max());
        arousalBar.setValue(player.getArousal().get());

        mojoBar = new JProgressBar();
        mojoBar.setBorder(new SoftBevelBorder(1, null, null, null, null));
        mojoBar.setForeground(GUIColors.MOJO_FOREGROUND);
        mojoBar.setBackground(GUIColors.METER_BACKGROUND);
        meter.add(mojoBar);
        mojoBar.setMaximum(player.getMojo().max());
        mojoBar.setValue(player.getMojo().get());

        willpowerBar = new JProgressBar();
        willpowerBar.setBorder(new SoftBevelBorder(1, null, null, null, null));
        willpowerBar.setForeground(GUIColors.WILLPOWER_FOREGROUND);
        willpowerBar.setBackground(GUIColors.METER_BACKGROUND);
        meter.add(willpowerBar);
        willpowerBar.setMaximum(player.getWillpower().max());
        willpowerBar.setValue(player.getWillpower().get());
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                        | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        JPanel bio = new JPanel();
        topPanel.add(bio);
        bio.setLayout(new GridLayout(2, 0, 0, 0));
        bio.setBackground(GUIColors.bgDark);

        JLabel name = new JLabel(player.getTrueName());
        name.setHorizontalAlignment(SwingConstants.LEFT);
        name.setFont(new Font("Sylfaen", Font.BOLD, 15));
        name.setForeground(GUIColors.textColorLight);
        bio.add(name);
        lvl = new JLabel("Lvl: " + player.getLevel());
        lvl.setFont(new Font("Sylfaen", Font.BOLD, 15));
        lvl.setForeground(GUIColors.textColorLight);

        bio.add(lvl);
        xp = new JLabel("XP: " + player.getXP());
        xp.setFont(new Font("Sylfaen", Font.BOLD, 15));
        xp.setForeground(GUIColors.textColorLight);
        bio.add(xp);

        UIManager.put("ToggleButton.select", new Color(75, 88, 102));
        stsbtn = new JToggleButton("Status");
        stsbtn.addActionListener(arg0 -> {
            if (stsbtn.isSelected()) {
                mainPanel.add(statusPanel, BorderLayout.EAST);
            } else {
                mainPanel.remove(statusPanel);
            }
            refresh();
            mainPanel.validate();
        });
        bio.add(stsbtn);

    	inventoryFrame = new JFrame("Inventory");
        inventoryFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        inventoryFrame.setVisible(false);
        inventoryFrame.setLocationByPlatform(true);
        inventoryFrame.setResizable(true);
        inventoryFrame.setMinimumSize(new Dimension(800, 100));

        JButton inventoryButton = new JButton("Inventory");
        inventoryButton.addActionListener(arg0 -> {
            toggleInventory();
        });
        loclbl = new JLabel();
        loclbl.setFont(new Font("Sylfaen", Font.BOLD, 16));
        loclbl.setForeground(GUIColors.textColorLight);

        //stsbtn.setBackground(new Color(85, 98, 112));
        //stsbtn.setForeground(GUIColors.textColorLight);
        bio.add(loclbl);

        timeLabel = new JLabel();
        timeLabel.setFont(new Font("Sylfaen", Font.BOLD, 16));
        timeLabel.setForeground(GUIColors.textColorLight);
        bio.add(timeLabel);
        cashLabel = new JLabel();
        cashLabel.setFont(new Font("Sylfaen", Font.BOLD, 16));
        cashLabel.setForeground(new Color(33, 180, 42));
        bio.add(cashLabel);
        bio.add(inventoryButton);
        removeClosetGUI();
        topPanel.validate();
        showNone();
        refresh();
    }

    public void showGameCreation() {
        mntmOptions.setEnabled(false);
        getContentPane().remove(gamePanel);
        if (creation != null) {
            getContentPane().remove(creation);
        }
        creation = new CreationGUI();
        getContentPane().add(creation);
        getContentPane().validate();
    }

    public void purgeGameState() {
        if (currentPrompt != null) {
            currentPrompt.cancel(true);
            currentPrompt = null;
        }
        SwingUtilities.invokeLater(this::purgeGameStateInternal);
    }

    private void purgeGameStateInternal() {
        getContentPane().remove(gamePanel);
        clearText();
        clearCommand();
        showNone();
        clearImage();
        mntmQuitMatch.setEnabled(false);
        combat = null;
        topPanel.removeAll();
    }

    public void clearText() {
        if (DebugFlags.isDebugOn(DebugFlags.DEBUG_GUI)) {
            System.out.println("Clearing messages");
            DebugHelper.printStackFrame(5, 1);
        }
        textPane.setText("");
    }

    public void clearTextIfNeeded() {
        textPane.getCaretPosition();
        textPane.setCaretPosition(textPane.getDocument().getLength());
        textPane.selectAll();
        int x = textPane.getSelectionEnd();
        textPane.select(x, x);
    }

    public void message(String text) {
        message(null, null, text);
    }

    public void message(Combat c, Character character, String text) {
        if (c != null) {
            if (character != null) {
                c.write(character, text);
            } else {
                c.write(text);
            }
        }
        if (text.trim().length() == 0) {
            return;
        }

        HTMLDocument doc = (HTMLDocument) textPane.getDocument();
        HTMLEditorKit editorKit = (HTMLEditorKit) textPane.getEditorKit();
        try {
            editorKit.insertHTML(doc, doc.getLength(),
                            "<font face='Georgia' color='white' size='" + fontsize + "'>" + text + "</font><br/>",
                            0, 0, null);
        } catch (BadLocationException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        refresh();
    }

    private void combatMessage(String text, boolean clearText) {
        HTMLDocument doc = (HTMLDocument) textPane.getDocument();
        HTMLEditorKit editorKit = (HTMLEditorKit) textPane.getEditorKit();
        if (clearText) {
            clearText();
        }
        try {
            editorKit.insertHTML(doc, doc.getLength(),
                            "<font face='Georgia' color='white' size='" + fontsize + "'>" + text + "</font><br/>",
                            0, 0, null);
        } catch (BadLocationException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void clearCommand() {
        skills.clear();
        chosenSkill = new CompletableFuture<>();
        Arrays.stream(TacticGroup.values()).forEach(tactic -> skills.put(tactic, new ArrayList<>()));
        groupBox.removeAll();
        commandPanel.reset();
    }

    public void addSkill(Combat com, Skill action, Character target) {
        SkillButton btn = new SkillButton(com, action, target, chosenSkill);
        skills.get(action.type(com).getGroup()).add(btn);
    }

    public Skill getChosenSkill() throws InterruptedException {
        try {
            return chosenSkill.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void showSkills() {
        commandPanel.reset();
        int i = 1;
        for (TacticGroup group : TacticGroup.values()) {
            SwitchTacticsButton tacticsButton = new SwitchTacticsButton(group);
            commandPanel.register(java.lang.Character.forDigit(i % 10, 10), tacticsButton);
            groupBox.add(tacticsButton);
            groupBox.add(Box.createHorizontalStrut(4));
            i += 1;
        }
        List<SkillButton> flatList = new ArrayList<>();
        for (TacticGroup group : TacticGroup.values()) {
            flatList.addAll(skills.get(group));
        }
        if (currentTactics == TacticGroup.all || flatList.size() <= 6 || skills.get(currentTactics).size() == 0) {
            flatList.forEach(commandPanel::add);
        } else {
            for (SkillButton button : skills.get(currentTactics)) {
                commandPanel.add(button);
            }
        }
        commandPanel.refresh();
    }

    public void addButton(KeyableButton button) {
        commandPanel.add(button);
        commandPanel.refresh();
    }

    private void prompt(List<? extends KeyableButton> choices) {
        clearCommand();
        for (KeyableButton button : choices) {
            commandPanel.add(button);
        }
        commandPanel.refresh();
    }

    public <T> void prompt(List<LabeledValue<T>> choices, CompletableFuture<T> future) {
        List<KeyableButton> buttons = choices.stream().map(choice -> new ValueButton<>(choice, future)).collect(
                        Collectors.toList());
        currentPrompt = future;
        prompt(buttons);
    }

    public <T> CompletableFuture<T> promptFuture(List<LabeledValue<T>> choices) {
        CompletableFuture<T> future = new CompletableFuture<>();
        prompt(choices, future);
        return future;
    }

    public <T> CompletableFuture<T> promptFuture(List<T> choices, Function<T, String> nameFunc) {
        return promptFuture(choices.stream().map(choice -> new LabeledValue<>(choice, nameFunc.apply(choice)))
                        .collect(Collectors.toList()));
    }

    public ContinueButton next(String label) {
        ContinueButton button = new ContinueButton(label);
        addButton(button);
        GUI.currentPrompt = button.future;
        return button;
    }

    public <T> void cancel(String label, CompletableFuture<T> future) {
        CancelButton button = new CancelButton(label, future);
        addButton(button);
    }

    public void refresh() {
        if (!this.refreshRequested) {
            this.refreshRequested = true;
            if (populatedPlayer != null) {
                SwingUtilities.invokeLater(() -> refreshInternal(populatedPlayer));
            }
        }
    }

    private void refreshInternal(Player player) {
        stamina.setText("Stamina: " + getLabelString(player.getStamina()));
        arousal.setText("Arousal: " + getLabelString(player.getArousal()));
        mojo.setText("Mojo: " + getLabelString(player.getMojo()));
        willpower.setText("Willpower: " + getLabelString(player.getWillpower()));
        lvl.setText("Lvl: " + player.getLevel());
        xp.setText("XP: " + player.getXP());
        staminaBar.setMaximum(player.getStamina().max());
        staminaBar.setValue(player.getStamina().get());
        arousalBar.setMaximum(player.getArousal().max());
        arousalBar.setValue(player.getArousal().get());
        mojoBar.setMaximum(player.getMojo().max());
        mojoBar.setValue(player.getMojo().get());
        willpowerBar.setMaximum(player.getWillpower().max());
        willpowerBar.setValue(player.getWillpower().get());
        loclbl.setText(player.location().name);
        cashLabel.setText("$" + player.money);
        if (map != null) {
            map.repaint();
        }
        // We may be in between setting NIGHT and building the Match object
        String timeText;
        String textColor = "rgb(0, 0, 0)";
        if (Time.getTime() == Time.NIGHT) {
            // yup... silverbard pls :D
            if (Match.getMatch() == null) {
                timeText = "9:50 pm";
            } else if (Match.getMatch().getHour() >= 12) {
                timeText = Match.getMatch().getTime() + " am";
            } else {
                timeText = Match.getMatch().getTime() + " pm";
            }
            textColor = "rgb(51, 101, 202)";
        } else if (Time.getTime() == Time.DAY) { // not updating correctly during daytime
            if (Daytime.getDay() != null) {
                timeText = Daytime.getDay().getTime();
            } else {
                timeText = "10:00 am";
            }
            textColor = "rgb(253, 184, 19)";
        } else {
            System.err.println("Unknown time of day: " + Time.getTime());
            timeText = "";
        }
        timeLabel.setText(String.format("<html>Day %d - <font color='%s'>%s</font></html>", Time.getDate(), textColor, timeText));
        displayStatus(player);
        List<Item> availItems = player.getInventory().entrySet().stream().filter(entry -> (entry.getValue() > 0))
                .map(Map.Entry::getKey).collect(Collectors.toList());

	    JPanel inventoryPane = new JPanel();
	    inventoryPane.setLayout(new GridLayout(0, 5));
	    inventoryPane.setSize(new Dimension(400, 800));
	    inventoryPane.setBackground(GUIColors.bgDark);

	    Map<Item, Integer> items = player.getInventory();
	    int count = 0;

	    for (Item i : availItems) {
	        JLabel label = new JLabel(i.getName() + ": " + items.get(i) + "\n");
	        label.setForeground(GUIColors.textColorLight);
	        label.setToolTipText(i.getDesc());
	        inventoryPane.add(label);
	        count++;
	    }
	    inventoryFrame.getContentPane().removeAll();
        inventoryFrame.getContentPane().add(BorderLayout.CENTER, inventoryPane);
        inventoryFrame.pack();
        refreshRequested = false;
    }

    private void toggleInventory() {
    	EventQueue.invokeLater(() -> {
    		if (!inventoryFrame.isVisible()) {
	    		refresh();
		        inventoryFrame.setVisible(true);
    		} else {
    			inventoryFrame.setVisible(false);
    		}
    	});
    }
    
    private void displayStatus(Player player) {
        statusPanel.removeAll();
        statusPanel.repaint();
        //statusPanel.setPreferredSize(new Dimension(400, mainPanel.getHeight()));
        statusPanel.setPreferredSize(new Dimension(width/4, mainPanel.getHeight()));

        
        if (width < 720) {
            statusPanel.setMaximumSize(new Dimension(height, width / 6));
            System.out.println("STATUS PANEL");
        }
        JPanel statsPanel = new JPanel(new GridLayout(0, 3));

        statusPanel.add(statsPanel);
        //statsPanel.setPreferredSize(new Dimension(400, 200));
        statsPanel.setPreferredSize(new Dimension(width/4, 200));

        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(statusPanel.getWidth(), 2));
        statusPanel.add(sep);
        int count = 0;
        statsPanel.setBackground(GUIColors.bgLight);
        int descFontSize = fontsize - 1;
        ArrayList<JLabel> attlbls = new ArrayList<>();
        for (Attribute a : Attribute.values()) {
            int amt = player.get(a);
            int pure = player.getPure(a);
            if (pure > 0 || amt > 0) {
                if (amt == pure) {
                    JLabel label = new JLabel(String.format("<html><font face='Georgia' size=%d>%s: %s</font></html>", descFontSize, a.name(), amt));
                    label.setForeground(GUIColors.textColorLight);
                    attlbls.add(count, label);
                    statsPanel.add(attlbls.get(count++));
                } else {
                    String attrColor;
                    String bonusColor;
                    if (amt < pure) {
                        attrColor = "255,100,100";
                        bonusColor = "255,0,0";
                    } else {
                        attrColor = "100,255,255";
                        bonusColor = "0,255,0";
                    }
                    int statBonusFontSize = descFontSize - 1;
                    String labelString = String.format("<html><font face='Georgia' size=%d>%s: <font color='rgb(%s)'>%d</font> <font size=%d color='rgb(%s)'>(%+d)</font></font></html>",
                                    descFontSize, a.name(), attrColor, amt, statBonusFontSize, bonusColor, amt - pure);
                    JLabel label = new JLabel(labelString);
                    label.setForeground(GUIColors.textColorLight);
                    attlbls.add(count, label);
                    statsPanel.add(attlbls.get(count++));
                }
            }
        }

        // statusText - body, clothing and status description
        JTextPane statusText = new JTextPane();
        DefaultCaret caret = (DefaultCaret) statusText.getCaret();
        caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
        statusText.setBackground(GUIColors.bgLight);
        statusText.setEditable(false);
        statusText.setContentType("text/html");
        HTMLDocument doc = (HTMLDocument) statusText.getDocument();
        HTMLEditorKit editorKit = (HTMLEditorKit) statusText.getEditorKit();
        Random.freezeRNG();
        try {
            editorKit.insertHTML(doc, doc.getLength(),
                            "<font face='Georgia' color='white' size='" + descFontSize + "'>"
                                            + player.getOutfit().describe(player) + "<br/>" + player.describeStatus()
                                            + "</font><br/>",
                            0, 0, null);
        } catch (BadLocationException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Random.unfreezeRNG();
        JScrollPane scrollPane = new JScrollPane(statusText);
        scrollPane.setBackground(GUIColors.bgLight);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        JPanel currentStatusPanel = new JPanel(new GridLayout());
//        statusPanel.setPreferredSize(new Dimension(400, height));
//        currentStatusPanel.setMaximumSize(new Dimension(400, 2000));
//        currentStatusPanel.setPreferredSize(new Dimension(400, 2000));
        statusPanel.setPreferredSize(new Dimension(width/4, height));
        currentStatusPanel.setMaximumSize(new Dimension(width/4, 2000));
        currentStatusPanel.setPreferredSize(new Dimension(width/4, 2000));
        
        currentStatusPanel.setBackground(GUIColors.bgLight);
        statusPanel.add(currentStatusPanel);
        currentStatusPanel.add(scrollPane);
        statusPanel.setBackground(GUIColors.bgLight);
        if (width < 720) {
            currentStatusPanel.setSize(new Dimension(height, width / 6));
            System.out.println("Oh god so tiny");
        }
        mainPanel.revalidate();
        statusPanel.revalidate();
        statusPanel.repaint();
    }

    @Override
    public void update(Observable arg0, Object arg1) {
        refresh();
        if (arg0 instanceof Combat) {
            Combat combat = (Combat) arg0;
            if (combat.combatMessageChanged) {
                combatMessage(combat.getMessage(), combat.clearText);
                combat.resetMessageAfterGUIUpdate();
            }
        }
    }

    public void changeClothes(Character character) throws InterruptedException {
        CompletableFuture<Boolean> changeClothes = new CompletableFuture<>();
        clothesPanel.removeAll();
        clothesPanel.add(new ClothesChangeGUI(character, changeClothes));
        CardLayout layout = (CardLayout) centerPanel.getLayout();
        layout.show(centerPanel, USE_CLOSET_UI);
        try {
            changeClothes.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } finally {
            removeClosetGUI();
        }
        character.change();
    }

    private void removeClosetGUI() {
        if (DebugFlags.isDebugOn(DebugFlags.DEBUG_GUI)) {
            System.out.println("remove closet gui");
        }
        clothesPanel.removeAll();
        CardLayout layout = (CardLayout) centerPanel.getLayout();
        layout.show(centerPanel, USE_MAIN_TEXT_UI);
    }

    public void systemMessage(String string) {
        if (Flag.checkFlag(Flag.systemMessages)) {
            message(string);
        }
    }

    int nSkillsForGroup(TacticGroup group) {
        return skills.get(group).size();
    }

    void switchTactics(TacticGroup group) {
        groupBox.removeAll();
        currentTactics = group;
        gui.showSkills();
    }

    public GameState getGameState() throws InterruptedException {
        if (Thread.interrupted()) {
            // Un-interrupt thread before blocking to wait for user input
            System.out.println("Uninterrupting thread: " + Thread.currentThread().getName());
        }
        GameState gameState = loadedState.take();
        populateGameState(gameState);
        return gameState;
    }
}

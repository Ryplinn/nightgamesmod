package nightgames.global;

import nightgames.characters.*;
import nightgames.characters.trait.Trait;
import nightgames.daytime.Daytime;
import nightgames.gui.GUI;
import nightgames.json.JsonUtils;
import nightgames.skills.SkillPool;
import nightgames.start.PlayerConfiguration;
import nightgames.start.StartConfiguration;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

/**
 * Creates, destroys, and maintains the state of a running game.
 */
public class GameState {
    static final double DEFAULT_XP_RATE = 1.0;
    public static final double DEFAULT_MONEY_RATE = 1.0;
    public double moneyRate;
    public double xpRate;
    private static boolean ingame = false;
    public CharacterPool characterPool;
    private volatile Thread loopThread;

    public GameState(String playerName, StartConfiguration config, List<Trait> pickedTraits,
                CharacterSex pickedGender, Map<Attribute, Integer> selectedAttributes) {
        this(playerName, config, pickedTraits, pickedGender, selectedAttributes, DEFAULT_XP_RATE, DEFAULT_MONEY_RATE);
    }

    public GameState(String playerName, StartConfiguration config, List<Trait> pickedTraits,
                    CharacterSex pickedGender, Map<Attribute, Integer> selectedAttributes, double xpRate, double moneyRate) {
        characterPool = new CharacterPool(config);
        this.xpRate = xpRate;
        this.moneyRate = moneyRate;
        PlayerConfiguration playerConfig = Optional.ofNullable(config).map(c -> c.player).orElse(null);
        Collection<String> cfgFlags = Optional.ofNullable(config).map(StartConfiguration::getFlags).orElse(new ArrayList<>());
        characterPool.human = new Player(playerName, pickedGender, playerConfig, pickedTraits, selectedAttributes);
        if(characterPool.human.has(Trait.largereserves)) {
            characterPool.human.getWillpower().gain(20);
        }
        SkillPool.buildSkillPool(characterPool.human);
        SkillPool.learnSkills(characterPool.human);
        Flag.flags = new HashSet<>(cfgFlags);
        Flag.resetCounters();
        Time.time = Time.NIGHT;
        Time.date = 1;
        Flag.setCharacterDisabledFlag(CharacterType.get("Yui"));
        Flag.setFlag(Flag.systemMessages, true);
    }

    /**
     * Creates GameState from SaveData object.
     * @param data A SaveData object, as loaded from save files.
     */
    protected GameState(SaveData data) {
        xpRate = data.xpRate;
        moneyRate = data.moneyRate;
        // legacy support: previously we only saved unlocked NPCs.
        if (Flag.checkFlag(Flag.LegacyCharAvailableSave)) {
            characterPool = new CharacterPool(null);
            characterPool.updateNPCs(data.npcs);
            characterPool.updatePlayer(data.player);
        } else {
            characterPool = new CharacterPool(data.player, data.npcs);
        }
        SkillPool.buildSkillPool(characterPool.human);
        Flag.flags = new HashSet<>(data.flags);
        Flag.counters = new HashMap<>(data.counters);
        Time.date = data.date;
        Time.time = data.time;
        GUI.gui.fontsize = data.fontsize;
    }

    // TODO: Make this its own scene.
    public static String getIntro() {
        return "You don't really know why you're going to the Student Union in the middle of the night."
                        + " You'd have to be insane to accept the invitation you received this afternoon."
                        + " Seriously, someone is offering you money to sexfight a bunch of other students?"
                        + " You're more likely to get mugged (though you're not carrying any money) or murdered if you show up."
                        + " Best case scenario, it's probably a prank for gullible freshmen."
                        + " You have no good reason to believe the invitation is on the level, but here you are, walking into the empty Student Union."
                        + "\n\n" + "Not quite empty, it turns out."
                        + " The same woman who approached you this afternoon greets you and brings you to a room near the back of the building."
                        + " Inside, you're surprised to find three quite attractive girls."
                        + " After comparing notes, you confirm they're all freshmen like you and received the same invitation today."
                        + " You're surprised, both that these girls would agree to such an invitation."
                        + " For the first time, you start to believe that this might actually happen."
                        + " After a few minutes of awkward small talk (though none of these girls seem self-conscious about being here), the woman walks in again leading another girl."
                        + " Embarrassingly you recognize the girl, named Cassie, who is a classmate of yours, and who you've become friends with over the past couple weeks."
                        + " She blushes when she sees you and the two of you consciously avoid eye contact while the woman explains the rules of the competition."
                        + "\n\n" + "There are a lot of specific points, but the rules basically boil down to this: "
                        + " competitors move around the empty areas of the campus and engage each other in sexfights."
                        + " When one competitor orgasms and doesn't have the will to go on, the other gets a point and can claim the loser's clothes."
                        + " Those two players are forbidden to engage again until the loser gets a replacement set of clothes at either the Student Union or the first floor of the dorm building."
                        + " It seems to be customary, but not required, for the loser to get the winner off after a fight, when it doesn't count."
                        + " After three hours, the match ends and each player is paid for each opponent they defeat, each set of clothes turned in, and a bonus for whoever scores the most points."
                        + "\n\n"
                        + "After the explanation, she confirms with each participant whether they are still interested in participating."
                        + " Everyone agrees." + " The first match starts at exactly 10:00.";
    }

    public static boolean inGame() {
        return ingame;
    }

    public static GameState getGameState() {
        return Main.gameState;
    }

    static void setGameState(GameState gameState) {
        Main.gameState = gameState;
    }

    synchronized void gameLoop() {
        System.out.println(String.format("Starting game with player %s", this.characterPool.human.getName()));
        loopThread = Thread.currentThread();
        ingame = true;
        while (!Thread.interrupted()) {
            try {
                if (Time.getTime() == Time.NIGHT) {
                    // do nighttime stuff
                    // choose match
                    CompletableFuture<Match> preparedMatch = new CompletableFuture<>();
                    Prematch prematch = Prematch.decideMatchType(preparedMatch);
                    // set up match
                    prematch.prompt(characterPool.human);
                    Match match = preparedMatch.get();
                    // start match
                    CountDownLatch matchComplete = match.matchComplete;
                    match.startMatch();
                    // 36 * 5 mins = 180 mins = 3 hours for a standard game
                    match.matchLoop(36);
                    // end match
                    match.end();
                    matchComplete.await();
                    Postmatch postmatch = new Postmatch(characterPool.getPlayer(), match.combatants);
                    postmatch.endMatch();
                    // set time to next day
                    Time.date++;
                    Time.time = Time.DAY;
                    postmatch.endMatchGui();
                    postmatch.readyForBed.await();
                    // autosave
                    SaveFile.autoSave();
                    // sleep
                }
                if (Time.getTime() == Time.DAY) {
                    Match.match = null;
                    Daytime.day = new Daytime(characterPool.human);
                    // do daytime stuff
                    Daytime.day.dayLoop();
                    // set time to night
                    Daytime.day = null;
                    Time.time = Time.NIGHT;
                    // autosave
                    SaveFile.autoSave();
                }
            } catch (InterruptedException e) {
                // Usually fires when choosing "New Game" or "Load Game" from the menu during a running game.
                Thread.currentThread().interrupt();
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println("Closing game");
        ingame = false;
    }

    public void closeGame() {
        loopThread.interrupt();
    }

    public static void closeCurrentGame() {
        if (GameState.getGameState() != null) {
            GameState.getGameState().closeGame();
            GameState.setGameState(null);
        }
    }
}

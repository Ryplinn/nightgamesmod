package nightgames.combat;

import nightgames.areas.Area;
import nightgames.characters.*;
import nightgames.characters.Character;
import nightgames.daytime.Daytime;
import nightgames.global.*;
import nightgames.global.Random;
import nightgames.gui.GUI;
import nightgames.gui.TestGUI;
import nightgames.modifier.standard.NoModifier;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class CombatStats {
    private static final Area NULL_AREA = new Area("", "", null);
    private static final int MATCH_COUNT = 10;

    private List<Character> combatants;
    private Map<String, Record> records;
    private Setup setup;

    private final AtomicInteger counter = new AtomicInteger();
    private final Object recordLock = new Object();

    public void setupTestRun(Setup setup) {
        this.setup = setup;
        records = new HashMap<>();
        combatants = setup.execute();
        combatants.forEach(c -> records.put(c.getTrueName(), new Record(c)));
        //Global.save(true);
        DebugFlags.debug = new boolean[DebugFlags.values().length];
    }

    private void doTestRun() throws InterruptedException {
        for (int i = 0; i < combatants.size(); i++) {
            for (int j = 0; j < i; j++) {
                fightMany(combatants.get(i), combatants.get(j), MATCH_COUNT);
            }
        }
        StringBuilder results = new StringBuilder(setup.toString());
        System.out.println("Fight counter: " + counter.get());
        System.out.println(setup);
        records.forEach((c, r) -> {
            String record = c + ": " + (double) r.totalWins / (double) r.totalPlayed + "\n" + r.toString();
            System.out.println(record);
            results.append(record);
        });
        File output = new File(setup.outputName());
        FileWriter fw;
        try {
            fw = new FileWriter(output);
            fw.write(results.toString());
            fw.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void fightMany(Character c1, Character c2, int count) throws InterruptedException {
        // ExecutorService threadPool = Executors.newFixedThreadPool(50);
        System.out.println(String.format("%s vs. %s (%dX)", c1.getTrueName(), c2.getTrueName(), count));
        for (int i = 0; i < count; i++) {
            try {
                Character clone1 = c1.clone();
                Character clone2 = c2.clone();
                fight(clone1, clone2);
                // threadPool.execute(() -> fight(clone1, clone2));
            } catch (CloneNotSupportedException e1) {
                e1.printStackTrace();
            } catch (Exception e) {
                System.err.println(String.format("Exception during fight: %s vs %s on bout %d, total fight count %d", c1, c2, i, counter.get()));
                throw e;
            }
        }
        /*
         * threadPool.shutdown(); try { threadPool.awaitTermination(3, TimeUnit.DAYS); } catch (InterruptedException e) { e.printStackTrace(); }
         */
    }

    private void fight(Character c1, Character c2) throws InterruptedException {
        Combat cbt = new Combat(c1, c2, NULL_AREA);
        cbt.runCombatNoDelay();
        counter.incrementAndGet();
        synchronized (recordLock) {
            if (!cbt.winner.isPresent()) {
                System.err.println("Error - winner is empty");
            } else if (cbt.winner.get().equals(NPC.noneCharacter())) {
                recordOf(c1).draw(c2);
                recordOf(c2).draw(c1);
            } else if (cbt.winner.get().equals(c1)) {
                recordOf(c1).win(c2);
                recordOf(c2).lose(c1);
            } else if (cbt.winner.get().equals(c2)) {
                recordOf(c1).lose(c2);
                recordOf(c2).win(c1);
            } else {
                System.err.println("Error - unknown causes");
            }
        }
    }

    private Record recordOf(Character c) {
        return records.get(c.getTrueName());
    }

    @Test
    public void combatStats() throws Exception {
        GameState gameState = new TestGameState();
        GUI gui = new TestGUI();
        Prematch prematch = new PreMatchSchool(new CompletableFuture<>());
        prematch.setUpMatch(new NoModifier());

        // Thread.sleep(10000);
        for (int i = 5; i < 75; i += 5) {
            Setup s3 = new Setup(i, new NPC("Reyka", new Reyka()), new NPC("Kat", new Kat()),
                            new NPC("Eve", new Eve()));
            setupTestRun(s3);
            doTestRun();
        }
    }

    private class Record {

        private Character subject;
        private volatile int totalPlayed, totalWins, totalLosses, totalDraws;
        private Map<String, Integer> wins, losses, draws;

        Record(Character subject) {
            this.subject = subject;
            wins = new HashMap<>();
            losses = new HashMap<>();
            draws = new HashMap<>();
            combatants.stream().filter(c -> !c.equals(subject)).forEach(c -> {
                wins.put(c.getTrueName(), 0);
                losses.put(c.getTrueName(), 0);
                draws.put(c.getTrueName(), 0);
            });
        }

        synchronized void win(Character opp) {
            totalPlayed++;
            totalWins++;
            wins.put(opp.getTrueName(), wins.get(opp.getTrueName()) + 1);
        }

        synchronized void lose(Character opp) {
            totalPlayed++;
            totalLosses++;
            losses.put(opp.getTrueName(), losses.get(opp.getTrueName()) + 1);
        }

        synchronized void draw(Character opp) {
            totalPlayed++;
            totalDraws++;
            draws.put(opp.getTrueName(), draws.get(opp.getTrueName()) + 1);
        }

        @Override
        public String toString() {
            return "Record [subject=" + subject + "\n\t totalPlayed=" + totalPlayed + "\n\t totalWins=" + totalWins
                            + "\n\t totalLosses=" + totalLosses + "\n\t totalDraws=" + totalDraws + "\n\t wins=" + wins
                            + "\n\t losses=" + losses + "\n\t draws=" + draws + "]";
        }
    }

    public static class Setup {

        private int level;
        private List<NPC> extraChars;

        public Setup(int level, NPC... extraChars) {
            this.level = level;
            this.extraChars = Arrays.asList(extraChars);
        }

        public String outputName() {
            return String.format("CombatStats-%d-%s-%d.txt", level, extraChars.stream()
                            .map(p -> p.getClass().getSimpleName().substring(0, 1)).collect(Collectors.joining()),
                            MATCH_COUNT);
        }

        public List<Character> execute() {
            extraChars.forEach(npc -> GameState.getGameState().characterPool.newChallenger(npc));
            List<Character> combatants = new ArrayList<>(Match.getParticipants());
            combatants.removeIf(Character::human);
            combatants.forEach(c -> {
                while (c.getLevel() < level) {
                    c.addLevelsImmediate(null, 1);
                    Character partner;
                    do {
                        partner = Random.pickRandomGuaranteed(combatants);
                    } while (c == partner);
                    Daytime.train(partner, c, Random.pickRandomGuaranteed(new ArrayList<>(c.att.keySet())));
                }
                c.modMoney(level * 500);
                Daytime.day = new Daytime(new Player("<player>"));
                Daytime.day.advance(999);
                Daytime.day.npcDaytime((NPC) c);
            });

            return combatants;
        }

        @Override
        public String toString() {
            return "Setup [level=" + level + ", extraChars=" + extraChars + "]";
        }
    }
}

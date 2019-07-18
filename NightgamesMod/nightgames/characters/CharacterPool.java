package nightgames.characters;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import nightgames.Resources.ResourceLoader;
import nightgames.characters.custom.CustomNPC;
import nightgames.characters.custom.JsonSourceNPCDataLoader;
import nightgames.characters.custom.NPCData;
import nightgames.characters.trait.Trait;
import nightgames.json.JsonUtils;
import nightgames.start.NPCConfiguration;
import nightgames.start.StartConfiguration;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Tracks Characters in current game.
 */
public class CharacterPool {
    public Map<CharacterType, NPC> characterPool;   // All starting and unlockable characters
    public Set<CharacterType> debugChars;
    public Player human;

    protected CharacterPool() {
        characterPool = new HashMap<>();
        debugChars = new HashSet<>();
    }

    /**
     * Creates a CharacterPool at the start of a new game.
     * @param startConfig The config of the new game.
     */
    public CharacterPool(StartConfiguration startConfig) {
        this();
        NPCConfiguration commonConfig = startConfig != null ? startConfig.npcCommon : null;

        try (InputStreamReader reader = new InputStreamReader(
                        ResourceLoader.getFileResourceAsStream("characters/included.json"))) {
            JsonArray characterSet = JsonUtils.rootJson(reader).getAsJsonArray();
            for (JsonElement element : characterSet) {
                String name = element.getAsString();
                try {
                    NPCData data = JsonSourceNPCDataLoader
                                    .load(ResourceLoader.getFileResourceAsStream("characters/" + name));
                    Personality npc = new CustomNPC(data, npcConfig, commonConfig);
                    characterPool.put(npc.getCharacter().getType(), npc.getCharacter());
                    System.out.println("Loaded " + name);
                } catch (JsonParseException e1) {
                    System.err.println("Failed to load NPC " + name);
                    e1.printStackTrace();
                }
            }
        } catch (JsonParseException | IOException e1) {
            System.err.println("Failed to load custom character set");
            e1.printStackTrace();
        }

        // TODO: Refactor into function and unify with CustomNPC handling.
        Personality cassie = new Cassie(findNpcConfig("Cassie", startConfig), commonConfig);
        Personality angel = new Angel(findNpcConfig("Angel", startConfig), commonConfig);
        Personality reyka = new Reyka(findNpcConfig("Reyka", startConfig), commonConfig);
        Personality kat = new Kat(findNpcConfig("Kat", startConfig), commonConfig);
        Personality mara = new Mara(findNpcConfig("Mara", startConfig), commonConfig);
        Personality jewel = new Jewel(findNpcConfig("Jewel", startConfig), commonConfig);
        Personality airi = new Airi(findNpcConfig("Airi", startConfig), commonConfig);
        Personality eve = new Eve(findNpcConfig("Eve", startConfig), commonConfig);
        Personality maya = new Maya(1, findNpcConfig("Maya", startConfig), commonConfig);
        Personality yui = new Yui(findNpcConfig("Yui", startConfig), commonConfig);
        characterPool.put(cassie.getCharacter().getType(), cassie.getCharacter());
        characterPool.put(angel.getCharacter().getType(), angel.getCharacter());
        characterPool.put(reyka.getCharacter().getType(), reyka.getCharacter());
        characterPool.put(kat.getCharacter().getType(), kat.getCharacter());
        characterPool.put(mara.getCharacter().getType(), mara.getCharacter());
        characterPool.put(jewel.getCharacter().getType(), jewel.getCharacter());
        characterPool.put(airi.getCharacter().getType(), airi.getCharacter());
        characterPool.put(eve.getCharacter().getType(), eve.getCharacter());
        characterPool.put(maya.getCharacter().getType(), maya.getCharacter());
        characterPool.put(yui.getCharacter().getType(), yui.getCharacter());
    }

    /**
     * Creates a CharacterPool from a list of instantiated npcs, such as when loading a save.
     * @param player The Player.
     * @param npcs The available NPCs.
     */
    public CharacterPool(Player player, Collection<NPC> npcs) {
        this(player, npcs, new HashSet<>());
    }

    private CharacterPool(Player player, Collection<NPC> npcs, Collection<CharacterType> debugNpcs) {
        human = player;
        characterPool = npcs.stream().collect(Collectors.toMap(NPC::getType, npc -> npc));
        debugChars = new HashSet<>(debugNpcs);
    }

    public Set<NPC> availableNpcs() {
        return characterPool.values().stream().filter(npc -> npc.available).collect(Collectors.toSet());
    }

    public Set<Character> everyone() {
        Set<Character> everyone = new HashSet<>(availableNpcs());
        everyone.add(human);
        return everyone;
    }

    public void newChallenger(NPC challenger) {
        newChallenger(challenger, human.getLevel());
    }

    public void newChallenger(NPC challenger, int targetLevel) {
        if (!availableNpcs().contains(challenger)) {
            challenger.available = true;
            if (challenger.has(Trait.leveldrainer)) {
                targetLevel -= 4;
            }
            challenger.addLevelsImmediate(null, targetLevel - challenger.getLevel());
        }
    }

    public NPC getNPC(String name) {
        for (Character c : allNPCs()) {
            if (c.getType().hasType(name)) {
                return (NPC) c;
            }
        }
        System.err.println("NPC \"" + name + "\" is not loaded.");
        return null;
    }

    public boolean characterTypeInGame(String type) {
        return availableNpcs().stream().anyMatch(c -> c.getType().hasType(type));
    }

    public Collection<NPC> allNPCs() {
        return characterPool.values();
    }

    public Character getParticipantByName(String name) {
        return availableNpcs().stream().filter(c -> c.getTrueName().equals(name)).findAny()
                        .orElseThrow(() -> new NoSuchElementException("Could not find particpant " + name));
    }

    public Character getCharacterByType(String typeName) {
        return getCharacterByType(CharacterType.get(typeName));
    }

    Character getCharacterByType(CharacterType type) {
        if (human.getType().equals(type)) {
            return human;
        }
        return getNPCByType(type);
    }

    public NPC getNPCByType(String typeName) {
        return getNPCByType(CharacterType.get(typeName));
    }

    private NPC getNPCByType(CharacterType type) {
        NPC results = characterPool.get(type);
        if (results == null) {
            System.err.println("failed to find NPC for type " + type);
        }
        return results;
    }

    public List<Character> getInAffectionOrder(List<Character> viableList) {
        List<Character> results = new ArrayList<>(viableList);
        results.sort(Comparator.comparingInt(a -> a.getAffection(getPlayer())));
        return results;
    }

    /**
     * WARNING DO NOT USE THIS IN ANY COMBAT RELATED CODE.
     * IT DOES NOT TAKE INTO ACCOUNT THAT THE PLAYER GETS CLONED. WARNING. WARNING.
     *
     * @return
     */
    public Player getPlayer() {
        return human;
    }

    public void updateNPCs(Set<NPC> npcs) {
        npcs.forEach(npc -> characterPool.put(npc.getType(), npc));
    }

    public void updatePlayer(Player player) {
        human = player;
    }

    public List<NPC> debugChars() {
        return debugChars.stream().map(this::getNPCByType).collect(Collectors.toList());
    }

    public static class CharacterNotFoundException extends RuntimeException {
        private static final long serialVersionUID = 4849211069716366301L;

        public CharacterNotFoundException(CharacterType type) {
            super("Character type " + type + " not found in character pool!");
        }
    }
}

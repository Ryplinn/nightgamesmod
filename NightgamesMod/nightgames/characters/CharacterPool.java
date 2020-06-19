package nightgames.characters;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import nightgames.Resources.ResourceLoader;
import nightgames.characters.custom.CustomNPC;
import nightgames.characters.custom.JsonSourceNPCDataLoader;
import nightgames.characters.custom.NPCData;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.json.JsonUtils;
import nightgames.pet.PetCharacter;
import nightgames.start.NPCConfiguration;
import nightgames.start.StartConfiguration;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * Tracks Characters in current game.
 */
public class CharacterPool {
    public Map<CharacterType, NPC> characterPool;   // All starting and unlockable characters
    private Set<CharacterType> debugChars;
    public Player human;
    private transient Combat activeCombat;
    private transient Combat origCombat;

    public CharacterPool() {
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
                    NPCConfiguration npcConfig = startConfig != null ?
                                    startConfig.findNpcConfig(CustomNPC.TYPE_PREFIX + data.getName()).orElse(null) :
                                    null;
                    CustomNPC customNPC = new CustomNPC(data, npcConfig, commonConfig);
                    characterPool.put(customNPC.getType(), customNPC);
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
        NPC cassie = new NPC(CharacterType.get("Cassie"), new Cassie(), startConfig);
        NPC angel = new NPC(CharacterType.get("Angel"), new Angel(), startConfig);
        NPC reyka = new NPC(CharacterType.get("Reyka"), new Reyka(), startConfig);
        NPC kat = new NPC(CharacterType.get("Kat"), new Kat(), startConfig);
        NPC mara = new NPC(CharacterType.get("Mara"), new Mara(), startConfig);
        NPC jewel = new NPC(CharacterType.get("Jewel"), new Jewel(), startConfig);
        NPC airi = new NPC(CharacterType.get("Airi"), new Airi(), startConfig);
        NPC eve = new NPC(CharacterType.get("Eve"), new Eve(), startConfig);
        NPC maya = new NPC(CharacterType.get("Maya"), new Maya(), startConfig);
        NPC yui = new NPC(CharacterType.get("Yui"), new Yui(), startConfig);
        characterPool.put(cassie.getType(), cassie);
        characterPool.put(angel.getType(), angel);
        characterPool.put(reyka.getType(), reyka);
        characterPool.put(kat.getType(), kat);
        characterPool.put(mara.getType(), mara);
        characterPool.put(jewel.getType(), jewel);
        characterPool.put(airi.getType(), airi);
        characterPool.put(eve.getType(), eve);
        characterPool.put(maya.getType(), maya);
        characterPool.put(yui.getType(), yui);
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
                        .orElseThrow(() -> new NoSuchElementException("Could not find participant " + name));
    }

    public Character getCharacterByType(String typeName) {
        return getCharacterByType(CharacterType.get(typeName), true);
    }

    Character getCharacterByType(CharacterType type, boolean required) {
        if (activeCombat != null) {
            Optional<PetCharacter> foundPet =
                            activeCombat.getOtherCombatants().stream().filter(pet -> pet.getType() == type).findFirst();
            if (foundPet.isPresent()) {
                return foundPet.get();
            }
        }
        if (human != null && human.getType().equals(type)) {
            return human;
        }
        return getNPCByType(type, required);
    }

    public NPC getNPCByType(String typeName) {
        return getNPCByType(CharacterType.get(typeName), true);
    }

    private NPC getNPCByType(CharacterType type, boolean required) {
        NPC results = characterPool.get(type);
        if (results == null && required) {
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
     * @return The human player character.
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

    private NPC getNPCByType(CharacterType type) {
        return getNPCByType(type, true);
    }

    public void putAll(Character... characters) {
        for (Character character : characters) {
            put(character);
        }
    }

    public void put(Character character) {
        if (character instanceof Player) {
            human = (Player) character;
        } else if (character instanceof NPC) {
            characterPool.put(character.getType(), (NPC) character);
        } else {
            String msg;
            if (character instanceof PetCharacter) {
                msg = "Pet characters should be added directly to active combat";
            } else {
                msg = "Attempted to add unknown character type to pool";
            }
            throw new UnsupportedOperationException(msg);
        }
    }

    public void combatStart(Combat activeCombat) {
        this.activeCombat = activeCombat;
    }

    public void combatEnd() {
        this.activeCombat = null;
    }

    public void combatSim(Combat simCombat) {
        this.origCombat = activeCombat;
        this.activeCombat = simCombat;
    }

    public void combatRestore() {
        this.activeCombat = origCombat;
        this.origCombat = null;
    }

    Character getCharacterByType(CharacterType type) {
        return getCharacterByType(type, true);
    }

    public void putAll(List<? extends Character> characters) {
        putAll(characters.toArray(new Character[] {}));
    }

    public String dump() {
        StringBuilder dump = new StringBuilder();
        dump.append("=================\n");
        dump.append("Characters in pool:\n");
        dump.append("Human: ").append(human).append("\n");
        dump.append("NPCs:\n");
        for (CharacterType npc : characterPool.keySet()) {
            dump.append("\t").append(npc).append("\n");
        }
        dump.append("Other combatants:\n");
        if (activeCombat == null || activeCombat.getOtherCombatants().size() == 0) {
            dump.append("none\n");
        } else {
            for (PetCharacter pet : activeCombat.getOtherCombatants()) {
                dump.append("\t").append(pet.getType()).append("\n");
            }
        }
        return dump.toString();
    }

    public static class CharacterNotFoundException extends RuntimeException {
        private static final long serialVersionUID = 4849211069716366301L;

        public CharacterNotFoundException(CharacterType type) {
            super("Character type " + type + " not found in character pool!");
        }
    }
}

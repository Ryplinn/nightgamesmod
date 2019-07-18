package nightgames.global;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import nightgames.characters.NPC;
import nightgames.characters.Personality;
import nightgames.characters.Player;
import nightgames.characters.custom.CustomNPC;
import nightgames.characters.custom.JsonSourceNPCDataLoader;
import nightgames.characters.custom.NPCData;
import nightgames.gui.GUI;
import nightgames.json.JsonUtils;

import java.io.FileNotFoundException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * SaveData specifies a schema for data that will be saved and loaded.
 */
public class SaveData {
    public final Set<NPC> npcs;
    public final Player player;
    public final Set<String> flags;
    public final Map<String, Float> counters;
    public final Time time;
    public final int date;
    public final int fontsize;
    public final double xpRate;
    public final double moneyRate;

    private enum JSONKey {
        PLAYERS("characters"), FLAGS("flags"), COUNTERS("counters"), TIME("time"), DATE("date"), FONTSIZE("fontsize");

        final String key;

        JSONKey(String key) {
            this.key = key;
        }
    }

    // If an NPC should be modified by a start config, but has not yet been unlocked, the save file will not reflect that.
    // TODO: resolve locked character discrepancy.
    SaveData(GameState gameState) {
        npcs = new HashSet<>(gameState.characterPool.characterPool.values());
        player = gameState.characterPool.human;
        flags = new HashSet<>(Flag.flags);
        counters = new HashMap<>(Flag.counters);
        time = Time.time;
        date = Time.date;
        fontsize = GUI.gui.fontsize;
        xpRate = gameState.xpRate;
        moneyRate = gameState.moneyRate;
    }

    SaveData(JsonObject rootJSON) throws SaveDataException {
        flags = new HashSet<>();
        counters = new HashMap<>();
        if (rootJSON.has("xpRate")) {
            xpRate = rootJSON.get("xpRate").getAsDouble();
        } else {
            xpRate = GameState.DEFAULT_XP_RATE;
        }
        if (rootJSON.has("moneyRate")) {
            moneyRate = rootJSON.get("moneyRate").getAsDouble();
        } else {
            moneyRate = GameState.DEFAULT_MONEY_RATE;
        }

        npcs = new HashSet<>();
        Player human = null;
        for (JsonElement element : rootJSON.getAsJsonArray(JSONKey.PLAYERS.key)) {
            JsonObject charJson = element.getAsJsonObject();
            String type = charJson.get("type").getAsString();
            if (type.equals(Player.class.getSimpleName())) {
                try {
                    human = new Player(charJson);
                } catch (Exception e) {
                    throw new SaveDataException(e);
                }
            } else if (type.startsWith(CustomNPC.TYPE_PREFIX)) {
                NPCData data;
                try {
                    data = JsonSourceNPCDataLoader.loadBaseData(charJson);
                    if (data == null) {
                        throw new FileNotFoundException("Could not find data file for custom NPC " + type);
                    }
                    npcs.add(new CustomNPC(data, charJson));
                } catch (FileNotFoundException | SaveFile.SaveFileException e) {
                    // TODO: warn the player in-game when NPCs are unable to be loaded.
                    e.printStackTrace();
                }
            } else {
                // this assumes that a character's type is the same as the name of their personality class
                // there's probably a better way to do this that remains extensible without excessive boilerplate
                // TODO: make this more robust vs class naming schemes
                try {
                    // find the right class
                    Class<?> personalityClass = Class.forName("nightgames.characters." + type);
                    Constructor ctor = personalityClass.getConstructor();
                    // make new instance
                    NPC npc = new NPC(type, (Personality) ctor.newInstance());
                    npc.load(charJson);
                    npcs.add(npc);
                } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
                    System.err.println("Could not create personality for " + type);
                    e.printStackTrace();
                }
            }
        }
        if (human == null) {
            throw new SaveDataException("Player data not found in save data");
        }
        player = human;

        JsonArray flagsJSON = rootJSON.getAsJsonArray(JSONKey.FLAGS.key);
        flagsJSON.forEach(element -> flags.add(element.getAsString()));

        JsonObject countersJSON = rootJSON.getAsJsonObject(JSONKey.COUNTERS.key);
        counters.putAll(JsonUtils.mapFromJson(countersJSON, String.class, Float.class));

        date = rootJSON.get(JSONKey.DATE.key).getAsInt();
        if (rootJSON.has(JSONKey.FONTSIZE.key)) {
            fontsize = rootJSON.get(JSONKey.FONTSIZE.key).getAsInt();
        } else {
            fontsize = 5;
        }
        
        time = Time.fromDesc(rootJSON.get(JSONKey.TIME.key).getAsString());
    }

    JsonObject toJson() {
        JsonObject rootJSON = new JsonObject();
        rootJSON.add("xpRate", new JsonPrimitive(xpRate));
        rootJSON.add("moneyRate", new JsonPrimitive(moneyRate));

        JsonArray characterJSON = new JsonArray();
        characterJSON.add(player.save());
        npcs.stream().map(NPC::save).forEach(characterJSON::add);
        rootJSON.add(JSONKey.PLAYERS.key, characterJSON);

        JsonArray flagJSON = new JsonArray();
        flags.forEach(flagJSON::add);
        rootJSON.add(JSONKey.FLAGS.key, flagJSON);

        JsonObject counterJSON = new JsonObject();
        counters.forEach(counterJSON::addProperty);
        rootJSON.add(JSONKey.COUNTERS.key, counterJSON);

        rootJSON.addProperty(JSONKey.TIME.key, time.desc);

        rootJSON.addProperty(JSONKey.DATE.key, date);
        rootJSON.addProperty(JSONKey.FONTSIZE.key, fontsize);

        return rootJSON;
    }

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        SaveData saveData = (SaveData) o;

        if (date != saveData.date)
            return false;
        if (fontsize != saveData.fontsize)
            return false;
        if (Double.compare(saveData.xpRate, xpRate) != 0)
            return false;
        if (Double.compare(saveData.moneyRate, moneyRate) != 0)
            return false;
        if (!npcs.equals(saveData.npcs))
            return false;
        if (!player.equals(saveData.player))
            return false;
        if (!flags.equals(saveData.flags))
            return false;
        if (!counters.equals(saveData.counters))
            return false;
        return time == saveData.time;
    }

    @Override public int hashCode() {
        int result;
        long temp;
        result = npcs.hashCode();
        result = 31 * result + player.hashCode();
        result = 31 * result + flags.hashCode();
        result = 31 * result + counters.hashCode();
        result = 31 * result + time.hashCode();
        result = 31 * result + date;
        result = 31 * result + fontsize;
        temp = Double.doubleToLongBits(xpRate);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(moneyRate);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override public String toString() {
        return "SaveData{" + "npcs=" + npcs + ", player=" + player + ", flags=" + flags + ", counters=" + counters
                        + ", time=" + time + ", date=" + date + ", fontsize=" + fontsize + ", xpRate=" + xpRate
                        + ", moneyRate=" + moneyRate + '}';
    }

    class SaveDataException extends Exception {
        private static final long serialVersionUID = 1024586601078477748L;

        SaveDataException(String message) {
            super(message);
        }

        SaveDataException(Throwable cause) {
            super(cause);
        }
    }
}

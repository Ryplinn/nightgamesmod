package nightgames.characters.custom;

import com.google.gson.JsonObject;
import nightgames.characters.*;
import nightgames.start.NPCConfiguration;

public class CustomNPC extends NPC {
    final NPCData data;
    private static final long serialVersionUID = -8169646189131720872L;

    public static final String TYPE_PREFIX = "CUSTOM_";

    public CustomNPC(NPCData data){
        this(data, null, null);
    }

    public CustomNPC(NPCData data, NPCConfiguration charConfig, NPCConfiguration commonConfig) {
        super(CharacterType.get(TYPE_PREFIX + data.getType()), data.getName(), 1,
                        new CustomPersonality(data));
        this.data = data;
        setupCharacter(charConfig, commonConfig);
        addLevelsImmediate(null, data.getStats().level);
    }

    public CustomNPC(NPCData data, JsonObject saveJson) {
        this(data);
        this.load(saveJson);
    }
    public String defaultImage() {
        return data.getDefaultPortraitName();
    }

    public NPCData getData() {
        return data;
    }
}

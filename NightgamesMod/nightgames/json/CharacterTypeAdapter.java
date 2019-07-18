package nightgames.json;

import com.google.gson.*;
import nightgames.characters.CharacterType;

import java.lang.reflect.Type;

/**
 * TODO: Write class-level documentation.
 */
public class CharacterTypeAdapter implements JsonDeserializer<CharacterType>, JsonSerializer<CharacterType> {
    @Override public CharacterType deserialize(JsonElement jsonElement, Type type,
                    JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return CharacterType.get(jsonElement.getAsString());
    }

    @Override
    public JsonElement serialize(CharacterType characterType, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(characterType.toString());
    }
}

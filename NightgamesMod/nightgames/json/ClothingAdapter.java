package nightgames.json;

import com.google.gson.*;
import nightgames.items.clothing.Clothing;
import nightgames.items.clothing.ClothingTable;

import java.lang.reflect.Type;

/**
 * Gson TypeAdapter for Clothing that serializes and deserializes based on the Clothing's ID.
 */
public class ClothingAdapter implements JsonSerializer<Clothing>, JsonDeserializer<Clothing> {

    @Override public Clothing deserialize(JsonElement jsonElement, Type type,
                    JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        String id = jsonElement.getAsString();
        return ClothingTable.getByID(id).orElseThrow(() -> new JsonParseException(
                        "Could not find clothing ID " + id + " during deserialization"));
    }

    @Override
    public JsonElement serialize(Clothing clothing, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(clothing.getID());
    }
}

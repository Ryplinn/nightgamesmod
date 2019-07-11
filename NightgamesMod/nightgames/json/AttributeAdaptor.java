package nightgames.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import nightgames.characters.Attribute;

import java.lang.reflect.Type;

/**
 * Gson adaptor that accounts for legacy Attribute member names.
 */
public class AttributeAdaptor implements JsonDeserializer<Attribute> {
    @Override public Attribute deserialize(JsonElement jsonElement, Type type,
                    JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return Attribute.fromLegacyName(jsonElement.getAsString());
    }
}

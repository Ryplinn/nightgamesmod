package nightgames.status;

import com.google.gson.JsonObject;

import nightgames.characters.Character;
import nightgames.characters.CharacterType;

public class TentacleBound extends Bound {
    private int stacks = 0;

    public TentacleBound(CharacterType affected, double dc, String source, int stacks) {
        super("Tentacle Bound", affected, dc, source, null);
        this.stacks = stacks;
        flag(Stsflag.tentacleBound);
    }

    @Override
    public Status instance(Character newAffected, Character opponent) {
        return new TentacleBound(newAffected.getType(), toughness, binding, getStacks());
    }

    @Override  public JsonObject saveToJson() {
        JsonObject obj = super.saveToJson();
        obj.addProperty("stacks", getStacks());
        return obj;
    }

    @Override public Status loadFromJson(JsonObject obj) {
        return new TentacleBound(null, obj.get("toughness").getAsFloat(), obj.get("binding").getAsString(), obj.get("stacks").getAsInt());
    }

    public int getStacks() {
        return stacks;
    }

    public void setStacks(int stacks) {
        this.stacks = stacks;
    }
}

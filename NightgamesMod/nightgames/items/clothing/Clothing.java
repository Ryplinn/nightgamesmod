package nightgames.items.clothing;

import nightgames.characters.Character;
import nightgames.characters.CharacterSex;
import nightgames.characters.trait.Trait;
import nightgames.items.Loot;

import java.text.DecimalFormat;
import java.util.Set;
import java.util.stream.Collectors;

public class Clothing implements Loot {
    public static final int N_LAYERS = 5;

    // TODO: Make as many of these fields final as possible. Shouldn't need to change clothing attributes under most circumstances.
    String name;
    int dc;
    String prefix;
    Set<ClothingTrait> attributes;
    // TODO: It's strange to have clothing know where it's sold, rather than stores knowing what they carry.
    Set<String> stores;
    Set<Trait> buffs;
    Set<ClothingSlot> slots;
    Set<CharacterSex> sex;
    int price;
    double exposure;
    final String id;
    double hotness;
    int layer;

    Clothing(String id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    public int dc(Character attacker) {
        if (attacker != null && attacker.has(Trait.dexterous) && layer <= 1 && slots.contains(ClothingSlot.top)) {
            return dc / 4;
        }
        if (attacker != null && attacker.has(Trait.dexterous) && layer <= 1 && slots.contains(ClothingSlot.bottom)) {
            return dc / 4;
        }
        return dc;
    }

    public int dc() {
        return dc(null);
    }

    @Override
    public String pre() {
        return prefix;
    }

    public boolean buffs(Trait test) {
        return buffs.contains(test);
    }

    public Set<Trait> buffs() {
        return buffs;
    }

    public Set<ClothingTrait> attributes() {
        return attributes;
    }

    @Override
    public int getPrice() {
        return price;
    }

    @Override
    public void pickup(Character owner) {
        if (!owner.has(this)) {
            owner.gain(this);
        }
    }

    public boolean is(ClothingTrait trait) {
        return attributes.contains(trait);
    }

    public double getExposure() {
        return exposure;
    }

    public int getLayer() {
        return layer;
    }

    public void setLayer(int layer) {
        this.layer = layer;
    }

    public Set<ClothingSlot> getSlots() {
        return slots;
    }

    public double getHotness() {
        return hotness;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public String getID() {
        return id;
    }

    public String getDesc() {
        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        DecimalFormat format = new DecimalFormat("#.##");
        sb.append(getName());
        if (!getSlots().isEmpty()) {
            sb.append("<br/>Slots: [");
            sb.append(slots.stream().map(ClothingSlot::name).collect(Collectors.joining(", ")));
            sb.append(']');
        }
        sb.append("<br/>Layer: ");
        sb.append(getLayer());
        sb.append("<br/>Appearance: ");
        sb.append(format.format(getHotness()));
        sb.append("<br/>Exposure: ");
        sb.append(format.format(getExposure()));
        if (!attributes().isEmpty()) {
            sb.append("<br/>Attributes: [");
            sb.append(attributes.stream().map(ClothingTrait::name).collect(Collectors.joining(", ")));
            sb.append(']');
        }
        if (!buffs().isEmpty()) {
            sb.append("<br/>Buffs: [");
            sb.append(buffs.stream().map(Trait::name).collect(Collectors.joining(", ")));
            sb.append(']');
        }
        sb.append("<br/>Price: ");
        sb.append(getPrice());
        sb.append("</html>");
        return sb.toString();
    }

    @Override public boolean equals(Object o) {
        return o instanceof Clothing && ((Clothing) o).id.equals(id);
    }

    @Override public int hashCode() {
        return id.hashCode();
    }
}

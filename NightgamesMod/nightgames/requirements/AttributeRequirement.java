package nightgames.requirements;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.combat.Combat;

/**
 * Returns true if character self has at least the given amount of the Attribute.
 */
public class AttributeRequirement extends BaseRequirement {
    private final Attribute att;
    private final int amount;

    public AttributeRequirement(Attribute att, int amount) {
        this.att = att;
        this.amount = amount;
    }

    public AttributeRequirement(String att, int amount) {
        this.att = Attribute.fromLegacyName(att);
        this.amount = amount;
    }

    @Override public boolean meets(Combat c, Character self, Character other) {
        return self.get(att) >= amount;
    }

    public Attribute getAtt() {
        return att;
    }

    public int getAmount() {
        return amount;
    }

}

package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.items.Item;
import nightgames.items.clothing.Clothing;
import nightgames.items.clothing.ClothingSlot;
import nightgames.nskills.tags.SkillTag;
import nightgames.status.Slimed;

public class Dissolve extends Skill {

    public Dissolve() {
        super("Dissolve");
        addTag(SkillTag.stripping);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return true;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return c.getStance().mobile(user) && user.canAct()
                        && (user.has(Item.DisSol) || user.get(Attribute.slime) > 0)
                        && target.outfit.getRandomShreddableSlot() != null && !c.getStance().prone(user);
    }

    public int accuracy(Combat c, Character user, Character target) {
        return user.get(Attribute.slime) > 0 || user.has(Item.Aersolizer) ? 200 : 80;
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        ClothingSlot toShred = null;
        if (!target.outfit.slotOpen(ClothingSlot.bottom) && target.outfit.slotShreddable(ClothingSlot.bottom)) {
            toShred = ClothingSlot.bottom;
        } else if (!target.outfit.slotOpen(ClothingSlot.top) && target.outfit.slotShreddable(ClothingSlot.top)) {
            toShred = ClothingSlot.top;
        }
        if (user.get(Attribute.slime) > 0) {
            Clothing destroyed = shred(target, toShred);
            String msg = "{self:SUBJECT-ACTION:reach|reaches} out with a slimy hand and"
                            + " {self:action:caress|caresses} {other:possessive} " + destroyed.getName()
                            + ". Slowly, it dissolves away beneath {self:possessive} touch.";
            c.write(user, Formatter.format(msg, user, target));
            if (user.has(Trait.VolatileSubstrate)) {
                target.add(c, new Slimed(target.getType(), user.getType(), Random.random(2, 4)));
            }
        } else {
            user.consume(Item.DisSol, 1);
            if (user.has(Item.Aersolizer)) {
                writeOutput(c, Result.special, user, target);
                shred(target, toShred);
            } else if (target.roll(user, accuracy(c, user, target))) {
                writeOutput(c, Result.normal, user, target);
                shred(target, toShred);
            } else {
                writeOutput(c, Result.miss, user, target);
                return false;
            }
        }
        return true;
    }

    private Clothing shred(Character target, ClothingSlot slot) {
        if (slot == null)
            return target.shredRandom();
        return target.shred(slot);
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.stripping;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.special) {
            return "You pop a Dissolving Solution into your Aerosolizer and spray " + target.getName()
                            + " with a cloud of mist. She emerges from the cloud with her clothes rapidly "
                            + "melting off her body.";
        } else if (modifier == Result.miss) {
            return "You throw a Dissolving Solution at " + target.getName()
                            + ", but she avoids most of it. Only a couple drops burn through her outfit.";
        } else {
            return "You throw a Dissolving Solution at " + target.getName() + ", which eats away her clothes.";
        }
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character attacker) {
        if (modifier == Result.special) {
            return String.format("%s inserts a bottle into the attachment on her arm. "
                            + "%s suddenly surrounded by a cloud of mist."
                            + " %s clothes begin to disintegrate immediately.",
                            user.subject(), Formatter.capitalizeFirstLetter(attacker.subjectAction("are", "is")),
                            Formatter.capitalizeFirstLetter(attacker.nameOrPossessivePronoun()));
        } else if (modifier == Result.miss) {
            return String.format("%s splashes a bottle of liquid in %s direction, but none of it hits %s.",
                            user.subject(), attacker.nameOrPossessivePronoun(), attacker.directObject());
        } else {
            return String.format("%s covers you with a clear liquid. %s clothes dissolve away, but it doesn't do anything to %s skin.",
                            user.subject(), Formatter.capitalizeFirstLetter(attacker.subject()), attacker.possessiveAdjective());
        }
    }

    @Override
    public String describe(Combat c, Character user) {
        if (user.get(Attribute.slime) > 0)
            return "Use your slime to dissolve your opponent's clothes";
        return "Throws dissolving solution to destroy opponent's clothes";
    }

}

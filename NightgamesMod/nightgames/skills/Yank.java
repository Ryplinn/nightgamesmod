package nightgames.skills;

import nightgames.characters.Character;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.items.clothing.ClothingSlot;
import nightgames.items.clothing.ClothingTrait;
import nightgames.nskills.tags.SkillTag;
import nightgames.stance.Stance;
import nightgames.status.Falling;

public class Yank extends Skill {

    public Yank() {
        super("Yank");
        addTag(SkillTag.usesToy);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.has(Trait.yank);
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return c.getStance().en  == Stance.neutral && (target.has(ClothingTrait.harpoonDildo)
                        || target.has(ClothingTrait.harpoonOnahole));
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Give a tug on your toy to trip your opponent.";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        int acc = 70;
        int removeChance = 50;
        if (user.has(Trait.intensesuction)) {
            acc += 20;
            removeChance /= 2;
        }
        if (target.roll(user, acc)) {
            c.write(user, Formatter.format("{self:SUBJECT-ACTION:yank|yanks} {other:name-do}"
                            + " forward by the toy still connecting them, and "
                            + " {other:pronoun-action} stumbles and falls.", user, target));
            target.add(c, new Falling(target.getType()));
            if (Random.random(100) < removeChance) {
                c.write("The powerful tug dislodges the toy, causing it to retract back where it was launched from.");
                target.outfit.unequip(target.outfit.getBottomOfSlot(ClothingSlot.bottom));
            }
            return true;
        } else {
            c.write(user, Formatter.format("{self:SUBJECT-ACTION:pull|pulls} {other:name-do}"
                            + " forward by the toy still connecting them, but "
                            + " {other:pronoun-action:keep|keeps} {other:possessive}"
                            + " balance.", user, target));
        }
        return false;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.positioning;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return null;
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return null;
    }

}

package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.NPC;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.skills.damage.DamageType;

public class Wait extends Skill {

    public Wait() {
        super("Wait");
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return user.canRespond();
    }

    @Override
    public int getMojoBuilt(Combat c, Character user) {
        if (channel(c, user)) {
            return 20 + user.getAttribute(Attribute.spellcasting) / 3;
        } else if (focused(c, user)) {
            return 20;
        } else {
            return 15;
        }
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        if (channel(c, user)) {
            writeOutput(c, Result.special, user, target);
            if (focused(c, user)) {
                user.heal(c, (int) DamageType.physical.modifyDamage(user, NPC.noneCharacter(), Random
                                .random(8, 16)));
                user.calm(c, Random.random(8, 14));
            } else {
                user.heal(c, (int) DamageType.physical.modifyDamage(user, NPC.noneCharacter(), Random
                                .random(4, 8)));
            }
        } else if (focused(c, user)) {
            writeOutput(c, Result.strong, user, target);
            user.heal(c, (int) DamageType.physical.modifyDamage(user, NPC.noneCharacter(), Random
                            .random(8, 16)));
            user.calm(c, Random.random(8, 14));
        } else {
            writeOutput(c, Result.normal, user, target);
            user.heal(c, (int) DamageType.physical.modifyDamage(user, NPC.noneCharacter(), Random
                            .random(4, 8)));
        }
        return true;
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return true;
    }

    @Override
    public int speed(Character user) {
        return 0;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        if (user != null) {
            // focus takes priority here
            if (focused(c, user)) {
                return Tactics.calming;
            } else if (channel(c, user)) {
                return Tactics.recovery;
            }
        }
        return Tactics.misc;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.special) {
            return "You revitalize yourself by channeling some of the natural energies around you.";
        } else if (modifier == Result.strong) {
            return "You take a moment to clear your thoughts, focusing your mind and calming your body.";
        } else {
            return "You bide your time, waiting to see what " + target.getName() + " will do.";
        }
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.special) {
            return Formatter.format("{self:SUBJECT} closes {self:possessive} eyes and takes a deep breath. "
                            + "You see a warm glow briefly surround {self:direct-object} before disappearing. "
                            + "When {self:pronoun} opens {self:possessive} eyes, {self:pronoun} looks reinvigorated.",
                            user, target);
        } else if (modifier == Result.strong) {
            return String.format("%s closes %s eyes and takes a deep breath. When %s opens %s eyes, "
                            + "%s seems more composed.", user.subject(), user.possessiveAdjective(),
                            user.pronoun(), user.possessiveAdjective(), user.pronoun());
        } else {
            return String.format("%s hesitates, watching %s closely.",
                            user.subject(), target.nameDirectObject());
        }
    }

    @Override
    public String getLabel(Combat c, Character user) {
        if (channel(c, user)) {
            return "Channel";
        } else if (focused(c, user)) {
            return "Focus";
        } else {
            return getName(c, user);
        }
    }

    @Override
    public String describe(Combat c, Character user) {
        if (channel(c, user)) {
            return "Focus and channel the natural energies around you";
        } else if (focused(c, user)) {
            return "Calm yourself and gain some mojo";
        } else {
            return "Do nothing";
        }
    }

    private boolean focused(Combat c, Character user) {
        return user.getAttribute(Attribute.cunning) >= 15 && !user.has(Trait.undisciplined) && user.canRespond() && !c.getStance().sub(user);
    }

    private boolean channel(Combat c, Character user) {
        return user.getAttribute(Attribute.spellcasting) >= 1 && user.canRespond() && !c.getStance().sub(user);
    }
}

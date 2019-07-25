package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Emotion;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.status.Enthralled;

public class Whisper extends Skill {

    public Whisper() {
        super("Whisper");
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return c.getStance().kiss(user, target) && user.canAct() && !user.has(Trait.direct);
    }

    @Override
    public float priorityMod(Combat c, Character user) {
        return user.has(Trait.darkpromises) ? .2f : 0;
    }

    @Override
    public int getMojoBuilt(Combat c, Character user) {
        return 10;
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        int roll = Random.centeredrandom(4, user.get(Attribute.darkness) / 5.0, 2);
        int m = 4 + Random.random(6);

        if (target.has(Trait.imagination)) {
            m += 4;
        }
        if (user.has(Trait.darkpromises)) {
            m += 3;
        }
        if (user.has(Trait.darkpromises) && roll == 4 && user.canSpend(15) && !target.wary()) {
            user.spendMojo(c, 15);
            writeOutput(c, Result.special, user, target);
            target.add(c, new Enthralled(target.getType(), user.getType(), 4));
        } else {
            writeOutput(c, Result.normal, user, target);
        }
        target.temptNoSource(c, user, m, this);
        target.emote(Emotion.horny, 30);
        return true;
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.get(Attribute.seduction) >= 32 && !user.has(Trait.direct);
    }

    @Override
    public int speed(Character user) {
        return 9;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.pleasure;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.special) {
            return "You whisper words of domination in " + target.getName()
                            + "'s ear, filling her with your darkness. The spirit in her eyes seems to dim as she submits to your will.";
        } else {
            return "You whisper sweet nothings in " + target.getName()
                            + "'s ear. Judging by her blush, it was fairly effective.";
        }
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.special) {
            return String.format("%s whispers in %s ear in some eldritch language."
                            + " %s words echo through %s head and %s %s a"
                            + " strong compulsion to do what %s tells %s.", user.subject(),
                            target.nameOrPossessivePronoun(), 
                            Formatter.capitalizeFirstLetter(user.possessiveAdjective()),
                                            target.possessiveAdjective(), target.pronoun(),
                                            target.action("feel"), user.subject(),
                                            target.directObject());
        } else {
            return String.format("%s whispers some deliciously seductive suggestions in %s ear.",
                            user.subject(), target.nameOrPossessivePronoun());
        }
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Arouse opponent by whispering in her ear";
    }
}

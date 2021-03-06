package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.body.BodyPart;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.nskills.tags.SkillTag;
import nightgames.stance.Jumped;
import nightgames.status.Falling;

public class ReverseCarry extends Carry {
    public ReverseCarry() {
        super("Jump");
        addTag(SkillTag.positioning);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.getAttribute(Attribute.power) >= 20 && user.hasPussy();
    }

    @Override
    public BodyPart getSelfOrgan(Character user) {
        return user.body.getRandomPussy();
    }

    @Override
    public BodyPart getTargetOrgan(Character target) {
        return target.body.getRandomCock();
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target, boolean rollSucceeded) {
        String premessage = premessage(c, user, target);

        if (rollSucceeded) {
            if (user.human()) {
                c.write(user, premessage + deal(c, premessage.length(), Result.normal, user, target));
            } else if (c.shouldPrintReceive(target, c)) {
                c.write(user, premessage + receive(c, premessage.length(), Result.normal, user, target));
            }
            int m = 5 + Random.random(5);
            int otherm = m;
            if (user.has(Trait.insertion)) {
                otherm += Math.min(user.getAttribute(Attribute.seduction) / 4, 40);
            }
            c.setStance(new Jumped(user.getType(), target.getType()), user, user.canMakeOwnDecision());
            target.body.pleasure(user, getSelfOrgan(user), getTargetOrgan(target), otherm, c, new SkillUsage<>(this, user, target));
            user.body.pleasure(target, getTargetOrgan(target), getSelfOrgan(user), m, c, new SkillUsage<>(this, user, target));
        } else {
            if (user.human()) {
                c.write(user, premessage + deal(c, premessage.length(), Result.miss, user, target));
            } else if (c.shouldPrintReceive(target, c)) {
                c.write(user, premessage + receive(c, premessage.length(), Result.miss, user, target));
            }
            user.add(c, new Falling(user.getType()));
            return false;
        }
        return true;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.miss) {
            return (damage > 0 ? "" : "You ") + "leap into " + target.possessiveAdjective()
                            + " arms, but she deposits you back onto the floor.";
        } else {
            return Formatter.format(
                            (damage > 0 ? "" : "You ")
                                            + " leap into {other:possessive} arms, impaling yourself onto her {other:body-part:cock} "
                                            + ". She lets out a noise that's equal parts surprise and delight as you bounce on her pole.",
                            user, target);
        }
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        String subject = (damage > 0 ? "" : user.subject() + " ");
        if (modifier == Result.miss) {
            return String.format("%sjumps onto %s, but %s %s %s back onto the floor.",
                            subject, target.nameDirectObject(), target.pronoun(),
                            target.action("deposit"), user.directObject());
        } else {
            return String.format("%sleaps into %s arms and impales %s on %s cock. "
                            + "%s wraps %s legs around %s torso and %s quickly %s %s so %s doesn't "
                            + "fall and injure %s or %s.", subject, target.nameOrPossessivePronoun(),
                            user.reflectivePronoun(), target.possessiveAdjective(),
                            user.subject(), user.possessiveAdjective(), target.nameOrPossessivePronoun(),
                            target.pronoun(), target.action("support"), user.pronoun(),
                            user.pronoun(),
                            user.reflectivePronoun(), target.directObject());
        }
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Jump into your opponent's arms and impale yourself on her cock: Mojo 10.";
    }

    @Override
    public boolean makesContact() {
        return true;
    }
    
    @Override
    public Stage getStage() {
        return Stage.FINISHER;
    }
}

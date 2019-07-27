package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.nskills.tags.SkillTag;
import nightgames.stance.Standing;
import nightgames.status.Falling;

public class Carry extends Fuck {
    public Carry(String name, int cooldown) {
        super(name, cooldown);
        addTag(SkillTag.pleasure);
        addTag(SkillTag.pleasureSelf);
        addTag(SkillTag.fucking);
        addTag(SkillTag.positioning);
    }

    public Carry() {
        this("Carry");
    }

    public Carry(String name) {
        this(name, 5);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.getAttribute(Attribute.power) >= 25 && !user.has(Trait.petite);
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return fuckable(c, user, target) && !target.wary() && getTargetOrgan(target).isReady(target) && user.canAct()
                        && c.getStance().mobile(user) && !c.getStance().prone(user)
                        && !c.getStance().prone(target) && c.getStance().facing(user, target) && user.getStamina().get() >= 15;
    }

    @Override
    public int getMojoCost(Combat c, Character user) {
        return 40;
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target, boolean rollSucceeded) {
        String premessage = premessage(c, user, target);
        if (rollSucceeded) {
            if (user.human()) {
                c.write(user, Formatter.capitalizeFirstLetter(
                                premessage + deal(c, premessage.length(), Result.normal, user, target)));
            } else if (c.shouldPrintReceive(target, c)) {
                c.write(user, premessage + receive(c, premessage.length(), Result.normal, user, target));
            }
            int m = 5 + Random.random(5);
            int otherm = m;
            if (user.has(Trait.insertion)) {
                otherm += Math.min(user.getAttribute(Attribute.seduction) / 4, 40);
            }
            c.setStance(new Standing(user.getType(), target.getType()), user, user.canMakeOwnDecision());
            target.body.pleasure(user, getSelfOrgan(user), getTargetOrgan(target), otherm, c, new SkillUsage<>(this, user, target));
            user.body.pleasure(target, getTargetOrgan(target), getSelfOrgan(user), m, c, new SkillUsage<>(this, user, target));
        } else {
            if (user.human()) {
                c.write(user, Formatter
                                .capitalizeFirstLetter(premessage + deal(c, premessage.length(), Result.miss, user,
                                                target)));
            } else if (c.shouldPrintReceive(target, c)) {
                c.write(user, premessage + receive(c, premessage.length(), Result.miss, user, target));
            }
            user.add(c, new Falling(user.getType()));
            return false;
        }
        return true;
    }

    @Override
    public int baseAccuracy(Combat c, Character user, Character target) {
        return 60;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.fucking;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.miss) {
            return "you pick up " + target.getName() + ", but she flips out of your arms and manages to trip you.";
        } else {
            return "you scoop up " + target.getName()
                            + ", lifting her into the air and simultaneously thrusting your dick into her hot depths. She lets out a noise that's "
                            + "equal parts surprise and delight as you bounce her on your pole.";
        }
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.miss) {
            return Formatter.format(
                            (damage > 0 ? "" : "{self:subject} ")
                                            + "picks {other:subject} up, but {other:pronoun-action:manage|manages} out of"
                                            + " {self:possessive} grip before {self:pronoun} can do anything. Moreover, "
                                            + "{other:pronoun-action:scramble|scrambles} to trip {self:direct-object} "
                                            + "while she's distracted.",
                            user, target);
        } else {
            return Formatter.format(
                            (damage > 0 ? "" : "{self:subject} ")
                                            + "scoops {other:subject} up in {self:possessive} powerful arms and simultaneously thrusts"
                                            + " {self:possessive} {self:body-part:cock} into {other:possessive} {other:body-part:pussy}.",
                            user, target);
        }
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Picks up opponent and penetrates her: Mojo 10.";
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

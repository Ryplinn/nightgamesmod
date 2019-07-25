package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Emotion;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Random;
import nightgames.nskills.tags.SkillTag;
import nightgames.stance.FlyingCarry;
import nightgames.status.Falling;

public class Fly extends Fuck {
    public Fly() {
        this("Fly");
    }

    public Fly(String name) {
        super(name, 5);
        addTag(SkillTag.positioning);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.body.get("wings").size() > 0 && user.get(Attribute.power) >= 15;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return fuckable(c, user, target) && !target.wary() && user.canAct() && c.getStance().mobile(user)
                        && !c.getStance().prone(user) && c.getStance().facing(user, target)
                        && user.getStamina().get() >= 15;
    }

    @Override
    public int getMojoCost(Combat c, Character user) {
        return 50;
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Take off and fuck your opponent's pussy in the air.";
    }

    @Override
    public int accuracy(Combat c, Character user, Character target) {
        return 65;
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        String premessage = premessage(c, user, target);

        Result result = target.roll(user, accuracy(c, user, target)) ? Result.normal : Result.miss;
        if (user.human()) {
            c.write(user, premessage + deal(c, premessage.length(), result, user, target));
        } else if (c.shouldPrintReceive(target, c)) {
            c.write(user, premessage + receive(c, premessage.length(), result, user, target));
        }
        if (result == Result.normal) {
            user.emote(Emotion.dominant, 50);
            user.emote(Emotion.horny, 30);
            target.emote(Emotion.desperate, 50);
            target.emote(Emotion.nervous, 75);
            int m = 5 + Random.random(5);
            int otherm = m;
            if (user.has(Trait.insertion)) {
                otherm += Math.min(user.get(Attribute.seduction) / 4, 40);
            }
            c.setStance(new FlyingCarry(user.getType(), target.getType()), user, user.canMakeOwnDecision());
            target.body.pleasure(user, getSelfOrgan(user), getTargetOrgan(target), otherm, c, new SkillUsage<>(this, user, target));
            user.body.pleasure(target, getTargetOrgan(target), getSelfOrgan(user), m, c, new SkillUsage<>(this, user, target));
        } else {
            user.add(c, new Falling(user.getType()));
        }
        return result != Result.miss;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.fucking;
    }

    @Override
    public String deal(Combat c, int amount, Result modifier, Character user, Character target) {
        if (modifier == Result.miss) {
            return (amount == 0 ? "You " : "") + "grab " + target.getName() + " tightly and try to take off. However "
                            + target.pronoun()
                            + " has other ideas. She knees your crotch as you approach and sends you sprawling to the ground.";
        } else {
            return (amount == 0 ? "You " : "") + "grab " + target.getName() + " tightly and take off, "
                            + (target.hasDick() && user.hasPussy()
                                            ? "inserting her dick into your hungry "
                                                            + user.body.getRandomPussy().describe(user) + "."
                                            : " holding her helpless in the air and thrusting deep into her wet "
                                                            + target.body.getRandomPussy().describe(user) + ".");
        }
    }

    @Override
    public String receive(Combat c, int amount, Result modifier, Character user, Character target) {
        String subject = amount == 0 ? target.subject() + " " : "";
        if (modifier == Result.miss) {
            return String.format("%slunges for %s with a hungry look in %s eyes. However %s other "
                            + "ideas. %s %s %s as %s approaches and %s %s sprawling to the floor.",
                            subject, target.nameDirectObject(), user.possessiveAdjective(),
                            target.subjectAction("have", "has"), target.pronoun(),
                            target.action("trip"), user.nameDirectObject(),
                            user.pronoun(), target.pronoun(), target.action("send"));
        } else {
            return String.format("%sleaps at %s, embracing %s tightly"
                            + ". %s then flaps %s %s hard and before %s it"
                            + " %s twenty feet in the sky held up by %s arms and legs."
                            + " Somehow, %s dick ended up inside of %s in the process and"
                            + " the rhythmic movements of %s flying arouse %s to no end.",
                            subject, target.nameDirectObject(), target.directObject(),
                            user.subject(), user.possessiveAdjective(),
                            user.body.getRandomWings().describe(user),
                            target.pronoun(), target.subjectAction("are", "is"),
                            user.nameOrPossessivePronoun(), target.nameOrPossessivePronoun(),
                            user.possessiveAdjective(), user.possessiveAdjective(),
                            target.directObject());
        }
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

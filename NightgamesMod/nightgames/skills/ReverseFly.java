package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Emotion;
import nightgames.characters.body.BodyPart;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.stance.FlyingCowgirl;
import nightgames.status.Falling;

public class ReverseFly extends Fly {
    public ReverseFly() {
        super("ReverseFly");
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Take off and fuck your opponent's cock in the air.";
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

        Result result = rollSucceeded ? Result.normal : Result.miss;
        if (user.human()) {
            c.write(user, premessage + deal(c, 0, result, user, target));
        } else if (c.shouldPrintReceive(target, c)) {
            c.write(user, premessage + receive(c, 0, result, user, target));
        }
        if (result == Result.normal) {
            user.emote(Emotion.dominant, 50);
            user.emote(Emotion.horny, 30);
            target.emote(Emotion.desperate, 50);
            target.emote(Emotion.nervous, 75);

            int m = 5 + Random.random(5);
            int otherm = m;
            if (user.has(Trait.insertion)) {
                otherm += Math.min(user.getAttribute(Attribute.seduction) / 4, 40);
            }
            c.setStance(new FlyingCowgirl(user.getType(), target.getType()), user, user.canMakeOwnDecision());
            target.body.pleasure(user, getSelfOrgan(user), getTargetOrgan(target), otherm, c, new SkillUsage<>(this, user, target));
            user.body.pleasure(target, getTargetOrgan(target), getSelfOrgan(user), m, c, new SkillUsage<>(this, user, target));
        } else {
            user.add(c, new Falling(user.getType()));
            return false;
        }
        return true;
    }

    @Override
    public String deal(Combat c, int amount, Result modifier, Character user, Character target) {
        if (modifier == Result.miss) {
            return "you grab " + target.getName() + " tightly and try to take off. However " + target.pronoun()
                            + " has other ideas. She knees your crotch as you approach and sends you sprawling to the ground.";
        } else {
            return "you grab " + target.getName() + " tightly and take off, " + "inserting his dick into your hungry "
                            + user.body.getRandomPussy().describe(user) + ".";
        }
    }

    @Override
    public String receive(Combat c, int amount, Result modifier, Character user, Character target) {
        if (modifier == Result.miss) {
            return String.format("%s lunges for %s with a hungry look in %s eyes. However, %s other ideas."
                            + " %s %s %s as %s approaches and send %s sprawling to the floor.",
                            user.subject(), target.nameDirectObject(), user.possessiveAdjective(),
                            target.subjectAction("have", "has"), Formatter.capitalizeFirstLetter(target.pronoun()),
                            target.action("trip"), user.directObject(), user.pronoun(),
                            user.directObject());
        } else {
            return String.format("Suddenly, %s leaps at %s, embracing %s tightly. %s then flaps %s %s"
                            + " hard and before %s %s it,"
                            + " %s twenty feet in the sky held up by %s arms and legs."
                            + " Somehow, %s dick ended up inside of %s in the process and"
                            + " the rhythmic movements of %s flying arouse %s to no end.",
                            user.subject(), target.nameDirectObject(), target.directObject(),
                            Formatter.capitalizeFirstLetter(user.pronoun()),
                            user.possessiveAdjective(), user.body.getRandomWings().describe(user),
                            target.pronoun(), target.action("know"), target.subjectAction("are", "is"),
                            user.possessiveAdjective(), target.nameOrPossessivePronoun(),
                            user.nameDirectObject(), user.possessiveAdjective(),
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

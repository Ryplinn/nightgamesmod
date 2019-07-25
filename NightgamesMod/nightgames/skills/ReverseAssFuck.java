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
import nightgames.items.Item;
import nightgames.nskills.tags.SkillTag;
import nightgames.stance.AnalCowgirl;
import nightgames.status.BodyFetish;
import nightgames.status.Oiled;
import nightgames.status.Stsflag;

public class ReverseAssFuck extends Fuck {
    public ReverseAssFuck() {
        super("Anal Ride", 0);
        addTag(SkillTag.anal);
    }

    @Override
    public float priorityMod(Combat c, Character user) {
        return ((user.getMood() == Emotion.dominant ? 1.0f : 0)
                        + (user.has(Trait.autonomousAss) ? 4.0f : 0)
                        + (user.has(Trait.oiledass) ? 2.0f : 0)
                        + (user.has(Trait.drainingass) ? 3.f : 0)
                        + (user.has(Trait.bewitchingbottom) ? 3.f : 0))
                        * (user.has(Trait.powerfulcheeks) ? 2.f : 1.f);
    }

    @Override
    public BodyPart getTargetOrgan(Character target) {
        return target.body.getRandomCock();
    }

    @Override
    public BodyPart getSelfOrgan(Character user) {
        return user.body.getRandom("ass");
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return fuckable(c, user, target) && c.getStance().mobile(user) && c.getStance().prone(target)
                        && !c.getStance().mobile(target) && user.canAct() && getTargetOrgan(target).isReady(target)
                        && (getSelfOrgan(user).isReady(user) || user.has(Item.Lubricant)
                                        || user.getArousal().percent() > 50 || user.has(Trait.alwaysready));
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        String premessage = premessage(c, user, target);
        if (!user.hasStatus(Stsflag.oiled) && user.getArousal().percent() > 50
                        || user.has(Trait.alwaysready)) {
            String fluids = user.hasDick() ? "copious pre-cum" : "own juices";
            if (premessage.isEmpty()) {
                premessage = "{self:subject-action:lube|lubes}";
            } else {
                premessage += "{self:action:lube|lubes}";
            }
            premessage += " up {self:possessive} ass with {self:possessive} " + fluids + ".";
            user.add(c, new Oiled(user.getType()));
        } else if (!user.hasStatus(Stsflag.oiled) && user.has(Item.Lubricant)) {
            if (premessage.isEmpty()) {
                premessage = "{self:subject-action:lube|lubes}";
            } else {
                premessage += "{self:action:lube|lubes}";
            }
            premessage += " up {self:possessive} ass.";
            user.add(c, new Oiled(user.getType()));
            user.consume(Item.Lubricant, 1);
        }
        c.write(user, Formatter.format(premessage, user, target));

        int m = Random.random(10, 15);
        writeOutput(c, Result.normal, user, target);

        int otherm = m;
        if (user.has(Trait.insertion)) {
            otherm += Math.min(user.getAttribute(Attribute.seduction) / 4, 40);
        }
        target.body.pleasure(user, getSelfOrgan(user), getTargetOrgan(target), otherm, c, new SkillUsage<>(this, user, target));
        user.body.pleasure(target, getTargetOrgan(target), getSelfOrgan(user), m, c, new SkillUsage<>(this, user, target));
        c.setStance(new AnalCowgirl(user.getType(), target.getType()), user, user.canMakeOwnDecision());
        user.emote(Emotion.dominant, 30);
        if (Random.random(100) < 5 + 2 * user.getAttribute(Attribute.fetishism) || user.has(Trait.bewitchingbottom)) {
            target.add(c, new BodyFetish(target.getType(), user.getType(), "ass", .25));
        }
        return true;
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.getAttribute(Attribute.seduction) >= 15;
    }

    @Override
    public int speed(Character user) {
        return 2;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.fucking;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return String.format(
                        "You make sure your %s is sufficiently lubricated and you push %s %s into your greedy hole.",
                        getSelfOrgan(user).describe(user), target.nameOrPossessivePronoun(),
                        getTargetOrgan(target).describe(target));
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return String.format("%s makes sure %s %s is sufficiently lubricated and pushes %s %s into %s greedy hole.",
                        user.getName(), user.possessiveAdjective(), getSelfOrgan(user).describe(user),
                        target.nameOrPossessivePronoun(),
                        getTargetOrgan(target).describe(target), user.possessiveAdjective());
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Fuck your opponent with your ass.";
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

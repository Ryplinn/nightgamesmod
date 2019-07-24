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
import nightgames.status.Bound;
import nightgames.status.Oiled;
import nightgames.status.Slimed;
import nightgames.status.Stsflag;

public class TentacleRape extends Skill {

    TentacleRape() {
        super("Tentacle Rape");
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return true;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return !target.wary() && !c.getStance().sub(user) && !c.getStance().prone(user)
                        && !c.getStance().prone(target) && user.canAct() && user.body.has("tentacles");
    }

    @Override
    public int getMojoCost(Combat c, Character user) {
        return 10;
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Violate your opponent with your tentacles.";
    }

    private BodyPart tentacles = null;

    @Override
    public int accuracy(Combat c, Character user, Character target) {
        return 90;
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        tentacles = user.body.getRandom("tentacles");
        if (target.roll(user, accuracy(c, user, target))) {
            if (target.mostlyNude()) {
                int m = 2 + Random.random(4);
                if (target.bound()) {
                    writeOutput(c, Result.special, user, target);
                    if (target.hasDick()) {
                        target.body.pleasure(user, tentacles, target.body.getRandom("cock"), m, c, new SkillUsage<>(this, user, target));
                        m = 2 + Random.random(4);
                    }
                    if (target.hasPussy()) {
                        target.body.pleasure(user, tentacles, target.body.getRandom("pussy"), m, c, new SkillUsage<>(this, user, target));
                        m = 2 + Random.random(4);
                    }
                    if (target.hasBreasts()) {
                        target.body.pleasure(user, tentacles, target.body.getRandom("breasts"), m, c, new SkillUsage<>(this, user, target));
                        m = 2 + Random.random(4);
                    }
                    if (target.body.has("ass")) {
                        target.body.pleasure(user, tentacles, target.body.getRandom("ass"), m, c, new SkillUsage<>(this, user, target));
                        target.emote(Emotion.horny, 10);
                    }
                } else {
                    writeOutput(c, Result.normal, user, target);
                    target.body.pleasure(user, tentacles, target.body.getRandom("skin"), m, c, new SkillUsage<>(this, user, target));
                }
                if (!target.is(Stsflag.oiled)) {
                    target.add(c, new Oiled(target.getType()));
                }
                target.emote(Emotion.horny, 20);
            } else {
                writeOutput(c, Result.weak, user, target);
            }
            target.add(c, new Bound(target.getType(), 30 + 2 * Math.sqrt(user.get(Attribute.fetishism) + user.get(Attribute.slime)), "tentacles"));
        } else {
            writeOutput(c, Result.miss, user, target);
            return false;
        }
        if (user.has(Trait.VolatileSubstrate) && user.has(Trait.slime)) {
            target.add(c, new Slimed(target.getType(), user.getType(), Random.random(2, 5)));
        }
        return true;
    }

    @Override
    public Skill copy(Character user) {
        return new TentacleRape();
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.pleasure;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.miss) {
            return "You use your " + tentacles.describe(user) + " to snare " + target.getName()
                            + ", but she nimbly dodges them.";
        } else if (modifier == Result.weak) {
            return "You use your " + tentacles.describe(user) + " to wrap around " + target.getName()
                            + "'s arms, holding her in place.";
        } else if (modifier == Result.normal) {
            return "You use your " + tentacles.describe(user) + " to wrap around " + target.getName()
                            + "'s naked body. They squirm against her and squirt slimy fluids on her body.";
        } else {
            return "You use your " + tentacles.describe(user) + " to toy with " + target.getName()
                            + "'s helpless form. The tentacles toy with her breasts and penetrate her genitals and ass.";
        }
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.miss) {
            return String.format("%s shoots %s %s forward at %s. %s barely able to avoid them.",
                            user.subject(), user.possessiveAdjective(),
                            tentacles.describe(user), target.nameDirectObject(),
                            Formatter.capitalizeFirstLetter(target.subjectAction("are", "is")));
        } else if (modifier == Result.weak) {
            return String.format("%s shoots %s %s forward at %s, entangling %s arms and legs.",
                            user.subject(), user.possessiveAdjective(), tentacles.describe(user),
                            target.nameDirectObject(), target.possessiveAdjective());
        } else if (modifier == Result.normal) {
            return String.format("%s shoots %s %s forward at %s, "
                            + "entangling %s arms and legs. The slimy appendages "
                            + "wriggle over %s body and coat %s in the slippery liquid.",
                            user.subject(), user.possessiveAdjective(), tentacles.describe(user),
                            target.nameDirectObject(), target.possessiveAdjective(),
                            target.nameOrPossessivePronoun(), target.directObject());
        } else {
            return String.format("%s %s cover %s helpless body, tease %s genitals, and probe %s ass.",
                            user.nameOrPossessivePronoun(), tentacles.describe(user),
                            target.nameOrPossessivePronoun(), target.possessiveAdjective(),
                            target.possessiveAdjective());
        }
    }

}

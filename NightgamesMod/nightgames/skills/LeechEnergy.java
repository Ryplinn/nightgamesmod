package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.nskills.tags.SkillTag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LeechEnergy extends Skill {

    LeechEnergy() {
        super("Leech Energy", 2);
        addTag(SkillTag.drain);
        addTag(SkillTag.staminaDamage);
        addTag(SkillTag.positioning);
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return user.canRespond() && user.body.has("tentacles");
    }

    @Override
    public int getMojoBuilt(Combat c, Character user) {
        return 0;
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        if (target.roll(user, accuracy(c, user, target))) {
            BodyPart part = null;
            BodyPart selfPart = user.body.getRandom("tentacles");
            List<String> targets =
                            new ArrayList<>(Arrays.asList("hands", "feet", "skin", "mouth", "cock", "pussy", "balls"));
            while (!targets.isEmpty()) {
                String type = targets.remove(Random.random(targets.size()));
                part = target.body.getRandom(type);
                if (part != null) {
                    break;
                }
            }
            if (part == null) {
                c.write(user, "<b>ERROR: Could not pick part in LeechEnergy!</b>");
                return false;
            }
            String partString = selfPart.describe(user);
            String partStringSingular = partString.substring(0, partString.length() - 1);
            if (part.isType("hands")) {
                c.write(user,
                                Formatter.format("{self:name-possessive} numerous " + selfPart.describe(user)
                                                + " latch onto {other:name-possessive} hands and swallow up {other:possessive} fingers. While the "
                                                + selfPart.describe(user)
                                                + " are lasciviously licking {other:possessive} digits, "
                                                + "{other:subject-action:start|starts} feeling weak as {other:possessive} energy is being drained.",
                                user, target));
            } else if (part.isType("feet")) {
                c.write(user,
                                Formatter.format("{self:name-possessive} numerous " + selfPart.describe(user)
                                                + " latch onto {other:name-possessive} legs and swallow up {other:possessive} feet. While the numerous bumps and ridges inside the "
                                                + selfPart.describe(user)
                                                + " are squeezing and pulling on {other:possessive} ankles, "
                                                + "{other:subject-action:start|starts} feeling weak as {other:possessive} energy is being drained through your toes.",
                                user, target));
            } else if (part.isType("skin")) {
                c.write(user,
                                Formatter.format("{self:name-possessive} numerous " + selfPart.describe(user)
                                                + " latch onto {other:name-possessive} body and coils around {other:possessive} waist. The numerous tips on the "
                                                + selfPart.describe(user)
                                                + " feel like tiny mouths nibbling on your skin as they suck the energy from {other:possessive} body.",
                                user, target));
            } else if (part.isType("mouth")) {
                c.write(user,
                                Formatter.format("A thick " + partStringSingular
                                                + " latches onto {other:name-possessive} mouth and violates {other:possessive} oral cavity. {other:NAME-POSSESSIVE} mouth feels as if the "
                                                + partStringSingular
                                                + " is deep kissing {other:direct-object} as {other:possessive} energy flows through the connection.",
                                user, target));
            } else if (part.isType("cock")) {
                c.write(user,
                                Formatter.format("A particularly thick " + partStringSingular
                                                + " latches onto {other:name-possessive} cock and swallows it whole. {other:SUBJECT-ACTION:gasp|gasps} in pleasure as the "
                                                + partStringSingular
                                                + "-pussy churns against {other:possessive} cock relentlessly, sucking out both precum and {other:possessive} precious energy.",
                                user, target));
            } else if (part.isType("balls")) {
                c.write(user,
                                Formatter.format("A particularly thick " + partStringSingular
                                                + " latches onto {other:name-possessive} balls and swallows it whole. {other:SUBJECT-ACTION:gasp|gasps} in pleasure as the "
                                                + partStringSingular
                                                + "-mouth sucks and chews on {other:possessive} balls relentlessly, sucking out what little fight {other:subject-action:have|has}.",
                                user, target));
            } else if (part.isType("pussy")) {
                c.write(user,
                                Formatter.format("A particularly thick " + partStringSingular
                                                + " latches onto {other:name-possessive} pussy and plunges inside. {other:SUBJECT-ACTION:gasp|gasps} in pleasure as the "
                                                + partStringSingular
                                                + "-cock thrusts in and out of {other:direct-object} relentlessly, draining {other:direct-object} of energy and replacing it with "
                                                + selfPart.getFluids(user) + ".", user, target));
            } else {
                c.write(user, "Wtf happened");
            }
            target.drain(c, user, 10 + Random.random(20), Character.MeterType.STAMINA, Character.MeterType.MOJO,
                            1.5f);
            target.body.pleasure(user, selfPart, part, 10 + Random.random(20), c, new SkillUsage<>(this, user, target));
        } else {
            writeOutput(c, Result.miss, user, target);
            return false;
        }
        return true;
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.get(Attribute.bio) >= 10;
    }

    @Override
    public Skill copy(Character user) {
        return new LeechEnergy();
    }

    @Override
    public int speed(Character user) {
        return 5;
    }

    @Override
    public int accuracy(Combat c, Character user, Character target) {
        return 80;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.pleasure;
    }

    @Override
    public String getLabel(Combat c, Character user) {
        if (user.get(Attribute.darkness) >= 1) {
            return "Drain energy";
        } else {
            return getName(c, user);
        }
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.miss) {
            BodyPart selfPart = user.body.getRandom("tentacles");
            return "You try to drain energy with your " + selfPart.describe(user) + ", but " + target.getName()
                            + " dodges out of the way.";
        }
        return "";
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        BodyPart selfPart = user.body.getRandom("tentacles");

        if (modifier == Result.miss) {
            return String.format("%s tries to drain energy with %s %s, but %s out of the way.",
                            user.subject(), user.possessiveAdjective(),
                            selfPart.describe(user), target.subjectAction("dodge"));
        }
        return "";
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Drains your opponent of energy with your tentacles";
    }

    @Override
    public boolean makesContact() {
        return true;
    }
}

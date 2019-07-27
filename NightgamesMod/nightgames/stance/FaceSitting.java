package nightgames.stance;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.characters.Emotion;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.skills.*;
import nightgames.skills.damage.DamageType;
import nightgames.status.Drained;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

public class FaceSitting extends Position {
    FaceSitting(CharacterType top, CharacterType bottom, Stance en) {
        super(top, bottom, en);
        facingType = FacingType.BEHIND;
    }
    public FaceSitting(CharacterType top, CharacterType bottom) {
        this(top, bottom, Stance.facesitting);
    }

    @Override
    public String describe(Combat c) {
        return Formatter.capitalizeFirstLetter(getTop().subjectAction("are", "is")) + " sitting on "
                        + getBottom().nameOrPossessivePronoun() + " face while holding " + getBottom().possessiveAdjective()
                        + " arms so " + getBottom().subject() + " cannot escape";
    }

    @Override
    public int pinDifficulty(Combat c, Character self) {
        return 7;
    }

    @Override
    public boolean mobile(Character c) {
        return c.getType() != bottom;
    }

    @Override
    public String image() {
        if (!getTop().useFemalePronouns()) {
            return "facesitting_m.jpg";
        }
        if (getTop().hasPussy() && getBottom().hasPussy()) {
            return "facesitting_ff.jpg";
        }
        return "facesitting.jpg";
    }

    @Override
    public boolean kiss(Character c, Character target) {
        return target.getType() == top && c.getType() != bottom;
    }

    @Override
    public boolean facing(Character c, Character target) {
        return c.getType() != bottom && target.getType() != bottom;
    }

    @Override
    public boolean dom(Character c) {
        return c.getType() == top;
    }

    @Override
    public boolean sub(Character c) {
        return c.getType() == bottom;
    }

    @Override
    public boolean reachTop(Character c) {
        return c.getType() != bottom;
    }

    @Override
    public boolean reachBottom(Character c) {
        return c.getType() != bottom;
    }

    @Override
    public boolean prone(Character c) {
        return c.getType() == bottom;
    }

    @Override
    public boolean feet(Character c, Character target) {
        return target.getType() == bottom;
    }

    @Override
    public boolean oral(Character c, Character target) {
        return (c.getType() == bottom && target.getType() == top) || (target.getType() == bottom && c.getType() != top);
    }

    @Override
    public boolean behind(Character c) {
        return c.getType() == top;
    }

    @Override
    public boolean inserted(Character c) {
        return false;
    }

    @Override
    public Optional<Position> insert(Combat c, Character pitcher, Character dom) {
        Character catcher = getPartner(c, pitcher);
        Character sub = getPartner(c, pitcher);
        if (pitcher.body.getRandomInsertable() == null || !catcher.hasPussy()) {
            // invalid
            return Optional.empty();
        }
        if (pitcher == dom && pitcher.getType() == top) {
            // guy is sitting on girl's face facing her feet, and is the
            // dominant one in the new stance
            return Optional.of(new UpsideDownMaledom(pitcher.getType(), catcher.getType()));
        }
        if (pitcher == sub && pitcher.getType() == top) {
            // guy is sitting on girl's face facing her feet, and is the
            // submissive one in the new stance
            return Optional.of(Cowgirl.similarInstance(catcher, pitcher));
        }
        if (pitcher == dom && pitcher.getType() == bottom) {
            // girl is sitting on guy's face facing his feet, and is the
            // submissive one in the new stance
            return Optional.of(new Doggy(pitcher.getType(), catcher.getType()));
        }
        if (pitcher == sub && pitcher.getType() == bottom) {
            // girl is sitting on guy's face facing his feet, and is the
            // dominant one in the new stance
            return Optional.of(new ReverseCowgirl(catcher.getType(), pitcher.getType()));
        }
        return Optional.empty();
    }

    @Override
    public void decay(Combat c) {
        time++;
        getBottom().weaken(c, (int) DamageType.stance.modifyDamage(getTop(), getBottom(), 5));
        getTop().emote(Emotion.dominant, 20);
        getTop().emote(Emotion.horny, 10);
        if (getTop().has(Trait.energydrain)) {
            c.write(getTop(), Formatter.format(
                            "{self:NAME-POSSESSIVE} body glows purple as {other:subject-action:feel|feels}"
                            + " {other:possessive} very spirit drained through %s connection.",
                            getTop(), getBottom(), c.bothPossessive(getBottom())));
            int m = Random.random(5) + 5;
            getBottom().drain(c, getTop(), (int) DamageType.drain.modifyDamage(getTop(), getBottom(), m), Character.MeterType.STAMINA);
        }
        if (getTop().has(Trait.drainingass)) {
            if (Random.random(3) == 0) {
                c.write(getTop(), Formatter.format("{self:name-possessive} ass seems to <i>inhale</i>, drawing"
                                + " great gouts of {other:name-possessive} strength from {other:possessive}"
                                + " body.", getTop(), getBottom()));
                getBottom().drain(c, getTop(), getTop().getLevel(), Character.MeterType.STAMINA);
                Drained.drain(c, getTop(), getBottom(), Attribute.power, 3, 10, true);
            } else {
                c.write(getTop(), Formatter.format("{other:SUBJECT-ACTION:feel} both {other:possessive} breath and energy being stolen by {self:NAME-POSSESSIVE} ass overlapping {other:POSSESSIVE} face."
                                + " .", getTop(), getBottom()));
                getBottom().drain(c, getTop(), getTop().getLevel()/2, Character.MeterType.STAMINA);
            }
        }
    }

    @Override
    public Collection<Skill> availSkills(Combat c, Character self) {
        if (self.getType() != bottom) {
            return Collections.emptySet();
        } else {
            Collection<Skill> avail = new HashSet<>();
            avail.add(new Cunnilingus());
            avail.add(new Anilingus());
            avail.add(new Blowjob());
            avail.add(new Escape());
            avail.add(new Struggle());
            avail.add(new Nothing());
            avail.add(new Wait());
            return avail;
        }
    }

    @Override
    public float priorityMod(Character self) {
        return getSubDomBonus(self, getTop().has(Trait.energydrain) ? 5.0f : 3.0f);
    }

    @Override
    public boolean faceAvailable(Character target) {
        return target.getType() == top;
    }

    @Override
    public double pheromoneMod(Character self) {
        if (self.getType() == top) {
            return 10;
        }
        return 2;
    }

    @Override
    public int dominance() {
        return 5;
    }
    @Override
    public int distance() {
        return 1;
    }

    public boolean isFaceSitting(Character self) {
        return self.getType() == top;
    }

    public boolean isFacesatOn(Character self) {
        return self.getType() == bottom;
    }

    @Override
    public void struggle(Combat c, Character struggler) {
        if (struggler.human()) {
            c.write(struggler, "You try to free yourself from " + getTop().getName()
                            + ", but she drops her ass over your face again, forcing you to service her.");
        } else if (c.shouldPrintReceive(getTop(), c)) {
            c.write(struggler, String.format("%s struggles against %s, but %s %s %s ass "
                            + "over %s face again, forcing %s to service %s.", struggler.subject(),
                            getTop().nameDirectObject(), getTop().pronoun(), getTop().action("drop"),
                            getTop().possessiveAdjective(), struggler.possessiveAdjective(),
                            struggler.directObject(), getTop().directObject()));
        }
        if (getTop().hasPussy() && !getTop().has(Trait.temptingass)) {
            new Cunnilingus().resolve(c, struggler, getTop(), true);
        } else {
            new Anilingus().resolve(c, struggler, getTop(), true);
        }
    }

    @Override
    public void escape(Combat c, Character escapee) {
        c.write(escapee, Formatter.format(
                        "{self:SUBJECT-ACTION:try} to escape {other:name-possessive} hold, but with"
                                        + " {other:direct-object} behind {self:direct-object} with {other:possessive} long legs wrapped around {self:possessive} waist securely, there is nothing {self:pronoun} can do.",
                        escapee, getTop()));
    }
}

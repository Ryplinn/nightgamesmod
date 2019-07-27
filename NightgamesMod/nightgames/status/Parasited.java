package nightgames.status;

import com.google.gson.JsonObject;
import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.characters.Emotion;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.gui.GUI;
import nightgames.skills.FootWorship;
import nightgames.skills.Masturbate;
import nightgames.stance.Engulfed;
import nightgames.stance.Kneeling;

public class Parasited extends Status {
    private CharacterType other;
    private double time;
    private int stage;

    public Parasited(CharacterType affected, CharacterType other) {
        super("Parasited", affected);
        this.other = other;
        this.stage = 0;
        this.time = 0;
        flag(Stsflag.parasited);
        flag(Stsflag.debuff);
        flag(Stsflag.purgable);
    }

    public Character getOther() {
        return other.fromPoolGuaranteed();
    }

    @Override
    public String initialMessage(Combat c, Status replacement) {
        return Formatter.format(
                        "{other:SUBJECT-ACTION:have|has} planted a part of {other:reflective} in {self:name-possessive} head!\n", getAffected(), getOther());
    }

    @Override
    public String describe(Combat c) {
        return String.format("%s a part of %s inside of %s head.", getAffected().subjectAction("have", "has"),
                        getOther().nameOrPossessivePronoun(), getAffected().possessiveAdjective());
    }

    @Override
    public float fitnessModifier() {
        return -40;
    }

    @Override
    public int mod(Attribute a) {
        return 0;
    }

    @Override
    public void tick(Combat c) {
        if (c == null) {
            return;
        }
        if (time >= 3) {
            if (stage < 3) {
                stage = 3;
                GUI.gui.message(c, getOther(),
                                Formatter.format("Suddenly, {self:pronoun-action:hear|hears} a disembodied but familiar voice. \"Testing... testing... Good, looks like it worked.\"",
                                getAffected(), getOther()));
                GUI.gui.message(c, getAffected(),
                                Formatter.format("{self:SUBJECT}... {self:action:seem|seems} to be hearing {other:name-possessive} voice inside {self:possessive} head. That's not good.",
                                getAffected(), getOther()));
                GUI.gui.message(c, getOther(),
                                Formatter.format("{other:NAME} gives {self:name-do} a satisfied smile and {other:possessive} disembodied voice echoes again inside {self:possessive} head, \"{self:NAME}, don't worry... I have connected myself with your brain... We will have so much fun together...\"",
                                getAffected(), getOther()));
            }
            switch(Random.random(8)) {
                case 0:
                    GUI.gui.message(c, getOther(),
                                    Formatter.format("\"...You will cum for me...\"",
                                    getAffected(), getOther()));
                    GUI.gui.message(c, getAffected(),
                                    Formatter.format("With absolutely no warning, {self:subject-action:feel|feels} an incredible orgasm rip through {self:possessive} body.",
                                    getAffected(), getOther()));
                    BodyPart part = Random.pickRandom(c.getStance().getPartsFor(c, getAffected(), getOther())).orElse(getAffected().body.getRandomGenital());
                    BodyPart otherPart = Random.pickRandom(c.getStance().getPartsFor(c, getOther(), getOther())).orElse(getOther().body.getRandom("skin"));
                    getAffected().doOrgasm(c, getOther(), part, otherPart);
                    break;
                case 1:
                    GUI.gui.message(c, getOther(),
                                    Formatter.format("\"...Give yourself to me...\"",
                                    getAffected(), getOther()));
                    GUI.gui.message(c, getAffected(),
                                    Formatter.format("With no input from {self:possessive} consciousness, {self:name-possessive} body mechanically walks up to {self:name-possessive} body and presses itself into {other:possessive} slime. While immobilized by {self:possessive} inability to send signals through {self:possessive} locomotive nerves, {self:name-possessive} body slowly sinks into {other:name-possessive} crystal blue body.",
                                    getAffected(), getOther()));
                    c.setStance(new Engulfed(other, affected));
                    getAffected().add(c, new Frenzied(affected, 2));
                    break;
                case 2:
                case 3:
                    GUI.gui.message(c, getOther(),
                                    Formatter.format("\"...You will please me...\"",
                                    getAffected(), getOther()));
                    GUI.gui.message(c, getAffected(),
                                    Formatter.format("{self:SUBJECT-ACTION:feel|feels} an immense need to service {self:NAME}!",
                                    getAffected(), getOther()));
                    c.getRandomWorshipSkill(getAffected(), getOther()).orElse(new FootWorship()).resolve(c,
                                    getAffected(), getOther(), true);
                    break;
                case 4:
                case 5:
                    if (!c.getStance().dom(getAffected()) && !c.getStance().prone(getAffected())) {
                        GUI.gui.message(c, getOther(),
                                        Formatter.format("\"...You will kneel for me...\"",
                                        getAffected(), getOther()));
                        c.setStance(new Kneeling(other, affected));
                        break;
                    }
                case 6:
                case 7:
                default:
                    GUI.gui.message(c, getOther(),
                                    Formatter.format("\"...You will pleasure yourself...\"",
                                    getAffected(), getOther()));
                    GUI.gui.message(c, getAffected(),
                                    Formatter.format("{self:name-possessive} hands involuntarily reach into {self:possessive} crotch and start masturbating!",
                                    getAffected(), getOther()));
                    (new Masturbate()).resolve(c, getAffected(), getOther(), true);
            }
        } else if (time >= 2) {
            if (stage < 2) {
                stage = 2;
                if (!c.shouldAutoresolve())
                    GUI.gui.message(c, getAffected(),
                                    Formatter.format("The parasite inside {self:subject} starts moving again. After a long journey, it has somehow reached inside {self:possessive} skull. Even though that part of {self:possessive} body should have no nerves, {self:pronoun-action:swear|swears} {self:pronoun} can feel its cold pseudopods integrating themselves with {self:possessive} brain.",
                                    getAffected(), getOther()));
            }
            if (!c.shouldAutoresolve())
                GUI.gui.message(c, getAffected(),
                                Formatter.format("{self:NAME-POSSESSIVE} thoughts slow down even further. It's becoming difficult to remember why {self:pronoun-action:are|is} even fighting in the first place.",
                                                getAffected(), getOther()));
            getAffected().loseWillpower(c, 2);
        } else if (time >= 1) {
            if (stage < 1) {
                stage = 1;
                if (!c.shouldAutoresolve())
                    GUI.gui.message(c, getAffected(),
                                    Formatter.format("The slimy parasite inside {self:name-possessive} starts moving again. {self:PRONOUN} can feel it crawling through {self:possessive} head.",
                                                    getAffected(), getOther()));
            }
            if (!c.shouldAutoresolve())
                GUI.gui.message(c, getAffected(),
                                Formatter.format("{self:NAME-POSSESSIVE} thoughts slow down. Somehow the parasite is sapping {self:possessive} will to fight.",
                                                getAffected(), getOther()));
            getAffected().loseWillpower(c, 1);
        } else {
            if (!c.shouldAutoresolve())
                GUI.gui.message(c, getAffected(), Formatter.format("A part of {other:name-possessive} slime is lodged inside {self:name-possessive} head. It doesn't feel too uncomfortable, but {self:pronoun-action:are|is} scared of the implications.",
                                getAffected(), getOther()));
            getAffected().emote(Emotion.desperate, 5);
            getAffected().emote(Emotion.nervous, 5);
        }

        time += .2;
    }

    @Override
    public int damage(Combat c, int x) {
        return 0;
    }

    @Override
    public double pleasure(Combat c, BodyPart withPart, BodyPart targetPart, double x) {
        return 0;
    }

    @Override
    public int weakened(Combat c, int x) {
        return 0;
    }

    @Override
    public int tempted(Combat c, int x) {
        return 0;
    }

    @Override
    public int evade() {
        return -5;
    }

    @Override
    public int escape(Character from) {
        return 0;
    }

    @Override
    public int gainmojo(int x) {
        return 0;
    }

    @Override
    public int spendmojo(int x) {
        return 0;
    }

    @Override
    public int counter() {
        return 0;
    }

    public String toString() {
        return "Parasited";
    }

    @Override
    public int value() {
        return 0;
    }

    @Override
    public Status instance(Character newAffected, Character newOther) {
        return new Parasited(newAffected.getType(), newOther.getType());
    }

     public JsonObject saveToJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", getClass().getSimpleName());
        return obj;
    }

    public Status loadFromJson(JsonObject obj) {
        return new Parasited(null, null);
    }

    @Override
    public int regen(Combat c) {
        return 0;
    }
}

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
import nightgames.skills.damage.DamageType;
import nightgames.skills.damage.Staleness;
import nightgames.stance.Stance;
import nightgames.status.DurationStatus;
import nightgames.status.Lovestruck;
import nightgames.status.Stsflag;

public class Kiss extends Skill {
    private static final String divineString = "Kiss of Baptism";
    private static final int divineCost = 30;

    public Kiss() {
        // kiss starts off strong, but becomes stale fast. It recovers pretty quickly too, but makes spamming it less effective
        super("Kiss", 0, Staleness.build().withDefault(1.0).withFloor(.20).withDecay(.50).withRecovery(.10));
        addTag(SkillTag.usesMouth);
        addTag(SkillTag.pleasure);
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return c.getStance().kiss(user, target) && user.canAct();
    }

    @Override
    public int getMojoBuilt(Combat c, Character user) {
        if (getLabel(c, user).equals(divineString)) {
            return 0;
        }
        return 10 + (user.has(Trait.romantic) ? 5 : 0);
    }

    @Override
    public int getMojoCost(Combat c, Character user) {
        if (getLabel(c, user).equals(divineString)) {
            return divineCost;
        }
        return 0;
    }

    @Override
    public int accuracy(Combat c, Character user, Character target) {
        int accuracy = c.getStance().en == Stance.neutral ? 70 : 100;
        if (user.has(Trait.romantic)) {
            accuracy += 20;
        }
        return accuracy;
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        int m = Random.random(6, 10);
        if (!target.roll(user, accuracy(c, user, target))) {
            writeOutput(c, Result.miss, user, target);
            return false;
        }
        boolean deep = getLabel(c, user).equals("Deep Kiss");
        if (user.has(Trait.romantic)) {
            m += 2;
            // if it's an advanced kiss.
            if (!getLabel(c, user).equals("Kiss")) {
                m += 2;
            }
        }
        Result res;
        if (user.get(Attribute.seduction) >= 9) {
            m += Random.random(4, 6);
            res = Result.normal;
        } else {
            res = Result.weak;
        }
        if (deep) {
            m += 2;
            res = Result.special;
        }
        if (user.has(Trait.experttongue)) {
            m += 2;
            res = Result.special;
        }
        if (user.has(Trait.soulsucker)) {
            res = Result.upgrade;
        }
        if (getLabel(c, user).equals(divineString)) {
            res = Result.divine;
            m += 12;
        }
        writeOutput(c, res, user, target);
        if (res == Result.upgrade) {
            target.drain(c, user, (int) DamageType.drain
                                .modifyDamage(user, target, target.getStamina().max() / 8), Character.MeterType.STAMINA);
            target.drain(c, user, (int) DamageType.drain.modifyDamage(user, target, 2), Character.MeterType.WILLPOWER, Character.MeterType.MOJO,
                            (float) 2);
        }
        if (res == Result.divine) {
            target.buildMojo(c, 50);
            target.heal(c, 100);
            target.loseWillpower(c, Random.random(3) + 2, false);
            target.add(c, new Lovestruck(target.getType(), user.getType(), 2));
            user.usedAttribute(Attribute.divinity, c, .5);
        }
        if (user.has(Trait.TenderKisses) && target.is(Stsflag.charmed) && Random.random(3) == 0) {
            DurationStatus charmed = (DurationStatus) target.getStatus(Stsflag.charmed);
            charmed.setDuration(charmed.getDuration() + Random.random(1, 2));
            c.write(user, Formatter.format("<b>The exquisite tenderness of {self:name-possessive} kisses"
                            + " reinforces the haze clouding {other:name-possessive} mind.</b>", user, target));
        }
        BodyPart selfMouth = user.body.getRandom("mouth");
        target.body.pleasure(user, selfMouth, target.body.getRandom("mouth"), m, c, new SkillUsage<>(this, user, target));
        int selfDamage = Math.max(1, m / 4);
        if (selfMouth.isErogenous()) {
            selfDamage = m / 2;
        }
        user.body.pleasure(target, target.body.getRandom("mouth"), selfMouth, selfDamage, c, new SkillUsage<>(this, user, target));
        return true;
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.get(Attribute.seduction) >= 3;
    }

    @Override
    public int speed(Character user) {
        return 6;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.pleasure;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.miss) {
            return "You pull " + target.getName()
                            + " in for a kiss, but " + target.pronoun() + " pushes your face away. Rude. (Maybe you should try pinning her down?)";
        }
        if (modifier == Result.divine) {
            return "You pull " + target.getName()
                            + " to you and kiss her passionately, sending your divine aura into her body though her mouth. "
                            + "You tangle your tongue around hers and probe the sensitive insides of her mouth while mirroring the action in the space of her soul, sending quakes of pleasure through her physical and astral body. "
                            + "As you finally break the kiss, she looks energized but desperate for more.";
        }
        if (modifier == Result.special) {
            return "You pull " + target.getName()
                            + " to you and kiss her passionately. You run your tongue over her lips until she opens them and immediately invade her mouth. "
                            + "You tangle your tongue around hers and probe the sensitive insides her mouth. As you finally break the kiss, she leans against you, looking kiss-drunk and needy.";
        }
        if (modifier == Result.upgrade) {
            return "You pull " + target.getName()
                            + " to you and kiss her passionately. You run your tongue over her lips until her opens them and immediately invade her mouth. "
                            + "You focus on her lifeforce inside her and draw it out through the kiss while overwhelming her defenses with heady pleasure. As you finally break the kiss, she leans against you, looking kiss-drunk and needy.";
        } else if (modifier == Result.weak) {
            return "You aggressively kiss " + target.getName()
                            + " on the lips. It catches her off guard for a moment, but she soon responds approvingly.";
        } else {
            switch (Random.random(4)) {
                case 0:
                    return "You pull " + target.getName()
                                    + " close and capture her lips. She returns the kiss enthusiastically and lets out a soft noise of approval when you "
                                    + "push your tongue into her mouth.";
                case 1:
                    return "You press your lips to " + target.getName()
                                    + "'s in a romantic kiss. You tease out her tongue and meet it with your own.";
                case 2:
                    return "You kiss " + target.getName()
                                    + " deeply, overwhelming her senses and swapping quite a bit of saliva.";
                default:
                    return "You steal a quick kiss from " + target.getName()
                                    + ", pulling back before she can respond. As she hesitates in confusion, you kiss her twice more, "
                                    + "lingering on the last to run your tongue over her lips.";
            }

        }
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.miss) {
            return user.subject()
                            + " pulls you in for a kiss, but you manage to push her face away.";
        }
        if (modifier == Result.divine) {
            return String.format("%s seductively pulls %s into a deep kiss. As first %s %s to match %s enthusiastic"
                            + " tongue with %s own, but %s starts using %s divine energy to directly attack %s soul. "
                            + "Golden waves of ecstasy flow through %s body, completely shattering every single thought %s and replacing them with %s.",
                            user.subject(), target.nameDirectObject(), target.pronoun(),
                            target.action("try", "tries"), user.possessiveAdjective(),
                            target.possessiveAdjective(), user.subject(), user.possessiveAdjective(),
                            target.nameOrPossessivePronoun(), target.possessiveAdjective(), 
                            target.subjectAction("hold"), user.reflectivePronoun());
        }
        if (modifier == Result.upgrade) {
            return String.format("%s seductively pulls %s into a deep kiss. As first %s %s to match %s "
                            + "enthusiastic tongue with %s own, but %s %s quickly overwhelmed. "
                            + "%s to feel weak as the kiss continues, and %s %s %s is "
                            + "draining %s; %s kiss is sapping %s will to fight through %s connection! "
                            + "%s to resist, but %s splendid tonguework prevents "
                            + "%s from mounting much of a defense.",
                            user.subject(), target.nameDirectObject(), target.subject(),
                            target.action("try", "tries"), user.possessiveAdjective(),
                            target.possessiveAdjective(), target.pronoun(), target.action("are", "is"),
                            Formatter.capitalizeFirstLetter(target.subjectAction("start")),
                            target.pronoun(), target.action("realize"), user.subject(),
                            target.directObject(), user.possessiveAdjective(),
                            target.nameOrPossessivePronoun(), c.bothPossessive(target), 
                            Formatter.capitalizeFirstLetter(target.subjectAction("try", "tries")),
                            user.nameOrPossessivePronoun(), target.directObject());
        }
        if (modifier == Result.special) {
            return String.format("%s seductively pulls %s into a deep kiss. As first %s %s to match %s "
                            + "enthusiastic tongue with %s own, but %s %s quickly overwhelmed. %s draws "
                            + "%s tongue into %s mouth and sucks on it in a way that seems to fill %s "
                            + "mind with a pleasant, but intoxicating fog.",
                            user.subject(), target.nameDirectObject(), target.pronoun(),
                            target.action("try", "tries"), user.possessiveAdjective(),
                            target.possessiveAdjective(), target.pronoun(), target.action("are", "is"),
                            user.subject(), target.nameOrPossessivePronoun(),
                            user.possessiveAdjective(), target.possessiveAdjective());
        } else if (modifier == Result.weak) {
            return String.format("%s presses %s lips against %s in a passionate, if not particularly skillful, kiss.",
                            user.subject(), user.possessiveAdjective(),
                            target.human() ? "yours" : target.nameOrPossessivePronoun());
        } else {
            switch (Random.random(3)) {
                case 0:
                    return String.format("%s grabs %s and kisses %s passionately on the mouth. "
                                    + "As %s for air, %s gently nibbles on %s bottom lip.",
                                    user.subject(), target.nameDirectObject(), target.directObject(),
                                    target.subjectAction("break"), user.subject(), target.possessiveAdjective());
                case 1:
                    return String.format("%s peppers quick little kisses around %s mouth before suddenly"
                                    + " taking %s lips forcefully and invading %s mouth with %s tongue.",
                                    user.subject(), target.nameOrPossessivePronoun(),
                                    target.possessiveAdjective(), target.possessiveAdjective(),
                                    user.possessiveAdjective());
                default:
                    return String.format("%s kisses %s softly and romantically, slowly drawing %s into %s "
                                    + "embrace. As %s part, %s teasingly brushes %s lips against %s.",
                                    user.subject(), target.nameDirectObject(), target.directObject(),
                                    user.possessiveAdjective(), c.bothSubject(target),
                                    user.subject(), target.possessiveAdjective(),
                                    target.human() ? "yours" : target.possessiveAdjective());
            }
        }
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Kiss your opponent";
    }

    @Override
    public boolean makesContact() {
        return true;
    }

    @Override
    public String getLabel(Combat c, Character user) {
        if (user.get(Attribute.divinity) >= 1 && user.canSpend(divineCost)) {
            return divineString;
        } else if (user.has(Trait.soulsucker)) {
            return "Drain Kiss";
        } else if (user.get(Attribute.seduction) >= 20) {
            return "Deep Kiss";
        } else {
            return "Kiss";
        }
    }
    
    @Override
    public Stage getStage() {
        return Stage.FOREPLAY;
    }
}

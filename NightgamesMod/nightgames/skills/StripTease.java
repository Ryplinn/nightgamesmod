package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Emotion;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Random;
import nightgames.nskills.tags.SkillTag;
import nightgames.status.Alluring;
import nightgames.status.Stsflag;

public class StripTease extends Skill {
    StripTease() {
        this("Strip Tease");
    }

    StripTease(String string) {
        super(string);
        addTag(SkillTag.undressing);
    }

    private static boolean hasRequirements(Character user) {
        return user.get(Attribute.seduction) >= 24 && !user.has(Trait.direct) && !user.has(Trait.shy)
                        && !user.has(Trait.temptress);
    }

    private static boolean isUsable(Combat c, Character self, Character target) {
        return self.stripDifficulty(target) == 0 && !self.has(Trait.strapped) && self.canAct() && c.getStance()
                                                                                                   .mobile(self)
                        && !self.mostlyNude() && !c.getStance()
                                                   .prone(self)
                        && c.getStance()
                            .front(self)
                        && (!self.breastsAvailable() || !self.crotchAvailable());
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return hasRequirements(user);
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return isUsable(c, user, target);
    }

    @Override
    public int getMojoBuilt(Combat c, Character user) {
        return 30;
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        if (user.human()) {
            c.write(user, deal(c, 0, Result.normal, user, target));
        } else if (c.shouldPrintReceive(target, c)) {
            if (target.human() && target.is(Stsflag.blinded))
                printBlinded(c, user);
            else
                c.write(user, receive(c, 0, Result.normal, user, target));
        }
        if (!target.is(Stsflag.blinded)) {
            int m = 15 + Random.random(5);
            target.temptNoSource(c, user, m, this);
            user.add(c, new Alluring(user.getType(), 5));
        }
        target.emote(Emotion.horny, 30);
        user.undress(c);
        user.emote(Emotion.confident, 15);
        user.emote(Emotion.dominant, 15);
        return true;
    }

    @Override
    public Skill copy(Character user) {
        return new StripTease();
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.pleasure;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return "During a brief respite in the fight as " + target.getName()
                        + " is catching her breath, you make a show of seductively removing your clothes. "
                        + "By the time you finish, she's staring with undisguised arousal, pressing a hand unconsciously against her groin.";
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return String.format("%s asks for a quick time out and starts sexily slipping %s own clothes off."
                        + " Although there are no time outs in the rules, %s can't help staring "
                        + "at the seductive display until %s finishes with a cute wiggle of %s naked ass.",
                        user.subject(), user.possessiveAdjective(), target.subject(),
                        user.pronoun(), user.possessiveAdjective());
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Tempt opponent by removing your clothes";
    }

}

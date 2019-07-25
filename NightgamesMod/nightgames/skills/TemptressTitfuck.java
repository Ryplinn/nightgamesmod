package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.body.BreastsPart;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Random;
import nightgames.nskills.tags.SkillTag;
import nightgames.status.BodyFetish;
import nightgames.status.FiredUp;
import nightgames.status.Stsflag;

public class TemptressTitfuck extends Paizuri {

    TemptressTitfuck() {
        super("Skillful Titfuck");
        addTag(SkillTag.usesBreasts);
        addTag(SkillTag.pleasure);
        addTag(SkillTag.oral);
    }

    @Override
    public float priorityMod(Combat c, Character user) {
        return super.priorityMod(c, user) + 1.5f;
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.has(Trait.temptress)&& user.getAttribute(Attribute.technique) >= 15;
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Use your supreme titfucking skills on your opponent's dick.";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        BreastsPart breasts = user.body.getLargestBreasts();
        for (int i = 0; i < 3; i++) {
            BreastsPart otherbreasts = user.body.getRandomBreasts();
            if (otherbreasts.getSize() > MIN_REQUIRED_BREAST_SIZE) {
                breasts = otherbreasts;
                break;
            }
        }
        

        int fetishChance = 7 + breasts.getSize() + user.getAttribute(Attribute.fetishism) / 2;

        int m = 7 + Random.random(user.getAttribute(Attribute.technique) / 2) + breasts.getSize();
        
        if(user.is(Stsflag.oiled)) {
            m += Random.random(2, 5);
        }
        
        if( user.has(Trait.lactating)) {
            m += Random.random(3, 5);
            fetishChance += 5;
        }

        if (target.roll(user, accuracy(c, user, target))) {
            if (!target.body.getRandomCock().isReady(target)) {
                m -= 7;
                target.body.pleasure(user, user.body.getRandom("breasts"), target.body.getRandomCock(), m, c, new SkillUsage<>(this, user, target));
                if (target.body.getRandomCock().isReady(target)) {
                    // Was flaccid, got hard
                    c.write(user, deal(c, 0, Result.special, user, target));
                    user.add(c, new FiredUp(user.getType(), target.getType(), "breasts"));
                    
                    target.body.pleasure(user, user.body.getRandom("breasts"), target.body.getRandom("cock"), m, c, new SkillUsage<>(this, user, target));
                    if (Random.random(100) < fetishChance) {
                        target.add(c, new BodyFetish(target.getType(), user.getType(), BreastsPart.a.getType(), .05 + (0.01 * breasts.getSize()) + user.getAttribute(Attribute.fetishism) * .01));
                    }
                } else {
                    // Was flaccid, still is
                    c.write(user, deal(c, 0, Result.weak, user, target));
                }
                
                
            } else {
                FiredUp status = (FiredUp) user.status.stream().filter(s -> s instanceof FiredUp).findAny()
                                .orElse(null);
                int stack = status == null || !status.getPart().equals("breasts") ? 0 : status.getStack();
                c.write(user, deal(c, stack, Result.normal, user, target));
                target.body.pleasure(user, user.body.getRandom("breasts"), target.body.getRandomCock(),
                                m + m * stack / 2, c, new SkillUsage<>(this, user, target));
                user.add(c, new FiredUp(user.getType(), target.getType(), "breasts"));
                
                if (Random.random(100) < fetishChance) {
                    target.add(c, new BodyFetish(target.getType(), user.getType(), BreastsPart.a.getType(), .05 + (0.01 * breasts.getSize()) + user.getAttribute(Attribute.fetishism) * .01));
                }
            }
        } else {
            c.write(user, deal(c, 0, Result.miss, user, target));
        }
        return true;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        switch (modifier) {
            case miss:
                return String.format("%s towards %s %s, but %s %s hips back.", user.subjectAction("move"),
                                target.nameOrPossessivePronoun(), target.body.getRandomCock().describe(target),
                                target.pronoun(), target.action("pull"));
            case weak:
                return String.format(
                                "%s %s up %s flaccid %s between %s %s, doing everything %s"
                                                + " can to get it hard, but %s %s back before %s can manage it.",
                                user.getName(),  user.subjectAction("wrap"), target.nameOrPossessivePronoun(), target.body.getRandomCock().describe(target),
                                user.pronoun(), user.body.getLargestBreasts().describe(user),
                                user.pronoun(), target.pronoun(), target.action("pull"), user.pronoun());
            case special:
                return String.format(
                                "%s %s %s %s between her %s and %s them with intense pressure. %s %s hardens"
                                                + " instantly, throbbing happily in it's new home.",
                                 user.pronoun(), user.subjectAction("trap"), target.possessivePronoun(),
                                target.body.getRandomCock().describe(target), user.body.getLargestBreasts().describe(user),
                                user.action("squeeze"), target.possessivePronoun(), target.body.getRandomCock().describe(target));
            default: // should be Result.normal
                switch (damage) {
                    case 0:
                        return String.format(
                                        "%s strokes %s %s with her %s in slow circular motions while"
                                                        + " lightly licking the tip, causing %s to groan in pleasure.",
                                        user.getName(), target.nameOrPossessivePronoun(),
                                        target.body.getRandomCock().describe(target), user.body.getLargestBreasts().fullDescribe(user), target.directObject());
                    case 1:
                        return String.format("%s tongue loops around the head of %s hard %s "
                                        + "and %s the shaft with her %s, constantly increasing  in intensity.",
                                        user.nameOrPossessivePronoun(),
                                        target.nameOrPossessivePronoun(), target.body.getRandomCock().describe(target),
                                        user.action("milk"), user.body.getLargestBreasts().fullDescribe(user));
                    default:
                        return String.format("As %s %s rapidly fuck %s %s, a pleasurable pressure constantly builds at the base. "
                                        + "All while %s %s the head sending bolts of electric pleasure back down %s shaft. "
                                        + "Overwhelmed from the pleasure, you grit %s teeth through a pleasure filled smile trying not to cum.",
                                        user.nameOrPossessivePronoun(), user.body.getLargestBreasts().describe(user),
                                        target.possessivePronoun(), target.body.getRandomCock().describe(target), user.getName(),
                                        user.action("suck"), target.possessivePronoun(), target.possessivePronoun());
                }
        }
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return deal(c, damage, modifier, user, target);
    }

}


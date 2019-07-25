package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.status.BodyFetish;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ImbueFetish extends Skill {

    private static final List<String> POSSIBLE_FETISHES =
                    Collections.unmodifiableList(Arrays.asList("pussy", "breasts", "feet", "ass", "cock"));

    private String chosenFetish;

    ImbueFetish() {
        super("Imbue Fetish", 3);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.getAttribute(Attribute.fetishism) >= 10;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return user.canAct() && c.getStance().mobile(user) && !c.getStance().prone(user)
                        && !c.getStance().inserted();
    }

    @Override
    public int getMojoCost(Combat c, Character user) {
        return 25;
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Bestow a random fetish on your opponent: 15 Mojo";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        Optional<String> fetish = Random.pickRandom(
                        POSSIBLE_FETISHES.stream().filter(part -> user.body.has(part)).toArray(String[]::new));
        if (!fetish.isPresent()) {
            return false;
        }
        chosenFetish = fetish.get();
        if (user.human()) {
            c.write(user, deal(c, 0, Result.normal, user, target));
        } else {
            c.write(user, receive(c, 0, Result.normal, user, target));
        }
        target.add(c, new BodyFetish(target.getType(), user.getType(), chosenFetish,
                        Random.randomdouble() * .2 + user.getAttribute(Attribute.fetishism) * .01));
        chosenFetish = null;
        return true;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.debuff;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return "You brandish one of your fetish needles - this one imbued with a " + chosenFetish
                        + " fetish - and stick it in " + target.getName()
                        + "'s arm. The needle is far too thin to cause any harm, but the "
                        + "mind-altering substance immediately takes effect.";
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return String.format("%s a tiny prick in %s arm, and when %s %s %s %s a small"
                        + " needle sticking out. %s the needle, but when %s %s back at %s"
                        + " - who has a maniacal look on %s face - %s %s an unnaturally "
                        + "strong attraction towards %s.",
                        target.subjectAction("feel"), target.possessiveAdjective(), target.pronoun(),
                        target.action("look"), target.pronoun(), target.action("see"),
                        Formatter.capitalizeFirstLetter(target.subjectAction("remove")),
                        target.pronoun(), target.action("look"), user.nameDirectObject(),
                        user.possessiveAdjective(), target.pronoun(), target.action("feel"),
                        chosenFetish);
    }

}

package nightgames.skills;

import nightgames.characters.Character;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.items.clothing.Clothing;
import nightgames.items.clothing.ClothingSlot;
import nightgames.nskills.tags.SkillTag;
import nightgames.stance.Behind;
import nightgames.status.Flatfooted;

public class Diversion extends Skill {

    public Diversion() {
        super("Diversion");
        addTag(SkillTag.undressing);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.has(Trait.misdirection);
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return !target.wary() && user.canAct() && c.getStance().mobile(user) && c.getStance().facing(user, target)
                        && !user.torsoNude() && !c.getStance().prone(user) && !c.getStance().inserted();
    }

    @Override
    public int getMojoBuilt(Combat c, Character user) {
        return 25;
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target, boolean rollSucceeded) {
        Clothing article = user.strip(ClothingSlot.top, c);
        if (article == null) {
            user.strip(ClothingSlot.bottom, c);
        }
        if (article != null) {
            if (user.human()) {
                c.write(user, "You quickly strip off your " + article.getName()
                                + " and throw it to the right, while you jump to the left. " + target.getName()
                                + " catches your discarded clothing, " + "losing sight of you in the process.");
            } else {
                c.write(user, Formatter.format("{other:SUBJECT-ACTION:lose} sight of {self:name-do} for just a moment, "
                                + "but then {other:pronoun-action:see} moving behind "
                                + "{other:reflective} in {other:possessive} peripheral vision. {other:SUBJECT} quickly {other:action:spin} "
                                + "around and {other:action:grab} {self:direct-object}, but {other:pronoun-action:find} {other:reflective} "
                                + "holding just {self:possessive} %s. Wait... what the fuck?", user, target,
                                article.getName()));
            }
            c.setStance(new Behind(user.getType(), target.getType()), user, true);
            target.add(c, new Flatfooted(target.getType(), 1));
            return true;
        } else {
            c.write(user, Formatter.format("{self:SUBJECT-ACTION:try} to divert {other:name-possessive} attention by stripping off {self:possessive} clothing, "
                            + "only to find out {self:pronoun-action:have} nothing left. ", user, target));
            return false;
        }
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.positioning;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        Clothing article = user.strip(modifier == Result.normal ? ClothingSlot.top : ClothingSlot.bottom, c);
        return "You quickly strip off your " + article.getName()
            + " and throw it to the right, while you jump to the left. " + target.getName()
            + " catches your discarded clothing, " + "losing sight of you in the process.";
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character attacker) {
        Clothing article = user.strip(modifier == Result.normal ? ClothingSlot.top : ClothingSlot.bottom, c);
        return String.format("%s sight of %s for just a moment, but then %s %s moving behind "
                        + "%s in %s peripheral vision. %s quickly %s around and grab %s, "
                        + "but you find yourself holding just %s %s. Wait... what the fuck?",
                        attacker.subjectAction("lose"), user.subject(), attacker.pronoun(),
                        attacker.action("see"),
                        user.directObject(), attacker.directObject(),
                        Formatter.capitalizeFirstLetter(attacker.subject()), attacker.action("spin"),
                        user.nameDirectObject(), user.possessiveAdjective(),
                        article.getName());
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Throws your clothes as a distraction";
    }

}

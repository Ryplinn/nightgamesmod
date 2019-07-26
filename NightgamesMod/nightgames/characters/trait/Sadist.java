package nightgames.characters.trait;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.global.Formatter;
import nightgames.requirements.Requirement;
import nightgames.skills.Skill;
import nightgames.skills.Tactics;
import nightgames.status.Masochistic;
import nightgames.status.Stsflag;

import java.util.Arrays;
import java.util.List;

import static nightgames.requirements.RequirementShortcuts.*;

public class Sadist extends BaseTrait {
    // Causes masochism status on strike and at end of turn. Also makes NPCs use violence more often.
    protected Sadist() {
        super("Sadist", "Skilled at providing pleasure alongside pain");
    }

    @Override List<Requirement> getRequirements() {
        return Arrays.asList(attribute(Attribute.fetishism, 25), level(25), trait(Trait.asshandler));
    }

    @Override public void onCausePain(Combat c, Character source, Character target) {
        if (!target.is(Stsflag.masochism)) {
            c.write("<br/>" + Formatter.capitalizeFirstLetter(Formatter.format(
                            "{self:name-possessive} blows {self:action:hit} all the right spots and {other:subject-action:awaken} to some masochistic tendencies.",
                            source, target)));
            target.add(c, new Masochistic(target.getType()));
        }
    }

    @Override public double skillWeightMod(Skill skill, Combat c, Character self) {
        return skill.type(c, self) == Tactics.damage ? 1.0 : 0;
    }

    @Override public String describe(Character self) {
        return Formatter.capitalizeFirstLetter(String.format("%s sneers in an unsettling way.", self.subject()));
    }

    @Override public void endOfTurn(Combat c, Character self, Character opponent) {
        if (!opponent.is(Stsflag.masochism)) {
            c.write("<br/>"+ Formatter.capitalizeFirstLetter(Formatter.format(
                            "{other:subject-action:seem} to shudder in arousal at the thought of pain.", self, opponent)));
            opponent.add(c, new Masochistic(opponent.getType()));
        }
    }
}

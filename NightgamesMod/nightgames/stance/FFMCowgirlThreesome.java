package nightgames.stance;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;
import nightgames.global.Formatter;
import nightgames.skills.Skill;
import nightgames.skills.Tactics;

public class FFMCowgirlThreesome extends FemdomSexStance {
    CharacterType domSexCharacter;

    public FFMCowgirlThreesome(CharacterType domSexCharacter, CharacterType top, CharacterType bottom) {
        super(top, bottom, Stance.reversecowgirl);
        this.domSexCharacter = domSexCharacter;
    }

    @Override
    public Character getDomSexCharacter() {
        return domSexCharacter.fromPoolGuaranteed();
    }

    @Override
    public void setOtherCombatants(List<? extends Character> others) {
        for (Character other : others) {
            if (other.getType().equals(domSexCharacter)) {
                domSexCharacter = other.getType();
            }
        }
    }

    @Override
    public List<BodyPart> partsForStanceOnly(Combat combat, Character self, Character other) {
        if (self == getDomSexCharacter() && other.getType() == bottom) {
            return topParts(combat);
        }
        return self.getType().equals(bottom) ? bottomParts() : Collections.emptyList();
    }

    public Character getPartner(Combat c, Character self) {
        Character domSex = getDomSexCharacter();
        if (self.getType() == top) {
            return getBottom();
        } else if (domSex == self) {
            return getBottom();
        } else {
            return domSex;
        }
    }

    @Override
    public String describe(Combat c) {
        if (getTop().human()) {
            return "";
        } else {
            return String.format("%s is holding %s down while %s fucking %s in the Cowgirl position.",
                            getTop().subject(), getBottom().nameDirectObject(), getDomSexCharacter().subjectAction("are", "is"), getBottom().directObject());
        }
    }

    @Override
    public boolean mobile(Character c) {
        return c.getType() != bottom;
    }

    @Override
    public String image() {
        if (getBottom().useFemalePronouns()) {
            return "ThreesomeFFMCowgirl_futa.jpg";
        } else {
            return "ThreesomeFFMCowgirl.jpg";
        }
    }

    @Override
    public boolean kiss(Character c, Character target) {
        return false;
    }

    @Override
    public boolean dom(Character c) {
        return c.getType() == top || c.getType() == domSexCharacter;
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
        return true;
    }

    @Override
    public boolean prone(Character c) {
        return c.getType() == bottom;
    }

    @Override
    public boolean behind(Character c) {
        return c.getType() == bottom;
    }

    @Override
    public Optional<Position> insertRandom(Combat c) {
        return new ReverseMount(top, bottom);
    }

    @Override
    public Position reverse(Combat c, boolean writeMessage) {
        if (writeMessage) {
            c.write(getBottom(), Formatter.format("{self:SUBJECT-ACTION:manage|manages} to unbalance {other:name-do} and push {other:direct-object} off {self:reflective}.", getBottom(), getDomSexCharacter()));
        }
        return new Neutral(bottom, top);
    }

    @Override
    public Optional<Position> checkOngoing(Combat c) {
        if (!c.otherCombatantsContains(getDomSexCharacter())) {
            c.write(getBottom(), Formatter.format(
                            "With the disappearance of {self:name-do}, {other:subject-action:manage|manages} to escape.",
                            getDomSexCharacter(), getBottom()));
            return Optional.of(new Neutral(top, bottom));
        }
        return null;
    }

    @Override
    public float priorityMod(Character self) {
        return super.priorityMod(self) + 3;
    }

    @Override
    public int dominance() {
        return 3;
    }

    @Override
    public Collection<Skill> availSkills(Combat c, Character self) {
        if (self.getType() != domSexCharacter) {
            return Collections.emptySet();
        } else {
            return self.getSkills().stream()
                            .filter(skill -> skill.requirements(c, self, getBottom()))
                            .filter(skill -> Skill.skillIsUsable(c, skill, getBottom()))
                            .filter(skill -> skill.type(c) == Tactics.fucking).collect(Collectors.toSet());
        }
    }
}

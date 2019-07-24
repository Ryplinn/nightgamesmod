package nightgames.stance;

import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;
import nightgames.global.Formatter;
import nightgames.skills.Skill;
import nightgames.skills.Tactics;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MFFMissionaryThreesome extends Threesome {
    public MFFMissionaryThreesome(CharacterType domSexCharacter, CharacterType top, CharacterType bottom) {
        super(domSexCharacter, top, bottom, Stance.missionary);
        this.domType = DomType.MALEDOM;
    }

    @Override
    public boolean inserted(Character c) {
        return c.getType() == domSexCharacter;
    }

    @Override
    public float priorityMod(Character self) {
        return super.priorityMod(self) + 3;
    }

    @Override
    public List<BodyPart> partsForStanceOnly(Combat combat, Character self, Character other) {
        if (self == getDomSexCharacter() && other.getType() == bottom) {
            return topParts();
        }
        return self.getType().equals(bottom) ? bottomParts() : Collections.emptyList();
    }

    @Override public Character getPartner(Combat c, Character self) {
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
            return String.format("%s is holding %s down while %s fucking %s in the missionary position.",
                            getTop().subject(), getBottom().nameDirectObject(), getDomSexCharacter().subjectAction("are", "is"), getBottom().directObject());
        }
    }

    @Override
    public boolean mobile(Character c) {
        return c.getType() != bottom;
    }

    @Override
    public String image() {
        return "ThreesomeMFFMissionary.jpg";
    }

    @Override
    public boolean kiss(Character c, Character target) {
        return true;
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
        return Optional.of(new Mount(top, bottom));
    }

    @Override
    public Position reverse(Combat c, boolean writeMessage) {
        if (writeMessage) {
            c.write(getBottom(), Formatter.format("{self:SUBJECT-ACTION:manage|manages} to unbalance {other:name-do} and push {other:direct-object} off {self:reflective}.", getBottom(), getTop()));
        }
        return new Neutral(bottom, top);
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
                            .filter(skill -> Skill.skillIsUsable(c, skill, self, getBottom()))
                            .filter(skill -> skill.type(c, self) == Tactics.fucking).collect(Collectors.toSet());
        }
    }

    @Override public List<Character> getAllPartners(Combat c, Character self) {
        // Only two people are fucking in this position; the other is just helping.
        return Collections.singletonList(getPartner(c, self));
    }
}

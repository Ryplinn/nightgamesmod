package nightgames.stance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import nightgames.characters.Character;
import nightgames.characters.Trait;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;
import nightgames.global.Global;
import nightgames.skills.Skill;
import nightgames.skills.Tactics;

public class XHFDaisyChainThreesome extends MaledomSexStance {
    protected Character domSexCharacter;

    public XHFDaisyChainThreesome(Character domSexCharacter, Character top, Character bottom) {
        super(top, bottom, Stance.doggy);
        this.domSexCharacter = domSexCharacter;
    }

    @Override
    public Character domSexCharacter(Combat c) {
        return domSexCharacter;
    }

    @Override
    public boolean inserted(Character c) {
        return c == domSexCharacter || c == top;
    }

    @Override
    public boolean canthrust(Combat c, Character self) {
        return domSexCharacter(c) == self || top == self || self.has(Trait.powerfulhips);
    }

    @Override
    public void checkOngoing(Combat c) {
        if (!c.getOtherCombatants().contains(domSexCharacter)) {
            c.write(bottom, Global.format("With the disappearance of {self:name-do}, {other:subject-action:manage|manages} to escape.", domSexCharacter, bottom));
            c.setStance(new Neutral(top, bottom));
        }
    }

    @Override
    public float priorityMod(Character self) {
        return super.priorityMod(self) + 3;
    }

    @Override
    public void setOtherCombatants(List<? extends Character> others) {
        for (Character other : others) {
            if (other.equals(domSexCharacter)) {
                domSexCharacter = other;
            }
        }
    }

    public List<BodyPart> partsFor(Combat combat, Character c) {
        if (c == domSexCharacter(combat)) {
            return topParts(combat);
        } else if (c == top) {
            return Arrays.asList(top.body.getRandomPussy()).stream().filter(part -> part != null && part.present())
                            .collect(Collectors.toList());
        }
        return c.equals(bottom) ? bottomParts() : Collections.emptyList();
    }

    @Override
    public List<BodyPart> bottomParts() {
        ArrayList<BodyPart> list = new ArrayList<>();
        list.add(bottom.body.getRandomPussy());
        list.add(bottom.body.getRandomCock());
        return list.stream().filter(part -> part != null && part.present())
                        .collect(Collectors.toList());
    }

    @Override
    public boolean vaginallyPenetratedBy(Combat c, Character self, Character other) {
        return (self == bottom && other == domSexCharacter) || (self == top && other == bottom);
    }

    @Override
    public Character getPenetratedCharacter(Combat c, Character self) {
        if (self == bottom) {
            return top;
        } else {
            return super.getPenetratedCharacter(c, self);
        }
    }

    public Character getPartner(Combat c, Character self) {
        Character domSex = domSexCharacter(c);
        if (self == top) {
            return bottom;
        } else if (domSex == self) {
            return bottom;
        } else {
            return domSex;
        }
    }

    @Override
    public String describe(Combat c) {
        if (top.human()) {
            return "";
        } else {
            return Global.format("{self:subject-action:are|is} fucking {other:name-do} from behind "
                            + "while {master:subject} is riding {other:possessive} dick, "
                            + "creating a {other:name}-sandwich.", domSexCharacter, bottom);
        }
    }

    @Override
    public boolean mobile(Character c) {
        return c != bottom;
    }

    @Override
    public String image() {
        return "ThreesomeHHFDaisyChain.jpg";
    }

    @Override
    public boolean kiss(Character c, Character target) {
        return c != bottom && c != domSexCharacter && c != top;
    }

    @Override
    public boolean dom(Character c) {
        return c == top || c == domSexCharacter;
    }

    @Override
    public boolean sub(Character c) {
        return c == bottom;
    }

    @Override
    public boolean reachTop(Character c) {
        return true;
    }

    @Override
    public boolean reachBottom(Character c) {
        return true;
    }

    @Override
    public boolean prone(Character c) {
        return c == bottom;
    }

    @Override
    public boolean behind(Character c) {
        return c == domSexCharacter;
    }

    @Override
    public Position insertRandom(Combat c) {
        return new Mount(top, bottom);
    }

    @Override
    public Position reverse(Combat c, boolean writeMessage) {
        if (writeMessage) {
            c.write(bottom, Global.format("{self:SUBJECT-ACTION:manage|manages} to unbalance {other:name-do} and push {other:direct-object} off {self:reflective}.", bottom, top));
        }
        return new Neutral(bottom, top);
    }
    
    @Override
    public int dominance() {
        return 4;
    }

    @Override
    public Collection<Skill> availSkills(Combat c, Character self) {
        if (self != domSexCharacter) {
            return Collections.emptySet();
        } else {
            Collection<Skill> avail = self.getSkills().stream()
                            .filter(skill -> skill.requirements(c, self, bottom))
                            .filter(skill -> Skill.skillIsUsable(c, skill, bottom))
                            .filter(skill -> skill.type(c) == Tactics.fucking).collect(Collectors.toSet());
            return avail;
        }
    }
}

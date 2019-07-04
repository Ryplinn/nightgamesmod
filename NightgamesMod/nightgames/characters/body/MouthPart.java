package nightgames.characters.body;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.skills.damage.DamageType;
import nightgames.status.*;
import nightgames.utilities.MathUtils;

public class MouthPart extends GenericBodyPart {
    /**
     *
     */
    public static final MouthPart generic = new MouthPart("mouth", 0, 1, 1);

    public MouthPart(String desc, String descLong, double hotness, double pleasure, double sensitivity, boolean notable,
                    String prefix) {
        super(desc, descLong, hotness, pleasure, sensitivity, notable, "mouth", prefix);
    }

    public MouthPart(String desc, double hotness, double pleasure, double sensitivity) {
        super(desc, hotness, pleasure, sensitivity, "mouth", "a ");
    }

    public MouthPart() {
        super(generic);
    }

    @Override
    public double applyBonuses(Character self, Character opponent, BodyPart target, double damage, Combat c) {
        double bonus = super.applyBonuses(self, opponent, target, damage, c);
        if (target.isErogenous() && opponent.has(Trait.lickable)) {
            c.write(opponent, Formatter.capitalizeFirstLetter(opponent.subjectAction("shudder", "shudders"))
                            + " when licked by " + self.directObject() + ".");
            bonus += Random.random(2, 4) + opponent.getLevel() / 20;
            if (target.isGenital()) {
                bonus += Random.random(2, 4) + Math.max(0, opponent.getLevel() / 20 - 2);
            }
        }
        String fluid = target.getFluids(opponent);
        if (!fluid.isEmpty() && opponent.has(Trait.lacedjuices)) {
            c.write(self, Formatter.capitalizeFirstLetter(opponent.nameOrPossessivePronoun()) + " drug-laced " + fluid
                            + " leaves " + self.nameOrPossessivePronoun() + " entire body tingling with arousal.");
            self.arouse(Math.max(opponent.getArousal().get() / 10, 5), c);
        }
        if (!fluid.isEmpty() && opponent.has(Trait.frenzyingjuices) && Random.random(5) == 0) {
            c.write(self, Formatter.capitalizeFirstLetter(opponent.nameOrPossessivePronoun()) + " madness-inducing "
                            + fluid + " leaves " + self.nameOrPossessivePronoun() + " in a state of frenzy.");
            self.add(c, new Frenzied(self, 3));
        }
        if (!fluid.isEmpty() && target.getFluidAddictiveness(opponent) > 0 && !self.is(Stsflag.tolerance)) {
            self.add(c, new FluidAddiction(self, opponent, target.getFluidAddictiveness(opponent), 5));
            FluidAddiction st = (FluidAddiction) self.getStatus(Stsflag.fluidaddiction);
            if (st == null) {
                // pass (addiction was resisted)
            } else if (st.activated()) {
                if (self.human()) {
                    c.write(self, Formatter.capitalizeFirstLetter(Formatter.format(
                                    "As {other:name-possessive} " + fluid
                                                    + " flow down your throat, your entire mind fogs up. "
                                                    + "You forget where you are, why you're here, and what you're doing. "
                                                    + "The only thing left in you is a primal need to obtain more of {other:possessive} fluids.",
                                    self, opponent)));
                } else {
                    c.write(self, Formatter.capitalizeFirstLetter(Formatter.format(
                                    "As your " + fluid
                                                    + " slides down {self:name-possessive} throat, you see a shadow pass over {self:possessive} face. "
                                                    + "Whereas {self:name} was playfully teasing you just a few seconds ago, you can now only see a desperate need that {self:pronoun} did not possess before.",
                                    self, opponent)));
                }
            } else if (!st.isActive()) {
                if (self.human()) {
                    c.write(self, Formatter.capitalizeFirstLetter(
                                    Formatter.format("You feel a strange desire to drink down more of {other:name-possessive} "
                                                    + fluid + ".", self, opponent)));
                } else {
                    c.write(self, Formatter.capitalizeFirstLetter(
                                    Formatter.format("{self:name} drinks down your " + fluid + " and seems to want more.",
                                                    self, opponent)));
                }
            }
        }
        if (self.has(Trait.experttongue)) {
            if (Random.random(6) == 0 && !opponent.wary() && damage > 5) {
                if (!self.human()) {
                    c.write(opponent, "<br/>Your mind falls into a pink colored fog from the tongue lashing.");
                } else {
                    c.write(opponent, "<br/>" + opponent.getName()
                                    + "'s mind falls into a pink colored fog from the tongue lashing.");
                }
                opponent.add(c, new Trance(opponent));
            }
            bonus += Random.random(3) + MathUtils.clamp(self.get(Attribute.Seduction) / 3, 10, 30)
                            * self.getArousal().percent() / 100.0;
        }
        if (self.has(Trait.sweetlips) && c.getStance().sub(self)) {
            c.write(opponent, Formatter.format("<br/>{self:name-possessive} enticing lips turns {other:direct-object} on as {other:subject-action:force|forces} {other:reflective} into them.",
                            self, opponent));
            opponent.temptNoSkill(c, self, this, (int) DamageType.temptation.modifyDamage(self, opponent, damage));
        }
        if (self.has(Trait.catstongue)) {
            c.write(opponent, Formatter.format("<br/>{self:name-possessive} abrasive tongue produces an unique sensation.",
                            self, opponent));

            bonus += Random.random(3) + 4;
            opponent.pain(c, opponent, 8 + Random.random(10), false, true);
        }
        if (self.has(Trait.Corrupting)) {
            opponent.add(c, new PartiallyCorrupted(opponent, self));
        }
        if (self.has(Trait.soulsucker) && target.isGenital()) {
            if (!self.human()) {
                c.write(opponent,
                                "<br/>You feel faint as her lips touch you, as if your will to fight is being sucked out through your "
                                                + target.describe(opponent) + " into her mouth.");
            } else {
                c.write(opponent,
                                "<br/>As your lips touch " + opponent.getName()
                                                + ", you instinctively draw in her spirit, forcing her energy through "
                                                + target.describe(opponent) + " into your mouth.");
            }
            bonus += Random.random(3) + 2;
            opponent.drainWillpowerAsMojo(c, self, (int) DamageType.drain.modifyDamage(self, opponent, 2), 2);
        }
        return bonus;
    }

    @Override
    public double getPleasure(Character self, BodyPart target) {
        double pleasureMod = super.getPleasure(self, target);
        pleasureMod += self.has(Trait.tongueTraining1) ? .5 : 0;
        pleasureMod += self.has(Trait.tongueTraining2) ? .7 : 0;
        pleasureMod += self.has(Trait.tongueTraining3) ? .9 : 0;
        return pleasureMod;
    }

    @Override
    public String getFluids(Character c) {
        if (super.getFluids(c).isEmpty()) {
            return "saliva";
        } else {
            return super.getFluids(c);
        }
    }

    @Override
    public boolean isVisible(Character c) {
        return true;
    }

    @Override
    public String adjective() {
        return "oral";
    }
}

package nightgames.characters.body;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.body.mods.*;
import nightgames.combat.Combat;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.pet.PetCharacter;
import nightgames.skills.damage.DamageType;
import nightgames.status.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class CockMod extends PartMod {
    public static final CockMod error = new CockMod("error", 1.0, 1.0, 1.0);
    public static final CockMod slimy = new CockMod("slimy", .5, 1.5, .7);
    public static final CockMod runic= new CockMod("runic", 2.0, 1.0, 1.0);
    public static final CockMod blessed = new CockMod("blessed", 1.0, 1.0, .75);
    public static final CockMod incubus= new CockMod("incubus", 1.25, 1.3, .9);
    public static final CockMod primal = new CockMod("primal", 1.0, 1.4, 1.2);
    public static final CockMod bionic = new CockMod("bionic", .8, 1.3, .5);
    public static final CockMod enlightened = new CockMod("enlightened", 1.0, 1.2, .8);
    public static final List<CockMod> ALL_MODS = Arrays.asList(slimy, runic, blessed, incubus, primal, bionic, enlightened);

    private CockMod(String name, double hotness, double pleasure, double sensitivity) {
        super(name, hotness, pleasure, sensitivity, 0);
    }

    // public constructor for adapter
    public CockMod() {
        super("error", 0, 1, 1, 0);
    }

    @Override
    public void loadData(JsonElement element) {
        Optional<CockMod> other = getFromType(element.getAsString());
        other.ifPresent(otherMod -> {
            this.modType = otherMod.modType;
            this.pleasure = otherMod.pleasure;
            this.hotness = otherMod.hotness;
            this.sensitivity = otherMod.sensitivity;
        });
    }

    public JsonElement saveData() {
        return new JsonPrimitive(getModType());
    }

    @Override
    public double applyBonuses(Combat c, Character self, Character opponent, BodyPart part, BodyPart target, double damage) {
        double bonus = super.applyBonuses(c, self, opponent, part, target, damage);
        if (this.equals(blessed) && target.isType("cock")) {
            if (self.getStatus(Stsflag.divinecharge) != null) {
                c.write(self, Formatter.format(
                                "{self:NAME-POSSESSIVE} concentrated divine energy in {self:possessive} cock rams into {other:name-possessive} pussy, sending unimaginable pleasure directly into {other:possessive} soul.",
                                self, opponent));
            }
            // no need for any effects, the bonus is in the pleasure mod
        }
        if (this.equals(runic)) {
            String message = "";
            if (target.moddedPartCountsAs(opponent, DemonicMod.INSTANCE)) {
                message += String.format(
                                "The fae energies inside %s %s radiate outward and into %s, causing %s %s to grow much more sensitive.",
                                self.nameOrPossessivePronoun(), part.describe(self), opponent.nameOrPossessivePronoun(),
                                opponent.possessiveAdjective(), target.describe(opponent));
                bonus += damage * 0.5; // +50% damage
            }
            if (Random.random(8) == 0 && !opponent.wary()) {
                message += String.format("Power radiates out from %s %s, seeping into %s and subverting %s will. ",
                                self.nameOrPossessivePronoun(), part.describe(self), opponent.nameOrPossessivePronoun(),
                                opponent.directObject());
                opponent.add(c, new Enthralled(opponent.getType(), self.getType(), 3));
            }
            if (self.hasStatus(Stsflag.cockbound)) {
                String binding = ((CockBound) self.getStatus(Stsflag.cockbound)).binding;
                message += String.format(
                                "With the merest of thoughts, %s %s out a pulse of energy from %s %s, freeing it from %s %s. ",
                                self.subject(), self.human() ? "send" : "sends", self.possessiveAdjective(),
                                part.describe(self), opponent.nameOrPossessivePronoun(), binding);
                self.removeStatus(Stsflag.cockbound);
            }
            c.write(self, message);
        } else if (this.equals(incubus)) {
            String message = String.format("%s demonic appendage latches onto %s will, trying to draw it into %s.",
                            self.nameOrPossessivePronoun(), opponent.nameOrPossessivePronoun(),
                            self.reflectivePronoun());
            int amtDrained;
            if (target.moddedPartCountsAs(opponent, FeralMod.INSTANCE)) {
                message += String.format(" %s %s gladly gives it up, eager for more pleasure.",
                                opponent.possessiveAdjective(), target.describe(opponent));
                amtDrained = 5;
                bonus += 2;
            } else if (target.moddedPartCountsAs(opponent, CyberneticMod.INSTANCE)) {
                message += String.format(
                                " %s %s does not oblige, instead sending a pulse of electricity through %s %s and up %s spine",
                                opponent.nameOrPossessivePronoun(), target.describe(opponent),
                                self.nameOrPossessivePronoun(), part.describe(self), self.possessiveAdjective());
                self.pain(c, opponent, Random.random(9) + 4);
                amtDrained = 0;
            } else {
                message += String.format(" Despite %s best efforts, some of the elusive energy passes into %s.",
                                opponent.nameOrPossessivePronoun(), self.nameDirectObject());
                amtDrained = 3;
            }
            int strength = (int) DamageType.drain.modifyDamage(self, opponent, amtDrained);
        	if (amtDrained != 0) {
        		if (self.isPet()) {
	                Character master = ((PetCharacter) self).getSelf().owner();
	                c.write(self, Formatter.format("The stolen strength seems to flow through to {self:possessive} {other:master} through {self:possessive} infernal connection.", self, master));
                    opponent.drain(c, master, strength, Character.MeterType.WILLPOWER);
                } else {
                    opponent.drain(c, self, strength, Character.MeterType.WILLPOWER);
                }
            }
            c.write(self, message);
        } else if (this.equals(bionic)) {
            String message = "";
            if (Random.random(5) == 0 && target.getType().equals("pussy")) {
                message += String.format(
                                "%s %s out inside %s %s, pressing the metallic head of %s %s tightly against %s cervix. "
                                                + "Then, a thin tube extends from %s urethra and into %s womb, pumping in a powerful aphrodisiac that soon has %s sensitive and"
                                                + " gasping for more.",
                                self.subject(), self.human() ? "bottom" : "bottoms", opponent.nameOrPossessivePronoun(),
                                target.describe(opponent), self.possessiveAdjective(), part.describe(self),
                                opponent.possessiveAdjective(), self.possessiveAdjective(), opponent.possessiveAdjective(),
                                opponent.directObject());
                opponent.add(c, new Hypersensitive(opponent.getType()));
                // Instantly addict
                opponent.add(c, new FluidAddiction(opponent.getType(), self.getType(), 1, 2));
                opponent.add(c, new FluidAddiction(opponent.getType(), self.getType(), 1, 2));
                opponent.add(c, new FluidAddiction(opponent.getType(), self.getType(), 1, 2));
                bonus -= 3; // Didn't actually move around too much
            } else if (target.moddedPartCountsAs(opponent, FieryMod.INSTANCE)) {
                message += String.format(
                                "Sensing the flesh around it, %s %s starts spinning rapidly, vastly increasing the friction against the walls of %s %s.",
                                self.nameOrPossessivePronoun(), part.describe(self), opponent.nameOrPossessivePronoun(),
                                target.describe(opponent));
                bonus += 5;
                if (Random.random(5) == 0) {
                    message += String.format(
                                    " The intense sensations cause %s to forget to breathe for a moment, leaving %s literally breathless.",
                                    opponent.subject(), opponent.directObject());
                    opponent.add(c, new Winded(opponent.getType(), 1));
                }
            }
            c.write(self, message);
        } else if (this.equals(enlightened)) {
            String message;
            if (target.moddedPartCountsAs(opponent, DemonicMod.INSTANCE)) {
                message = String.format(
                                "Almost instinctively, %s %s entire being into %s %s. While this would normally be a good thing,"
                                                + " whilst fucking a succubus it is very, very bad indeed.",
                                self.subjectAction("focus", "focuses"), self.possessiveAdjective(),
                                self.possessiveAdjective(), part.describe(self));
                c.write(self, message);
                // Actual bad effects are dealt with in PussyPart
            } else {
                message = String.format(
                                "Drawing upon %s extensive training, %s %s will into %s %s, enhancing %s own abilities",
                                self.possessiveAdjective(), self.subjectAction("concentrate", "concentrates"),
                                self.possessiveAdjective(), self.possessiveAdjective(), part.describe(self),
                                self.possessiveAdjective());
                c.write(self, message);
                for (int i = 0; i < Math.max(2, (self.getAttribute(Attribute.ki) + 5) / 10); i++) { // +5
                                                                                           // for
                                                                                           // rounding:
                                                                                           // 24->29->20,
                                                                                           // 25->30->30
                    Attribute attr = new Attribute[] {Attribute.power, Attribute.cunning, Attribute.seduction}[Random
                                    .random(3)];
                    self.add(c, new AttributeBuff(self.getType(), attr, 1, 10));
                }
                self.buildMojo(c, 5);
                self.restoreWillpower(c, 1);
            }
        }
        return bonus;
    }

    public Optional<String> getFluids() {
        if (this.equals(bionic)) {
            return Optional.of("artificial lubricant");
        }
        return Optional.empty();
    }

    public double applyReceiveBonuses(Combat c, Character self, Character opponent, BodyPart part, BodyPart target, double damage) {
        if (this.equals(blessed) && c.getStance().inserted(self)) {
            DivineCharge charge = (DivineCharge) self.getStatus(Stsflag.divinecharge);
            if (charge == null) {
                c.write(self, Formatter.format(
                                "{self:NAME-POSSESSIVE} " + part.fullDescribe(self)
                                                + " radiates a golden glow as {self:subject-action:groan|groans}. "
                                                + "{other:SUBJECT-ACTION:realize|realizes} {self:subject-action:are|is} feeding on {self:possessive} own pleasure to charge up {self:possessive} divine energy.",
                                self, opponent));
                self.add(c, new DivineCharge(self.getType(), .25));
            } else {
                c.write(self, Formatter.format(
                                "{self:SUBJECT-ACTION:continue|continues} feeding on {self:possessive} own pleasure to charge up {self:possessive} divine energy.",
                                self, opponent));
                self.add(c, new DivineCharge(self.getType(), charge.magnitude));
            }
        }
        return 0;
    }

    @Override
    public void onOrgasmWith(Combat c, Character self, Character opponent, BodyPart part, BodyPart target, boolean selfCame) {
        if (this.equals(incubus) && c.getStance().inserted(self)) {
            if (selfCame) {
                if (target.moddedPartCountsAs(opponent, CyberneticMod.INSTANCE)) {
                    c.write(self, String.format(
                                    "%s demonic seed splashes pointlessly against the walls of %s %s, failing even in %s moment of defeat.",
                                    self.nameOrPossessivePronoun(), opponent.nameOrPossessivePronoun(),
                                    target.describe(opponent), self.possessiveAdjective()));
                } else {
                    int duration = Random.random(3) + 2;
                    String message = String.format(
                                    "The moment %s erupts inside %s, %s mind goes completely blank, leaving %s pliant and ready.",
                                    self.subject(), opponent.subject(), opponent.possessiveAdjective(),
                                    opponent.directObject());
                    if (target.moddedPartCountsAs(opponent, FeralMod.INSTANCE)) {
                        message += String.format(" %s no resistance to the subversive seed.",
                                        Formatter.capitalizeFirstLetter(opponent.subjectAction("offer", "offers")));
                        duration += 2;
                    }
                    opponent.add(c, new Enthralled(opponent.getType(), self.getType(), duration));
                    c.write(self, message);
                }
            } else {
                if (target.moddedPartCountsAs(opponent, CyberneticMod.INSTANCE)) {
                    c.write(self, String.format(
                                    "Sensing %s moment of passion, %s %s greedily draws upon the rampant flows of orgasmic energy within %s, transferring the power back into %s.",
                                    opponent.nameOrPossessivePronoun(), self.nameOrPossessivePronoun(),
                                    part.describe(self), opponent.directObject(), self.directObject()));
                    int attDamage = target.moddedPartCountsAs(opponent, FeralMod.INSTANCE) ? 10 : 5;
                    int willDamage = target.moddedPartCountsAs(opponent, FeralMod.INSTANCE) ? 10 : 5;
                    Drained.drain(c, self, opponent, Attribute.power, attDamage, 20, true);
                    Drained.drain(c, self, opponent, Attribute.cunning, attDamage, 20, true);
                    Drained.drain(c, self, opponent, Attribute.seduction, attDamage, 20, true);
                    opponent.drain(c, self,
                                    (int) DamageType.drain.modifyDamage(self, opponent, willDamage), Character.MeterType.WILLPOWER);
                }
            }
        }
    }

    public void tickHolding(Combat c, Character self, Character opponent, BodyPart otherOrgan, CockPart part) {
        if (this.equals(primal)) {
            c.write(self, String.format("Raw sexual energy flows from %s %s into %s %s, inflaming %s lust",
                            self.nameOrPossessivePronoun(), part.describe(self), opponent.nameOrPossessivePronoun(),
                            otherOrgan.describe(opponent), opponent.possessiveAdjective()));
            opponent.add(c, Pheromones.getWith(self, opponent, Random.random(3) + 1, 3, " primal passion"));

        }
    }

    @Override
    public void onStartPenetration(Combat c, Character self, Character opponent, BodyPart part, BodyPart target) {
        if (this.equals(blessed) && target.isErogenous()) {
            if (!self.human()) {
                c.write(self, Formatter.format(
                                "As soon as {self:subject} penetrates you, you realize you're screwed. Both literally and figuratively. While it looks innocuous enough, {self:name-possessive} {self:body-part:cock} "
                                                + "feels like pure ecstasy. {self:SUBJECT} hasn't even begun moving yet, but {self:possessive} cock simply sitting within you radiates a heat that has you squirming uncontrollably.",
                                self, opponent));
            }
        }
    }

    public static Optional<CockMod> getFromType(String type) {
        return ALL_MODS.stream().filter(mod -> mod.getModType().equals(type)).findAny();
    }

    @Override
    public String describeAdjective(String partType) {
        if (this.equals(bionic)) {
            return "bionic implants";
        } else if (this.equals(blessed)) {
            return "holy aura";
        } else if (this.equals(enlightened)) {
            return "imposing presence";
        } else if (this.equals(slimy)) {
            return "slimy transparency";
        } else if (this.equals(runic)) {
            return "runic symbols";
        } else if (this.equals(primal)) {
            return "primal musk";
        } else if (this.equals(incubus)) {
            return "corruption";
        }
        return "weirdness (ERROR)";
    }
}

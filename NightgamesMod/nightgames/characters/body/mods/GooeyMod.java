package nightgames.characters.body.mods;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.status.CockBound;

import java.util.Optional;

public class GooeyMod extends PartMod {
    public static final GooeyMod INSTANCE = new GooeyMod();

    public GooeyMod() {
        super("gooey", .2, .5, .2, 2);
    }

    public void onOrgasmWith(Combat c, Character self, Character opponent, BodyPart part, BodyPart target, boolean selfCame) {
        if (c.getStance().isPartFuckingPartInserted(c, opponent, target, self, part) && !selfCame) {
            String partName = part.describe(self);
            c.write(self, Formatter.format(
                            "{self:NAME-POSSESSIVE} %s clenches down hard"
                                            + " on {other:name-possessive} {other:body-part:cock}. The suction is so strong that the cum"
                                            + " leaves the shaft in a constant flow rather than spurts. When {other:possessive} orgasm is"
                                            + " over, {other:subject-action:are} much more drained of cum than usual.",
                            self, opponent, partName));
            opponent.loseWillpower(c, 10 + Random.random(Math.min(20, self.getAttribute(Attribute.bio))));
        }
    }

    public void onStartPenetration(Combat c, Character self, Character opponent, BodyPart part, BodyPart target) {
        if (c.getStance().isPartFuckingPartInserted(c, opponent, target, self, part)) {
            String partName = part.describe(self);
            c.write(self, Formatter.format("{self:NAME-POSSESSIVE} %s envelops"
                            + " {other:possessive} {other:body-part:cock} in a sticky grip, making extraction more"
                            + " difficult.", self, opponent, partName));
            opponent.add(c, new CockBound(opponent.getType(), 7, self.nameOrPossessivePronoun() + " " + partName));
        }
    }

    public void tickHolding(Combat c, Character self, Character opponent, BodyPart part, BodyPart otherOrgan) {
        String partName = part.describe(self);
        c.write(self, Formatter.format(
                        "The slimy filaments inside {self:possessive} %s constantly massage"
                                        + " {other:possessive} %s, filling every inch of it with pleasure.",
                        self, opponent, partName, otherOrgan.describe(opponent)));
        opponent.body.pleasure(self, part, otherOrgan, 1 + Random.random(7), c);
    }

    public Optional<String> getFluids() {
        return Optional.of("slime");
    }

    @Override
    public String describeAdjective(String partType) {
        return "gooey consistency";
    }
}

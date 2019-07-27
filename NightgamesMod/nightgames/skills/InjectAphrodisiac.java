package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Emotion;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.items.Item;
import nightgames.status.Horny;
import nightgames.status.InducedEuphoria;

public class InjectAphrodisiac extends Skill {
    private InjectAphrodisiac() {
        super("Inject Aphrodisiac");
    }

    public boolean requirements(Combat c, Character user, Character target) {
        return user.getAttribute(Attribute.medicine) >= 4;
    }

    public boolean usable(Combat c, Character user, Character target) {
        return (c.getStance().mobile(user)) && (user.has(Item.Aphrodisiac) && (!user.human()))
                        && (user.canAct()) && user.has(Item.MedicalSupplies, 1)
                        && (!c.getStance().mobile(user) || !target.canAct());
    }

    @Override
    public int getMojoCost(Combat c, Character user) {
        return 25;
    }

    public boolean resolve(Combat c, Character user, Character target, boolean rollSucceeded) {
        int magnitude = 2 + user.getAttribute(Attribute.medicine);
        if (user.human()) {
            c.write(user, deal(c, magnitude, Result.normal, user, target));
        } else {
            c.write(user, receive(c, magnitude, Result.normal, user, user));
        }
        target.emote(Emotion.horny, 20);
        user.consume(Item.Aphrodisiac, 1);
        target.add(c, Horny.getWithBiologicalType(user, target, magnitude, 10, "Aphrodisac Injection"));
        target.add(c, new InducedEuphoria(target.getType()));
        user.consume(Item.MedicalSupplies, 1);

        return true;
    }

    public Tactics type(Combat c, Character user) {
        return Tactics.pleasure;
    }

    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return Formatter.format(
                        "You quickly grab one of your syringes full of potent aphrodisiac before grabbing {other:name-do} and injecting {other:direct-object} with its contents. After you do so you see a bright flush spread across {other:possessive} face and {other:possessive} breathing picks up.",
                        user, target);
    }

    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return Formatter.format(
                        "{self:SUBJECT} grins as {self:pronoun} flashes a hypodermic needle filled with light purple liquid. {other:SUBJECT-ACTION:gasp|gasps} as {self:pronoun} grab {other:possessive} arm before jabbing {other:direct-object} with the needle skillfully, pushing the plunger down to unload its cargo. A warmth floods through {other:name-possessive} body as the drug begins to take effect. It was an aphrodisiac!",
                        user, target);
    }

    public String describe(Combat c, Character user) {
        return "Injects your opponent with aphrodisiac";
    }
}

package nightgames.items;

import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.global.Global;
import nightgames.status.Status;

public class BuffEffect extends ItemEffect {
    private Status applied;

    public BuffEffect(String verb, String otherverb, Status status) {
        super(verb, otherverb, true, true);
        applied = status;
    }

    @Override
    public boolean use(Combat c, Character user, Character opponent, Item item) {
        if (c != null) {
            c.write(user, Global.global.format(String.format("{self:SUBJECT-ACTION:are|is} now %s", applied.name), user,
                            opponent));
        } else if (user.human()) {
            Global.global.gui().message(Global.global
                            .format(String.format("{self:SUBJECT-ACTION:are|is} now %s", applied.name), user,
                            opponent));
        }
        user.add(c, applied.instance(user, opponent));
        return true;
    }
}

package nightgames.skills;

import nightgames.characters.Character;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.status.addiction.Addiction;
import nightgames.status.addiction.AddictionType;

@SuppressWarnings("unused")
public class Bite extends Skill {

    public Bite() {
        super("Bite", 5);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.has(Trait.breeder);
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return c.getStance().penetratedBy(c, user, target) && c.getStance().kiss(user, target);
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Instill a lasting need to fuck";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        boolean katOnTop = c.getStance().dom(user);
        if (katOnTop) {
            c.write(user, "Kat leans in close, grinding her breasts against you and biting your neck!"
                            + " You briefly panic, but you know Kat wouldn't seriously hurt you. She quickly sits"
                            + " back up, riding you with a fierce intensity. An unnatural warmth spreads from where"
                            + " she's bitten you, and her movements suddenly feel even better than before.");
        } else {
            c.write(user, "Kat grabs your head and pulls it down beside hers, then she twists and bites you!"
                            + " You think she's broken your skin, but you're not bleeding. A warmth spreads down"
                            + " from your neck as Kay smiles at you coyly. <i>\"It, ah, feels so much better with a"
                            + " little bit of animal instinct, nya?\"</i> You're not sure what she means, but"
                            + " you do realize you've sped up your thrusting and it does seem to feel even"
                            + " better than before.");            
        }
        target.addict(c, AddictionType.BREEDER, user, Addiction.MED_INCREASE);
        
        return true;
    }
    
    public float priorityMod(Combat c, Character user) {
        return 10.f;
    }

    @Override
    public Skill copy(Character user) {
        return new Bite();
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.fucking;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        // TODO Auto-generated method stub
        return null;
    }

}

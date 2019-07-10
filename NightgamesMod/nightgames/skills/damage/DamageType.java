package nightgames.skills.damage;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.trait.Trait;
import nightgames.global.DebugFlags;

/**
 * Damage Type Mechanics
 *
 * Different damage types are affected by different attributes, both defensively and offensively. Attributes contribute
 * additively to a multiplier.
 *
 * Damage multiplier
 * 1 + (.03 * offensive power from source) - (.015 * defensive power sum from target)
 */
// TODO: Resolve confusion between damage method (pleasure, tempt, strike, weaken) and damage flavor (stance, arcane, biological)
public enum DamageType {
    pleasure, // Lust damage, usually through direct contact. Can trigger orgasm.
    temptation, // Lust damage, via teasing and tits. Does not trigger orgasm.
    physical,   // Stamina damage, usually through direct contact.
    drain,  // Transfers stamina from target to cause.
    weaken, // Non-punch stamina damage.
    willpower,  // Direct damage to willpower via non-orgasm means
    stance, // Stance upkeep costs
    arcane, // Damage from spells
    gadgets,    // Damage from toys
    technique,  // Damage from tickling
    biological, // Damage from pheremones
    ;

    /**
     * Every point of defensive power reduces incoming damage by .75%
     *
     * @param target The character receiving the damage
     * @return The characters defensive power against the damage type
     */
    public double getDefensivePower(Character target){
        switch (this) {
            // TODO: consider adding vulnerabilities
            // arcane disrupts gadgets? dark vulnerable to divine? asphyxiation prevents ki breath control?
            case arcane:
                // (2 * arcane + dark + divinity + ki)
                // each point of arcane reduces incoming damage by 1.5%
                // each point of the other attributes reduces incoming damage by .75%
                return 2 * target.get(Attribute.spellcasting) + target.get(Attribute.darkness) + target.get(Attribute.divinity) +
                                target.get(Attribute.ki);
            case biological:
                // (animism + bio + medicine + science + cunning + seduction) / 2
                // each point of these attributes reduces incoming damage by .75%
                return target.get(Attribute.animism) + target.get(Attribute.bio) + target.get(Attribute.medicine) + target
                                .get(Attribute.science) + target.get(Attribute.cunning) + target.get(Attribute.seduction);
            case pleasure:
                // each point of seduction reduces incoming damage by 1.5%
                return 2 * target.get(Attribute.seduction);
            case temptation:
                // (2 * seduction + 2 * submissive + cunning) / 2
                // each point of seduction or submission reduces incoming damage by 1.5%
                // cunning reduces incoming damage by .75%
                return target.get(Attribute.seduction) * 2 + target.get(Attribute.submission) * 2 + target.get(Attribute.cunning);
            case technique:
                // each point of cunning reduces incoming damage by 1.5%
                return 2 * target.get(Attribute.cunning);
            case physical:
                // (2 * power + cunning)
                // each point of power reduces incoming damage by 1.5%
                // each point of cunning reduces incoming damage by 0.75%
                return (target.get(Attribute.power) * 2 + target.get(Attribute.cunning));
            case gadgets:
                // each point of cunning reduces incoming damage by 1.5%
                return 2 * target.get(Attribute.cunning);
            case drain:
                // (2 * dark + arcane)
                // each point of dark reduces incoming damage by 1.5%
                // each point of arcane reduces incoming damage by 0.75%
                return target.get(Attribute.darkness) * 2 + target.get(Attribute.spellcasting);
            case stance:
                // TODO: stance damage should not be used for both smothering (e.g., from facesitting) and gradual exhaustion (e.g., from carrying someone while fucking them).
                // (2 * cunning + power)
                // each point of cunning reduces incoming damage by 1.5%
                // each point of power reduces incoming damage by 0.75%
                return target.get(Attribute.cunning) * 2 + target.get(Attribute.power);
            case weaken:
                // (2 * dark + divinity)
                // each point of dark reduces incoming damage by 1.5%
                // each point of power reduces incoming damage by .75%
                return target.get(Attribute.darkness) * 2 + target.get(Attribute.divinity);
            case willpower:
                // (dark + fetish + 2 * divinity + level)
                // each level reduces incoming damage by .75%
                // each point of divinity reduces incoming damage by 1.5%
                // each point of fetish or dark reduces incoming damage by .75%
                return target.get(Attribute.darkness) + target.get(Attribute.fetishism) + target.get(Attribute.divinity) * 2 + target
                                .getLevel();
            default:
                return 0;
        }
    }

    /**
     * Every point of offensive power increases outgoing damage by 1%.
     *
     * @param source The character causing the damage
     * @return The character's offensive power of the damage type
     */
    public double getOffensivePower(Character source){
        switch (this) {
            case biological:
                // 2 * animism + 2 * bio + medicine + science
                // each point of animism or bio increases outgoing damage by 2%
                // each point of medicine or science increases outgoing damage by 1%
                return 2 * source.get(Attribute.animism) + 2 * source.get(Attribute.bio) + source.get(Attribute.medicine) + source
                                .get(Attribute.science);
            case gadgets:
                // (2 * science + cunning) + toymaster ? 60
                // each point of science increases outgoing damage by 2%
                // each point of cunning increases outgoing damage by 1%
                // toymaster trait increases outgoing damage by 60%
                return (source.get(Attribute.science) * 2 + source.get(Attribute.cunning) + (source.has(Trait.toymaster) ? 60 : 0));
            case pleasure:
                // each point of seduction increases outgoing damage by 3%
                return 3 * source.get(Attribute.seduction);
            case arcane:
                // each point of arcane increases outgoing damage by 3%
                return 3 * source.get(Attribute.spellcasting);
            case temptation:
                // 2 * seduction + cunning
                // each point of seduction increases outgoing damage by 2%
                // each point of cunning increases outgoing damage by 1%
                return source.get(Attribute.seduction) * 2 + source.get(Attribute.cunning);
            case technique:
                // each point of cunning increases outgoing damage by 3%
                return 3 * source.get(Attribute.cunning);
            case physical:
                // 2 * power + cunning + 2 * ki
                // each point of power or ki increases outgoing damage by 2%
                // each point of cunning increases outgoing damage by 1%
                // power represents raw strength
                // cunning represents weakpoint targeting
                // ki represents showing them your moves
                return source.get(Attribute.power) * 2 + source.get(Attribute.cunning) + source.get(Attribute.ki) * 2;
            case drain:
                // with gluttony:
                // (4 * dark + 2 * arcane)
                // each point of dark increases outgoing damage by 4%
                // each point of arcane increases outgoing damage by 2%
                // without gluttony:
                // (2 * dark + arcane)
                // each point of dark increases outgoing damage by 3%
                // each point of arcane increases outgoing damage by 1.5%
                // ergo:
                // gluttony increases drain power by 33%
                // TODO: Move gluttony trait effect to drain damage method instead of type.
                if (source.has(Trait.gluttony)) {
                    return 4 * source.get(Attribute.darkness) + 2 * source.get(Attribute.spellcasting);
                } else {
                    return (source.get(Attribute.darkness) * 3 + 1.5 * source.get(Attribute.spellcasting));
                }
            case stance:
                // (2 * cunning + power)
                // each point of cunning increases outgoing damage by 2%
                // each point of power increases outgoing damage by 1%
                return source.get(Attribute.cunning) * 2 + source.get(Attribute.power);
            case weaken:
                // (2 * dark + divinity + ki)
                // each point of dark increases outgoing damage by 2%
                // each point of divinity or ki increases outgoing damage by 1%
                return source.get(Attribute.darkness) * 2 + source.get(Attribute.divinity) + source.get(Attribute.ki);
            case willpower:
                // (dark + fetish + 2 * divinity + level)
                // each point of dark or fetish increases outgoing damage by 1%
                // each point of divinity increases outgoing damage by 2%
                // each level increases outgoing damage by 1%
                return source.get(Attribute.darkness) + source.get(Attribute.fetishism) + source.get(Attribute.divinity) * 2 + source
                                .getLevel();
                return 3 * source.get(Attribute.divinity) + source.get(Attribute.spellcasting);
            default:
                return 0;
        }
    }

    public double modifyDamage(Character source, Character target, double baseDamage) {
        // damage should be max capped to (2 * (100 + attacker's level * 1.5))%
        // damage should be min capped to (.5 * (100 + attacker's level * 1.5))%
        double maxDamage = baseDamage * 2 * (1 + .015 * source.getLevel());
        double minDamage = baseDamage * .5 * (1 + .015 * source.getLevel());
        double offensivePower = getOffensivePower(source);
        double defensivePower = getDefensivePower(target);
        double multiplier = (1 + .01 * offensivePower - .0075 * defensivePower);
        if (DebugFlags.isDebugOn(DebugFlags.DEBUG_DAMAGE)) {
            System.out.println(
                            baseDamage + " from " + source.getTrueName() + " has multiplier " + multiplier + " against "
                                            + target.getTrueName() + " [" + offensivePower + ", " + defensivePower
                                            + "].");
        }
        double damage = baseDamage * multiplier;
        return Math.min(Math.max(minDamage, damage), maxDamage);
    }
}

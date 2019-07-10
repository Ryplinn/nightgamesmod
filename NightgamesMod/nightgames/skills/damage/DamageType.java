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
     * Every point of defensive power reduces incoming damage by 1.5%.
     *
     * @param target The character receiving the damage
     * @return The characters defensive power against the damage type
     */
    public double getDefensivePower(Character target){
        switch (this) {
            case arcane:
                // (2 * arcane + dark + divinity + ki) / 2
                // each point of arcane reduces incoming damage by 1.5%
                // each point of the other attributes reduces incoming damage by .75%
                return target.get(Attribute.spellcasting) + target.get(Attribute.darkness) / 2.0 + target.get(Attribute.divinity) / 2.0 + target
                                .get(Attribute.ki) / 2.0;
            case biological:
                // (animism + bio + medicine + science + cunning + seduction) / 2
                // each point of these attributes reduces incoming damage by .75%
                return target.get(Attribute.animism) / 2.0 + target.get(Attribute.bio) / 2.0 + target.get(Attribute.medicine) / 2.0 + target
                                .get(Attribute.science) / 2.0 + target.get(Attribute.cunning) / 2.0 + target.get(Attribute.seduction) / 2.0;
            case pleasure:
                // each point of seduction reduces incoming damage by 1.5%
                return target.get(Attribute.seduction);
            case temptation:
                // (2 * seduction + 2 * submissive + cunning) / 2
                // each point of seduction or submission reduces incoming damage by 1.5%
                // cunning reduces incoming damage by .75%
                return (target.get(Attribute.seduction) * 2 + target.get(Attribute.submission) * 2 + target.get(Attribute.cunning)) / 2.0;
            case technique:
                // each point of cunning reduces incoming damage by 1.5%
                return target.get(Attribute.cunning);
            case physical:
                // (2 * power + cunning) / 2
                // each point of power reduces incoming damage by 1.5%
                // each point of cunning reduces incoming damage by 0.75%
                return (target.get(Attribute.power) * 2 + target.get(Attribute.cunning)) / 2.0;
            case gadgets:
                // each point of cunning reduces incoming damage by 1.5%
                return target.get(Attribute.cunning);
            case drain:
                // (2 * dark + arcane) / 2
                // each point of dark reduces incoming damage by 1.5%
                // each point of arcane reduces incoming damage by 0.75%
                return (target.get(Attribute.darkness) * 2 + target.get(Attribute.spellcasting)) / 2.0;
            case stance:
                // (2 * cunning + power) / 2
                // each point of cunning reduces incoming damage by 1.5%
                // each point of power reduces incoming damage by 0.75%
                return (target.get(Attribute.cunning) * 2 + target.get(Attribute.power)) / 2.0;
            case weaken:
                // (2 * dark + divinity) / 2
                // each point of dark reduces incoming damage by 1.5%
                // each point of power reduces incoming damage by .75%
                return (target.get(Attribute.darkness) * 2 + target.get(Attribute.divinity)) / 2.0;
            case willpower:
                // (dark + fetish + 2 * divinity + level) / 2
                // each level reduces incoming damage by .75%
                // each point of divinity reduces incoming damage by 1.5%
                // each point of fetish or dark reduces incoming damage by .75%
                return (target.get(Attribute.darkness) + target.get(Attribute.fetishism) + target.get(Attribute.divinity) * 2 + target
                                .getLevel()) / 2.0;
            default:
                return 0;
        }
    }

    /**
     * Every point of offensive power increases outgoing damage by 3%.
     *
     * @param source The character causing the damage
     * @return The character's offensive power of the damage type
     */
    public double getOffensivePower(Character source){
        switch (this) {
            case biological:
                // (animism + bio + medicine + science) / 2
                // each point of these attributes increases outgoing damage by 1.5%
                return (source.get(Attribute.animism) + source.get(Attribute.bio) + source.get(Attribute.medicine) + source
                                .get(Attribute.science)) / 2.0;
            case gadgets:
                // (2 * science + cunning) / 3 + 20(has toymaster)
                // each point of science increases outgoing damage by 2%
                // each point of cunning increases outgoing damage by 1%
                // toymaster trait increases outgoing damage by 60%
                double power = (source.get(Attribute.science) * 2 + source.get(Attribute.cunning)) / 3.0;
                if (source.has(Trait.toymaster)) {
                    power += 20;
                }
                return power;
            case pleasure:
                // each point of seduction increases outgoing damage by 3%
                return source.get(Attribute.seduction);
            case arcane:
                // each point of arcane increases outgoing damage by 3%
                return source.get(Attribute.spellcasting);
            case temptation:
                // (2 * seduction + cunning) / 3
                // each point of seduction increases outgoing damage by 2%
                // each point of cunning increases outgoing damage by 1%
                return (source.get(Attribute.seduction) * 2 + source.get(Attribute.cunning)) / 3.0;
            case technique:
                // each point of cunning increases outgoing damage by 3%
                return source.get(Attribute.cunning);
            case physical:
                // (2 * power + cunning + 2 * ki) / 3
                // each point of power or ki increases outgoing damage by 2%
                // each point of cunning increases outgoing damage by 1%
                // power represents raw strength
                // cunning represents weakpoint targeting
                // ki represents showing them your moves
                return (source.get(Attribute.power) * 2 + source.get(Attribute.cunning) + source.get(Attribute.ki) * 2) / 3.0;
            case drain:
                // with gluttony:
                // (4 * dark + 2 * arcane) / 3
                // each point of dark increases outgoing damage by 4%
                // each point of arcane increases outgoing damage by 2%
                // without gluttony:
                // (2 * dark + arcane) / 2
                // each point of dark increases outgoing damage by 3%
                // each point of arcane increases outgoing damage by 1.5%
                // ergo:
                // gluttony increases drain power by 33%
                return (source.get(Attribute.darkness) * 2 + source.get(Attribute.spellcasting)) / (source.has(Trait.gluttony) ? 1.5 : 2.0);
            case stance:
                // (2 * cunning + power) / 3
                // each point of cunning increases outgoing damage by 2%
                // each point of power increases outgoing damage by 1%
                return (source.get(Attribute.cunning) * 2 + source.get(Attribute.power)) / 3.0;
            case weaken:
                // (2 * dark + divinity + ki) / 3
                // each point of dark increases outgoing damage by 2%
                // each point of divinity or ki increases outgoing damage by 1%
                return (source.get(Attribute.darkness) * 2 + source.get(Attribute.divinity) + source.get(Attribute.ki)) / 3.0;
            case willpower:
                // (dark + fetish + divinity * 2 + level) / 3
                // each point of dark or fetish increases outgoing damage by 1%
                // each point of divinity increases outgoing damage by 2%
                // each level increases outgoing damage by 1%
                return (source.get(Attribute.darkness) + source.get(Attribute.fetishism) + source.get(Attribute.divinity) * 2 + source
                                .getLevel()) / 3.0;
            default:
                return 0;
        }
    }

    public double modifyDamage(Character source, Character target, double baseDamage) {
        // so for each damage type, one level from the attacker should result in about 3% increased damage, while a point in defense should reduce damage by around 1.5% per level.
        // this differential should be max capped to (2 * (100 + attacker's level * 1.5))%
        // this differential should be min capped to (.5 * (100 + attacker's level * 1.5))%
        double maxDamage = baseDamage * 2 * (1 + .015 * source.getLevel());
        double minDamage = baseDamage * .5 * (1 + .015 * source.getLevel());
        double multiplier = (1 + .03 * getOffensivePower(source) - .015 * getDefensivePower(target));
        if (DebugFlags.isDebugOn(DebugFlags.DEBUG_DAMAGE)) {
            System.out.println(baseDamage + " from " + source.getTrueName() + " has multiplier " + multiplier + " against " + target.getTrueName() + " ["+ getOffensivePower(
                            source) +", " + getDefensivePower(target) + "].");
        }
        double damage = baseDamage * multiplier;
        return Math.min(Math.max(minDamage, damage), maxDamage);
    }
}

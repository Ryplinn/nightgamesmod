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
    stance, // Stamina damage from smothering and such
    arcane, // Damage from spells
    gadgets,    // Damage from toys
    technique,  // Damage from tickling
    biological, // Damage from pheremones
    ;

    public double getDefensivePower(Character target){
        switch (this) {
            case arcane:
                return target.get(Attribute.Arcane) + target.get(Attribute.Dark) / 2.0 + target.get(Attribute.Divinity) / 2.0 + target
                                .get(Attribute.Ki) / 2.0;
            case biological:
                return target.get(Attribute.Animism) / 2.0 + target.get(Attribute.Bio) / 2.0 + target.get(Attribute.Medicine) / 2.0 + target
                                .get(Attribute.Science) / 2.0 + target.get(Attribute.Cunning) / 2.0 + target.get(Attribute.Seduction) / 2.0;
            case pleasure:
                return target.get(Attribute.Seduction);
            case temptation:
                return (target.get(Attribute.Seduction) * 2 + target.get(Attribute.Submissive) * 2 + target.get(Attribute.Cunning)) / 2.0;
            case technique:
                return target.get(Attribute.Cunning);
            case physical:
                return (target.get(Attribute.Power) * 2 + target.get(Attribute.Cunning)) / 2.0;
            case gadgets:
                return target.get(Attribute.Cunning);
            case drain:
                return (target.get(Attribute.Dark) * 2 + target.get(Attribute.Arcane)) / 2.0;
            case stance:
                return (target.get(Attribute.Cunning) * 2 + target.get(Attribute.Power)) / 2.0;
            case weaken:
                return (target.get(Attribute.Dark) * 2 + target.get(Attribute.Divinity)) / 2.0;
            case willpower:
                return (target.get(Attribute.Dark) + target.get(Attribute.Fetish) + target.get(Attribute.Divinity) * 2 + target
                                .getLevel()) / 2.0;
            default:
                return 0;
        }
    }

    public double getOffensivePower(Character source){
        switch (this) {
            case biological:
                return (source.get(Attribute.Animism) + source.get(Attribute.Bio) + source.get(Attribute.Medicine) + source
                                .get(Attribute.Science)) / 2.0;
            case gadgets:
                double power = (source.get(Attribute.Science) * 2 + source.get(Attribute.Cunning)) / 3.0;
                if (source.has(Trait.toymaster)) {
                    power += 20;
                }
                return power;
            case pleasure:
                return source.get(Attribute.Seduction);
            case arcane:
                return source.get(Attribute.Arcane);
            case temptation:
                return (source.get(Attribute.Seduction) * 2 + source.get(Attribute.Cunning)) / 3.0;
            case technique:
                return source.get(Attribute.Cunning);
            case physical:
                return (source.get(Attribute.Power) * 2 + source.get(Attribute.Cunning) + source.get(Attribute.Ki) * 2) / 3.0;
            case drain:
                return (source.get(Attribute.Dark) * 2 + source.get(Attribute.Arcane)) / (source.has(Trait.gluttony) ? 1.5 : 2.0);
            case stance:
                return (source.get(Attribute.Cunning) * 2 + source.get(Attribute.Power)) / 3.0;
            case weaken:
                return (source.get(Attribute.Dark) * 2 + source.get(Attribute.Divinity) + source.get(Attribute.Ki)) / 3.0;
            case willpower:
                return (source.get(Attribute.Dark) + source.get(Attribute.Fetish) + source.get(Attribute.Divinity) * 2 + source
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

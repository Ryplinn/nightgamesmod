The attribute value tables specify the bonuses each attribute point provides to offense and defense.

{
    "attribute_x": {
        "offense": {
            "damage_type_a": 1,
            "damage_type_b": 4
        },
        "defense": {
            "damage_type_a": 2,
            "damage_type_c": 2
        },
        "protection": {
            "stamina": 1,
            "willpower": 3
        }
    },
    "attribute_y": {
        "offense": {
            "damage_type_b": 1,
            "damage_type_c": 3,
            "damage_type_d": 1
        },
        "defense": {
            "damage_type_b": 3,
        },
        "protection": {
            "stamina": 1,
            "arousal": 1
        }
    }
}

Offensive and defensive power provide percentage increases in outgoing damage or decreases in incoming damage. The precise
amounts are subject to balance changes, but as of this writing are:
1 offensive power = 1% increased outgoing damage
1 defensive power = .75% decreased incoming damage

A character with 10 points of attribute_x and 20 points of attribute_y would have:
+10 offensive power with damage type a -> +10% damage with type a
+(10*4 + 20*1) = +60 offensive power with type b -> +60% damage with type b
+60 offensive power with type c -> +60% damage with type c
+20 offensive power with type d -> +20% damage with type d

They would also have:
+20 defensive power vs damage type a -> -15% damage taken from type a
+60 defensive power vs damage type b -> -45% damage from type b
+20 defensive power vs damage type c -> -15% damage from type c

The same character using a skill that deals damage of both type a and b would have +70 total offensive power with that skill,
increasing damage by 70%. If that skill were used on them instead, they would have +80 defensive power against it, reducing
damage taken by 60%.

Protection operates similarly to defensive power, but applies to damage taken by a particular meter (stamina, arousal,
mojo, willpower) rather than a damage type. Damage reductions from type and protection stack additively.

Damage types not specified in an attribute's table default to 0, providing no bonus offensive or defensive power. Negative
damage values in these tables (representing vulnerabilities or anti-synergies) are permitted, but are currently unused.

For a list of damage types, see skills/damage/DamageType.java.

A "NOTES" string field is considered a comment and ignored, existing only because JSON has no comment syntax.

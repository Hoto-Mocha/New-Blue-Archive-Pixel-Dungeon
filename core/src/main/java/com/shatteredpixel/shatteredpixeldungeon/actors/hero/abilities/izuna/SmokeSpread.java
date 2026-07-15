package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.izuna;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;

public class SmokeSpread extends ArmorAbility {
    {
        baseChargeUse = 25f;
    }

    @Override
    public int icon() {
        return HeroIcon.IZUNA_1;
    }

    @Override
    protected void activate(ClassArmor armor, Hero hero, Integer target) {


        armor.charge -= chargeUse( hero );
        armor.updateQuickslot();
        Invisibility.dispel();
    }

    @Override
    public Talent[] talents() {
        return new Talent[]{Talent.IZUNA_ARMOR1_1, Talent.IZUNA_ARMOR1_2, Talent.IZUNA_ARMOR1_3, Talent.HEROIC_ENERGY};
    }
}

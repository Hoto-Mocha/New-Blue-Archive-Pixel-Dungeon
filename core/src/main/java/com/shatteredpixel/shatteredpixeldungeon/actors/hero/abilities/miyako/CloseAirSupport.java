package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.miyako;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;

public class CloseAirSupport extends ArmorAbility {
    {
        baseChargeUse = 50f;
    }

    @Override
    protected void activate(ClassArmor armor, Hero hero, Integer target) {
        if (target != null) {
            armor.charge -= chargeUse(hero);
            armor.updateQuickslot();
            Invisibility.dispel();
        }
    }

    @Override
    public int icon() {
        return HeroIcon.MIYAKO_3;
    }

    @Override
    public Talent[] talents() {
        return new Talent[]{Talent.MIYAKO_ARMOR3_1, Talent.MIYAKO_ARMOR3_2, Talent.MIYAKO_ARMOR3_3, Talent.HEROIC_ENERGY};
    }
}

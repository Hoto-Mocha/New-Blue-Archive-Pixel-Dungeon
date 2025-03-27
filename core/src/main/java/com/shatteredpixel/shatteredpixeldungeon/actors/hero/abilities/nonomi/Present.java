package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.nonomi;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;

public class Present extends ArmorAbility {
    {
        baseChargeUse = 25f;
    }

    @Override
    protected void activate(ClassArmor armor, Hero hero, Integer target) {
        armor.charge -= chargeUse(hero);
        armor.updateQuickslot();
        Invisibility.dispel();

        hero.sprite.operate(hero.pos);
    }

    @Override
    public int icon() {
        return HeroIcon.NONOMI_1;
    }

    @Override
    public Talent[] talents() {
        return new Talent[]{Talent.NONOMI_ARMOR1_1, Talent.NONOMI_ARMOR1_2, Talent.NONOMI_ARMOR1_3, Talent.HEROIC_ENERGY};
    }
}

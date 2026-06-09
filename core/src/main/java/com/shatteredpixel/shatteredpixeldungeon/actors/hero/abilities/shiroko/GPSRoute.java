package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.shiroko;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;

public class GPSRoute extends ArmorAbility {
    {
        baseChargeUse = 50f;
    }

    @Override
    public int icon() {
        return HeroIcon.SHIROKO_3;
    }

    @Override
    protected void activate(ClassArmor armor, Hero hero, Integer target) {

    }

    @Override
    public Talent[] talents() {
        return new Talent[]{Talent.SHIROKO_ARMOR1_1, Talent.SHIROKO_ARMOR1_2, Talent.SHIROKO_ARMOR1_3, Talent.HEROIC_ENERGY};
    }
}

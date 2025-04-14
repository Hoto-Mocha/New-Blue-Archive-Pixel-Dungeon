package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.miyako;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;

public class Helicopter extends ArmorAbility {
    {
        baseChargeUse = 35f;
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
        return HeroIcon.MIYAKO_2;
    }

    @Override
    public String targetingPrompt() {
        return Messages.get(this, "prompt");
    }

    @Override
    public Talent[] talents() {
        return new Talent[]{Talent.MIYAKO_ARMOR2_1, Talent.MIYAKO_ARMOR2_2, Talent.MIYAKO_ARMOR2_3, Talent.HEROIC_ENERGY};
    }
}

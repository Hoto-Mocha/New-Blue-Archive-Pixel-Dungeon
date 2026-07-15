package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.izuna;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;

public class Blink extends ArmorAbility {
    {
        baseChargeUse = 35f;
    }

    @Override
    public int icon() {
        return HeroIcon.IZUNA_2;
    }

    @Override
    public int targetedPos(Char user, int dst) {
        return super.targetedPos(user, dst);
    }

    @Override
    public String targetingPrompt() {
        return Messages.get(this, "prompt");
    }

    @Override
    protected void activate(ClassArmor armor, Hero hero, Integer target) {


        armor.charge -= chargeUse( hero );
        armor.updateQuickslot();
        Invisibility.dispel();
    }

    @Override
    public Talent[] talents() {
        return new Talent[]{Talent.IZUNA_ARMOR2_1, Talent.IZUNA_ARMOR2_2, Talent.IZUNA_ARMOR2_3, Talent.HEROIC_ENERGY};
    }
}

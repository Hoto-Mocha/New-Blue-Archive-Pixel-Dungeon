package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.miyu;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;

public class Flashbang extends ArmorAbility {
    {
        baseChargeUse = 25f;
    }

    @Override
    public int icon() {
        return HeroIcon.MIYU_1;
    }

    @Override
    public String targetingPrompt() {
        return Messages.get(this, "prompt");
    }

    @Override
    public int targetedPos(Char user, int dst) {
        return dst;
    }

    @Override
    protected void activate(ClassArmor armor, Hero hero, Integer target) {
        if (target == null) return;



        Invisibility.dispel();
        armor.charge -= chargeUse(hero);
        armor.updateQuickslot();
    }

    @Override
    public Talent[] talents() {
        return new Talent[]{Talent.MIYU_ARMOR1_1, Talent.MIYU_ARMOR1_2, Talent.MIYU_ARMOR1_3, Talent.HEROIC_ENERGY};
    }
}

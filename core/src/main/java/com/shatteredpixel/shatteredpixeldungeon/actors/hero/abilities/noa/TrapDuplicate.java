package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.noa;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;

public class TrapDuplicate extends ArmorAbility {
    {
        baseChargeUse = 35f;
    }

    @Override
    public int icon() {
        return HeroIcon.NOA_1;
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

    }

    @Override
    public Talent[] talents() {
        return new Talent[]{Talent.NOA_ARMOR1_1, Talent.NOA_ARMOR1_2, Talent.NOA_ARMOR1_3, Talent.HEROIC_ENERGY};
    }
}

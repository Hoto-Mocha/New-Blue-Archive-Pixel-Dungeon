package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.mari;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;

public class DivinePunishment extends ArmorAbility {
    {
        baseChargeUse = 50f;
    }

    @Override
    public int icon() {
        return HeroIcon.MARI_3;
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
    public float chargeUse(Hero hero) {
        float chargeUse = super.chargeUse(hero);

        return chargeUse;
    }

    @Override
    protected void activate(ClassArmor armor, Hero hero, Integer target) {
        if (target == null || target == hero.pos) return;

    }

    @Override
    public Talent[] talents() {
        return new Talent[]{Talent.MARI_ARMOR3_1, Talent.MARI_ARMOR3_2, Talent.MARI_ARMOR3_3, Talent.HEROIC_ENERGY};
    }
}

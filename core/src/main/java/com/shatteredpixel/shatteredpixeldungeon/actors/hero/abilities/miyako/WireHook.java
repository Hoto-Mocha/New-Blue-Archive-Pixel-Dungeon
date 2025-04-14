package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.miyako;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.warrior.HeroicLeap;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;

public class WireHook extends ArmorAbility {
    {
        baseChargeUse = 25f;
    }

    @Override
    protected void activate(ClassArmor armor, Hero hero, Integer target) {
        if (target != null) {
            armor.charge -= chargeUse(hero);
            armor.updateQuickslot();
            Invisibility.dispel();

            hero.spendAndNext(1f);
            hero.sprite.zap(target);
        }
    }

    @Override
    public int icon() {
        return HeroIcon.MIYAKO_1;
    }

    @Override
    public String targetingPrompt() {
        return Messages.get(this, "prompt");
    }

    @Override
    public float chargeUse( Hero hero ) {
        float chargeUse = super.chargeUse(hero);
//        if (hero.buff(HeroicLeap.DoubleJumpTracker.class) != null){
//            //reduced charge use by 16%/30%/41%/50%
//            chargeUse *= Math.pow(0.84, hero.pointsInTalent(Talent.DOUBLE_JUMP));
//        }
        return chargeUse;
    }

    @Override
    public Talent[] talents() {
        return new Talent[]{Talent.MIYAKO_ARMOR1_1, Talent.MIYAKO_ARMOR1_2, Talent.MIYAKO_ARMOR1_3, Talent.HEROIC_ENERGY};
    }
}

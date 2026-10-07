package com.shatteredpixel.shatteredpixeldungeon.actors.hero.miracles;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CrossNecklace;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;

public class Protection extends MariMiracle {

    @Override
    public float chargeUse(Hero hero) {
        return 3;
    }

    @Override
    public void onCast(CrossNecklace cross, Hero hero) {
        Buff.affect(hero, ProtectionBuff.class, ProtectionBuff.DURATION);
    }

    public static class ProtectionBuff extends FlavourBuff {

        public static final float DURATION = 50f;

        {
            type = buffType.POSITIVE;
        }

        @Override
        public int icon() {
            return BuffIndicator.PROTECTION;
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0, (DURATION-visualcooldown())/DURATION);
        }

        public int defenseBonus(Hero hero) {
            if (hero.subClass == HeroSubClass.DEVOUT_PRAYER) {
                return 3;
            } else {
                return 1;
            }
        }

        public int thunderDamageBonus(Hero hero) {
            if (hero.subClass == HeroSubClass.DEVOUT_PRAYER) {
                return 4;
            } else {
                return 2;
            }
        }

    }
}

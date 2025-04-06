package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.nonomi;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

public class Bipod extends ArmorAbility {
    {
        baseChargeUse = 50f;
    }

    @Override
    protected void activate(ClassArmor armor, Hero hero, Integer target) {
        armor.charge -= chargeUse(hero);
        armor.updateQuickslot();
        Invisibility.dispel();

        Buff.affect(hero, BipodBuff.class).set(hero.pos);

        Sample.INSTANCE.play(Assets.Sounds.UNLOCK);
        hero.sprite.operate(hero.pos);
        hero.spendAndNext(1f);
    }

    @Override
    public int icon() {
        return HeroIcon.NONOMI_3;
    }

    @Override
    public Talent[] talents() {
        return new Talent[]{Talent.NONOMI_ARMOR3_1, Talent.NONOMI_ARMOR3_2, Talent.NONOMI_ARMOR3_3, Talent.HEROIC_ENERGY};
    }

    public static class BipodBuff extends Buff {
        {
            type = buffType.POSITIVE;

            announced = true;
        }

        @Override
        public int icon() {
            return BuffIndicator.INVERT_MARK;
        }

        @Override
        public void tintIcon(Image icon) {
            icon.hardlight(1, 1, 0);
        }

        private int pos = -1;

        public void set(int pos) {
            this.pos = pos;
        }

        @Override
        public boolean act() {
            if (this.pos == -1 || target.pos != this.pos) {
                detach();
            }
            spend(Actor.TICK);
            return true;
        }

        private static String POS = "pos";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(POS, pos);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            pos = bundle.getInt(POS);
        }
    }
}

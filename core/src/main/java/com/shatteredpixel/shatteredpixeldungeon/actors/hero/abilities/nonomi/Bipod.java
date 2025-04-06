package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.nonomi;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.Gun;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
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
    public float chargeUse(Hero hero) {
        if (hero.buff(BipodTracker.class) != null){
            return super.chargeUse(hero) * 0.5f;
        } else {
            return super.chargeUse(hero);
        }
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
        private int count = 0;

        public void set(int pos) {
            this.pos = pos;
        }

        @Override
        public boolean act() {
            if (Dungeon.hero.hasTalent(Talent.NONOMI_ARMOR3_1)) {
                count++;
                if (count >= 9-Dungeon.hero.pointsInTalent(Talent.NONOMI_ARMOR3_1)) { //8/7/6/5
                    count = 0;
                    if (Dungeon.hero.belongings.weapon() instanceof Gun) {
                        ((Gun) Dungeon.hero.belongings.weapon()).manualReload();
                    }
                }
            } else {
                count = 0;
            }
            if (this.pos == -1 || target.pos != this.pos) {
                detach();
            }
            spend(Actor.TICK);
            return true;
        }

        @Override
        public void detach() {
            if (target instanceof Hero && Dungeon.hero.hasTalent(Talent.NONOMI_ARMOR3_3)) {
                Buff.affect(target, BipodTracker.class, Dungeon.hero.pointsInTalent(Talent.NONOMI_ARMOR3_3));
            }
            super.detach();
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

        public static float bulletAccMultiplier() {
            return 1+0.2f*Dungeon.hero.pointsInTalent(Talent.NONOMI_ARMOR3_2);
        }
    }

    public static class BipodTracker extends FlavourBuff {
        {
            type = buffType.POSITIVE;
        }

        public static final float DURATION = 4f;

        @Override
        public int icon() {
            return BuffIndicator.TIME;
        }

        @Override
        public void tintIcon(Image icon) {
            icon.hardlight(1, 1, 0);
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0, (DURATION - visualcooldown()) / DURATION);
        }
    }
}

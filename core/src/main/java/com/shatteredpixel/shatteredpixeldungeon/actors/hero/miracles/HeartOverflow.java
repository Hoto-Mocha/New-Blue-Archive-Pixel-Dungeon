package com.shatteredpixel.shatteredpixeldungeon.actors.hero.miracles;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.mari.IdolConcert;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CrossNecklace;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;

public class HeartOverflow extends MariMiracle {
    public static HeartOverflow INSTANCE = new HeartOverflow();

    @Override
    public int icon() {
        return HeroIcon.HEART_OVERFLOW;
    }

    @Override
    public float chargeUse(Hero hero) {
        return 10;
    }

    @Override
    public String desc() {
        int interval = 6-Dungeon.hero.pointsInTalent(Talent.MARI_ARMOR3_3);
        int healing = 5;
        return Messages.get(this, "desc", interval, healing) + "\n\n" + Messages.get(this, "charge_cost", (int)chargeUse(Dungeon.hero));
    }

    @Override
    public boolean canCast(Hero hero) {
        return super.canCast(hero) && hero.hasTalent(Talent.MARI_ARMOR3_3) && hero.buff(HeartOverflowBuff.class) == null;
    }

    @Override
    public void onCast(CrossNecklace cross, Hero hero) {
        hero.busy();
        hero.sprite.operate(hero.pos, new Callback() {
            @Override
            public void call() {
                hero.sprite.idle();
                hero.spendAndNext(Actor.TICK);
            }
        });
        Buff.affect(hero, HeartOverflowBuff.class).setHeal(5, 6-hero.pointsInTalent(Talent.MARI_ARMOR3_3));

        onMiracleCast(cross, hero);
    }

    public static class HeartOverflowBuff extends Buff {

        private int healingLeft;
        private int interval;

        {
            actPriority = HERO_PRIO - 4;

            type = buffType.POSITIVE;
        }

        @Override
        public boolean act(){
            target.heal(healingPerTick());
            if (target.buff(IdolConcert.ConcertBuff.class) == null) {
                healingLeft -= healingPerTick();
            }
            if (healingLeft <= 0) {
                detach();
            }
            spend(interval);

            return true;
        }

        private int healingPerTick(){
            return 1;
        }

        public void setHeal(int amount, int interval){
            healingLeft = Math.max(healingLeft, amount);
            this.interval = interval;
        }

        @Override
        public void fx(boolean on) {
            if (on) target.sprite.add(CharSprite.State.HEARTS);
            else    target.sprite.remove(CharSprite.State.HEARTS);
        }

        private static final String LEFT = "left";
        private static final String INTERVAL = "interval";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(LEFT, healingLeft);
            bundle.put(INTERVAL, interval);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            healingLeft = bundle.getInt(LEFT);
            interval = bundle.getInt(INTERVAL);
        }

        @Override
        public int icon() {
            return BuffIndicator.HEALING;
        }

        @Override
        public String iconTextDisplay() {
            return Integer.toString(healingLeft);
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc", interval, healingLeft);
        }
    }
}

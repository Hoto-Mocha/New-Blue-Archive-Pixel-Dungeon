package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.SMG;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.CounterBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.KindOfWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.Gun;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.SpecialGun;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class BeyondTheLumination extends SMG implements SpecialGun {
    public static final int HEAL_COUNT = 30;

    {
        image = ItemSpriteSheet.BEYOND_THE_LUMINATION;
        tier = 4;
        shootingAccuracy = 0.8f;
        adjacentShootingAccuracy = 1.25f;
    }

    @Override
    public int baseBulletMax(int lvl) {
        return 2 * (tier()) +
                Math.round(0.5f * lvl * (tier()-1)); //3티어 성능
    }

    @Override
    public int defenseFactor( Char owner ) {
        return DRMax();
    }

    public int DRMax(){
        return DRMax(buffedLvl());
    }

    public int DRMax(int lvl){
        return 4 + lvl;
    }

    public static void defenseProc(Hero hero, KindOfWeapon weapon, int wepDr) {
        if (weapon instanceof BeyondTheLumination && wepDr > 0) {
            Buff.affect(hero, BeyondTheLumination.DRCount.class).countUp(wepDr);
        }
    }

    public String statsInfo(){
        if (isIdentified()){
            return Messages.get(this, "stats_desc", 4+buffedLvl(), HEAL_COUNT);
        } else {
            return Messages.get(this, "typical_stats_desc", 4, HEAL_COUNT);
        }
    }

    public static class DRCount extends CounterBuff {
        @Override
        public void countUp(float inc) {
            super.countUp(inc);
            if (count() >= HEAL_COUNT) {
                target.heal(1);
                detach();
            }
        }
    }

    public static class Recipe extends BaseRecipe {

        @Override
        public Class<? extends Gun> ingredients() {
            return SMG.class;
        }

        @Override
        public Class<? extends Gun> result() {
            return BeyondTheLumination.class;
        }
    }
}

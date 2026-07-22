package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.SG;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.Gun;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class SG extends Gun {
    {
        max_round = 2;
        round = max_round;
        shotPerShoot = 5;
        spread = true;
    }

    @Override
    public int STRReq(int lvl) {
        int req = super.STRReq(lvl);
        if (Dungeon.hero != null && Dungeon.hero.heroClass == HeroClass.HOSHINO){
            req -= 1;
        }
        return req;
    }

    @Override
    public int bulletUse() {
        return maxRound()-round;
    }

    @Override
    public int baseBulletMax(int lvl) {
        return  (tier()+3) + // 초기 피해량 4/5/6/7/8
                Math.round(0.5f * lvl * (tier())); //2강 당 1/2/3/4/5 증가
    }

    @Override
    public Bullet knockBullet(){
        return new SGBullet();
    }

    public class SGBullet extends Bullet {
        {
            image = ItemSpriteSheet.TRIPLE_BULLET;
        }

        @Override
        protected float adjacentAccFactor(Char owner, Char target) {
            if (Dungeon.level.adjacent( owner.pos, target.pos )) {
                return super.adjacentAccFactor(owner, target) * 3f;
            } else {
                return super.adjacentAccFactor(owner, target);
            }
        }
    }

    public static class PlaceHolder extends Gun.PlaceHolder {
        {
            image = ItemSpriteSheet.SG_PLACEHOLDER;
        }

        @Override
        public boolean isSimilar(Item item) {
            return item instanceof SG;
        }
    }
}

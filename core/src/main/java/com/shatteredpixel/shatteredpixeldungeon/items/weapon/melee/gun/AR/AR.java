package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.AR;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.Gun;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class AR extends Gun {

    {
        max_round = 4;
        round = max_round;
    }

    @Override
    public int STRReq(int lvl) {
        int req = super.STRReq(lvl);
        if (Dungeon.hero != null && Dungeon.hero.heroClass == HeroClass.SHIROKO){
            req -= 1;
        }
        return req;
    }

    @Override
    public int baseBulletMax(int lvl) {
        return 4 * (tier() + 1) +
                lvl * (tier() + 1);
    }

    @Override
    public Bullet knockBullet(){
        return new ARBullet();
    }

    public class ARBullet extends Bullet {
        {
            image = ItemSpriteSheet.SINGLE_BULLET;
        }
    }

    public static AR getAR(int tier) {
        switch (tier) {
            case 5:
                return new AR_T5();
            case 4:
                return new AR_T4();
            case 3:
                return new AR_T3();
            case 2:
                return new AR_T2();
            case 1: default:
                return new AR_T1();
        }
    }
}

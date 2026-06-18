package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.SR;

import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.Gun;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class SR extends Gun {

    {
        max_round = 2;
        round = max_round;
        reload_time = 3f;
        shootingAccuracy = 1.5f;
    }

    @Override
    public int baseBulletMax(int lvl) {
        return 4 * (tier()+2) +
                lvl * (tier()+2);
    }

    @Override
    public Bullet knockBullet(){
        return new SRBullet();
    }

    public class SRBullet extends Bullet {
        {
            image = ItemSpriteSheet.SNIPER_BULLET;
        }
    }

    public static SR getSR(int tier) {
        switch (tier) {
            case 5:
                return new SR_T5();
            case 4:
                return new SR_T4();
            case 3:
                return new SR_T3();
            case 2:
                return new SR_T2();
            case 1: default:
                return new SR_T1();
        }
    }
}

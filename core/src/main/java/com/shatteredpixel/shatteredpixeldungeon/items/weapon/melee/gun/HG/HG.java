package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.HG;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.Gun;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class HG extends Gun {

    {
        max_round = 4;
        round = max_round;
        shootingSpeed = 0.5f;
        reload_time = 1f;
    }

    @Override
    public int STRReq(int lvl) {
        int req = super.STRReq(lvl);
        if (Dungeon.hero != null && Dungeon.hero.heroClass == HeroClass.NOA){
            req -= 1;
        }
        return req;
    }

    @Override
    public int baseBulletMax(int lvl) {
        return 2 * (tier() + 1) +
                lvl * (tier() + 1);
    }

    @Override
    public Bullet knockBullet(){
        return new HGBullet();
    }

    public class HGBullet extends Bullet {
        {
            image = ItemSpriteSheet.SINGLE_BULLET;
        }
    }

    public static class PlaceHolder extends Gun.PlaceHolder {
        {
            image = ItemSpriteSheet.HG_PLACEHOLDER;
        }

        @Override
        public boolean isSimilar(Item item) {
            return item instanceof HG;
        }
    }
}

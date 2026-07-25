package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.MT;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.nonomi.Bipod;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.Gun;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class MT extends Gun {

    {
        max_round = 2;
        round = max_round;
        reload_time = 2f;
        shootingSpeed = 1.5f;
        explode = true;
        quickUse = true;
        ignoreWall = true;
    }

    @Override
    protected int baseBulletMin(int lvl) {
        return super.baseBulletMin(lvl)*2;
    }

    @Override
    public int baseBulletMax(int lvl) {
        return 5 * tier() +
                lvl * tier();
    }

    @Override
    public Bullet knockBullet() {
        return new MTBullet();
    }

    public class MTBullet extends Bullet {
        {
            image = ItemSpriteSheet.GRENADE_RED;
        }

        @Override
        public float delayFactor(Char user) {
            float speed = super.delayFactor(user);

            if (!MT.this.isEquipped(Dungeon.hero)) {
                speed *= 2f;
            }

            return speed;
        }

        @Override
        public float accuracyFactor(Char owner, Char target) {
            float accuracy = super.accuracyFactor(owner, target);
            if (Dungeon.level.heroFOV[target.pos]) {
                accuracy *= 0.1f;
            }
            return accuracy;
        }
    }

}

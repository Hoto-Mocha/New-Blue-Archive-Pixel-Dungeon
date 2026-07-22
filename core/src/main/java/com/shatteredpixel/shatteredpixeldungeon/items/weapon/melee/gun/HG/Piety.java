package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.HG;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.SpecialGun;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

public class Piety extends HG implements SpecialGun {
    {
        image = ItemSpriteSheet.PIETY;
        tier = 4;
        shootingSpeed = 1;
        max_round = 2;
        round = max_round;
        reload_time = 1f;
    }

    @Override
    public int bulletMin() {
        Hero hero = Dungeon.hero;
        if (hero != null) {
            Char enemy = hero.attackTarget();
            if (Char.hasProp(enemy, Char.Property.DEMONIC) || Char.hasProp(enemy, Char.Property.UNDEAD)) {
                return bulletMax();
            }
        }
        return super.bulletMin();
    }

    @Override
    public int baseBulletMax(int lvl) {
        return 3 * (tier() + 1) +
                lvl * (tier() + 1);
    }

    @Override
    public Bullet knockBullet() {
        return new PietyBullet();
    }

    public class PietyBullet extends HGBullet {
        {
            image = ItemSpriteSheet.GHOST_BULLET;
        }

        @Override
        public int proc(Char attacker, Char defender, int damage) {
            if (Char.hasProp(defender, Char.Property.DEMONIC) || Char.hasProp(defender, Char.Property.UNDEAD)) {
                defender.sprite.emitter().start( ShadowParticle.UP, 0.05f, 10+buffedLvl() );
                Sample.INSTANCE.play(Assets.Sounds.BURNING);
            }
            return super.proc(attacker, defender, damage);
        }

        @Override
        public void throwSound() {
            Sample.INSTANCE.play( Assets.Sounds.HIT_CRUSH, 1, Random.Float(0.25f, 0.5f) ); //더 낮은 음
        }
    }
}

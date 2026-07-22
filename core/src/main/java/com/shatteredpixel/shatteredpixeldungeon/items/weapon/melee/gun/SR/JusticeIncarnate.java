package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.SR;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bless;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.JusticeParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.SpecialGun;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class JusticeIncarnate extends SR implements SpecialGun {
    {
        image = ItemSpriteSheet.JUSTICE_INCARNATE;
        tier = 5;
    }

    @Override
    protected int baseBulletMin(int lvl) {
        return (tier() - 1 + lvl)*2;
    }

    @Override
    public int baseBulletMax(int lvl) {
        return  5 * (tier()+1) +
                lvl * (tier()+1); //4티어 성능
    }

    @Override
    public Bullet knockBullet() {
        return new JusticeIncarnateBullet();
    }

    public class JusticeIncarnateBullet extends SRBullet {
        @Override
        public int proc(Char attacker, Char defender, int damage) {
            if (Random.Float() < (3f+buffedLvl())/(15f+buffedLvl())) {
                defender.damage(damage, new Bless());
                CellEmitter.heroCenter(defender.pos).burst(JusticeParticle.factory(), 1);
            }
            return super.proc(attacker, defender, damage);
        }
    }
}

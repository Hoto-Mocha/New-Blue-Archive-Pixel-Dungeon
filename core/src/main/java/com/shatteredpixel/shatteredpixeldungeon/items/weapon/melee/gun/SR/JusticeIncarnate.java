package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.SR;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bless;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.JusticeParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.Gun;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.SpecialGun;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

import java.util.ArrayList;

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
            int finalDmg = super.proc(attacker, defender, damage);
            if (Random.Float() < (3f+buffedLvl())/(15f+buffedLvl())) {
                defender.damage(finalDmg, new Bless());
                CellEmitter.heroCenter(defender.pos).burst(JusticeParticle.factory(), 1);
            }
            return finalDmg;
        }
    }

    public static class Recipe extends BaseRecipe {

        @Override
        public ArrayList<Class<? extends Gun>> ingredients() {
            ArrayList<Class<? extends Gun>> result = new ArrayList<>();
            result.add(SR_T1.class);
            result.add(SR_T2.class);
            result.add(SR_T3.class);
            result.add(SR_T4.class);
            result.add(SR_T5.class);
            return result;
        }

        @Override
        public Class<? extends Gun> result() {
            return JusticeIncarnate.class;
        }
    }
}

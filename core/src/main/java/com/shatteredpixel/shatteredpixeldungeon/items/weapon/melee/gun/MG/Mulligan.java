package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.MG;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.CursedWand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.SpecialGun;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

public class Mulligan extends MG implements SpecialGun {
    {
        image = ItemSpriteSheet.MULLIGAN;
        tier = 4;
    }

    @Override
    public Bullet knockBullet() {
        return new MulliganBullet();
    }

    public class MulliganBullet extends MGBullet {
        @Override
        public int proc(Char attacker, Char defender, int damage) {
            randomEffect(attacker, defender, buffedLvl());
            return super.proc(attacker, defender, damage);
        }
    }

    public void randomEffect(Char owner, Char enemy, int lvl) {
        Ballistica aim = new Ballistica(owner.pos, enemy.pos, Ballistica.STOP_TARGET);
        CursedWand.randomValidEffect(null, owner, aim, false).effect(null, owner, aim, Random.Float() < (1f+lvl)/(6f+lvl));
    }
}

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.SG;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Amok;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.SpecialGun;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class ShootingStar extends SG implements SpecialGun {
    {
        image = ItemSpriteSheet.SHOOTING_STAR;
        tier = 4;
    }

    @Override
    public int baseBulletMax(int lvl) {
        return  (tier()+2) +
                Math.round(0.5f * lvl * (tier()-1)); //3티어급 성능
    }

    @Override
    public Bullet knockBullet() {
        return new ShootingStarBullet();
    }

    public class ShootingStarBullet extends SGBullet {
        @Override
        public void onShoot(boolean shootAll, boolean useRound) {
            curUser.sprite.centerEmitter().start( Speck.factory( Speck.SCREAM ), 0.3f, 3 );
            for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
                mob.beckon( curUser.pos );
            }
            super.onShoot(shootAll, useRound);
        }

        @Override
        public int proc(Char attacker, Char defender, int damage) {
            if (!(defender.isImmune(Amok.class))) {
                Buff.affect(defender, Amok.class, 2f);
            }
            return super.proc(attacker, defender, damage);
        }
    }
}

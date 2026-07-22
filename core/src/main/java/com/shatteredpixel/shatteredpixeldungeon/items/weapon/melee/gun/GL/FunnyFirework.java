package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.GL;

import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Light;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.SpecialGun;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class FunnyFirework extends GL implements SpecialGun {

    {
        image = ItemSpriteSheet.FUNNY_FIREWORK;
        tier = 4;
        selfHarm = false;
    }

    @Override
    public Bullet knockBullet(){
        return new FunnyFireworkBullet();
    }

    public class FunnyFireworkBullet extends GLBullet {
        @Override
        public int proc(Char attacker, Char defender, int damage) {
            if (!defender.isImmune(Blindness.class)) {
                Buff.affect(defender, Blindness.class, 2f);
            }
            return super.proc(attacker, defender, damage);
        }

        @Override
        public void onShoot(boolean shootAll, boolean useRound) {
            super.onShoot(shootAll, useRound);

            if (Dungeon.level.viewDistance < 6 ){
                GameScene.flash(0x60FFFFFF);
                if (Dungeon.isChallenged(Challenges.DARKNESS)){
                    Buff.prolong( curUser, Light.class, 2f + buffedLvl());
                } else {
                    Buff.prolong( curUser, Light.class, 10f+buffedLvl()*5);
                }
            }
        }
    }
}

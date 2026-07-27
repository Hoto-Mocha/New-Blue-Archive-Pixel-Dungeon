package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.AR;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.Gun;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.SpecialGun;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class TacticalTherapy extends AR {
    {
        image = ItemSpriteSheet.TACTICAL_THERAPY;
        tier = 3;
    }

    @Override
    public Bullet knockBullet() {
        return new TacticalTherapyBullet();
    }

    public class TacticalTherapyBullet extends ARBullet {
        @Override
        public int proc(Char attacker, Char defender, int damage) {
            int dmg = super.proc(attacker, defender, damage);
            if (defender.alignment == Char.Alignment.ALLY) {
                defender.heal(Math.round(dmg*0.4f));
                return 0;
            }
            return dmg;
        }
    }

    public static class Recipe extends SpecialGun.BaseRecipe {

        @Override
        public Class<? extends Gun> ingredients() {
            return AR.class;
        }

        @Override
        public Class<? extends Gun> result() {
            return TacticalTherapy.class;
        }
    }
}

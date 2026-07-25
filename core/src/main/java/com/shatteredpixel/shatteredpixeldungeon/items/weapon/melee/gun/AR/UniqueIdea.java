package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.AR;

import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.Gun;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.SpecialGun;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

import java.util.ArrayList;

public class UniqueIdea extends AR implements SpecialGun {
    {
        image = ItemSpriteSheet.UNIQUE_IDEA;
        tier = 4;
    }

    @Override
    public int baseBulletMax(int lvl) {
        return 4 * (tier() - 1) +   //2티어급 기본 피해량
                lvl * (tier() + 3); //6티어급 강화 효율
    }

    public static class Recipe extends BaseRecipe {

        @Override
        public ArrayList<Class<? extends Gun>> ingredients() {
            ArrayList<Class<? extends Gun>> result = new ArrayList<>();
            result.add(AR_T1.class);
            result.add(AR_T2.class);
            result.add(AR_T3.class);
            result.add(AR_T4.class);
            result.add(AR_T5.class);
            return result;
        }

        @Override
        public Class<? extends Gun> result() {
            return UniqueIdea.class;
        }
    }
}

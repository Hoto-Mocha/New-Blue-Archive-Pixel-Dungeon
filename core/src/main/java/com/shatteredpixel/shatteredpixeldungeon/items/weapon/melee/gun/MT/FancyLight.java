package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.MT;

import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.GL.GL;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.Gun;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.SpecialGun;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class FancyLight extends MT implements SpecialGun {

    {
        image = ItemSpriteSheet.FACNY_LIGHT;
        tier = 4;
    }

    public static class Recipe extends BaseRecipe {

        @Override
        public Class<? extends Gun> ingredients() {
            return GL.class;
        }

        @Override
        public Class<? extends Gun> result() {
            return FancyLight.class;
        }
    }
}

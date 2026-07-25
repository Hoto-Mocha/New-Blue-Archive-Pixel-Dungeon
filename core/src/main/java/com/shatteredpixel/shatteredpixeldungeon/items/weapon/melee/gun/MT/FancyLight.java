package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.MT;

import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.AR.AR_T1;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.AR.AR_T2;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.AR.AR_T3;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.AR.AR_T4;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.AR.AR_T5;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.AR.UniqueIdea;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.GL.GL_T1;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.GL.GL_T2;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.GL.GL_T3;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.GL.GL_T4;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.GL.GL_T5;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.Gun;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.SpecialGun;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

import java.util.ArrayList;

public class FancyLight extends MT implements SpecialGun {

    {
        image = ItemSpriteSheet.FACNY_LIGHT;
        tier = 4;
    }

    public static class Recipe extends BaseRecipe {

        @Override
        public ArrayList<Class<? extends Gun>> ingredients() {
            ArrayList<Class<? extends Gun>> result = new ArrayList<>();
            result.add(GL_T1.class);
            result.add(GL_T2.class);
            result.add(GL_T3.class);
            result.add(GL_T4.class);
            result.add(GL_T5.class);
            return result;
        }

        @Override
        public Class<? extends Gun> result() {
            return FancyLight.class;
        }
    }
}

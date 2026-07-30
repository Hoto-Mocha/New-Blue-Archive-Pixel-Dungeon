package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.MG;

import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.Gun;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.SpecialGun;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class TrickOrTrick extends MG implements SpecialGun {
    {
        image = ItemSpriteSheet.TRICK_OR_TRICK;
        tier = 5;
    }

    @Override
    public int STRReq(int lvl) {
        return super.STRReq(lvl)+2; //기본 힘 요구 수치 20
    }

    @Override
    public int baseBulletMax(int lvl) {
        return 3 * (tier()+2) + //기존 5티어 대비 기본 최대 피해량 50% 증가
                Math.round(0.5f * lvl * (tier()+1)); //2강 당 2/3/4/5/6 증가
    }

    public static class Recipe extends BaseRecipe {

        @Override
        public Class<? extends Gun> ingredients() {
            return MG.class;
        }

        @Override
        public Class<? extends Gun> result() {
            return TrickOrTrick.class;
        }
    }
}

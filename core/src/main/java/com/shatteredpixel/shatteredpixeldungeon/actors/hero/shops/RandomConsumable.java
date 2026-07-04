package com.shatteredpixel.shatteredpixeldungeon.actors.hero.shops;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;

public class RandomConsumable extends YuzuShopContent {
    public static RandomConsumable INSTANCE = new RandomConsumable();

    @Override
    public int icon() {
        return HeroIcon.SHOP_1;
    }

    @Override
    public String desc() {
        return super.desc();
    }

    @Override
    public void onSelect(Hero hero) {

    }

    @Override
    public float goldUse(Hero hero) {
        return 150 * (1+(int)(hero.lvl/5));
    }

    @Override
    public boolean canSelect(Hero hero) {
        return super.canSelect(hero);
    }
}

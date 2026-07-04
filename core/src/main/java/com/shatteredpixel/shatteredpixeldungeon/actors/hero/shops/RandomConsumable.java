package com.shatteredpixel.shatteredpixeldungeon.actors.hero.shops;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfWealth;
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
        Item prize = RingOfWealth.genConsumableDrop(0);
        Dungeon.level.drop(prize, hero.pos).sprite.drop();
        RingOfWealth.showFlareForBonusDrop(hero.sprite);
    }

    @Override
    public int creditUse(Hero hero) {
        return 150 * (1+(hero.lvl/5));
    }

    @Override
    public boolean canSelect(Hero hero) {
        return super.canSelect(hero);
    }
}

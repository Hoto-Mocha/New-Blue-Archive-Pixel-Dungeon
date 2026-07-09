package com.shatteredpixel.shatteredpixeldungeon.actors.hero.shops;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.active.console.Console;
import com.shatteredpixel.shatteredpixeldungeon.items.active.console.ContinueConsole;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;

public class BuyContinueConsole extends YuzuShopContent {
    public static final BuyContinueConsole INSTANCE = new BuyContinueConsole();

    @Override
    public int icon() {
        return HeroIcon.CONTINUE_CONSOLE;
    }

    @Override
    public void onSelect(Hero hero) {
        Console prize = new ContinueConsole();
        hero.spend(-prize.pickupDelay());
        if (!prize.doPickUp(hero)) {
            Dungeon.level.drop(prize, hero.pos);
        }
    }

    @Override
    public int creditUse(Hero hero) {
        return 50*inflationParameter();
    }
}

package com.shatteredpixel.shatteredpixeldungeon.actors.hero.shops;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.active.console.Console;
import com.shatteredpixel.shatteredpixeldungeon.items.active.console.FighterConsole;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;

public class BuyFighterConsole extends YuzuShopContent {
    public static final BuyFighterConsole INSTANCE = new BuyFighterConsole();

    @Override
    public int icon() {
        return HeroIcon.FIGHTER_CONSOLE;
    }

    @Override
    public void onSelect(Hero hero) {
        Console prize = new FighterConsole();
        hero.spend(-prize.pickupDelay());
        if (!prize.doPickUp(hero)) {
            Dungeon.level.drop(prize, hero.pos);
        }
    }

    @Override
    public int creditUse(Hero hero) {
        return 300*inflationParameter();
    }

    @Override
    public boolean canSelect(Hero hero) {
        return super.canSelect(hero) && hero.hasTalent(Talent.YUZU_EX2_1);
    }
}

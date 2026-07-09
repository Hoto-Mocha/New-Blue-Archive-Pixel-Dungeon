package com.shatteredpixel.shatteredpixeldungeon.actors.hero.shops;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.active.console.Console;
import com.shatteredpixel.shatteredpixeldungeon.items.active.console.FantasyConsole;
import com.shatteredpixel.shatteredpixeldungeon.items.active.console.FighterConsole;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;

public class BuyFantasyConsole extends YuzuShopContent {
    public static final BuyFantasyConsole INSTANCE = new BuyFantasyConsole();

    @Override
    public int icon() {
        return HeroIcon.FANTASY_CONSOLE;
    }

    @Override
    public void onSelect(Hero hero) {
        Console prize = new FantasyConsole();
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
        return hero.hasTalent(Talent.YUZU_EX2_2);
    }
}

package com.shatteredpixel.shatteredpixeldungeon.items.active.console;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.console.fantasy.FantasyConsoleContent;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;

public class FantasyConsole extends Console {
    {
        image = ItemSpriteSheet.FANTASY_CONSOLE;
    }

    @Override
    public void showWindow(Hero hero) {
        Buff.affect(hero, FantasyConsoleContent.FantasyConsoleBuff.class).set();
        BuffIndicator.refreshHero();
        super.showWindow(hero);
        detach(hero.belongings.backpack);
    }

    @Override
    public int value() {
        return 300/5;
    }

}

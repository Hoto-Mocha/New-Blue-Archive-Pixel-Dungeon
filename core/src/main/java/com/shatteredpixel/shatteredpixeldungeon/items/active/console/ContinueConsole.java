package com.shatteredpixel.shatteredpixeldungeon.items.active.console;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.console.fighter.FighterConsoleContent;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class ContinueConsole extends Console {
    {
        image = ItemSpriteSheet.CONTINUE_CONSOLE;
    }

    @Override
    public void showWindow(Hero hero) {
        if (hero.buff(FighterConsoleContent.FighterConsoleBuff.class) != null) {
            new FighterConsole().showWindow(hero);
            detach(hero.belongings.backpack);
            return;
        }

        hero.yellW("no_active_console");
    }

    @Override
    public int value() {
        return 50/5;
    }
}

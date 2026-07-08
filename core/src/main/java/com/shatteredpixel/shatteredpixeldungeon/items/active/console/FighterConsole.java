package com.shatteredpixel.shatteredpixeldungeon.items.active.console;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.console.fighter.FighterConsoleContent;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndYuzuFighterConsole;

public class FighterConsole extends Console {
    {
        image = ItemSpriteSheet.FIGHTER_CONSOLE;
    }

    @Override
    public void showWindow(Hero hero) {
        Buff.affect(hero, FighterConsoleContent.FighterConsoleBuff.class).set();
        BuffIndicator.refreshHero();
        GameScene.show(new WndYuzuFighterConsole(this, hero));
        detach(hero.belongings.backpack);
    }

    @Override
    public int value() {
        return 300/5;
    }
}

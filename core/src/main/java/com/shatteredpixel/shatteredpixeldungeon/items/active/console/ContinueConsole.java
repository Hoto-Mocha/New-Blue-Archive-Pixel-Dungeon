package com.shatteredpixel.shatteredpixeldungeon.items.active.console;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.console.fantasy.FantasyConsoleContent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.console.fighter.FighterConsoleContent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.console.sandbox.SandboxConsoleContent;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndYuzuConsole;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndYuzuFighterConsole;

public class ContinueConsole extends Console {
    {
        image = ItemSpriteSheet.CONTINUE_CONSOLE;
    }

    @Override
    public void showWindow(Hero hero) {
        if (hero.buff(FighterConsoleContent.FighterConsoleBuff.class) != null) {
            GameScene.show(new WndYuzuFighterConsole(new FighterConsole(), hero, false));
            detach(hero.belongings.backpack);
            return;
        } else if (hero.buff(FantasyConsoleContent.FantasyConsoleBuff.class) != null) {
            GameScene.show(new WndYuzuConsole(new FantasyConsole(), hero, false));
            detach(hero.belongings.backpack);
            return;
        } else if (hero.buff(SandboxConsoleContent.SandboxConsoleBuff.class) != null) {
            GameScene.show(new WndYuzuConsole(new SandboxConsole(), hero, false));
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

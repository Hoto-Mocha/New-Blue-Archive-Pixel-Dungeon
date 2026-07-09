package com.shatteredpixel.shatteredpixeldungeon.items.active.console;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.console.YuzuConsoleContent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.console.fantasy.FantasyConsoleContent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.console.sandbox.SandboxConsoleContent;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;

public class SandboxConsole extends Console {
    {
        image = ItemSpriteSheet.SNADBOX_CONSOLE;
    }

    @Override
    public void showWindow(Hero hero) {
        for (Buff b: hero.buffs()) {
            if (b instanceof YuzuConsoleContent.ConsoleBuff && !(b instanceof SandboxConsoleContent.SandboxConsoleBuff)) {
                b.detach();
            }
        }
        Buff.affect(hero, SandboxConsoleContent.SandboxConsoleBuff.class).set();
        BuffIndicator.refreshHero();
        super.showWindow(hero);
        detach(hero.belongings.backpack);
    }

    @Override
    public int value() {
        return 300/5;
    }

}

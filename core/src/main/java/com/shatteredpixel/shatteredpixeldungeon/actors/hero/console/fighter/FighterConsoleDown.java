package com.shatteredpixel.shatteredpixeldungeon.actors.hero.console.fighter;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;

public class FighterConsoleDown extends FighterConsoleMove {
    public static final FighterConsoleDown INSTANCE = new FighterConsoleDown();

    @Override
    public int icon() {
        return HeroIcon.FIGHTER_DOWN;
    }

    @Override
    public int targetPos(Hero hero) {
        return hero.pos + Dungeon.level.width();
    }
}

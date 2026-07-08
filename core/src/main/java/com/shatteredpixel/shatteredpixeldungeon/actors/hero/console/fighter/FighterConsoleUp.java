package com.shatteredpixel.shatteredpixeldungeon.actors.hero.console.fighter;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;

public class FighterConsoleUp extends FighterConsoleMove {
    public static final FighterConsoleUp INSTANCE = new FighterConsoleUp();

    @Override
    public int icon() {
        return HeroIcon.FIGHTER_UP;
    }

    @Override
    public int targetPos(Hero hero) {
        return hero.pos - Dungeon.level.width();
    }
}

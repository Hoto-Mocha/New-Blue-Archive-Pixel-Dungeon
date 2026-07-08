package com.shatteredpixel.shatteredpixeldungeon.actors.hero.console.fighter;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;

public class FighterConsoleLeft extends FighterConsoleMove {
    public static final FighterConsoleLeft INSTANCE = new FighterConsoleLeft();

    @Override
    public int icon() {
        return HeroIcon.FIGHTER_LEFT;
    }

    @Override
    public int targetPos(Hero hero) {
        return hero.pos - 1;
    }
}

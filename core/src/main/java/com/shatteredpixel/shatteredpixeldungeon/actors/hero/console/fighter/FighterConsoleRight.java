package com.shatteredpixel.shatteredpixeldungeon.actors.hero.console.fighter;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;

public class FighterConsoleRight extends FighterConsoleMove {
    public static final FighterConsoleRight INSTANCE = new FighterConsoleRight();

    @Override
    public int icon() {
        return HeroIcon.FIGHTER_RIGHT;
    }

    @Override
    public int targetPos(Hero hero) {
        return hero.pos + 1;
    }
}

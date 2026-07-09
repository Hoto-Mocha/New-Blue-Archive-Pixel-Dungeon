package com.shatteredpixel.shatteredpixeldungeon.actors.hero.console.fighter;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;

public class MoveLeft extends Move {
    public static final MoveLeft INSTANCE = new MoveLeft();

    @Override
    public int icon() {
        return HeroIcon.FIGHTER_LEFT;
    }

    @Override
    public int targetPos(Hero hero) {
        return hero.pos - 1;
    }
}

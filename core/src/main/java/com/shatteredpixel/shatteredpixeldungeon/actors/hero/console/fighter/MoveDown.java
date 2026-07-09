package com.shatteredpixel.shatteredpixeldungeon.actors.hero.console.fighter;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;

public class MoveDown extends Move {
    public static final MoveDown INSTANCE = new MoveDown();

    @Override
    public int icon() {
        return HeroIcon.FIGHTER_DOWN;
    }

    @Override
    public int targetPos(Hero hero) {
        return hero.pos + Dungeon.level.width();
    }
}

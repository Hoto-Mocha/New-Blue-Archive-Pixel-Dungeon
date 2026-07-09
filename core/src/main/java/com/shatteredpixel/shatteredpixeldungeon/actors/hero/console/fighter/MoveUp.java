package com.shatteredpixel.shatteredpixeldungeon.actors.hero.console.fighter;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;

public class MoveUp extends Move {
    public static final MoveUp INSTANCE = new MoveUp();

    @Override
    public int icon() {
        return HeroIcon.FIGHTER_UP;
    }

    @Override
    public int targetPos(Hero hero) {
        return hero.pos - Dungeon.level.width();
    }
}

package com.shatteredpixel.shatteredpixeldungeon.actors.hero.console;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;

public class FighterFinish extends YuzuConsoleContent {
    public static final FighterFinish INSTANCE = new FighterFinish();

    @Override
    public void onSelect(Hero hero) {

    }

    @Override
    public int creditUse(Hero hero) {
        return 10*inflationParameter();
    }

    @Override
    public int icon() {
        return HeroIcon.FIGHTER_4;
    }
}

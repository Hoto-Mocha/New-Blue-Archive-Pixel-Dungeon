package com.shatteredpixel.shatteredpixeldungeon.actors.hero.console;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;

public class FighterPunch extends YuzuConsoleContent {
    public static final FighterPunch INSTANCE = new FighterPunch();

    @Override
    public void onSelect(Hero hero) {

    }

    @Override
    public int creditUse(Hero hero) {
        return 10*inflationParameter();
    }

    @Override
    public int icon() {
        return super.icon();
    }
}

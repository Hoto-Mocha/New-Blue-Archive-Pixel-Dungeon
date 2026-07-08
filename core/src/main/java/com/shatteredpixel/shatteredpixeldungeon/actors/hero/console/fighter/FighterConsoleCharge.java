package com.shatteredpixel.shatteredpixeldungeon.actors.hero.console.fighter;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;

public class FighterConsoleCharge extends FighterConsoleContent {
    public static final FighterConsoleCharge INSTANCE = new FighterConsoleCharge();

    @Override
    public int icon() {
        return HeroIcon.FIGHTER_CHARGE;
    }

    @Override
    public void execute(Hero hero) {
        super.execute(hero);
        Buff.affect(hero, FighterConsoleBuff.class).enhance();
        if (isEnhanced(hero)) {
            Buff.affect(hero, FighterConsoleBuff.class).countUp(2);
        }
        hero.spendAndNext(1f);
    }
}

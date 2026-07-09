package com.shatteredpixel.shatteredpixeldungeon.actors.hero.console.fantasy;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.console.YuzuConsoleContent;
import com.watabou.noosa.Image;

public abstract class FantasyConsoleContent extends YuzuConsoleContent {
    @Override
    public boolean usesTargeting() {
        return true;
    }

    @Override
    public boolean hideWindow() {
        return true;
    }

    @Override
    public boolean canSelect(Hero hero) {
        return hero.buff(FantasyConsoleBuff.class) != null && hero.buff(FantasyConsoleBuff.class).count() >=0;
    }

    @Override
    public void onContentExecuted(Hero hero) {
        Buff.affect(hero, FantasyConsoleBuff.class).countDown(countUse());
    }

    public int countUse() {
        return 1;
    }

    public int min(Hero hero) {
        return 10;
    }

    public int max(Hero hero) {
        return 30;
    }

    public int damageRoll(Hero hero) {
        return Hero.heroDamageIntRange(min(hero), max(hero));
    }

    //판타지 콘솔 버프
    public static class FantasyConsoleBuff extends ConsoleBuff {
        @Override
        public void tintIcon(Image icon) {
            icon.hardlight(0f, 0, 1f);
        }
    }
}

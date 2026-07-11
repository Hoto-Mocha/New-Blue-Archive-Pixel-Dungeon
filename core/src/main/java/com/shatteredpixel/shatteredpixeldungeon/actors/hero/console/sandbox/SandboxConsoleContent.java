package com.shatteredpixel.shatteredpixeldungeon.actors.hero.console.sandbox;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.console.YuzuConsoleContent;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.noosa.Image;

public abstract class SandboxConsoleContent extends YuzuConsoleContent {
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
        return hero.buff(SandboxConsoleBuff.class) != null && hero.buff(SandboxConsoleBuff.class).count() >=0;
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc") + "\n\n" + Messages.get(this, "token_cost", countUse());
    }

    @Override
    public void onContentExecuted(Hero hero) {
        Buff.affect(hero, SandboxConsoleBuff.class).countDown(countUse());
    }

    public boolean canBuild(int target) {
        return false;
    }

    //판타지 콘솔 버프
    public static class SandboxConsoleBuff extends ConsoleBuff {
        @Override
        public void tintIcon(Image icon) {
            icon.hardlight(1f, 1f, 0);
        }
    }
}

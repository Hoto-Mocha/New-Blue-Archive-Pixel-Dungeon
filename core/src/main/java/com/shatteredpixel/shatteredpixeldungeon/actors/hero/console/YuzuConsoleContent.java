package com.shatteredpixel.shatteredpixeldungeon.actors.hero.console;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.CounterBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.console.fighter.FighterConsoleCharge;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.console.fighter.FighterConsoleDown;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.console.fighter.FighterConsoleLeft;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.console.fighter.FighterConsoleRight;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.console.fighter.FighterConsoleStrongAttack;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.console.fighter.FighterConsoleUp;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.console.fighter.FighterConsoleWeakAttack;
import com.shatteredpixel.shatteredpixeldungeon.items.active.console.Console;
import com.shatteredpixel.shatteredpixeldungeon.items.active.console.FighterConsole;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndYuzuConsole;
import com.watabou.utils.Bundle;

import java.util.ArrayList;

public abstract class YuzuConsoleContent {
    public void onSelect(Hero hero) {
        if (useTargeting()) {
            GameScene.selectCell(selector);
        } else {
            execute(hero);
        }
    };

    public abstract void execute(Hero hero);

    public abstract boolean canSelect( Hero hero );

    public boolean useTargeting() {
        return false;
    }

    public boolean isEnhanced(Hero hero) {
        boolean enhanced = false;
        for (Buff b: hero.buffs()) {
            if (b instanceof ConsoleBuff) {
                enhanced = ((ConsoleBuff) b).isEnhanced();
                if (enhanced) {
                    enhanced = true;
                    break;
                }
            }
        }
        return enhanced;
    }

    public int icon(){
        return HeroIcon.NONE;
    }

    public void onContentSelect(Console console, Hero hero) {
        if (!hideWindow()) GameScene.show(new WndYuzuConsole(console, hero));
    }

    public boolean hideWindow() {
        return false;
    }

    public static ArrayList<YuzuConsoleContent> getContentList(Hero yuzu, Console console) {
        ArrayList<YuzuConsoleContent> contents = new ArrayList<>();

        if (console instanceof FighterConsole) {
            contents.add(FighterConsoleWeakAttack.INSTANCE);
            contents.add(FighterConsoleStrongAttack.INSTANCE);
            contents.add(FighterConsoleCharge.INSTANCE);
            contents.add(FighterConsoleLeft.INSTANCE);
            contents.add(FighterConsoleUp.INSTANCE);
            contents.add(FighterConsoleDown.INSTANCE);
            contents.add(FighterConsoleRight.INSTANCE);
        }

        return contents;
    }

    protected static CellSelector.Listener selector = new CellSelector.Listener() {
        @Override
        public void onSelect(Integer cell) {
            //do nothing by default
        }

        @Override
        public String prompt() {
            return "";
        }
    };

    public static class ConsoleBuff extends CounterBuff {
        private boolean enhanced = false;

        {
            type = buffType.POSITIVE;
        }

        public void set(int tokens) {
            countUp(tokens);
        }

        @Override
        public void countDown(float inc) {
            super.countDown(inc);
            enhanced = false;
            if (count() <= 0) detach();
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc", count());
        }

        public void enhance() {
            enhanced = true;
        }

        public boolean isEnhanced() {
            return enhanced;
        }

        private static final String ENHANCED = "enhanced";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(ENHANCED, enhanced);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            enhanced = bundle.getBoolean(ENHANCED);
        }
    }

}

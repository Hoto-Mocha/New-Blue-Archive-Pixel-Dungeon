package com.shatteredpixel.shatteredpixeldungeon.actors.hero.console;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.CounterBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.console.fantasy.BlackHole;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.console.fantasy.FireBall;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.console.fantasy.Hydropump;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.console.fantasy.IceLance;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.console.fantasy.Thunder;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.console.fighter.Charge;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.console.fighter.MoveDown;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.console.fighter.MoveLeft;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.console.fighter.MoveRight;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.console.fighter.StrongAttack;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.console.fighter.MoveUp;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.console.fighter.WeakAttack;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.console.sandbox.BarricadeBuild;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.console.sandbox.BridgeBuild;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.console.sandbox.Farming;
import com.shatteredpixel.shatteredpixeldungeon.items.active.console.Console;
import com.shatteredpixel.shatteredpixeldungeon.items.active.console.FantasyConsole;
import com.shatteredpixel.shatteredpixeldungeon.items.active.console.FighterConsole;
import com.shatteredpixel.shatteredpixeldungeon.items.active.console.SandboxConsole;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndYuzuConsole;
import com.watabou.utils.Bundle;

import java.util.ArrayList;

public abstract class YuzuConsoleContent {
    public void onSelect(Hero hero) {
        if (usesTargeting()) {
            GameScene.selectCell(selector);
        } else {
            if (execute(hero, -1)) {
                onContentExecuted(hero);
            }
        }
    };

    public abstract boolean execute(Hero hero, int target);

    public abstract boolean canSelect( Hero hero );

    public boolean usesTargeting() {
        return false;
    }

    public boolean isEnhanced(Hero hero) {
        boolean enhanced = false;
        for (Buff b: hero.buffs()) {
            if (b instanceof ConsoleBuff) {
                enhanced = ((ConsoleBuff) b).isEnhanced();
                if (enhanced) {
                    break;
                }
            }
        }
        return enhanced;
    }

    public int icon(){
        return HeroIcon.NONE;
    }

    public void onContentSelect(Console console, Hero hero) { //버튼을 눌렀을 때 작동
        if (!hideWindow()) GameScene.show(new WndYuzuConsole(console, hero));
    }

    public void onContentExecuted(Hero hero) { //컨텐츠 내용을 성공적으로 실행했을 때 작동
        //no nothing by default
    }

    public boolean hideWindow() {
        return false;
    }

    public static ArrayList<YuzuConsoleContent> getContentList(Hero yuzu, Console console) {
        ArrayList<YuzuConsoleContent> contents = new ArrayList<>();

        if (console instanceof FighterConsole) {
            contents.add(WeakAttack.INSTANCE);
            contents.add(StrongAttack.INSTANCE);
            contents.add(Charge.INSTANCE);
            contents.add(MoveLeft.INSTANCE);
            contents.add(MoveUp.INSTANCE);
            contents.add(MoveDown.INSTANCE);
            contents.add(MoveRight.INSTANCE);
        } else if (console instanceof FantasyConsole) {
            contents.add(FireBall.INSTANCE);
            contents.add(Hydropump.INSTANCE);
            contents.add(Thunder.INSTANCE);
            contents.add(IceLance.INSTANCE);
            contents.add(BlackHole.INSTANCE);
        } else if (console instanceof SandboxConsole) {
            contents.add(Farming.INSTANCE);
            contents.add(BarricadeBuild.INSTANCE);
            contents.add(BridgeBuild.INSTANCE);
        }

        return contents;
    }

    protected CellSelector.Listener selector = new CellSelector.Listener() {
        @Override
        public void onSelect(Integer cell) {
            if (cell == null) return;
            if (YuzuConsoleContent.this.execute(Dungeon.hero, cell)) {
                YuzuConsoleContent.this.onContentExecuted(Dungeon.hero);
            }
        }

        @Override
        public String prompt() {
            return Messages.get(YuzuConsoleContent.this, "prompt");
        }
    };

    public static class ConsoleBuff extends CounterBuff {
        private static final int MAX_COUNT = 10;
        private boolean enhanced = false;

        {
            type = buffType.POSITIVE;
        }

        @Override
        public int icon() {
            return BuffIndicator.CONSOLE;
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0, (MAX_COUNT-count())/(float)MAX_COUNT);
        }

        @Override
        public String iconTextDisplay() {
            return Integer.toString((int)count());
        }

        public void set() {
            countUp(MAX_COUNT);
        }

        @Override
        public void countUp(float inc) {
            if ((int)count() > MAX_COUNT) return;
            if ((int)count() + inc > MAX_COUNT) inc = MAX_COUNT - count();
            super.countUp(inc);
        }

        @Override
        public void countDown(float inc) {
            super.countDown(inc);
            enhanced = false;
            if (count() <= 0) detach();
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc", (int)count());
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

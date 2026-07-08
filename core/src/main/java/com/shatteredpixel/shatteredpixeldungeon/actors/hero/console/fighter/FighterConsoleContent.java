package com.shatteredpixel.shatteredpixeldungeon.actors.hero.console.fighter;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.console.YuzuConsoleContent;
import com.shatteredpixel.shatteredpixeldungeon.items.active.console.Console;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndYuzuFighterConsole;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;

public abstract class FighterConsoleContent extends YuzuConsoleContent {

    @Override
    public boolean execute(Hero hero) {
        if (!hero.ready) return false;
        if (hero.buff(FighterConsoleBuff.class) == null) return false;
        return true;
    }

    @Override
    public boolean canSelect(Hero hero) {
        return hero.buff(FighterConsoleBuff.class) != null && hero.buff(FighterConsoleBuff.class).count() >=0;
    }

    public void onContentSelect(Console console, Hero hero) {
        if (!hideWindow()) GameScene.show(new WndYuzuFighterConsole(console, hero));
    }

    @Override
    public void onContentExecuted(Hero hero) {
        Buff.affect(hero, FighterConsoleContent.FighterConsoleBuff.class).countDown(1);
    }

    //파이터 콘솔 버프
    public static class FighterConsoleBuff extends ConsoleBuff {
        boolean attackEnhanced = false;

        @Override
        public int icon() {
            return BuffIndicator.HASTE;
        }

        @Override
        public void tintIcon(Image icon) {
            icon.hardlight(1f, 0, 0);
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0, (10-count())/10f);
        }

        @Override
        public String iconTextDisplay() {
            return Integer.toString((int)count());
        }

        @Override
        public void countDown(float inc) {
            if (!enhancedThisTurn) attackEnhanced = false;
            super.countDown(inc);
        }

        public void attackEnhance() {
            attackEnhanced = true;
        }

        public boolean isAttackEnhanced() {
            return attackEnhanced;
        }

        private static final String ATTACK_ENHANCED = "attackEnhanced";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(ATTACK_ENHANCED, attackEnhanced);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            attackEnhanced = bundle.getBoolean(ATTACK_ENHANCED);
        }
    }


    // *** Weapon-like properties ***

    private static float tier(int str){
        float tier = Math.max(1, (str - 8)/2f);
        //each str point after 18 is half as effective
        if (tier > 5){
            tier = 5 + (tier - 5) / 2f;
        }
        return tier;
    }

    public static int damageRoll( Hero hero ){
        int level = 0;
        float tier = tier(hero.STR());
        int dmg = Hero.heroDamageIntRange(min(level, tier), max(level, tier));
        return dmg;
    }

    //same as equivalent tier weapon
    private static int min(int lvl, float tier){
        if (lvl <= 0) tier = 1; //tier is forced to 1 if cursed

        return Math.max( 0, Math.round(
                tier +  //base
                        lvl     //level scaling
        ));
    }

    //same as equivalent tier weapon
    private static int max(int lvl, float tier){
        if (lvl <= 0) tier = 1; //tier is forced to 1 if cursed

        return Math.max( 0, Math.round(
                5*(tier+1) +    //base
                        lvl*(tier+1)    //level scaling
        ));
    }
}

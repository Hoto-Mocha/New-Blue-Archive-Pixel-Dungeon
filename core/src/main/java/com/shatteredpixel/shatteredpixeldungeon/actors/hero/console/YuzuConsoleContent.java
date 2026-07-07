package com.shatteredpixel.shatteredpixeldungeon.actors.hero.console;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.active.console.Console;
import com.shatteredpixel.shatteredpixeldungeon.items.active.console.FighterConsole;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndYuzuConsole;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

import java.util.ArrayList;

public abstract class YuzuConsoleContent {
    public abstract void onSelect(Hero hero);

    public abstract int creditUse(Hero hero);

    public boolean canSelect( Hero hero ){
        return Dungeon.gold >= creditUse(hero);
    }

    public int icon(){
        return HeroIcon.NONE;
    }

    public void onContentSelect(Console console, Hero hero) {
        Dungeon.gold -= creditUse(hero);
        if (hero.hasTalent(Talent.YUZU_T2_5)) {
            new Gold(Math.round(creditUse(hero)*0.05f*hero.pointsInTalent(Talent.YUZU_T2_5))).doPickUp(hero, hero.pos);
        } else {
            Sample.INSTANCE.play( Assets.Sounds.GOLD, 1, 1, Random.Float( 0.9f, 1.1f ) );
        }
        if (!hideWindow()) GameScene.show(new WndYuzuConsole(console, hero));
    }

    public boolean hideWindow() {
        return false;
    }

    public static int inflationParameter() {
        //based on the depth of deepest floor that has been visited
        return 1+(Math.min(Statistics.deepestFloor, 25)/5);
    }

    public static ArrayList<YuzuConsoleContent> getContentList(Hero yuzu, Console console) {
        ArrayList<YuzuConsoleContent> contents = new ArrayList<>();

        if (console instanceof FighterConsole) {
            contents.add(FighterPunch.INSTANCE);
        }

        return contents;
    }

}

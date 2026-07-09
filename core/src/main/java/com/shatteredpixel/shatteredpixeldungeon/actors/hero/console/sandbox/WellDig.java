package com.shatteredpixel.shatteredpixeldungeon.actors.hero.console.sandbox;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.watabou.noosa.audio.Sample;

public class WellDig extends SandboxConsoleContent {
    public static final WellDig INSTANCE = new WellDig();

    @Override
    public int icon() {
        return HeroIcon.SANDBOX_WELL;
    }

    @Override
    public boolean execute(Hero hero, int target) {
        if (Dungeon.level.distance(hero.pos, target) > 1) {
            hero.yellW("need_to_be_adjacent");
            return false;
        }
        if (!canBuild(target)) {
            hero.yellW("cannot_do");
            return false;
        }

        hero.sprite.zap(target);
        hero.spendAndNext(1);

        Dungeon.level.map[target] = Terrain.EMPTY_WELL;
        CellEmitter.get( target ).start(Speck.factory(Speck.ROCK), 0.07f, 10);
        CellEmitter.center( target ).burst( Speck.factory( Speck.STAR ), 7 );
        Sample.INSTANCE.play(Assets.Sounds.ROCKS);
        Sample.INSTANCE.play(Assets.Sounds.EVOKE);

        GameScene.updateMap(target);

        return true;
    }

    @Override
    public int countUse() {
        return 10;
    }

    @Override
    public boolean canBuild(int target) {
        return Dungeon.level.passable[target] && !Dungeon.level.avoid[target] && Dungeon.level.map[target] != Terrain.OPEN_DOOR;
    }
}

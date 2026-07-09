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

public class BridgeBuild extends SandboxConsoleContent {
    public static final BridgeBuild INSTANCE = new BridgeBuild();

    @Override
    public int icon() {
        return HeroIcon.SANDBOX_BRIDGE;
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
        Dungeon.level.map[target] = Terrain.EMPTY_SP;
        hero.spendAndNext(1);

        Dungeon.level.pit[target] = false;
        Dungeon.level.avoid[target] = false;
        Dungeon.level.passable[target] = true;

        GameScene.updateMap(target);
        CellEmitter.get( target ).start(Speck.factory(Speck.ROCK), 0.07f, 10);
        CellEmitter.center( target ).burst( Speck.factory( Speck.STAR ), 7 );
        Sample.INSTANCE.play(Assets.Sounds.ROCKS);
        Sample.INSTANCE.play(Assets.Sounds.EVOKE);
        if (Dungeon.level.heroFOV[target]) Dungeon.observe();

        return true;
    }

    @Override
    public boolean canBuild(int target) {
        return Dungeon.level.map[target] == Terrain.CHASM;
    }


}

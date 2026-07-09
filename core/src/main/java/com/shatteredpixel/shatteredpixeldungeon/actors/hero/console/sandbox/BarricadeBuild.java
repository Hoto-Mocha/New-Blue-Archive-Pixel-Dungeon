package com.shatteredpixel.shatteredpixeldungeon.actors.hero.console.sandbox;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.watabou.noosa.audio.Sample;

public class BarricadeBuild extends SandboxConsoleContent {
    public static final BarricadeBuild INSTANCE = new BarricadeBuild();

    @Override
    public int icon() {
        return HeroIcon.SANDBOX_BARRICADE;
    }

    @Override
    public boolean canBuild(int target) {
        return Dungeon.level.map[target] == Terrain.DOOR || (Dungeon.level.passable[target] && !Dungeon.level.avoid[target]);
    }

    @Override
    public boolean execute(Hero hero, int target) {
        if (Dungeon.level.distance(hero.pos, target) > 1) {
            hero.yellW("need_to_be_adjacent");
            return false;
        }
        if (target == hero.pos && Dungeon.level.map[target] == Terrain.OPEN_DOOR) {
            hero.yellW("cannot_build");
            return false;
        }

        hero.sprite.zap(target);
        if (Dungeon.level.map[target] != Terrain.OPEN_DOOR && Dungeon.level.map[target] != Terrain.DOOR) {
            if (Dungeon.level.map[target] == Terrain.EMPTY_SP) {
                Level.set(target, Terrain.STATUE_SP);
            } else {
                Level.set(target, Terrain.STATUE);
            }
        } else {
            Level.set(target, Terrain.BARRICADE);
        }
        hero.spendAndNext(1);

        GameScene.updateMap(target);
        CellEmitter.get( target - Dungeon.level.width() ).start(Speck.factory(Speck.ROCK), 0.07f, 10);
        Sample.INSTANCE.play(Assets.Sounds.ROCKS);
        if (Dungeon.level.heroFOV[target]) Dungeon.observe();
        return true;
    }

    @Override
    public int countUse() {
        return 2;
    }

    @Override
    public boolean canSelect(Hero hero) {
        return super.canSelect(hero) && Dungeon.depth % 5 != 0;
    }
}

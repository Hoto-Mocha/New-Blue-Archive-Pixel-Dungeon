package com.shatteredpixel.shatteredpixeldungeon.actors.hero.console.sandbox;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.LeafParticle;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;

public class Farming extends SandboxConsoleContent {
    public static final Farming INSTANCE = new Farming();

    @Override
    public int icon() {
        return HeroIcon.SANDBOX_FARMING;
    }

    @Override
    public boolean execute(Hero hero, int target) {
        if (Dungeon.level.distance(hero.pos, target) > 1) {
            hero.yellW("need_to_be_adjacent");
            return false;
        }
        if (Dungeon.level.map[target] != Terrain.GRASS
                && Dungeon.level.map[target] != Terrain.EMPTY
                && Dungeon.level.map[target] != Terrain.EMPTY_DECO
                && Dungeon.level.map[target] != Terrain.WATER) {
            hero.yellW("cannot_build");
            return false;
        }

        hero.sprite.zap(target);
        if (Dungeon.level.map[target] == Terrain.GRASS) {
            Dungeon.level.map[target] = Terrain.HIGH_GRASS;
            return true;
        } else if (Dungeon.level.map[target] == Terrain.EMPTY
                    || Dungeon.level.map[target] == Terrain.EMPTY_DECO
                    || Dungeon.level.map[target] == Terrain.WATER) {
            Dungeon.level.map[target] = Terrain.GRASS;
        }
        hero.spendAndNext(1);

        GameScene.updateMap(target);

        CellEmitter.get(target).burst(LeafParticle.LEVEL_SPECIFIC, 4);
        if (Dungeon.level.heroFOV[target]) Dungeon.observe();
        return true;
    }
}

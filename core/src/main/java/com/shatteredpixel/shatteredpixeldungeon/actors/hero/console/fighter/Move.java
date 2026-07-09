package com.shatteredpixel.shatteredpixeldungeon.actors.hero.console.fighter;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Door;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;

public abstract class Move extends FighterConsoleContent {
    public abstract int targetPos(Hero hero);

    @Override
    public boolean execute(Hero hero, int target) {
        if (!super.execute(hero, target)) return false;

        if (!(Dungeon.level.passable[targetPos(hero)] || Dungeon.level.avoid[targetPos(hero)])
                || Actor.findChar(targetPos(hero)) != null) return false;

        final float MOVE_TURNS = isEnhanced(hero) ? 0 : 1 / (hero.speed()*3);
        hero.busy();
        Sample.INSTANCE.play(Assets.Sounds.MISS);
        hero.sprite.emitter().start(Speck.factory(Speck.JET), 0.01f,4);
        hero.sprite.jump(hero.pos, targetPos(hero), 0, 0.1f, new Callback() {
            @Override
            public void call() {
                if (Dungeon.level.map[hero.pos] == Terrain.OPEN_DOOR) {
                    Door.leave( hero.pos );
                }
                hero.pos = targetPos(hero);
                Dungeon.level.occupyCell(hero);
                hero.spendAndNext(MOVE_TURNS);
            }
        });

        return true;
    }
}

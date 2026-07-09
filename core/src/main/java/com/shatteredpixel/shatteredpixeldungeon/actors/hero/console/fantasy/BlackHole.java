package com.shatteredpixel.shatteredpixeldungeon.actors.hero.console.fantasy;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Levitation;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.PitfallParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.PitfallTrap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.BArray;
import com.watabou.utils.PathFinder;

import java.util.ArrayList;

public class BlackHole extends FantasyConsoleContent {
    public static final BlackHole INSTANCE = new BlackHole();

    @Override
    public int icon() {
        return HeroIcon.FANTASY_BLACKHOLE;
    }

    @Override
    public boolean execute(Hero hero, int cell) {
        if (cell == hero.pos) {
            GLog.i( Messages.get(Wand.class, "self_target") );
            return false;
        }

        if (Dungeon.depth % 5 == 0) {
            hero.yellW("cannot_do_boss");
        }

        hero.busy();
        hero.sprite.zap(cell);

        if (isEnhanced(hero)) {
            Buff.affect(hero, Levitation.class, 2f);
        }

        PitfallTrap.DelayedPit p = Buff.append(Dungeon.hero, PitfallTrap.DelayedPit.class, 1);
        p.depth = Dungeon.depth;
        p.branch = Dungeon.branch;

        ArrayList<Integer> positions = new ArrayList<>();
        PathFinder.buildDistanceMap( cell, BArray.and( BArray.not(Dungeon.level.solid, null), Dungeon.level.passable, null ), 3 );
        for (int i = 0; i < PathFinder.distance.length; i++) {
            if (PathFinder.distance[i] < Integer.MAX_VALUE) {
                CellEmitter.floor(i).burst(PitfallParticle.FACTORY4, 8);
                positions.add(i);
            }
        }
        p.setPositions(positions);
        Sample.INSTANCE.play(Assets.Sounds.CHARGEUP);
        Sample.INSTANCE.play(Assets.Sounds.MISS);

        hero.spendAndNext(1f);

        return true;
    }

    @Override
    public int countUse() {
        return 10;
    }
}

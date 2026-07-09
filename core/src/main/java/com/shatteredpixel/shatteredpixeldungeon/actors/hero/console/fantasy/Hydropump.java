package com.shatteredpixel.shatteredpixeldungeon.actors.hero.console.fantasy;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.WaterOfAwareness;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.WaterOfHealth;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.WellWater;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GeyserTrap;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Random;

public class Hydropump extends FantasyConsoleContent {
    public static final Hydropump INSTANCE = new Hydropump();
    private static final Class<?>[] WATERS =
            { WaterOfAwareness.class, WaterOfAwareness.class, WaterOfAwareness.class, WaterOfAwareness.class, WaterOfHealth.class };

    @Override
    public int icon() {
        return HeroIcon.FANTASY_HYDROPUMP;
    }

    @Override
    public boolean execute(Hero hero, int cell) {
        if (cell == hero.pos) {
            GLog.i( Messages.get(Wand.class, "self_target") );
            return false;
        }

        hero.busy();
        hero.sprite.zap(cell);

        GeyserTrap geyser = new GeyserTrap();
        geyser.pos = cell;
        geyser.source = this;

        Char enemy = Actor.findChar(cell);
        if (enemy != null) {
            enemy.damage(Math.round(damageRoll(hero)*0.5f), geyser);
        }

        Ballistica aim = new Ballistica(hero.pos, cell, Ballistica.STOP_TARGET);
        if (aim.path.size() > aim.dist+1) {
            geyser.centerKnockBackDirection = aim.path.get(aim.dist + 1);
        }
        geyser.activate();

        if (Dungeon.level.map[cell] == Terrain.EMPTY_WELL && Random.Float() < 0.1f) {
            Level.set(cell, Terrain.WELL);
            Class<? extends WellWater> waterClass = (Class<? extends WellWater>) Random.element( WATERS );
            Blob b = WellWater.seed(cell, 1, waterClass, Dungeon.level);
            Notes.add( b.landmark() );
            GameScene.updateMap( cell );
        }

        hero.spendAndNext(1f);

        return true;
    }

    @Override
    public int countUse() {
        return isEnhanced(Dungeon.hero) ? 0 : 2;
    }
}

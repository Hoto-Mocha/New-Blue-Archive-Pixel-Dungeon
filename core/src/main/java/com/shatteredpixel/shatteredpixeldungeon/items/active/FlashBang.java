package com.shatteredpixel.shatteredpixeldungeon.items.active;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.FlashBangParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SparkParticle;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.ShadowCaster;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.BArray;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class FlashBang extends Grenade {
    {
        image = ItemSpriteSheet.FLASHBANG_GRENADE;

        max_amount = 1;
        amount = max_amount;
        dropChance = 0.25f;
    }

    @Override
    public int explodeMinDmg(int lvl) {
        return 0;
    }

    @Override
    public int explodeMaxDmg(int lvl) {
        return 0;
    }

    int explosionRange() {
        return 12;
    }

    @Override
    public void explode(int cell) {
        Hero hero = Dungeon.hero;
        boolean[] FOV = new boolean[Dungeon.level.length()];
        Point c = Dungeon.level.cellToPoint(cell);
        ShadowCaster.castShadow(c.x, c.y, Dungeon.level.width(), FOV, Dungeon.level.losBlocking, explosionRange());

        ArrayList<Char> affected = new ArrayList<>();

        for (int i = 0; i < FOV.length; i++) {
            if (FOV[i]) {
                if (Dungeon.level.heroFOV[i] && !Dungeon.level.solid[i]) {
                    CellEmitter.center(i).burst(FlashBangParticle.FACTORY, Random.IntRange(1, 3));
                }
                Char ch = Actor.findChar(i);
                if (ch != null){
                    affected.add(ch);
                }
            }
        }
        for (Char ch : affected) {
            if (ch instanceof Hero) {
                GameScene.flash(0x60FFFFFF);
            }
            Buff.affect(ch, Blindness.class, 5f);
            Buff.affect(ch, Paralysis.class, 2f);
        }

        Sample.INSTANCE.play(Assets.Sounds.BLAST);
        CellEmitter.center( cell ).burst( SparkParticle.FACTORY, 3 );
    }

    @Override
    public Grenade.Boomer knockItem(){
        return new FlashBangGrenadeBoomer();
    }

    public class FlashBangGrenadeBoomer extends Boomer {

        {
            image = ItemSpriteSheet.FLASHBANG_GRENADE;
        }

        //needs to be overridden
        @Override
        protected void activate(int cell) {
            super.activate(cell);
            explode(cell);
        }

        @Override
        public int throwPos(Hero user, int dst) {
            PathFinder.buildDistanceMap( user.pos, BArray.or(BArray.not( Dungeon.level.solid, null ), Dungeon.level.passable, null), 2 ); //기본적으로 벽을 통과할 수 없지만 이동 가능한 문은 통과 가능
            if (PathFinder.distance[dst] < Integer.MAX_VALUE) {
                return dst;
            } else {
                return super.throwPos(user, dst);
            }
        }
    }
}

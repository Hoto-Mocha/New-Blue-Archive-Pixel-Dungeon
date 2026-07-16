package com.shatteredpixel.shatteredpixeldungeon.actors.hero.console.fantasy;

import static com.shatteredpixel.shatteredpixeldungeon.Dungeon.hero;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Electricity;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.Lightning;
import com.shatteredpixel.shatteredpixeldungeon.effects.ThunderBolt;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SparkParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Shocking;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.tweeners.Tweener;
import com.watabou.utils.PointF;

import java.util.ArrayList;

public class Thunder extends FantasyConsoleContent {
    public static final Thunder INSTANCE = new Thunder();

    @Override
    public int icon() {
        return HeroIcon.FANTASY_LIGHTNING;
    }

    @Override
    public boolean execute(Hero hero, int cell) {
        if (cell == hero.pos) return false;
        Char enemy = Actor.findChar(cell);
        if (enemy == null) return false;
        hero.busy();
        hero.sprite.zap(cell);
        thunderEffect(enemy.sprite);
        hero.next();

        final int HIT_TIMES = isEnhanced(hero) ? 5 : 3;
        for (int hit = 0; hit < HIT_TIMES; hit++) {
            int finalHit = hit;
            hero.sprite.parent.add(new Tweener(hero.sprite.parent, 0.2f * finalHit) {
                @Override
                protected void updateValues(float progress) {}

                @Override
                protected void onComplete() {
                    if (finalHit == HIT_TIMES-1 || !enemy.isAlive()) {
                        hero.spendAndNext(1f);
                    }
                    if (!enemy.isAlive()) return;
                    int damage = Math.round(damageRoll(hero)*0.4f);
                    ArrayList<Char> affected = new ArrayList<>();
                    ArrayList<Lightning.Arc> arcs = new ArrayList<>();
                    Shocking.arc( hero, enemy, Dungeon.level.map[enemy.pos] == Terrain.WATER ? 2 : 1, affected, arcs );
                    for (Char ch : affected) {
                        if (ch == enemy) continue;
                        ch.damage( Math.round( damage * 0.4f ), new Electricity() );
                        if (ch == Dungeon.hero && !ch.isAlive()){
                            Dungeon.fail(this);
                        }
                    }

                    enemy.sprite.flash();
                    Sample.INSTANCE.play( Assets.Sounds.LIGHTNING );
                    enemy.sprite.centerEmitter().burst(SparkParticle.FACTORY, 3);

                    enemy.damage(damage, new Electricity());
                    super.onComplete();
                }
            });
        }

        return true;
    }

    @Override
    public int countUse() {
        return 3;
    }

    public static void thunderEffect(CharSprite sprite) {
        if (sprite != null) {
            ArrayList<ThunderBolt.Arc> arcs = new ArrayList<>();
            sprite.flash();
            arcs.add(new ThunderBolt.Arc(new PointF( sprite.center().x, sprite.center().y-35 ), sprite.center()));
            hero.sprite.parent.addToFront( new ThunderBolt( arcs, null ) );
            Sample.INSTANCE.play(Assets.Sounds.ROCKS, 1.2f, 0.5f);
            Sample.INSTANCE.play( Assets.Sounds.BLAST );
            sprite.centerEmitter().burst( SparkParticle.FACTORY, 5 );
        }
    }
}

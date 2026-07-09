package com.shatteredpixel.shatteredpixeldungeon.actors.hero.console.fantasy;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.FlameParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.ShadowCaster;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.Point;

public class FireBall extends FantasyConsoleContent {
    public static final FireBall INSTANCE = new FireBall();

    @Override
    public int icon() {
        return HeroIcon.FANTASY_FIREBALL;
    }

    @Override
    public boolean execute(Hero hero, int cell) {
        if (cell == hero.pos) {
            GLog.i( Messages.get(Wand.class, "self_target") );
            return false;
        }
        hero.busy();
        hero.sprite.zap(cell);
        Ballistica bolt = new Ballistica(hero.pos, cell, Ballistica.MAGIC_BOLT);

        if (Actor.findChar( bolt.collisionPos ) == hero){
            GLog.i( Messages.get(Wand.class, "self_target") );
            return false;
        }
        
        Point c = Dungeon.level.cellToPoint(bolt.collisionPos);
        boolean[] fieldOfView = new boolean[Dungeon.level.length()];
        ShadowCaster.castShadow(c.x, c.y, Dungeon.level.width(), fieldOfView, Dungeon.level.solid, isEnhanced(hero) ? 3 : 1);
        boolean positiveOnly = isEnhanced(hero);

        MagicMissile.boltFromChar( hero.sprite.parent,
                MagicMissile.FIRE,
                hero.sprite,
                bolt.collisionPos,
                new Callback() {
                    @Override
                    public void call() {
                        for (int i = 0; i < Dungeon.level.length(); i++){
                            if (fieldOfView[i] && !Dungeon.level.solid[i]){
                                //does not directly harm allies
                                if (positiveOnly && Actor.findChar(i) != null && Actor.findChar(i).alignment == Char.Alignment.ALLY){
                                    continue;
                                }

                                CellEmitter.get(i).burst(FlameParticle.FACTORY, 10);
                                if (Actor.findChar(i) != null){
                                    Char ch = Actor.findChar(i);
                                    Burning burning = Buff.affect(ch, Burning.class);
                                    burning.reignite(ch);
                                    int dmg = damageRoll(hero);
                                    ch.damage(dmg, burning);
                                }
                                if (Dungeon.level.flamable[i]){
                                    GameScene.add(Blob.seed(i, 4, Fire.class));
                                }

                            }
                        }
                        WandOfBlastWave.BlastWave.blast(bolt.collisionPos, isEnhanced(hero) ? 6 : 3);
                        Sample.INSTANCE.play(Assets.Sounds.BLAST);
                        Sample.INSTANCE.play(Assets.Sounds.BURNING);
                        hero.spendAndNext(1f);
                    }
                } );
        Sample.INSTANCE.play( Assets.Sounds.ZAP );

        return true;
    }

}

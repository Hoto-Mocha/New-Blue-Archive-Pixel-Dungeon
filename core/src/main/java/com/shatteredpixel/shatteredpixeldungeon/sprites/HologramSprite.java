package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.BlastParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SmokeParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

public class HologramSprite extends MirrorSprite {

    {
        tint(0x00FFFF, 0.5f);
        alpha(0.6f);
    }

    public int cellToAttack;

    @Override
    public void attack( int cell ) {
        if (!Dungeon.level.adjacent( cell, ch.pos )) {

            cellToAttack = cell;
            turnTo( ch.pos , cell );
            play( zap );

        } else {

            super.attack( cell );

        }
    }

    @Override
    public void onComplete( Animation anim ) {
        if (anim == zap) {
            idle();
            CellEmitter.get(ch.pos).burst(SmokeParticle.FACTORY, 2);
            CellEmitter.center(ch.pos).burst(BlastParticle.FACTORY, 2);
            Sample.INSTANCE.play( Assets.Sounds.HIT_CRUSH, 1, Random.Float(0.33f, 0.66f) );
            ((MissileSprite)parent.recycle( MissileSprite.class )).
                    reset( this, cellToAttack, new HologramShot(), new Callback() {

                        @Override
                        public void call() {
                            ch.onAttackComplete();
                        }
                    } );
        } else {
            super.onComplete( anim );
        }
    }

    @Override
    public void updateArmor( int tier ) {
        super.updateArmor( tier );
        zap = attack.clone();
    }

    public class HologramShot extends Item {
        {
            image = ItemSpriteSheet.SINGLE_BULLET;
        }
    }
}

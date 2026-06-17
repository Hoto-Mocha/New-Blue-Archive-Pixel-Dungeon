package com.shatteredpixel.shatteredpixeldungeon.sprites;


import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.BlastParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SmokeParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

public class NikoSprite extends MobSprite {

    public int cellToAttack;

    public NikoSprite() {
        super();

        texture(Assets.Sprites.NIKO);

        TextureFilm film = new TextureFilm( texture, 12, 17 );

        idle = new Animation( 1, true );
        idle.frames( film, 0, 0, 0, 1, 0, 0, 1, 1 );

        run = new Animation( 20, true );
        run.frames( film, 2, 3, 4, 5, 6, 7 );

        die = new Animation( 20, false );
        die.frames( film, 8, 9, 10, 11, 12 );

        attack = new Animation( 15, false );
        attack.frames( film, 13, 14, 15, 0 );

        zap = attack.clone();

        idle();
        resetColor();
    }

    @Override
    public void attack( int cell ) {
        cellToAttack = cell;
        turnTo( ch.pos , cell );
        play( zap );
    }

    @Override
    public void onComplete( Animation anim ) {
        if (anim == zap) {
            idle();
            CellEmitter.get(ch.pos).burst(SmokeParticle.FACTORY, 2);
            CellEmitter.center(ch.pos).burst(BlastParticle.FACTORY, 2);
            Sample.INSTANCE.play( Assets.Sounds.HIT_CRUSH, 1, Random.Float(0.33f, 0.66f) );
            ((MissileSprite)parent.recycle( MissileSprite.class )).
                    reset( this, cellToAttack, new NikoShot(), new Callback() {

                        @Override
                        public void call() {
                            ch.onAttackComplete();
                        }
                    } );
        } else {
            super.onComplete( anim );
        }
    }

    public class NikoShot extends Item {
        {
            image = ItemSpriteSheet.TRIPLE_BULLET;
        }
    }

}
package com.shatteredpixel.shatteredpixeldungeon.effects.particles;

import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.particles.PixelParticle;
import com.watabou.utils.Random;

public class FlashBangParticle extends PixelParticle.Shrinking {
    public static final Emitter.Factory FACTORY = new Emitter.Factory() {
        @Override
        public void emit( Emitter emitter, int index, float x, float y ) {
            ((FlashBangParticle)emitter.recycle( FlashBangParticle.class )).reset( x, y );
        }
        @Override
        public boolean lightMode() {
            return false;
        }
    };

    public FlashBangParticle() {
        super();

        lifespan = 1.2f;

        color( 0xFFFFFF );
    }

    public void reset( float x, float y){
        revive();

        this.x = x+Random.IntRange(-10, +10);
        this.y = y+Random.IntRange(-10, +10);

        left = lifespan;
        size = 4;

        speed.set( Random.Float( -3, +3 ), Random.Float( -3, +3 ) );
    }

    @Override
    public void update() {
        super.update();

        am = 1 - left / lifespan;
    }
}

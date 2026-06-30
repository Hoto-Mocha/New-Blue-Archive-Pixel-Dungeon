package com.shatteredpixel.shatteredpixeldungeon.effects.particles;

import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.particles.PixelParticle;
import com.watabou.utils.Random;

public class SnipeAreaParticle extends PixelParticle.Shrinking {

	public static final Emitter.Factory FACTORY = new Emitter.Factory() {
		@Override
		public void emit( Emitter emitter, int index, float x, float y ) {
			((SnipeAreaParticle)emitter.recycle( SnipeAreaParticle.class )).reset( x, y );
		}
		@Override
		public boolean lightMode() {
			return false;
		}
	};

	public SnipeAreaParticle() {
		super();

		lifespan = 0.6f;

		color( 0xFF0000 );
	}

	public void reset( float x, float y){
		revive();

		this.x = x;
		this.y = y;

		left = lifespan;
		size = 8;

		speed.set( Random.Float( -4, +4 ), Random.Float( -8, -16 ) );
	}

	@Override
	public void update() {
		super.update();

		am = 1 - left / lifespan;
	}

}

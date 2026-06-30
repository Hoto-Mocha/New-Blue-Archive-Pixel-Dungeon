package com.shatteredpixel.shatteredpixeldungeon.effects.particles;

import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.particles.PixelParticle;
import com.watabou.utils.Random;

public class BlueParticle extends PixelParticle.Shrinking {

	public static final Emitter.Factory FACTORY = new Emitter.Factory() {
		@Override
		public void emit( Emitter emitter, int index, float x, float y ) {
			((BlueParticle)emitter.recycle( BlueParticle.class )).reset( x, y );
		}
		@Override
		public boolean lightMode() {
			return false;
		}
	};

	public BlueParticle() {
		super();

		lifespan = 0.6f;

		color( 0x548CFD );
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

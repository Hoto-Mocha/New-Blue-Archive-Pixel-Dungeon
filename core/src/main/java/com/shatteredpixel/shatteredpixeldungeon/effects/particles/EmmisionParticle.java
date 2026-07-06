package com.shatteredpixel.shatteredpixeldungeon.effects.particles;

import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.particles.Emitter.Factory;
import com.watabou.noosa.particles.PixelParticle;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

public class EmmisionParticle extends PixelParticle {

	public static final Factory FACTORY = new Factory() {
		@Override
		public void emit( Emitter emitter, int index, float x, float y ) {
			((EmmisionParticle)emitter.recycle( EmmisionParticle.class )).reset( x, y );
		}
	};

	public EmmisionParticle() {
		super();
		
		color( 0xD5D5D5 );
	}
	
	public void reset( float x, float y ) {
		revive();
		
		this.x = x;
		this.y = y-5;

		size = 0;
		left = lifespan = Random.Float( 1f, 1.2f );
		speed.set( Random.Float(-2, 2), Random.Float(-4, -6) );
		acc.set(0, -8);
	}
	
	@Override
	public void update() {
		super.update();
        am = left / lifespan;
		size( 8 * (1 - left / lifespan) );
	}
}
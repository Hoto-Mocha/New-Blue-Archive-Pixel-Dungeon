package com.shatteredpixel.shatteredpixeldungeon.effects.particles;

import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.particles.Emitter.Factory;
import com.watabou.noosa.particles.PixelParticle;
import com.watabou.utils.Random;

public class LightPillarParticle extends PixelParticle {

	public static final Factory FACTORY = new Factory() {
		@Override
		public void emit( Emitter emitter, int index, float x, float y ) {
			((LightPillarParticle)emitter.recycle( LightPillarParticle.class )).reset( x, y );
		}
		@Override
		public boolean lightMode() {
			return true;
		}
	};

	public LightPillarParticle() {
		super();

		origin.set( 0.5f, 1f );

		lifespan = 0.3f;
		speed.set( 0, 0 );
	}

	public void reset( float x, float y ) {
		revive();

		this.x = x;
		this.y = y;

		int[] colors = { 0xFFFFFF, 0xFFF0A0, 0xFFE066, 0xFFD700 };
		color( colors[Random.Int( colors.length )] );

		left = lifespan;
	}

	@Override
	public void update() {
		super.update();

		float p = left / lifespan;
		float progress = 1f - p;

		am = p < 0.5f ? p * 2f : (1f - p) * 2f;

		scale.x = 12f - progress * 12f;
		scale.y = 8f + progress * 32f;
	}
}

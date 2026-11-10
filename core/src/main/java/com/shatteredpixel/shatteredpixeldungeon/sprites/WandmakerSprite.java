/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.effects.ShieldHalo;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ElmoParticle;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;

public class WandmakerSprite extends MobSprite {
	
	private ShieldHalo shield;

	protected Animation awake;
	
	public WandmakerSprite() {
		super();
		
		texture( Assets.Sprites.MOMOI );
		
		TextureFilm frames = new TextureFilm( texture, 13, 17 );
		
		idle = new Animation( 2, true );
		idle.frames( frames, 0,0,0,0,0,0,0,0, 1,1,1,1, 0,0,0,0,0,0,0,0, 2, 3,3,3,3,3,3, 4,4,4,5,5,5,4,4,4,5,5,5,4,4,4,5,5,5,4,4,4,5,5,5, 2 );

		awake = new Animation( 8, false );
		awake.frames( frames, 0,0,0,0,0,0,0,0, 6, 0,0,0,0,0,0,0,0, 6, 0, 6 );
		
		run = new Animation( 20, true );
		run.frames( frames, 0 );
		
		die = new Animation( 20, false );
		die.frames( frames, 0 );
		
		play( idle );
	}
	
	@Override
	public void link( Char ch ) {
		super.link( ch );
//		add(State.SHIELDED);
	}
	
	@Override
	public void die() {
		super.die();

//		processStateRemoval(State.SHIELDED);
		emitter().start( ElmoParticle.FACTORY, 0.03f, 60 );

		if (visible) {
			Sample.INSTANCE.play( Assets.Sounds.BURNING );
		}
	}

	public void awake() {
		animCallback = new Callback() {
			@Override
			public void call() {
				play(idle);
			}
		};
		play(awake);
	}

}

/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2024 Evan Debenham
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

package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.watabou.noosa.Image;
import com.watabou.noosa.TextureFilm;

//icons for hero subclasses and abilities atm, maybe add classes?
public class HeroIcon extends Image {

	private static TextureFilm film;
	private static final int SIZE = 16;

	//transparent icon
	public static final int NONE    = 119;

	//subclasses
	public static final int BERSERKER   = 0;
	public static final int GLADIATOR   = 1;
//	public static final int 			= 2;
//	public static final int 			= 3;

	public static final int BATTLEMAGE  = 4;
	public static final int WARLOCK     = 5;
//	public static final int 			= 6;
//	public static final int 			= 7;

	public static final int ASSASSIN    = 8;
	public static final int FREERUNNER  = 9;
//	public static final int 			= 10;
//	public static final int 			= 11;

	public static final int SNIPER      = 12;
	public static final int WARDEN      = 13;
//	public static final int 			= 14;
//	public static final int 			= 15;

	public static final int CHAMPION    = 16;
	public static final int MONK        = 17;
//	public static final int 			= 18;
//	public static final int 			= 19;

//	public static final int 			= 20;
//	public static final int 			= 21;
//	public static final int 			= 22;
//	public static final int 			= 23;

	//new classes
	public static final int HERO_OF_LIGHT		= 24;
	public static final int BATTERY_CHARGE		= 25;
//	public static final int 			= 26;
//	public static final int 			= 27;

//	public static final int 			= 28;
//	public static final int 			= 29;
//	public static final int 			= 30;
//	public static final int 			= 31;

//	public static final int 			= 32;
//	public static final int 			= 33;
//	public static final int 			= 34;
//	public static final int 			= 35;

//	public static final int 			= 36;
//	public static final int 			= 37;
//	public static final int 			= 38;
//	public static final int 			= 39;

//	public static final int 			= 40;
//	public static final int 			= 41;
//	public static final int 			= 42;
//	public static final int 			= 43;

//	public static final int 			= 44;
//	public static final int 			= 45;
//	public static final int 			= 46;
//	public static final int 			= 47;

	//abilities
	public static final int HEROIC_LEAP     = 48;
	public static final int SHOCKWAVE       = 49;
	public static final int ENDURE          = 50;
//	public static final int 				= 51;

	public static final int ELEMENTAL_BLAST = 52;
	public static final int WILD_MAGIC      = 53;
	public static final int WARP_BEACON     = 54;
//	public static final int 				= 55;

	public static final int SMOKE_BOMB      = 56;
	public static final int DEATH_MARK      = 57;
	public static final int SHADOW_CLONE    = 58;
//	public static final int 				= 59;

	public static final int SPECTRAL_BLADES = 60;
	public static final int NATURES_POWER   = 61;
	public static final int SPIRIT_HAWK     = 62;
//	public static final int 				= 63;

	public static final int CHALLENGE       = 64;
	public static final int ELEMENTAL_STRIKE= 65;
	public static final int FEINT           = 66;
//	public static final int 				= 67;

//	public static final int 				= 68;
//	public static final int 				= 69;
//	public static final int 				= 70;
//	public static final int 				= 71;

	//new abilities
	public static final int ARIS_1			= 72;
	public static final int ARIS_2			= 73;
	public static final int ARIS_3			= 74;
//	public static final int 				= 75;

//	public static final int 				= 76;
//	public static final int 				= 77;
//	public static final int 				= 78;
//	public static final int 				= 79;

//	public static final int 				= 80;
//	public static final int 				= 81;
//	public static final int 				= 82;
//	public static final int 				= 83;

//	public static final int 				= 84;
//	public static final int 				= 85;
//	public static final int 				= 86;
//	public static final int 				= 87;

//	public static final int 				= 88;
//	public static final int 				= 89;
//	public static final int 				= 90;
//	public static final int 				= 91;

//	public static final int 				= 92;
//	public static final int 				= 93;
//	public static final int 				= 94;
//	public static final int 				= 95;

	public static final int RATMOGRIFY      = 32+96;

	//action indicator visuals
	public static final int BERSERK         = 32+104;
	public static final int COMBO           = 32+105;
//	public static final int 		        = 32+106;
//	public static final int					= 32+107;

//	public static final int					= 32+108;
//	public static final int					= 32+109;
//	public static final int					= 32+110;
//	public static final int					= 32+111;

	public static final int PREPARATION     = 32+112;
	public static final int MOMENTUM        = 32+113;
//	public static final int					= 32+114;
//	public static final int					= 32+115;

	public static final int SNIPERS_MARK    = 32+116;
//	public static final int					= 32+117;
//	public static final int					= 32+118;
//	public static final int					= 32+119;

	public static final int WEAPON_SWAP     = 32+120;
	public static final int MONK_ABILITIES  = 32+121;
//	public static final int 				= 32+122;
//	public static final int 				= 32+123;

//	public static final int 				= 32+124;
//	public static final int 				= 32+125;
//	public static final int 				= 32+126;
//	public static final int 				= 32+127;

  	public static final int CHARGE			= 32+128;
//  public static final int  				= 32+129;
//  public static final int  				= 32+130;
//	public static final int  				= 32+131;

//	public static final int  				= 32+132;
//	public static final int  				= 32+133;
//	public static final int  				= 32+134;
//	public static final int  				= 32+135;

//	public static final int  				= 32+136;
//	public static final int  				= 32+137;
//	public static final int  				= 32+138;
//	public static final int  				= 32+139;

//	public static final int  				= 32+140;
//	public static final int  				= 32+141;
//	public static final int  				= 32+142;
//	public static final int 				= 32+143;

	public HeroIcon(HeroSubClass subCls){
		super( Assets.Interfaces.HERO_ICONS );
		if (film == null){
			film = new TextureFilm(texture, SIZE, SIZE);
		}
		frame(film.get(subCls.icon()));
	}

	public HeroIcon(ArmorAbility abil){
		super( Assets.Interfaces.HERO_ICONS );
		if (film == null){
			film = new TextureFilm(texture, SIZE, SIZE);
		}
		frame(film.get(abil.icon()));
	}

	public HeroIcon(ActionIndicator.Action action){
		super( Assets.Interfaces.HERO_ICONS );
		if (film == null){
			film = new TextureFilm(texture, SIZE, SIZE);
		}
		frame(film.get(action.actionIcon()));
	}

}

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

package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.console.YuzuConsoleContent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.shops.YuzuShopContent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.ClericSpell;
import com.watabou.noosa.Image;
import com.watabou.noosa.TextureFilm;

//icons for hero subclasses and abilities atm, maybe add classes?
public class HeroIcon extends Image {

	private static TextureFilm film;
	private static final int SIZE = 16;

	//transparent icon
	public static final int NONE    = 127;

	//subclasses
	public static final int BERSERKER   = 0;
	public static final int GLADIATOR   = 1;
	public static final int BATTLEMAGE  = 2;
	public static final int WARLOCK     = 3;
	public static final int ASSASSIN    = 4;
	public static final int FREERUNNER  = 5;
	public static final int SNIPER      = 6;
	public static final int WARDEN      = 7;
	public static final int CHAMPION    = 8;
	public static final int MONK        = 9;
	public static final int PRIEST      = 10;
	public static final int PALADIN     = 11;

	//abilities
	public static final int HEROIC_LEAP     = 16;
	public static final int SHOCKWAVE       = 17;
	public static final int ENDURE          = 18;
	public static final int ELEMENTAL_BLAST = 19;
	public static final int WILD_MAGIC      = 20;
	public static final int WARP_BEACON     = 21;
	public static final int SMOKE_BOMB      = 22;
	public static final int DEATH_MARK      = 23;
	public static final int SHADOW_CLONE    = 24;
	public static final int SPECTRAL_BLADES = 25;
	public static final int NATURES_POWER   = 26;
	public static final int SPIRIT_HAWK     = 27;
	public static final int CHALLENGE       = 28;
	public static final int ELEMENTAL_STRIKE= 29;
	public static final int FEINT           = 30;
	public static final int ASCENDED_FORM   = 31;
	public static final int TRINITY         = 32;
	public static final int POWER_OF_MANY   = 33;
	public static final int RATMOGRIFY      = 34;

	//cleric spells
	public static final int GUIDING_LIGHT   = 40;
	public static final int HOLY_WEAPON     = 41;
	public static final int HOLY_WARD       = 42;
	public static final int HOLY_INTUITION  = 43;
	public static final int SHIELD_OF_LIGHT = 44;
	public static final int RECALL_GLYPH    = 45;
	public static final int SUNRAY          = 46;
	public static final int DIVINE_SENSE    = 47;
	public static final int BLESS           = 48;
	public static final int CLEANSE         = 49;
	public static final int RADIANCE        = 50;
	public static final int HOLY_LANCE      = 51;
	public static final int HALLOWED_GROUND = 52;
	public static final int MNEMONIC_PRAYER = 53;
	public static final int SMITE           = 54;
	public static final int LAY_ON_HANDS    = 55;
	public static final int AURA_OF_PROTECTION = 56;
	public static final int WALL_OF_LIGHT   = 57;
	public static final int DIVINE_INTERVENTION = 58;
	public static final int JUDGEMENT       = 59;
	public static final int FLASH           = 60;
	public static final int BODY_FORM       = 61;
	public static final int MIND_FORM       = 62;
	public static final int SPIRIT_FORM     = 63;
	public static final int BEAMING_RAY     = 64;
	public static final int LIFE_LINK       = 65;
	public static final int STASIS          = 66;

	//all cleric spells have a separate icon with no background for the action indicator
	public static final int SPELL_ACTION_OFFSET      = 32;

	//action indicator visuals
	public static final int BERSERK         = 104;
	public static final int COMBO           = 105;
	public static final int PREPARATION     = 106;
	public static final int MOMENTUM        = 107;
	public static final int SNIPERS_MARK    = 108;
	public static final int WEAPON_SWAP     = 109;
	public static final int MONK_ABILITIES  = 110;

	//new subclasses
	public static final int HERO_OF_LIGHT	= 128+0;
	public static final int BATTERY_CHARGE	= 128+1;
	public static final int SHOOT_ALL		= 128+2;
	public static final int SPREAD_SHOT		= 128+3;
	public static final int RABBIT_SQUAD	= 128+4;
	public static final int SUPPORT_DRONE	= 128+5;
	public static final int SHIELD_BASH		= 128+6;
	public static final int SWIFT_MOVEMENT	= 128+7;
	public static final int PROFESSIONAL_RIDING	= 128+8;
	public static final int BANK_ROBBER		= 128+9;
	public static final int DOUBLE_BARREL	= 128+10;
	public static final int CONVERSATION	= 128+11;
	public static final int TELESCOPE	 	= 128+12;
	public static final int CAMOUFLAGE	 	= 128+13;
	public static final int AVANT_GARDE_KUN = 128+14;
	public static final int GAME_START		= 128+15;

	//new armor abilities
	public static final int ARIS_1			= 128+24;
	public static final int ARIS_2			= 128+25;
	public static final int ARIS_3			= 128+26;
	public static final int NONOMI_1		= 128+27;
	public static final int NONOMI_2		= 128+28;
	public static final int NONOMI_3		= 128+29;
	public static final int MIYAKO_1		= 128+30;
	public static final int MIYAKO_2		= 128+31;
	public static final int MIYAKO_3		= 128+32;
	public static final int HOSHINO_1		= 128+33;
	public static final int HOSHINO_2		= 128+34;
	public static final int HOSHINO_3		= 128+35;
	public static final int SHIROKO_1		= 128+36;
	public static final int SHIROKO_2		= 128+37;
	public static final int SHIROKO_3		= 128+38;
	public static final int NOA_1			= 128+39;
	public static final int NOA_2			= 128+40;
	public static final int NOA_3			= 128+41;
	public static final int MIYU_1			= 128+42;
	public static final int MIYU_2			= 128+43;
	public static final int MIYU_3			= 128+44;
	public static final int YUZU_1			= 128+45;
	public static final int YUZU_2			= 128+46;
	public static final int YUZU_3			= 128+47;

	//new action indicator visuals
  	public static final int CHARGE						= 128+64;
  	public static final int SHOOT_ALL_ACTION			= 128+65;
  	public static final int SPREAD_SHOT_ACTION			= 128+66;
  	public static final int RABBIT_SQUAD_ACTION			= 128+67;
  	public static final int SHIELD_BASH_ACTION			= 128+68;
  	public static final int SWIFT_MOVEMENT_ACTION		= 128+69;
  	public static final int PROFESSIONAL_RIDING_ACTION	= 128+70;
  	public static final int BANK_ROBBER_ACTION			= 128+71;
  	public static final int DOUBLE_BARREL_ACTION		= 128+72;
  	public static final int CONVERSATION_ACTION			= 128+73;
  	public static final int TELESCOPE_ACTION			= 128+74;
  	public static final int AVANT_GARDE_KUN_ACTION		= 128+75;

	//Yuzu abilities
	public static final int SHOP_1			= 128 + 88;
	public static final int SHOP_2			= 128 + 89;
	public static final int SHOP_3			= 128 + 90;
	public static final int SHOP_4			= 128 + 91;
	public static final int SHOP_5			= 128 + 92;
	public static final int SHOP_6			= 128 + 93;
	public static final int SHOP_7			= 128 + 94;
	public static final int SHOP_8			= 128 + 95;
	public static final int SHOP_9			= 128 + 96;
	public static final int SHOP_10			= 128 + 97;
	public static final int CONTINUE_CONSOLE= 128 + 98;
	public static final int FIGHTER_CONSOLE	= 128 + 99;
	public static final int FANTASY_CONSOLE = 128 + 100;
	public static final int SHOP_14			= 128 + 101;
	public static final int SHOP_15			= 128 + 102;

	public static final int FIGHTER_WEAK_ATK= 128 + 112;
	public static final int FIGHTER_STROING_ATK= 128 + 113;
	public static final int FIGHTER_CHARGE	= 128 + 114;
	public static final int FIGHTER_LEFT	= 128 + 115;
	public static final int FIGHTER_UP		= 128 + 116;
	public static final int FIGHTER_DOWN	= 128 + 117;
	public static final int FIGHTER_RIGHT	= 128 + 118;

	public static final int FANTASY_FIREBALL= 128 + 119;





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

	public HeroIcon(ClericSpell spell){
		super( Assets.Interfaces.HERO_ICONS );
		if (film == null){
			film = new TextureFilm(texture, SIZE, SIZE);
		}
		frame(film.get(spell.icon()));
	}

	public HeroIcon(YuzuShopContent content){
		super( Assets.Interfaces.HERO_ICONS );
		if (film == null){
			film = new TextureFilm(texture, SIZE, SIZE);
		}
		frame(film.get(content.icon()));
	}

	public HeroIcon(YuzuConsoleContent content){
		super( Assets.Interfaces.HERO_ICONS );
		if (film == null){
			film = new TextureFilm(texture, SIZE, SIZE);
		}
		frame(film.get(content.icon()));
	}

}

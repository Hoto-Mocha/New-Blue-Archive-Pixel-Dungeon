/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2025 Evan Debenham
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

package com.shatteredpixel.shatteredpixeldungeon.actors.hero;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.GamesInProgress;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ArtifactRecharge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.CounterBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.EnhancedRings;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Haste;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LostInventory;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.PhysicalEmpower;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.RabbitSquadBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Recharging;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.RevealedArea;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Roots;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ScrollEmpower;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ShootAllBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.SuperNovaCharge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.SupportDrone;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.WandEmpower;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.Ratmogrify;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.aris.Division;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.nonomi.Bipod;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.DivineSense;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.RecallInscription;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.effects.SpellSprite;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.LeafParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.BrokenSeal;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.KindOfWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClothArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CloakOfShadows;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HolyTome;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HornOfPlenty;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMagicMapping;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRecharging;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.Runestone;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfIntuition;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.ShardOfOblivion;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Elastic;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Gloves;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.SuperNova;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.Gun;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.MG.MG;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.MG.MG_SP;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.SMG.SMG;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.AttackIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.GameMath;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;

public enum Talent {

	//Warrior T1
	HEARTY_MEAL					(-1,  0),
	VETERANS_INTUITION			(-1,  0),
	PROVOKED_ANGER				(-1,  0),
	IRON_WILL					(-1,  0),
	//Warrior T2
	IRON_STOMACH				(-1,  0),
	LIQUID_WILLPOWER			(-1,  0),
	RUNIC_TRANSFERENCE			(-1,  0),
	LETHAL_MOMENTUM				(-1,  0),
	IMPROVISED_PROJECTILES		(-1,  0),
	//Warrior T3
	HOLD_FAST					(-1, 0, 3),
	STRONGMAN					(-1, 0, 3),
	//Berserker T3
	ENDLESS_RAGE				(-1, 0, 3),
	DEATHLESS_FURY				(-1, 0, 3),
	ENRAGED_CATALYST			(-1, 0, 3),
	//Gladiator T3
	CLEAVE						(-1, 0, 3),
	LETHAL_DEFENSE				(-1, 0, 3),
	ENHANCED_COMBO				(-1, 0, 3),
	//Heroic Leap T4
	BODY_SLAM					(-1, 0, 4),
	IMPACT_WAVE					(-1, 0, 4),
	DOUBLE_JUMP					(-1, 0, 4),
	//Shockwave T4
	EXPANDING_WAVE				(-1, 0, 4),
	STRIKING_WAVE				(-1, 0, 4),
	SHOCK_FORCE					(-1, 0, 4),
	//Endure T4
	SUSTAINED_RETRIBUTION		(-1, 0, 4),
	SHRUG_IT_OFF				(-1, 0, 4),
	EVEN_THE_ODDS				(-1, 0, 4),

	//Mage T1
	EMPOWERING_MEAL				(-1,  1),
	SCHOLARS_INTUITION			(-1,  1),
	LINGERING_MAGIC				(-1,  1),
	BACKUP_BARRIER				(-1,  1),
	//Mage T2
	ENERGIZING_MEAL				(-1,  1),
	INSCRIBED_POWER				(-1,  1),
	WAND_PRESERVATION			(-1,  1),
	ARCANE_VISION				(-1,  1),
	SHIELD_BATTERY				(-1,  1),
	//Mage T3
	DESPERATE_POWER				(-1, 1, 3),
	ALLY_WARP					(-1, 1, 3),
	//Battlemage T3
	EMPOWERED_STRIKE			(-1, 1, 3),
	MYSTICAL_CHARGE				(-1, 1, 3),
	EXCESS_CHARGE				(-1, 1, 3),
	//Warlock T3
	SOUL_EATER					(-1, 1, 3),
	SOUL_SIPHON					(-1, 1, 3),
	NECROMANCERS_MINIONS		(-1, 1, 3),
	//Elemental Blast T4
	BLAST_RADIUS				(-1, 1, 4),
	ELEMENTAL_POWER				(-1, 1, 4),
	REACTIVE_BARRIER			(-1, 1, 4),
	//Wild Magic T4
	WILD_POWER					(-1, 1, 4),
	FIRE_EVERYTHING				(-1, 1, 4),
	CONSERVED_MAGIC				(-1, 1, 4),
	//Warp Beacon T4
	TELEFRAG					(-1, 1, 4),
	REMOTE_BEACON				(-1, 1, 4),
	LONGRANGE_WARP				(-1, 1, 4),

	//Rogue T1
	CACHED_RATIONS				(-1,  2),
	THIEFS_INTUITION			(-1,  2),
	SUCKER_PUNCH				(-1,  2),
	PROTECTIVE_SHADOWS			(-1,  2),
	//Rogue T2
	MYSTICAL_MEAL				(-1,  2),
	INSCRIBED_STEALTH			(-1,  2),
	WIDE_SEARCH					(-1,  2),
	SILENT_STEPS				(-1,  2),
	ROGUES_FORESIGHT			(-1,  2),
	//Rogue T3
	ENHANCED_RINGS				(-1, 2, 3),
	LIGHT_CLOAK					(-1, 2, 3),
	//Assassin T3
	ENHANCED_LETHALITY			(-1, 2, 3),
	ASSASSINS_REACH				(-1, 2, 3),
	BOUNTY_HUNTER				(-1, 2, 3),
	//Freerunner T3
	EVASIVE_ARMOR				(-1, 2, 3),
	PROJECTILE_MOMENTUM			(-1, 2, 3),
	SPEEDY_STEALTH				(-1, 2, 3),
	//Smoke Bomb T4
	HASTY_RETREAT				(-1, 2, 4),
	BODY_REPLACEMENT			(-1, 2, 4),
	SHADOW_STEP					(-1, 2, 4),
	//Death Mark T4
	FEAR_THE_REAPER				(-1, 2, 4),
	DEATHLY_DURABILITY			(-1, 2, 4),
	DOUBLE_MARK					(-1, 2, 4),
	//Shadow Clone T4
	SHADOW_BLADE				(-1, 2, 4),
	CLONED_ARMOR				(-1, 2, 4),
	PERFECT_COPY				(-1, 2, 4),

	//Huntress T1
	NATURES_BOUNTY				(-1,  3),
	SURVIVALISTS_INTUITION		(-1,  3),
	FOLLOWUP_STRIKE				(-1,  3),
	NATURES_AID					(-1,  3),
	//Huntress T2
	INVIGORATING_MEAL			(-1,  3),
	LIQUID_NATURE				(-1,  3),
	REJUVENATING_STEPS			(-1,  3),
	HEIGHTENED_SENSES			(-1,  3),
	DURABLE_PROJECTILES			(-1,  3),
	//Huntress T3
	POINT_BLANK					(-1, 3, 3),
	SEER_SHOT					(-1, 3, 3),
	//Sniper T3
	FARSIGHT					(-1, 3, 3),
	SHARED_ENCHANTMENT			(-1, 3, 3),
	SHARED_UPGRADES				(-1, 3, 3),
	//Warden T3
	DURABLE_TIPS				(-1, 3, 3),
	BARKSKIN					(-1, 3, 3),
	SHIELDING_DEW				(-1, 3, 3),
	//Fighter T3
	SWIFT_MOVEMENT				(-1, 3, 3),
	LESS_RESIST					(-1, 3, 3),
	RING_KNUCKLE				(-1, 3, 3),
	//Spectral Blades T4
	FAN_OF_BLADES				(-1, 3, 4),
	PROJECTING_BLADES			(-1, 3, 4),
	SPIRIT_BLADES				(-1, 3, 4),
	//Natures Power T4
	GROWING_POWER				(-1, 3, 4),
	NATURES_WRATH				(-1, 3, 4),
	WILD_MOMENTUM				(-1, 3, 4),
	//Spirit Hawk T4
	EAGLE_EYE					(-1, 3, 4),
	GO_FOR_THE_EYES				(-1, 3, 4),
	SWIFT_SPIRIT				(-1, 3, 4),

	//Duelist T1
	STRENGTHENING_MEAL			(-1,  4),
	ADVENTURERS_INTUITION		(-1,  4),
	PATIENT_STRIKE				(-1,  4),
	AGGRESSIVE_BARRIER			(-1,  4),
	//Duelist T2
	FOCUSED_MEAL				(-1,  4),
	LIQUID_AGILITY				(-1,  4),
	WEAPON_RECHARGING			(-1,  4),
	LETHAL_HASTE				(-1,  4),
	SWIFT_EQUIP					(-1,  4),
	//Duelist T3
	PRECISE_ASSAULT				(-1, 4, 3),
	DEADLY_FOLLOWUP				(-1, 4, 3),
	//Champion T3
	VARIED_CHARGE				(-1, 4, 3),
	TWIN_UPGRADES				(-1, 4, 3),
	COMBINED_LETHALITY			(-1, 4, 3),
	//Monk T3
	UNENCUMBERED_SPIRIT			(-1, 4, 3),
	MONASTIC_VIGOR				(-1, 4, 3),
	COMBINED_ENERGY				(-1, 4, 3),
	//Challenge T4
	CLOSE_THE_GAP				(-1, 4, 4),
	INVIGORATING_VICTORY		(-1, 4, 4),
	ELIMINATION_MATCH			(-1, 4, 4),
	//Elemental Strike T4
	ELEMENTAL_REACH				(-1, 4, 4),
	STRIKING_FORCE				(-1, 4, 4),
	DIRECTED_POWER				(-1, 4, 4),
	//Feint T4
	FEIGNED_RETREAT				(-1, 4, 4),
	EXPOSE_WEAKNESS				(-1, 4, 4),
	COUNTER_ABILITY				(-1, 4, 4),

	//Cleric T1
	SATIATED_SPELLS				(-1, 5),
	HOLY_INTUITION				(-1, 5),
	SEARING_LIGHT				(-1, 5),
	SHIELD_OF_LIGHT				(-1, 5),
	//Cleric T2
	ENLIGHTENING_MEAL			(-1, 5),
	RECALL_INSCRIPTION			(-1, 5),
	SUNRAY						(-1, 5),
	DIVINE_SENSE				(-1, 5),
	BLESS						(-1, 5),
	//Cleric T3
	CLEANSE						(-1, 5, 3),
	LIGHT_READING				(-1, 5, 3),
	//Priest T3
	HOLY_LANCE					(-1, 5, 3),
	HALLOWED_GROUND				(-1, 5, 3),
	MNEMONIC_PRAYER				(-1, 5, 3),
	//Paladin T3
	LAY_ON_HANDS				(-1, 5, 3),
	AURA_OF_PROTECTION			(-1, 5, 3),
	WALL_OF_LIGHT				(-1, 5, 3),
	//Ascended Form T4
	DIVINE_INTERVENTION			(-1, 5, 4),
	JUDGEMENT					(-1, 5, 4),
	FLASH						(-1, 5, 4),
	//Trinity T4
	BODY_FORM					(-1, 5, 4),
	MIND_FORM					(-1, 5, 4),
	SPIRIT_FORM					(-1, 5, 4),
	//Power of Many T4
	BEAMING_RAY					(-1, 5, 4),
	LIFE_LINK					(-1, 5, 4),
	STASIS						(-1, 5, 4),

	//Aris T1
	ARIS_T1_1(0, 0, 2),
	ARIS_T1_2(1, 0, 2),
	ARIS_T1_3(2, 0, 2),
	ARIS_T1_4(3, 0, 2),

	//Aris T2
	ARIS_T2_1(4, 0, 2),
	ARIS_T2_2(5, 0, 2),
	ARIS_T2_3(6, 0, 2),
	ARIS_T2_4(7, 0, 2),
	ARIS_T2_5(8, 0, 2),

	//Aris T3
	ARIS_T3_1(9, 0, 3),
	ARIS_T3_2(10, 0, 3),

	//Hero of Light T3
	ARIS_EX1_1(11, 0, 3),
	ARIS_EX1_2(12, 0, 3),
	ARIS_EX1_3(13, 0, 3),

	//Collapse of Balance T3
	ARIS_EX2_1(14, 0, 3),
	ARIS_EX2_2(15, 0, 3),
	ARIS_EX2_3(16, 0, 3),

	//Armor Ability 1 T4
	ARIS_ARMOR1_1(17, 0, 4),
	ARIS_ARMOR1_2(18, 0, 4),
	ARIS_ARMOR1_3(19, 0, 4),

	//Armor Ability 2 T4
	ARIS_ARMOR2_1(20, 0, 4),
	ARIS_ARMOR2_2(21, 0, 4),
	ARIS_ARMOR2_3(22, 0, 4),

	//Armor Ability 3 T4
	ARIS_ARMOR3_1(23, 0, 4),
	ARIS_ARMOR3_2(24, 0, 4),
	ARIS_ARMOR3_3(25, 0, 4),

	//Nonomi T1
	NONOMI_T1_1(0, 1, 2),
	NONOMI_T1_2(1, 1, 2),
	NONOMI_T1_3(2, 1, 2),
	NONOMI_T1_4(3, 1, 2),

	//Nonomi T2
	NONOMI_T2_1(4, 1, 2),
	NONOMI_T2_2(5, 1, 2),
	NONOMI_T2_3(6, 1, 2),
	NONOMI_T2_4(7, 1, 2),
	NONOMI_T2_5(8, 1, 2),

	//Nonomi T3
	NONOMI_T3_1(9, 1, 3),
	NONOMI_T3_2(10, 1, 3),

	//Shoot All T3
	NONOMI_EX1_1(11, 1, 3),
	NONOMI_EX1_2(12, 1, 3),
	NONOMI_EX1_3(13, 1, 3),

	//Spreading Shot T3
	NONOMI_EX2_1(14, 1, 3),
	NONOMI_EX2_2(15, 1, 3),
	NONOMI_EX2_3(16, 1, 3),

	//Armor Ability 1 T4
	NONOMI_ARMOR1_1(17, 1, 4),
	NONOMI_ARMOR1_2(18, 1, 4),
	NONOMI_ARMOR1_3(19, 1, 4),

	//Armor Ability 2 T4
	NONOMI_ARMOR2_1(20, 1, 4),
	NONOMI_ARMOR2_2(21, 1, 4),
	NONOMI_ARMOR2_3(22, 1, 4),

	//Armor Ability 3 T4
	NONOMI_ARMOR3_1(23, 1, 4),
	NONOMI_ARMOR3_2(24, 1, 4),
	NONOMI_ARMOR3_3(25, 1, 4),

	//Miyako T1
	MIYAKO_T1_1(0, 2, 2),
	MIYAKO_T1_2(1, 2, 2),
	MIYAKO_T1_3(2, 2, 2),
	MIYAKO_T1_4(3, 2, 2),

	//Miyako T2
	MIYAKO_T2_1(4, 2, 2),
	MIYAKO_T2_2(5, 2, 2),
	MIYAKO_T2_3(6, 2, 2),
	MIYAKO_T2_4(7, 2, 2),
	MIYAKO_T2_5(8, 2, 2),

	//Miyako T3
	MIYAKO_T3_1(9, 2, 3),
	MIYAKO_T3_2(10, 2, 3),

	//Rabbit Squad T3
	MIYAKO_EX1_1(11, 2, 3),
	MIYAKO_EX1_2(12, 2, 3),
	MIYAKO_EX1_3(13, 2, 3),

	//Support Drone T3
	MIYAKO_EX2_1(14, 2, 3),
	MIYAKO_EX2_2(15, 2, 3),
	MIYAKO_EX2_3(16, 2, 3),

	//Armor Ability 1 T4
	MIYAKO_ARMOR1_1(17, 2, 4),
	MIYAKO_ARMOR1_2(18, 2, 4),
	MIYAKO_ARMOR1_3(19, 2, 4),

	//Armor Ability 2 T4
	MIYAKO_ARMOR2_1(20, 2, 4),
	MIYAKO_ARMOR2_2(21, 2, 4),
	MIYAKO_ARMOR2_3(22, 2, 4),

	//Armor Ability 3 T4
	MIYAKO_ARMOR3_1(23, 2, 4),
	MIYAKO_ARMOR3_2(24, 2, 4),
	MIYAKO_ARMOR3_3(25, 2, 4),

	//Hoshino T1
	HOSHINO_T1_1(0, 3, 2),
	HOSHINO_T1_2(1, 3, 2),
	HOSHINO_T1_3(2, 3, 2),
	HOSHINO_T1_4(3, 3, 2),

	//Hoshino T2
	HOSHINO_T2_1(4, 3, 2),
	HOSHINO_T2_2(5, 3, 2),
	HOSHINO_T2_3(6, 3, 2),
	HOSHINO_T2_4(7, 3, 2),
	HOSHINO_T2_5(8, 3, 2),

	//Hoshino T3
	HOSHINO_T3_1(9, 3, 3),
	HOSHINO_T3_2(10, 3, 3),

	//Shield Bash T3
	HOSHINO_EX1_1(11, 3, 3),
	HOSHINO_EX1_2(12, 3, 3),
	HOSHINO_EX1_3(13, 3, 3),

	//Defense Posture T3
	HOSHINO_EX2_1(14, 3, 3),
	HOSHINO_EX2_2(15, 3, 3),
	HOSHINO_EX2_3(16, 3, 3),

	//Armor Ability 1 T4
	HOSHINO_ARMOR1_1(17, 3, 4),
	HOSHINO_ARMOR1_2(18, 3, 4),
	HOSHINO_ARMOR1_3(19, 3, 4),

	//Armor Ability 2 T4
	HOSHINO_ARMOR2_1(20, 3, 4),
	HOSHINO_ARMOR2_2(21, 3, 4),
	HOSHINO_ARMOR2_3(22, 3, 4),

	//Armor Ability 3 T4
	HOSHINO_ARMOR3_1(23, 3, 4),
	HOSHINO_ARMOR3_2(24, 3, 4),
	HOSHINO_ARMOR3_3(25, 3, 4),

	//universal T4
	HEROIC_ENERGY(26, 0, 4), //See icon() and title() for special logic for this one
	//Ratmogrify T4
	RATSISTANCE(215, 4), RATLOMACY(216, 4), RATFORCEMENTS(217, 4);

	public static class ImprovisedProjectileCooldown extends FlavourBuff{
		public int icon() { return BuffIndicator.TIME; }
		public void tintIcon(Image icon) { icon.hardlight(0.15f, 0.2f, 0.5f); }
		public float iconFadePercent() { return Math.max(0, visualcooldown() / 50); }
	};
	public static class LethalMomentumTracker extends FlavourBuff{};
	public static class StrikingWaveTracker extends FlavourBuff{};
	public static class WandPreservationCounter extends CounterBuff{{revivePersists = true;}};
	public static class EmpoweredStrikeTracker extends FlavourBuff{
		//blast wave on-hit doesn't resolve instantly, so we delay detaching for it
		public boolean delayedDetach = false;
	};
	public static class ProtectiveShadowsTracker extends Buff {
		float barrierInc = 0.5f;

		@Override
		public boolean act() {
			//barrier every 2/1 turns, to a max of 3/5
			if (((Hero)target).hasTalent(Talent.PROTECTIVE_SHADOWS) && target.invisible > 0){
				Barrier barrier = Buff.affect(target, Barrier.class);
				if (barrier.shielding() < 1 + 2*((Hero)target).pointsInTalent(Talent.PROTECTIVE_SHADOWS)) {
					barrierInc += 0.5f * ((Hero) target).pointsInTalent(Talent.PROTECTIVE_SHADOWS);
				}
				if (barrierInc >= 1){
					barrierInc = 0;
					barrier.incShield(1);
				} else {
					barrier.incShield(0); //resets barrier decay
				}
			} else {
				detach();
			}
			spend( TICK );
			return true;
		}

		private static final String BARRIER_INC = "barrier_inc";
		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put( BARRIER_INC, barrierInc);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			barrierInc = bundle.getFloat( BARRIER_INC );
		}
	}
	public static class BountyHunterTracker extends FlavourBuff{};
	public static class RejuvenatingStepsCooldown extends FlavourBuff{
		public int icon() { return BuffIndicator.TIME; }
		public void tintIcon(Image icon) { icon.hardlight(0f, 0.35f, 0.15f); }
		public float iconFadePercent() { return GameMath.gate(0, visualcooldown() / (15 - 5*Dungeon.hero.pointsInTalent(REJUVENATING_STEPS)), 1); }
	};
	public static class RejuvenatingStepsFurrow extends CounterBuff{{revivePersists = true;}};
	public static class SeerShotCooldown extends FlavourBuff{
		public int icon() { return target.buff(RevealedArea.class) != null ? BuffIndicator.NONE : BuffIndicator.TIME; }
		public void tintIcon(Image icon) { icon.hardlight(0.7f, 0.4f, 0.7f); }
		public float iconFadePercent() { return Math.max(0, visualcooldown() / 20); }
	};
	public static class SpiritBladesTracker extends FlavourBuff{};
	public static class PatientStrikeTracker extends Buff {
		public int pos;
		{ type = Buff.buffType.POSITIVE; }
		public int icon() { return BuffIndicator.TIME; }
		public void tintIcon(Image icon) { icon.hardlight(0.5f, 0f, 1f); }
		@Override
		public boolean act() {
			if (pos != target.pos) {
				detach();
			} else {
				spend(TICK);
			}
			return true;
		}
		private static final String POS = "pos";
		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(POS, pos);
		}
		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			pos = bundle.getInt(POS);
		}
	};
	public static class AggressiveBarrierCooldown extends FlavourBuff{
		public int icon() { return BuffIndicator.TIME; }
		public void tintIcon(Image icon) { icon.hardlight(0.35f, 0f, 0.7f); }
		public float iconFadePercent() { return Math.max(0, visualcooldown() / 50); }
	};
	public static class LiquidAgilEVATracker extends FlavourBuff{
		{
			//detaches after hero acts, not after mobs act
			actPriority = HERO_PRIO+1;
		}
	};
	public static class LiquidAgilACCTracker extends FlavourBuff{
		public int uses;

		{ type = buffType.POSITIVE; }
		public int icon() { return BuffIndicator.INVERT_MARK; }
		public void tintIcon(Image icon) { icon.hardlight(0.5f, 0f, 1f); }
		public float iconFadePercent() { return Math.max(0, 1f - (visualcooldown() / 5)); }

		private static final String USES = "uses";
		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(USES, uses);
		}
		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			uses = bundle.getInt(USES);
		}
	};
	public static class LethalHasteCooldown extends FlavourBuff{
		public int icon() { return BuffIndicator.TIME; }
		public void tintIcon(Image icon) { icon.hardlight(0.35f, 0f, 0.7f); }
		public float iconFadePercent() { return Math.max(0, visualcooldown() / 100); }
	};
	public static class SwiftEquipCooldown extends FlavourBuff{
		public boolean secondUse;
		public boolean hasSecondUse(){
			return secondUse;
		}

		public int icon() { return BuffIndicator.TIME; }
		public void tintIcon(Image icon) {
			if (hasSecondUse()) icon.hardlight(0.85f, 0f, 1.0f);
			else                icon.hardlight(0.35f, 0f, 0.7f);
		}
		public float iconFadePercent() { return GameMath.gate(0, visualcooldown() / 20f, 1); }

		private static final String SECOND_USE = "second_use";
		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(SECOND_USE, secondUse);
		}
		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			secondUse = bundle.getBoolean(SECOND_USE);
		}
	};
	public static class DeadlyFollowupTracker extends FlavourBuff{
		public int object;
		{ type = Buff.buffType.POSITIVE; }
		public int icon() { return BuffIndicator.INVERT_MARK; }
		public void tintIcon(Image icon) { icon.hardlight(0.5f, 0f, 1f); }
		public float iconFadePercent() { return Math.max(0, 1f - (visualcooldown() / 5)); }
		private static final String OBJECT    = "object";
		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(OBJECT, object);
		}
		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			object = bundle.getInt(OBJECT);
		}
	}
	public static class PreciseAssaultTracker extends FlavourBuff{
		{ type = buffType.POSITIVE; }
		public int icon() { return BuffIndicator.INVERT_MARK; }
		public void tintIcon(Image icon) { icon.hardlight(1f, 1f, 0.0f); }
		public float iconFadePercent() { return Math.max(0, 1f - (visualcooldown() / 5)); }
	};
	public static class VariedChargeTracker extends Buff{
		public Class weapon;

		private static final String WEAPON    = "weapon";
		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(WEAPON, weapon);
		}
		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			weapon = bundle.getClass(WEAPON);
		}
	}
	public static class CombinedLethalityAbilityTracker extends FlavourBuff{
		public MeleeWeapon weapon;
	};
	public static class CombinedEnergyAbilityTracker extends FlavourBuff{
		public boolean monkAbilused = false;
		public boolean wepAbilUsed = false;

		private static final String MONK_ABIL_USED  = "monk_abil_used";
		private static final String WEP_ABIL_USED   = "wep_abil_used";
		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(MONK_ABIL_USED, monkAbilused);
			bundle.put(WEP_ABIL_USED, wepAbilUsed);
		}
		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			monkAbilused = bundle.getBoolean(MONK_ABIL_USED);
			wepAbilUsed = bundle.getBoolean(WEP_ABIL_USED);
		}
	}
	public static class CounterAbilityTacker extends FlavourBuff{}
	public static class SatiatedSpellsTracker extends Buff{
		@Override
		public int icon() {
			return BuffIndicator.SPELL_FOOD;
		}
	}
	//used for metamorphed searing light
	public static class SearingLightCooldown extends FlavourBuff{
		@Override
		public int icon() {
			return BuffIndicator.TIME;
		}
		public void tintIcon(Image icon) { icon.hardlight(0f, 0f, 1f); }
		public float iconFadePercent() { return Math.max(0, visualcooldown() / 20); }
	}

	int icon;
	int maxPoints;

	// tiers 1/2/3/4 start at levels 2/7/13/21
	public static int[] tierLevelThresholds = new int[]{0, 2, 7, 13, 21, 31};

	public static final int TALENT_NUMBER = 32;

	Talent( int x, int y ){
		this(x, y, 2);
	}

	Talent( int x, int y, int maxPoints ){
		this.icon = x+TALENT_NUMBER*y;
		this.maxPoints = maxPoints;
	}

	public int icon(){
		if (this == HEROIC_ENERGY){
			int x = 26;
			int y = 0;
			HeroClass cls = Dungeon.hero != null ? Dungeon.hero.heroClass : GamesInProgress.selectedClass;
			switch (cls){
				case ARIS: default:
					y = 0;
					break;
				case NONOMI:
					y = 1;
					break;
				case MIYAKO:
					y = 2;
					break;
				case HOSHINO:
					y = 3;
					break;
//				case SHIROKO:
//					y = 4;
//					break;
//				case NOA:
//					y = 5;
//					break;
//				case MIYU:
//					y = 6;
//					break;
//				case YUZU:
//					y = 7;
//					break;
//				case IZUNA:
//					y = 8;
//					break;
			}
			if (Ratmogrify.useRatroicEnergy){
				y = 9;
			}
			return x+TALENT_NUMBER*y;
		} else {
			return icon;
		}
	}

	public int maxPoints(){
		return maxPoints;
	}

	public String title(){
		if (this == HEROIC_ENERGY && Ratmogrify.useRatroicEnergy){
			return Messages.get(this, name() + ".rat_title");
		}
		return Messages.get(this, name() + ".title");
	}

	public final String desc(){
		return desc(false);
	}

	public String desc(boolean metamorphed){
		if (metamorphed){
			String metaDesc = Messages.get(this, name() + ".meta_desc");
			if (!metaDesc.equals(Messages.NO_TEXT_FOUND)){
				return Messages.get(this, name() + ".desc") + "\n\n" + metaDesc;
			}
		}
		return Messages.get(this, name() + ".desc");
	}

	public static void onTalentUpgraded( Hero hero, Talent talent ){
		//for metamorphosis
		if (talent == IRON_WILL && hero.heroClass != HeroClass.WARRIOR){
			Buff.affect(hero, BrokenSeal.WarriorShield.class);
		}

		if (talent == VETERANS_INTUITION && hero.pointsInTalent(VETERANS_INTUITION) == 2){
			if (hero.belongings.armor() != null && !ShardOfOblivion.passiveIDDisabled())  {
				hero.belongings.armor.identify();
			}
		}
		if (talent == THIEFS_INTUITION && hero.pointsInTalent(THIEFS_INTUITION) == 2){
			if (hero.belongings.ring instanceof Ring && !ShardOfOblivion.passiveIDDisabled()) {
				hero.belongings.ring.identify();
			}
			if (hero.belongings.misc instanceof Ring && !ShardOfOblivion.passiveIDDisabled()) {
				hero.belongings.misc.identify();
			}
			for (Item item : Dungeon.hero.belongings){
				if (item instanceof Ring){
					((Ring) item).setKnown();
				}
			}
		}
		if (talent == THIEFS_INTUITION && hero.pointsInTalent(THIEFS_INTUITION) == 1){
			if (hero.belongings.ring instanceof Ring) hero.belongings.ring.setKnown();
			if (hero.belongings.misc instanceof Ring) ((Ring) hero.belongings.misc).setKnown();
		}
		if (talent == ADVENTURERS_INTUITION && hero.pointsInTalent(ADVENTURERS_INTUITION) == 2){
			if (hero.belongings.weapon() != null && !ShardOfOblivion.passiveIDDisabled()){
				hero.belongings.weapon().identify();
			}
		}

		if (talent == PROTECTIVE_SHADOWS && hero.invisible > 0){
			Buff.affect(hero, Talent.ProtectiveShadowsTracker.class);
		}

		if (talent == LIGHT_CLOAK && hero.heroClass == HeroClass.ROGUE){
			for (Item item : Dungeon.hero.belongings.backpack){
				if (item instanceof CloakOfShadows){
					if (!hero.belongings.lostInventory() || item.keptThroughLostInventory()) {
						((CloakOfShadows) item).activate(Dungeon.hero);
					}
				}
			}
		}

		if (talent == HEIGHTENED_SENSES || talent == FARSIGHT || talent == DIVINE_SENSE || talent == MIYAKO_T2_3 || talent == MIYAKO_EX2_3){
			Dungeon.observe();
		}

		if (talent == TWIN_UPGRADES || talent == DESPERATE_POWER
				|| talent == STRONGMAN || talent == DURABLE_PROJECTILES){
			Item.updateQuickslot();
		}

		if (talent == UNENCUMBERED_SPIRIT && hero.pointsInTalent(talent) == 3){
			Item toGive = new ClothArmor().identify();
			if (!toGive.collect()){
				Dungeon.level.drop(toGive, hero.pos).sprite.drop();
			}
			toGive = new Gloves().identify();
			if (!toGive.collect()){
				Dungeon.level.drop(toGive, hero.pos).sprite.drop();
			}
		}

		if (talent == LIGHT_READING && hero.heroClass == HeroClass.CLERIC){
			for (Item item : Dungeon.hero.belongings.backpack){
				if (item instanceof HolyTome){
					if (!hero.belongings.lostInventory() || item.keptThroughLostInventory()) {
						((HolyTome) item).activate(Dungeon.hero);
					}
				}
			}
		}

		//if we happen to have spirit form applied with a ring of might
		if (talent == SPIRIT_FORM){
			Dungeon.hero.updateHT(false);
		}

		if (talent == ARIS_T1_2 && hero.pointsInTalent(ARIS_T1_2) == 2){
			if (hero.belongings.armor() != null && !ShardOfOblivion.passiveIDDisabled())  {
				hero.belongings.armor.identify();
			}
			if (hero.belongings.weapon() != null && !ShardOfOblivion.passiveIDDisabled())  {
				hero.belongings.weapon.identify();
			}
		}

		if (talent == NONOMI_T1_2 && !ShardOfOblivion.passiveIDDisabled()) {
			if (hero.pointsInTalent(NONOMI_T1_2) == 1) {
				if (hero.belongings.weapon() instanceof MG)  {
					hero.belongings.weapon.identify();
				}
			}
			if (hero.pointsInTalent(NONOMI_T1_2) == 2) {
				for (Item i : hero.belongings.getAllItems(MG.class)) {
					i.identify();
				}
			}
		}

		if (talent == MIYAKO_T1_2 && !ShardOfOblivion.passiveIDDisabled()) {
			if (hero.pointsInTalent(MIYAKO_T1_2) == 1) {
				if (hero.belongings.weapon() instanceof SMG)  {
					hero.belongings.weapon.identify();
				}
			}
			if (hero.pointsInTalent(MIYAKO_T1_2) == 2) {
				for (Item i : hero.belongings.getAllItems(SMG.class)) {
					i.identify();
				}
			}
		}

		if (talent == NONOMI_T3_1 && hero.pointsInTalent(NONOMI_T3_1) == 1) {
			new MG_SP().identify().collect();
		}
		if (talent == NONOMI_T3_1) {
			Item.updateQuickslot();
		}

		if (talent == NONOMI_EX1_1) {
			Item.updateQuickslot();
		}

		if (talent == MIYAKO_T2_2) {
			Item.updateQuickslot();
		}

		if (talent == MIYAKO_T2_5) {
			hero.updateHT(true);
		}
	}

	public static class CachedRationsDropped extends CounterBuff{{revivePersists = true;}};
	public static class NatureBerriesDropped extends CounterBuff{{revivePersists = true;}};

	public static void onFoodEaten( Hero hero, float foodVal, Item foodSource ){
		if (hero.hasTalent(HEARTY_MEAL)){
			//4/6 HP healed, when hero is below 33% health (with a little rounding up)
			if (hero.HP/(float)hero.HT < 0.334f) {
				int healing = 2 + 2 * hero.pointsInTalent(HEARTY_MEAL);
				hero.HP = Math.min(hero.HP + healing, hero.HT);
				hero.sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(healing), FloatingText.HEALING);

			}
		}
		if (hero.hasTalent(NONOMI_T1_1)){
			//3/5 HP healed, when hero is below 30% health
			if (hero.HP/(float)hero.HT <= 0.4f) {
				int healing = 1 + 2 * hero.pointsInTalent(NONOMI_T1_1);
				hero.heal(healing);
			}
		}
		if (hero.hasTalent(IRON_STOMACH) || hero.hasTalent(Talent.ARIS_T2_1)){
			if (hero.cooldown() > 0) {
				Buff.affect(hero, WarriorFoodImmunity.class, hero.cooldown());
			}
		}
		if (hero.hasTalent(EMPOWERING_MEAL)){
			//2/3 bonus wand damage for next 3 zaps
			Buff.affect( hero, WandEmpower.class).set(1 + hero.pointsInTalent(EMPOWERING_MEAL), 3);
			ScrollOfRecharging.charge( hero );
		}
		int wandChargeTurns = 0;
		if (hero.hasTalent(ENERGIZING_MEAL)){
			//5/8 turns of recharging
			wandChargeTurns += 2 + 3*hero.pointsInTalent(ENERGIZING_MEAL);
		}
		int artifactChargeTurns = 0;
		if (hero.hasTalent(MYSTICAL_MEAL)){
			//3/5 turns of recharging
			artifactChargeTurns += 1 + 2*hero.pointsInTalent(MYSTICAL_MEAL);
		}
		if (hero.hasTalent(INVIGORATING_MEAL)){
			//effectively 1/2 turns of haste
			Buff.prolong( hero, Haste.class, 0.67f+hero.pointsInTalent(INVIGORATING_MEAL));
		}
		if (hero.hasTalent(STRENGTHENING_MEAL)){
			//3 bonus physical damage for next 2/3 attacks
			Buff.affect( hero, PhysicalEmpower.class).set(3, 1 + hero.pointsInTalent(STRENGTHENING_MEAL));
		}
		if (hero.hasTalent(FOCUSED_MEAL)){
			if (hero.heroClass == HeroClass.DUELIST){
				//0.67/1 charge for the duelist
				Buff.affect( hero, MeleeWeapon.Charger.class ).gainCharge((hero.pointsInTalent(FOCUSED_MEAL)+1)/3f);
				ScrollOfRecharging.charge( hero );
			} else {
				// lvl/3 / lvl/2 bonus dmg on next hit for other classes
				Buff.affect( hero, PhysicalEmpower.class).set(Math.round(hero.lvl / (4f - hero.pointsInTalent(FOCUSED_MEAL))), 1);
			}
		}
		if (hero.hasTalent(SATIATED_SPELLS)){
			if (hero.heroClass == HeroClass.CLERIC) {
				Buff.affect(hero, SatiatedSpellsTracker.class);
			} else {
				//3/5 shielding, delayed up to 10 turns
				int amount = 1 + 2*hero.pointsInTalent(SATIATED_SPELLS);
				Barrier b = Buff.affect(hero, Barrier.class);
				if (b.shielding() <= amount){
					b.setShield(amount);
					b.delay(Math.max(10-b.cooldown(), 0));
				}
			}
		}
		if (hero.hasTalent(ENLIGHTENING_MEAL)){
			if (hero.heroClass == HeroClass.CLERIC) {
				HolyTome tome = hero.belongings.getItem(HolyTome.class);
				if (tome != null) {
					// 2/3 of a charge at +1, 1 full charge at +2
					tome.directCharge( (1+hero.pointsInTalent(ENLIGHTENING_MEAL))/3f );
					ScrollOfRecharging.charge(hero);
				}
			} else {
				//2/3 turns of recharging, both kinds
				wandChargeTurns += 1 + hero.pointsInTalent(ENLIGHTENING_MEAL);
				artifactChargeTurns += 1 + hero.pointsInTalent(ENLIGHTENING_MEAL);
			}
		}

		//we process these at the end as they can stack together from some talents
		if (wandChargeTurns > 0){
			Buff.prolong( hero, Recharging.class, wandChargeTurns );
			ScrollOfRecharging.charge( hero );
			SpellSprite.show(hero, SpellSprite.CHARGE);
		}
		if (artifactChargeTurns > 0){
			ArtifactRecharge buff = Buff.affect( hero, ArtifactRecharge.class);
			if (buff.left() < artifactChargeTurns){
				buff.set(artifactChargeTurns).ignoreHornOfPlenty = foodSource instanceof HornOfPlenty;
			}
			ScrollOfRecharging.charge( hero );
			SpellSprite.show(hero, SpellSprite.CHARGE, 0, 1, 1);
		}
		if (hero.hasTalent(Talent.NONOMI_T2_1)) {
			KindOfWeapon weapon = hero.belongings.weapon();
			if (weapon instanceof Gun) {
				((Gun) weapon).quickReload();
				if (hero.pointsInTalent(Talent.NONOMI_T2_1) == 2) {
					((Gun) weapon).manualReload(1, true);
				}
			}
		}
		if (hero.hasTalent(Talent.MIYAKO_T2_1)) {
			Buff.affect(hero, Barrier.class).setShield((int)(hero.HT*(0.5f+0.5f*hero.pointsInTalent(Talent.MIYAKO_T2_1))));
		}
	}

	public static class WarriorFoodImmunity extends FlavourBuff{
		{ actPriority = HERO_PRIO+1; }
	}

	public static float itemIDSpeedFactor( Hero hero, Item item ){
		float factor = 1f;

		// Affected by both Warrior(1.75x/2.5x) and Duelist(2.5x/inst.) talents
		if (item instanceof MeleeWeapon){
			factor *= 1f + 1.5f*hero.pointsInTalent(ADVENTURERS_INTUITION); //instant at +2 (see onItemEquipped)
			factor *= 1f + 0.75f*hero.pointsInTalent(VETERANS_INTUITION);
			factor *= 1f + 1.5f*hero.pointsInTalent(ARIS_T1_2);
		}
		// Affected by both Warrior(2.5x/inst.) and Duelist(1.75x/2.5x) talents
		if (item instanceof Armor){
			factor *= 1f + 0.75f*hero.pointsInTalent(ADVENTURERS_INTUITION);
			factor *= 1f + hero.pointsInTalent(VETERANS_INTUITION); //instant at +2 (see onItemEquipped)
			factor *= 1f + 1.5f*hero.pointsInTalent(ARIS_T1_2);
		}
		// 3x/instant for Mage (see Wand.wandUsed())
		if (item instanceof Wand){
			factor *= 1f + 2.0f*hero.pointsInTalent(SCHOLARS_INTUITION);
		}
		// 3x/instant speed with Huntress talent (see MissileWeapon.proc)
		if (item instanceof MissileWeapon){
			factor *= 1f + 2.0f*hero.pointsInTalent(SURVIVALISTS_INTUITION);
		}
		// 2x/instant for Rogue (see onItemEqupped), also id's type on equip/on pickup
		if (item instanceof Ring){
			factor *= 1f + hero.pointsInTalent(THIEFS_INTUITION);
		}
		return factor;
	}

	public static void onPotionUsed( Hero hero, int cell, float factor ){
		if (hero.hasTalent(LIQUID_WILLPOWER)){
			// 6.5/10% of max HP
			int shieldToGive = Math.round( factor * hero.HT * (0.030f + 0.035f*hero.pointsInTalent(LIQUID_WILLPOWER)));
			hero.sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(shieldToGive), FloatingText.SHIELDING);
			Buff.affect(hero, Barrier.class).setShield(shieldToGive);
		}
		if (hero.hasTalent(LIQUID_NATURE)){
			ArrayList<Integer> grassCells = new ArrayList<>();
			for (int i : PathFinder.NEIGHBOURS9){
				grassCells.add(cell+i);
			}
			Random.shuffle(grassCells);
			for (int grassCell : grassCells){
				Char ch = Actor.findChar(grassCell);
				if (ch != null && ch.alignment == Char.Alignment.ENEMY){
					//1/2 turns of roots
					Buff.affect(ch, Roots.class, factor * hero.pointsInTalent(LIQUID_NATURE));
				}
				if (Dungeon.level.map[grassCell] == Terrain.EMPTY ||
						Dungeon.level.map[grassCell] == Terrain.EMBERS ||
						Dungeon.level.map[grassCell] == Terrain.EMPTY_DECO){
					Level.set(grassCell, Terrain.GRASS);
					GameScene.updateMap(grassCell);
				}
				CellEmitter.get(grassCell).burst(LeafParticle.LEVEL_SPECIFIC, 4);
			}
			// 4/6 cells total
			int totalGrassCells = (int) (factor * (2 + 2 * hero.pointsInTalent(LIQUID_NATURE)));
			while (grassCells.size() > totalGrassCells){
				grassCells.remove(0);
			}
			for (int grassCell : grassCells){
				int t = Dungeon.level.map[grassCell];
				if ((t == Terrain.EMPTY || t == Terrain.EMPTY_DECO || t == Terrain.EMBERS
						|| t == Terrain.GRASS || t == Terrain.FURROWED_GRASS)
						&& Dungeon.level.plants.get(grassCell) == null){
					Level.set(grassCell, Terrain.HIGH_GRASS);
					GameScene.updateMap(grassCell);
				}
			}
			Dungeon.observe();
		}
		if (hero.hasTalent(LIQUID_AGILITY)){
			Buff.prolong(hero, LiquidAgilEVATracker.class, hero.cooldown() + Math.max(0, factor-1));
			if (factor >= 0.5f){
				Buff.prolong(hero, LiquidAgilACCTracker.class, 5f).uses = Math.round(factor);
			}
		}
	}

	public static void onScrollUsed( Hero hero, int pos, float factor, Class<?extends Item> cls ){
		if (hero.hasTalent(INSCRIBED_POWER)){
			// 2/3 empowered wand zaps
			Buff.affect(hero, ScrollEmpower.class).reset((int) (factor * (1 + hero.pointsInTalent(INSCRIBED_POWER))));
		}
		if (hero.hasTalent(INSCRIBED_STEALTH)){
			// 3/5 turns of stealth
			Buff.affect(hero, Invisibility.class, factor * (1 + 2*hero.pointsInTalent(INSCRIBED_STEALTH)));
			Sample.INSTANCE.play( Assets.Sounds.MELD );
		}
		if (hero.hasTalent(RECALL_INSCRIPTION) && Scroll.class.isAssignableFrom(cls) && cls != ScrollOfUpgrade.class){
			if (hero.heroClass == HeroClass.CLERIC){
				Buff.prolong(hero, RecallInscription.UsedItemTracker.class, hero.pointsInTalent(RECALL_INSCRIPTION) == 2 ? 300 : 10).item = cls;
			} else {
				// 10/15%
				if (Random.Int(20) < 1 + hero.pointsInTalent(RECALL_INSCRIPTION)){
					Reflection.newInstance(cls).collect();
					GLog.p("refunded!");
				}
			}
		}
	}

	public static void onRunestoneUsed( Hero hero, int pos, Class<?extends Item> cls ){
		if (hero.hasTalent(RECALL_INSCRIPTION) && Runestone.class.isAssignableFrom(cls)){
			if (hero.heroClass == HeroClass.CLERIC){
				Buff.prolong(hero, RecallInscription.UsedItemTracker.class, hero.pointsInTalent(RECALL_INSCRIPTION) == 2 ? 300 : 10).item = cls;
			} else {

				//don't trigger on 1st intuition use
				if (cls.equals(StoneOfIntuition.class) && hero.buff(StoneOfIntuition.IntuitionUseTracker.class) != null){
					return;
				}
				// 10/15%
				if (Random.Int(20) < 1 + hero.pointsInTalent(RECALL_INSCRIPTION)){
					Reflection.newInstance(cls).collect();
					GLog.p("refunded!");
				}
			}
		}
	}

	public static void onArtifactUsed( Hero hero ){
		if (hero.hasTalent(ENHANCED_RINGS)){
			Buff.prolong(hero, EnhancedRings.class, 3f*hero.pointsInTalent(ENHANCED_RINGS));
		}

		if (Dungeon.hero.heroClass != HeroClass.CLERIC
				&& Dungeon.hero.hasTalent(Talent.DIVINE_SENSE)){
			Buff.prolong(Dungeon.hero, DivineSense.DivineSenseTracker.class, Dungeon.hero.cooldown()+1);
		}

		// 10/20/30%
		if (Dungeon.hero.heroClass != HeroClass.CLERIC
				&& Dungeon.hero.hasTalent(Talent.CLEANSE)
				&& Random.Int(10) < Dungeon.hero.pointsInTalent(Talent.CLEANSE)){
			boolean removed = false;
			for (Buff b : Dungeon.hero.buffs()) {
				if (b.type == Buff.buffType.NEGATIVE
						&& !(b instanceof LostInventory)) {
					b.detach();
					removed = true;
				}
			}
			if (removed && Dungeon.hero.sprite != null) {
				new Flare( 6, 32 ).color(0xFF4CD2, true).show( Dungeon.hero.sprite, 2f );
			}
		}
	}

	public static void onItemEquipped( Hero hero, Item item ){
		boolean identify = false;
		if (hero.pointsInTalent(VETERANS_INTUITION) == 2 && item instanceof Armor){
			identify = true;
		}
		if (hero.hasTalent(THIEFS_INTUITION) && item instanceof Ring){
			if (hero.pointsInTalent(THIEFS_INTUITION) == 2){
				identify = true;
			}
			((Ring) item).setKnown();
		}
		if (hero.pointsInTalent(ADVENTURERS_INTUITION) == 2 && item instanceof Weapon){
			identify = true;
		}
		if (hero.pointsInTalent(ARIS_T1_2) == 2 && (item instanceof Weapon || item instanceof Armor)){
			identify = true;
		}
		if (hero.hasTalent(NONOMI_T1_2) && (item instanceof MG)){
			identify = true;
		}
		if (hero.hasTalent(MIYAKO_T1_2) && (item instanceof SMG)){
			identify = true;
		}

		if (identify) {
			if (ShardOfOblivion.passiveIDDisabled()) {
				if (item instanceof Weapon){
					((Weapon) item).setIDReady();
				} else if (item instanceof Armor){
					((Armor) item).setIDReady();
				} else if (item instanceof Ring){
					((Ring) item).setIDReady();
				}
			} else {
				item.identify();
			}
		}
	}

	public static void onItemCollected( Hero hero, Item item ){
		if (hero.pointsInTalent(THIEFS_INTUITION) == 2){
			if (item instanceof Ring) ((Ring) item).setKnown();
		}

		boolean identify = false;

		if (hero.pointsInTalent(NONOMI_T1_2) == 2 && (item instanceof MG)){
			identify = true;
		}

		if (hero.pointsInTalent(MIYAKO_T1_2) == 2 && (item instanceof SMG)){
			identify = true;
		}

		if (identify && !ShardOfOblivion.passiveIDDisabled()){
			item.identify();
		}
	}

	//note that IDing can happen in alchemy scene, so be careful with VFX here
	public static void onItemIdentified( Hero hero, Item item ){
		//currently no talents that trigger here, it wasn't a very popular trigger =(
	}

	public static int onAttackProc( Hero hero, Char enemy, int dmg ){

		if (hero.hasTalent(Talent.PROVOKED_ANGER)
			&& hero.buff(ProvokedAngerTracker.class) != null){
			dmg += 1 + 2*hero.pointsInTalent(Talent.PROVOKED_ANGER);
			hero.buff(ProvokedAngerTracker.class).detach();
		}

		if (hero.hasTalent(Talent.LINGERING_MAGIC)
				&& hero.buff(LingeringMagicTracker.class) != null){
			dmg += Random.IntRange(hero.pointsInTalent(Talent.LINGERING_MAGIC) , 2);
			hero.buff(LingeringMagicTracker.class).detach();
		}

		if (hero.hasTalent(Talent.SUCKER_PUNCH)
				&& enemy instanceof Mob && ((Mob) enemy).surprisedBy(hero)
				&& enemy.buff(SuckerPunchTracker.class) == null){
			dmg += Random.IntRange(hero.pointsInTalent(Talent.SUCKER_PUNCH) , 2);
			Buff.affect(enemy, SuckerPunchTracker.class);
		}

		if (hero.hasTalent(Talent.FOLLOWUP_STRIKE) && enemy.isAlive() && enemy.alignment == Char.Alignment.ENEMY) {
			if (hero.belongings.attackingWeapon() instanceof MissileWeapon) {
				Buff.prolong(hero, FollowupStrikeTracker.class, 5f).object = enemy.id();
			} else if (hero.buff(FollowupStrikeTracker.class) != null
					&& hero.buff(FollowupStrikeTracker.class).object == enemy.id()){
				dmg += 1 + hero.pointsInTalent(FOLLOWUP_STRIKE);
				hero.buff(FollowupStrikeTracker.class).detach();
			}
		}

		if (hero.buff(Talent.SpiritBladesTracker.class) != null
				&& Random.Int(10) < 3*hero.pointsInTalent(Talent.SPIRIT_BLADES)){
			SpiritBow bow = hero.belongings.getItem(SpiritBow.class);
			if (bow != null) dmg = bow.proc( hero, enemy, dmg );
			hero.buff(Talent.SpiritBladesTracker.class).detach();
		}

		if (hero.hasTalent(PATIENT_STRIKE)){
			if (hero.buff(PatientStrikeTracker.class) != null
					&& !(hero.belongings.attackingWeapon() instanceof MissileWeapon)){
				hero.buff(PatientStrikeTracker.class).detach();
				dmg += Random.IntRange(hero.pointsInTalent(Talent.PATIENT_STRIKE), 2);
			}
		}

		if (hero.hasTalent(DEADLY_FOLLOWUP) && enemy.alignment == Char.Alignment.ENEMY) {
			if (hero.belongings.attackingWeapon() instanceof MissileWeapon) {
				if (!(hero.belongings.attackingWeapon() instanceof SpiritBow.SpiritArrow)) {
					Buff.prolong(hero, DeadlyFollowupTracker.class, 5f).object = enemy.id();
				}
			} else if (hero.buff(DeadlyFollowupTracker.class) != null
					&& hero.buff(DeadlyFollowupTracker.class).object == enemy.id()){
				dmg = Math.round(dmg * (1.0f + .1f*hero.pointsInTalent(DEADLY_FOLLOWUP)));
			}
		}

		if (hero.hasTalent(Talent.ARIS_T1_1)) {
			dmg += hero.pointsInTalent(Talent.ARIS_T1_1);
		}

		if (hero.hasTalent(Talent.ARIS_T2_2)
				&& enemy.buff(HikariyoTracker.class) == null){
			Buff.affect(enemy, Blindness.class, 1+hero.pointsInTalent(Talent.ARIS_T2_2));
			Buff.affect(enemy, HikariyoTracker.class);
		}

		if (hero.hasTalent(Talent.ARIS_EX1_3) && hero.buff(SuperNova.SuperNovaCooldown.class) != null) {
			hero.buff(SuperNova.SuperNovaCooldown.class).hit(hero.pointsInTalent(Talent.ARIS_EX1_3));
		}

		if (hero.subClass == HeroSubClass.BATTERY_CHARGE) {
			Buff.affect(hero, SuperNovaCharge.class).hit();
		}

		if (hero.buff(Division.DivisionBuff.class) != null) {
			dmg = hero.buff(Division.DivisionBuff.class).attackProc(hero, enemy, dmg);
		}

		if (hero.hasTalent(Talent.NONOMI_T2_5) && enemy.buff(PushingTracker.class) == null && !(hero.belongings.attackingWeapon() instanceof MissileWeapon)) {
			Buff.affect(enemy, PushingTracker.class);
			Elastic.pushEnemyWithoutPit(hero, enemy, hero.belongings.weapon(), 1 + hero.pointsInTalent(Talent.NONOMI_T2_5));
			if (Random.Float() < 0.2f) {
				hero.yellI(Messages.get(Hero.class, "nonomi_push"));
			}
		}

		if (hero.hasTalent(Talent.NONOMI_EX1_3)
				&& !(hero.belongings.attackingWeapon() instanceof MissileWeapon)
				&& hero.buff(ShootAllBuff.OverHeat.class) != null) {
			if (Random.Float() < 0.1f * (hero.buff(ShootAllBuff.OverHeat.class).duration() + hero.pointsInTalent(Talent.NONOMI_EX1_3) - 1)) { //10*(OverHeat turns left + talent level - 1)
				Buff.affect(enemy, Burning.class).reignite(enemy);
			}
		}

		if (hero.hasTalent(Talent.NONOMI_EX1_2)
				&& !(hero.belongings.attackingWeapon() instanceof MissileWeapon)
				&& hero.buff(ShootAllBuff.OverHeat.class) != null) {
			if (Random.Float() < hero.pointsInTalent(Talent.NONOMI_EX1_2) / 3f) {
				hero.buff(ShootAllBuff.OverHeat.class).hit();
			}
		}

		if (hero.buff(Bipod.BipodBuff.class) != null
				&& !(hero.belongings.attackingWeapon() instanceof Gun.Bullet)) {
			hero.buff(Bipod.BipodBuff.class).detach();
		}

		if (hero.hasTalent(Talent.MIYAKO_T1_3) && hero.belongings.attackingWeapon() instanceof SMG) {
			dmg += Random.IntRange(1, hero.pointsInTalent(Talent.MIYAKO_T1_3));
		}

		if (hero.hasTalent(Talent.MIYAKO_T1_4)
				&& enemy instanceof Mob && ((Mob) enemy).surprisedBy(hero)
				&& enemy.buff(ConfusionTracker.class) == null){
			dmg += Random.IntRange(hero.pointsInTalent(Talent.MIYAKO_T1_4) , 2);
			Buff.affect(enemy, ConfusionTracker.class);
		}

		if (hero.buff(RabbitSquadBuff.class) != null) {
			hero.buff(RabbitSquadBuff.class).attack(enemy);
		}

		if (hero.buff(SupportDrone.class) != null) {
			hero.buff(SupportDrone.class).attackProc(enemy);
		}

		return dmg;
	}

	public static int onDefenseProc(Hero hero, Char enemy, int damage) {

		return damage;
	}

	public static class ProvokedAngerTracker extends FlavourBuff{
		{ type = Buff.buffType.POSITIVE; }
		public int icon() { return BuffIndicator.WEAPON; }
		public void tintIcon(Image icon) { icon.hardlight(1.43f, 1.43f, 1.43f); }
		public float iconFadePercent() { return Math.max(0, 1f - (visualcooldown() / 5)); }
	}
	public static class LingeringMagicTracker extends FlavourBuff{
		{ type = Buff.buffType.POSITIVE; }
		public int icon() { return BuffIndicator.WEAPON; }
		public void tintIcon(Image icon) { icon.hardlight(1.43f, 1.43f, 0f); }
		public float iconFadePercent() { return Math.max(0, 1f - (visualcooldown() / 5)); }
	}
	public static class SuckerPunchTracker extends Buff{};
	public static class FollowupStrikeTracker extends FlavourBuff{
		public int object;
		{ type = Buff.buffType.POSITIVE; }
		public int icon() { return BuffIndicator.INVERT_MARK; }
		public void tintIcon(Image icon) { icon.hardlight(0f, 0.75f, 1f); }
		public float iconFadePercent() { return Math.max(0, 1f - (visualcooldown() / 5)); }
		private static final String OBJECT    = "object";
		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(OBJECT, object);
		}
		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			object = bundle.getInt(OBJECT);
		}
	};

	//new buff here

	public static class HikariyoTracker extends Buff{};

	public static class RiposteTracker extends Buff {
		{
			actPriority = VFX_PRIO;
		}

		public Char enemy;

		@Override
		public boolean act() {
			target.sprite.attack(enemy.pos, new Callback() {
				@Override
				public void call() {
					AttackIndicator.target(enemy);
					if (target.attack(enemy, 1f, 0, 1)) {
						Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG);
					}

					next();
				}
			});
			detach();
			return false;
		}
	}

	public static class PushingTracker extends Buff {};
	public static class ConfusionTracker extends Buff{};

	//new buff here

	public static final int MAX_TALENT_TIERS = 4;

	public static void initClassTalents( Hero hero ){
		initClassTalents( hero.heroClass, hero.talents, hero.metamorphedTalents );
	}

	public static void initClassTalents( HeroClass cls, ArrayList<LinkedHashMap<Talent, Integer>> talents){
		initClassTalents( cls, talents, new LinkedHashMap<>());
	}

	public static void initClassTalents( HeroClass cls, ArrayList<LinkedHashMap<Talent, Integer>> talents, LinkedHashMap<Talent, Talent> replacements ){
		while (talents.size() < MAX_TALENT_TIERS){
			talents.add(new LinkedHashMap<>());
		}

		ArrayList<Talent> tierTalents = new ArrayList<>();

		//tier 1
		switch (cls){
			case ARIS: default:
				Collections.addAll(tierTalents, ARIS_T1_1, ARIS_T1_2, ARIS_T1_3, ARIS_T1_4);
				break;
			case NONOMI:
				Collections.addAll(tierTalents, NONOMI_T1_1, NONOMI_T1_2, NONOMI_T1_3, NONOMI_T1_4);
				break;
			case MIYAKO:
				Collections.addAll(tierTalents, MIYAKO_T1_1, MIYAKO_T1_2, MIYAKO_T1_3, MIYAKO_T1_4);
				break;
			case HOSHINO:
				Collections.addAll(tierTalents, HOSHINO_T1_1, HOSHINO_T1_2, HOSHINO_T1_3, HOSHINO_T1_4);
				break;
			case WARRIOR:
				Collections.addAll(tierTalents, HEARTY_MEAL, VETERANS_INTUITION, PROVOKED_ANGER, IRON_WILL);
				break;
			case MAGE:
				Collections.addAll(tierTalents, EMPOWERING_MEAL, SCHOLARS_INTUITION, LINGERING_MAGIC, BACKUP_BARRIER);
				break;
			case ROGUE:
				Collections.addAll(tierTalents, CACHED_RATIONS, THIEFS_INTUITION, SUCKER_PUNCH, PROTECTIVE_SHADOWS);
				break;
			case HUNTRESS:
				Collections.addAll(tierTalents, NATURES_BOUNTY, SURVIVALISTS_INTUITION, FOLLOWUP_STRIKE, NATURES_AID);
				break;
			case DUELIST:
				Collections.addAll(tierTalents, STRENGTHENING_MEAL, ADVENTURERS_INTUITION, PATIENT_STRIKE, AGGRESSIVE_BARRIER);
				break;
			case CLERIC:
				Collections.addAll(tierTalents, SATIATED_SPELLS, HOLY_INTUITION, SEARING_LIGHT, SHIELD_OF_LIGHT);
				break;
		}
		for (Talent talent : tierTalents){
			if (replacements.containsKey(talent)){
				talent = replacements.get(talent);
			}
			talents.get(0).put(talent, 0);
		}
		tierTalents.clear();

		//tier 2
		switch (cls){
			case ARIS: default:
				Collections.addAll(tierTalents, ARIS_T2_1, ARIS_T2_2, ARIS_T2_3, ARIS_T2_4, ARIS_T2_5);
				break;
			case NONOMI:
				Collections.addAll(tierTalents, NONOMI_T2_1, NONOMI_T2_2, NONOMI_T2_3, NONOMI_T2_4, NONOMI_T2_5);
				break;
			case MIYAKO:
				Collections.addAll(tierTalents, MIYAKO_T2_1, MIYAKO_T2_2, MIYAKO_T2_3, MIYAKO_T2_4, MIYAKO_T2_5);
				break;
			case HOSHINO:
				Collections.addAll(tierTalents, HOSHINO_T2_1, HOSHINO_T2_2, HOSHINO_T2_3, HOSHINO_T2_4, HOSHINO_T2_5);
				break;
			case WARRIOR:
				Collections.addAll(tierTalents, IRON_STOMACH, LIQUID_WILLPOWER, RUNIC_TRANSFERENCE, LETHAL_MOMENTUM, IMPROVISED_PROJECTILES);
				break;
			case MAGE:
				Collections.addAll(tierTalents, ENERGIZING_MEAL, INSCRIBED_POWER, WAND_PRESERVATION, ARCANE_VISION, SHIELD_BATTERY);
				break;
			case ROGUE:
				Collections.addAll(tierTalents, MYSTICAL_MEAL, INSCRIBED_STEALTH, WIDE_SEARCH, SILENT_STEPS, ROGUES_FORESIGHT);
				break;
			case HUNTRESS:
				Collections.addAll(tierTalents, INVIGORATING_MEAL, LIQUID_NATURE, REJUVENATING_STEPS, HEIGHTENED_SENSES, DURABLE_PROJECTILES);
				break;
			case DUELIST:
				Collections.addAll(tierTalents, FOCUSED_MEAL, LIQUID_AGILITY, WEAPON_RECHARGING, LETHAL_HASTE, SWIFT_EQUIP);
				break;
			case CLERIC:
				Collections.addAll(tierTalents, ENLIGHTENING_MEAL, RECALL_INSCRIPTION, SUNRAY, DIVINE_SENSE, BLESS);
				break;
		}
		for (Talent talent : tierTalents){
			if (replacements.containsKey(talent)){
				talent = replacements.get(talent);
			}
			talents.get(1).put(talent, 0);
		}
		tierTalents.clear();

		//tier 3
		switch (cls){
			case ARIS: default:
				Collections.addAll(tierTalents, ARIS_T3_1, ARIS_T3_2);
				break;
			case NONOMI:
				Collections.addAll(tierTalents, NONOMI_T3_1, NONOMI_T3_2);
				break;
			case MIYAKO:
				Collections.addAll(tierTalents, MIYAKO_T3_1, MIYAKO_T3_2);
				break;
			case HOSHINO:
				Collections.addAll(tierTalents, HOSHINO_T3_1, HOSHINO_T3_2);
				break;
			case WARRIOR:
				Collections.addAll(tierTalents, HOLD_FAST, STRONGMAN);
				break;
			case MAGE:
				Collections.addAll(tierTalents, DESPERATE_POWER, ALLY_WARP);
				break;
			case ROGUE:
				Collections.addAll(tierTalents, ENHANCED_RINGS, LIGHT_CLOAK);
				break;
			case HUNTRESS:
				Collections.addAll(tierTalents, POINT_BLANK, SEER_SHOT);
				break;
			case DUELIST:
				Collections.addAll(tierTalents, PRECISE_ASSAULT, DEADLY_FOLLOWUP);
				break;
			case CLERIC:
				Collections.addAll(tierTalents, CLEANSE, LIGHT_READING);
				break;
		}
		for (Talent talent : tierTalents){
			if (replacements.containsKey(talent)){
				talent = replacements.get(talent);
			}
			talents.get(2).put(talent, 0);
		}
		tierTalents.clear();

		//tier4
		//TBD
	}

	public static void initSubclassTalents( Hero hero ){
		initSubclassTalents( hero.subClass, hero.talents );
	}

	public static void initSubclassTalents( HeroSubClass cls, ArrayList<LinkedHashMap<Talent, Integer>> talents ){
		if (cls == HeroSubClass.NONE) return;

		while (talents.size() < MAX_TALENT_TIERS){
			talents.add(new LinkedHashMap<>());
		}

		ArrayList<Talent> tierTalents = new ArrayList<>();

		//tier 3
		switch (cls){
			case LIGHT_HERO: default:
				Collections.addAll(tierTalents, ARIS_EX1_1, ARIS_EX1_2, ARIS_EX1_3);
				break;
			case BATTERY_CHARGE:
				Collections.addAll(tierTalents, ARIS_EX2_1, ARIS_EX2_2, ARIS_EX2_3);
				break;
			case SHOOT_ALL:
				Collections.addAll(tierTalents, NONOMI_EX1_1, NONOMI_EX1_2, NONOMI_EX1_3);
				break;
			case SPREAD_SHOT:
				Collections.addAll(tierTalents, NONOMI_EX2_1, NONOMI_EX2_2, NONOMI_EX2_3);
				break;
			case RABBIT_SQUAD:
				Collections.addAll(tierTalents, MIYAKO_EX1_1, MIYAKO_EX1_2, MIYAKO_EX1_3);
				break;
			case SUPPORT_DRONE:
				Collections.addAll(tierTalents, MIYAKO_EX2_1, MIYAKO_EX2_2, MIYAKO_EX2_3);
				break;
			case SHIELD_BASH:
				Collections.addAll(tierTalents, HOSHINO_EX1_1, HOSHINO_EX1_2, HOSHINO_EX1_3);
				break;
			case DEFENSE_POSTURE:
				Collections.addAll(tierTalents, HOSHINO_EX2_1, HOSHINO_EX2_2, HOSHINO_EX2_3);
				break;
			case BERSERKER:
				Collections.addAll(tierTalents, ENDLESS_RAGE, DEATHLESS_FURY, ENRAGED_CATALYST);
				break;
			case GLADIATOR:
				Collections.addAll(tierTalents, CLEAVE, LETHAL_DEFENSE, ENHANCED_COMBO);
				break;
			case BATTLEMAGE:
				Collections.addAll(tierTalents, EMPOWERED_STRIKE, MYSTICAL_CHARGE, EXCESS_CHARGE);
				break;
			case WARLOCK:
				Collections.addAll(tierTalents, SOUL_EATER, SOUL_SIPHON, NECROMANCERS_MINIONS);
				break;
			case ASSASSIN:
				Collections.addAll(tierTalents, ENHANCED_LETHALITY, ASSASSINS_REACH, BOUNTY_HUNTER);
				break;
			case FREERUNNER:
				Collections.addAll(tierTalents, EVASIVE_ARMOR, PROJECTILE_MOMENTUM, SPEEDY_STEALTH);
				break;
			case SNIPER:
				Collections.addAll(tierTalents, FARSIGHT, SHARED_ENCHANTMENT, SHARED_UPGRADES);
				break;
			case WARDEN:
				Collections.addAll(tierTalents, DURABLE_TIPS, BARKSKIN, SHIELDING_DEW);
				break;
			case CHAMPION:
				Collections.addAll(tierTalents, VARIED_CHARGE, TWIN_UPGRADES, COMBINED_LETHALITY);
				break;
			case MONK:
				Collections.addAll(tierTalents, UNENCUMBERED_SPIRIT, MONASTIC_VIGOR, COMBINED_ENERGY);
				break;
			case PRIEST:
				Collections.addAll(tierTalents, HOLY_LANCE, HALLOWED_GROUND, MNEMONIC_PRAYER);
				break;
			case PALADIN:
				Collections.addAll(tierTalents, LAY_ON_HANDS, AURA_OF_PROTECTION, WALL_OF_LIGHT);
				break;
		}
		for (Talent talent : tierTalents){
			talents.get(2).put(talent, 0);
		}
		tierTalents.clear();

	}

	public static void initArmorTalents( Hero hero ){
		initArmorTalents( hero.armorAbility, hero.talents);
	}

	public static void initArmorTalents(ArmorAbility abil, ArrayList<LinkedHashMap<Talent, Integer>> talents ){
		if (abil == null) return;

		while (talents.size() < MAX_TALENT_TIERS){
			talents.add(new LinkedHashMap<>());
		}

		for (Talent t : abil.talents()){
			talents.get(3).put(t, 0);
		}
	}

	private static final String TALENT_TIER = "talents_tier_";

	public static void storeTalentsInBundle( Bundle bundle, Hero hero ){
		for (int i = 0; i < MAX_TALENT_TIERS; i++){
			LinkedHashMap<Talent, Integer> tier = hero.talents.get(i);
			Bundle tierBundle = new Bundle();

			for (Talent talent : tier.keySet()){
				if (tier.get(talent) > 0){
					tierBundle.put(talent.name(), tier.get(talent));
				}
				if (tierBundle.contains(talent.name())){
					tier.put(talent, Math.min(tierBundle.getInt(talent.name()), talent.maxPoints()));
				}
			}
			bundle.put(TALENT_TIER+(i+1), tierBundle);
		}

		Bundle replacementsBundle = new Bundle();
		for (Talent t : hero.metamorphedTalents.keySet()){
			replacementsBundle.put(t.name(), hero.metamorphedTalents.get(t));
		}
		bundle.put("replacements", replacementsBundle);
	}

	private static final HashSet<String> removedTalents = new HashSet<>();
	static{
		//nothing atm
	}

	private static final HashMap<String, String> renamedTalents = new HashMap<>();
	static{
		//nothing atm
	}

	public static void restoreTalentsFromBundle( Bundle bundle, Hero hero ){
		if (bundle.contains("replacements")){
			Bundle replacements = bundle.getBundle("replacements");
			for (String key : replacements.getKeys()){
				String value = replacements.getString(key);
				if (renamedTalents.containsKey(key)) key = renamedTalents.get(key);
				if (renamedTalents.containsKey(value)) value = renamedTalents.get(value);
				if (!removedTalents.contains(key) && !removedTalents.contains(value)){
					try {
						hero.metamorphedTalents.put(Talent.valueOf(key), Talent.valueOf(value));
					} catch (Exception e) {
						ShatteredPixelDungeon.reportException(e);
					}
				}
			}
		}

		if (hero.heroClass != null)     initClassTalents(hero);
		if (hero.subClass != null)      initSubclassTalents(hero);
		if (hero.armorAbility != null)  initArmorTalents(hero);

		for (int i = 0; i < MAX_TALENT_TIERS; i++){
			LinkedHashMap<Talent, Integer> tier = hero.talents.get(i);
			Bundle tierBundle = bundle.contains(TALENT_TIER+(i+1)) ? bundle.getBundle(TALENT_TIER+(i+1)) : null;

			if (tierBundle != null){
				for (String tName : tierBundle.getKeys()){
					int points = tierBundle.getInt(tName);
					if (renamedTalents.containsKey(tName)) tName = renamedTalents.get(tName);
					if (!removedTalents.contains(tName)) {
						try {
							Talent talent = Talent.valueOf(tName);
							if (tier.containsKey(talent)) {
								tier.put(talent, Math.min(points, talent.maxPoints()));
							}
						} catch (Exception e) {
							ShatteredPixelDungeon.reportException(e);
						}
					}
				}
			}
		}
	}

	public static boolean[] compassed = new boolean[32];

	public static float compassChance(){
		return compassChance(Dungeon.hero.pointsInTalent(Talent.ARIS_T3_2));
	}

	public static float compassChance( int level ){
		if (level == 0){
			return 0f;
		} else {
//            if (DeviceCompat.isDebug()) return 1;
//            else
			return level/3f;
		}
	}

	public static void onSwitchLevel() {
		if (Dungeon.branch == 0 && !compassed[Dungeon.depth]) { //브랜치 층에서는 작동하지 않음
			if (Random.Float() < compassChance()) {
				int len = Dungeon.level.length();
				boolean[] p = Dungeon.level.passable;
				boolean[] s = Dungeon.level.secret;
				boolean[] a = Dungeon.level.avoid;
				boolean[] passable = new boolean[len];
				for (int i = 0; i < len; i++) {
					passable[i] = (p[i] || s[i]) && !a[i];
				}

				PathFinder.Path path = Dungeon.findPath(Dungeon.hero, Dungeon.level.exit(), passable, passable, false);
				if (PathFinder.distance[Dungeon.level.exit()] == Integer.MAX_VALUE || path == null){
					Dungeon.hero.yellN(Messages.get(Hero.class, "aris_cant_find_path"));
				} else {
					int[] map = Dungeon.level.map;
					boolean[] mapped = Dungeon.level.mapped;
					boolean[] discoverable = Dungeon.level.discoverable;
					for (int i : path) {
						if (discoverable[i]) {
							int terr = map[i];

							mapped[i] = true;
							if ((Terrain.flags[terr] & Terrain.SECRET) != 0) {

								Dungeon.level.discover( i );

								if (Dungeon.level.heroFOV[i]) {
									GameScene.discoverTile( i, terr );
									ScrollOfMagicMapping.discover( i );
								}
							}
						}
					}
					GameScene.updateFog();
					Dungeon.hero.yellP(Messages.get(Hero.class, "aris_found_path"));
				}
			}

			compassed[Dungeon.depth] = true;
		}
	}
}

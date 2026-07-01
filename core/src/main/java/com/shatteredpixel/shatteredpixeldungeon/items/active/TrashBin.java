package com.shatteredpixel.shatteredpixeldungeon.items.active;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.NPC;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.LeafParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class TrashBin extends Item {

	{
		image = ItemSpriteSheet.TRASH_BIN;
		levelKnown = true;

		defaultAction = AC_USE;
		usesTargeting = false;

		bones = false;
		unique = true;
	}

	private static final String AC_USE = "USE";

	@Override
	public ArrayList<String> actions(Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		actions.add( AC_USE );
		return actions;
	}

	@Override
	public void execute( Hero hero, String action ) {
		super.execute( hero, action );
		if (action.equals(AC_USE)) {
			if (Dungeon.hero.buff(TrashBinCooldown.class) == null) {
				for (Char ch : Actor.chars()) {
					if (ch instanceof Mob && ch.alignment == Char.Alignment.ENEMY && ((Mob) ch).state != ((Mob) ch).SLEEPING) {
						new FlavourBuff(){
							{actPriority = VFX_PRIO;}
							public boolean act() {
								if (((Mob) ch).state == ((Mob) ch).HUNTING || ((Mob) ch).state == ((Mob) ch).FLEEING){
									((Mob) ch).clearEnemy();
									((Mob) ch).state = ((Mob) ch).WANDERING;
									((Mob) ch).beckon(Dungeon.level.randomDestination(ch));
									ch.sprite.showLost();
								}
								return super.act();
							}
						}.attachTo(ch);
					}
				}

				if (hero.subClass == HeroSubClass.CAMOUFLAGE) {
					for (int i : PathFinder.NEIGHBOURS8) {
						int cell = hero.pos + i;
						int t = Dungeon.level.map[cell];
						if (t == Terrain.EMPTY || t == Terrain.EMPTY_DECO || t == Terrain.EMBERS
								|| t == Terrain.GRASS) {
							Level.set(cell, Terrain.FURROWED_GRASS);
							CellEmitter.get(cell).burst(LeafParticle.LEVEL_SPECIFIC, 4);
							if (Dungeon.level.heroFOV[cell]) Dungeon.observe();
						}
						if (hero.hasTalent(Talent.MIYU_EX2_2) && t == Terrain.WATER) {
							switch (hero.pointsInTalent(Talent.MIYU_EX2_2)) {
								case 1: default:
									Level.set(cell, Terrain.GRASS);
									break;
								case 2:
									Level.set(cell, Terrain.FURROWED_GRASS);
									break;
								case 3:
									Level.set(cell, Terrain.HIGH_GRASS);
									break;
							}
							CellEmitter.get(cell).burst(LeafParticle.LEVEL_SPECIFIC, 4);
							if (Dungeon.level.heroFOV[cell]) Dungeon.observe();
						}
					}
				}
				hero.yellI("bin_use_" + (Random.IntRange(1, 3)));
				curUser.sprite.operate(curUser.pos);
				Sample.INSTANCE.play(Assets.Sounds.PUFF);
				CellEmitter.get( hero.pos ).burst( Speck.factory( Speck.WOOL ), 6 );
				curUser.next();

				updateQuickslot();

				Buff.affect(hero, TrashBinCooldown.class, TrashBinCooldown.DURATION-1);
			} else {
				hero.yellN("bin_cooldown");	//"...아직은 사용할 수 없어요..."
			}
		}
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}

	@Override
	public boolean isIdentified() {
		return true;
	}

	@Override
	public int value() {
		return -1;
	}

	public static class TrashBinCooldown extends FlavourBuff {

		public static final float DURATION = 20f;

		{
			type = buffType.NEUTRAL;
			announced = false;
		}

		@Override
		public int icon() {
			return BuffIndicator.TIME;
		}

		@Override
		public void tintIcon(Image icon) {
			icon.hardlight(0x2091DB);
		}

		@Override
		public float iconFadePercent() {
			return Math.max(0, (DURATION - visualcooldown()) / DURATION);
		}

	}

	public static class EvasionBuff extends FlavourBuff {

		public static final float DURATION = 3f;

		{
			type = buffType.POSITIVE;
			announced = true;
		}

		@Override
		public int icon() {
			return BuffIndicator.HASTE;
		}

		@Override
		public void tintIcon(Image icon) {
			icon.hardlight(0.25f, 1.5f, 1f);
		}

		@Override
		public float iconFadePercent() {
			return Math.max(0, (DURATION - visualcooldown()) / DURATION);
		}
	}
}

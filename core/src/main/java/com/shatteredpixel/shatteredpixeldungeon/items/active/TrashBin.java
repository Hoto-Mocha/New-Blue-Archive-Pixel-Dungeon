package com.shatteredpixel.shatteredpixeldungeon.items.active;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.NPC;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
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
					if (ch instanceof Mob && !(ch instanceof NPC) && ch.alignment == Char.Alignment.ENEMY) {
						new FlavourBuff(){
							{actPriority = VFX_PRIO;}
							public boolean act() {
								if (((Mob) ch).state == ((Mob) ch).HUNTING){
									((Mob) ch).state = ((Mob) ch).WANDERING;
									((Mob) ch).beckon(Dungeon.level.randomDestination(ch));
									ch.sprite.showLost();
								}
								return super.act();
							}
						}.attachTo(ch);
					}
				}
				Dungeon.hero.yellI("bin_use_" + (Random.IntRange(1, 3)));
				curUser.sprite.operate(curUser.pos);
				Sample.INSTANCE.play(Assets.Sounds.PUFF);
				CellEmitter.get( hero.pos ).burst( Speck.factory( Speck.WOOL ), 6 );
				curUser.spendAndNext(Actor.TICK);

				Buff.affect(hero, TrashBinCooldown.class, TrashBinCooldown.DURATION);
			} else {
				Dungeon.hero.yellN("bin_cooldown");	//"...아직은 사용할 수 없어요..."
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
}

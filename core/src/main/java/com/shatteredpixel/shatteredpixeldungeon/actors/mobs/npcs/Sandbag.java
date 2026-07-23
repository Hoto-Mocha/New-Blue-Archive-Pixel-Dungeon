package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.CounterBuff;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SheepSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Random;

public class Sandbag extends NPC {

	{
		spriteClass = SheepSprite.class;
		HP = HT = 10;
		properties.add(Property.IMMOVABLE);
		properties.add(Property.DEMONIC);
		properties.add(Property.STATIC);
		properties.add(Property.INORGANIC);
	}

	@Override
	public int drRoll() {
		return 0;
	}

	@Override
	public int defenseSkill(Char enemy) {
		return 0;
	}

	@Override
	public void damage( int dmg, Object src ) {
		sprite.showStatus( CharSprite.NEGATIVE, Integer.toString(dmg) );
		Buff.affect(this, DPTTracker.class).countUp(dmg);
	}

	@Override
	public boolean interact(Char c) {
		return true;
	}

	public static class DPTTracker extends CounterBuff {
		{
			actPriority = HERO_PRIO+1;
		}

		@Override
		public void detach() {
			GLog.i(Messages.get(Sandbag.class, "damage", Integer.toString((int)count())));
			super.detach();
		}

		@Override
		public boolean act() {
			detach();
			return true;
		}
	}
}
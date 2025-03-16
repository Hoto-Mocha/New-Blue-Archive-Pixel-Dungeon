/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2023 Evan Debenham
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

package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.aris;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AllyBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corruption;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Levitation;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;

public class Division extends ArmorAbility {

	{
		baseChargeUse = 35f;
	}

	@Override
	protected void activate(ClassArmor armor, Hero hero, Integer target) {
		if (hero.buff(DivisionBuff.class) != null) {
			GLog.w(Messages.get(this, "already_activated"));
			return;
		}
		armor.charge -= chargeUse(hero);
		armor.updateQuickslot();
		Invisibility.dispel();
		hero.spendAndNext(Actor.TICK);
		hero.yellN(Messages.get(Hero.class, "aris_division_activate"));
		Buff.prolong(hero, DivisionBuff.class, 20f);
		if (hero.hasTalent(Talent.ARIS_ARMOR2_1)) {
			Buff.affect(hero, Barrier.class).setShield(hero.pointsInTalent(Talent.ARIS_ARMOR2_1) * 2);
		}
	}

	@Override
	public int icon() {
		return HeroIcon.ARIS_2;
	}

	@Override
	public Talent[] talents() {
		return new Talent[]{Talent.ARIS_ARMOR2_1, Talent.ARIS_ARMOR2_2, Talent.ARIS_ARMOR2_3, Talent.HEROIC_ENERGY};
	}

	public static class DivisionBuff extends Levitation {

		@Override
		public int icon() {
			return BuffIndicator.KEY;
		}

		@Override
		public void tintIcon(Image icon) {
			icon.hardlight(0xFF0000);
		}

		int extended = 0;

		public int attackProc(Char attacker, Char defender, int damage) {
			if (damage >= defender.HP
					&& !defender.isImmune(Corruption.class)
					&& defender.buff(Corruption.class) == null
					&& defender instanceof Mob
					&& defender.isAlive()) {

				Mob enemy = (Mob) defender;
				Hero hero = (attacker instanceof Hero) ? (Hero) attacker : Dungeon.hero;

				Corruption.corruptionHeal(enemy);

				AllyBuff.affectAndLoot(enemy, hero, Corruption.class);

				if (hero.hasTalent(Talent.ARIS_ARMOR2_2)) {
					hero.heal(hero.pointsInTalent(Talent.ARIS_ARMOR2_2) * 2);
				}

				if (hero.hasTalent(Talent.ARIS_ARMOR2_3) && extended < 8) {
					extend(hero.pointsInTalent(Talent.ARIS_ARMOR2_3) * 2);
					extended++;
				}
				return 0;
			}
			return damage;
		}

		public void extend(float time) {
			this.spend(time);
		}

		@Override
		public void detach() {
			super.detach();
			Dungeon.hero.yellI(Messages.get(Hero.class, "aris_division_detach"));
		}
	}
}

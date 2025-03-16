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

import static com.shatteredpixel.shatteredpixeldungeon.Dungeon.hero;

import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.SuperNova;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;

public class ExtendedLaser extends ArmorAbility {

	{
		baseChargeUse = 35f;
	}

	@Override
	protected void activate(ClassArmor armor, Hero hero, Integer target) {
		if (hero.buff(SuperNova.SuperNovaCooldown.class) != null) {
			GLog.w(Messages.get(this, "cooldown"));
			return;
		}
		if (hero.buff(ExtendedLaser.ExtendedLaserBuff.class) != null) {
			GLog.w(Messages.get(this, "already_used"));
			return;
		}
		hero.yellI(Messages.get(Hero.class, "aris_extendedlaser_activate"));
		armor.charge -= chargeUse(hero);
		armor.updateQuickslot();
		hero.sprite.operate(hero.pos);
		hero.spendAndNext(Actor.TICK);
		Buff.affect(hero, ExtendedLaserBuff.class);
	}

	@Override
	public int icon() {
		return HeroIcon.ARIS_1;
	}

	@Override
	public Talent[] talents() {
		return new Talent[]{Talent.ARIS_ARMOR1_1, Talent.ARIS_ARMOR1_2, Talent.ARIS_ARMOR1_3, Talent.HEROIC_ENERGY};
	}

	public static class ExtendedLaserBuff extends Buff {
		{
			type = buffType.POSITIVE;
			announced = true;
		}

		@Override
		public int icon() {
			return BuffIndicator.RECHARGING;
		}

		@Override
		public void tintIcon(Image icon) {
			icon.hardlight(0xFF0000);
		}
	}
}

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

import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.SuperNova;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.watabou.utils.Random;

public class BatteryChange extends ArmorAbility {

	{
		baseChargeUse = 35f;
	}

	@Override
	protected void activate(ClassArmor armor, Hero hero, Integer target) {
		if (hero.buff(SuperNova.SuperNovaCooldown.class) == null) {
			hero.yellW(Messages.get(Hero.class, "aris_no_cooldown"));
			return;
		}
		hero.yellP(Messages.get(Hero.class, "aris_battery_change"));
		hero.buff(SuperNova.SuperNovaCooldown.class).detach();

		armor.charge -= chargeUse(hero);
		armor.updateQuickslot();
		Invisibility.dispel();
		if (Random.Float() > 0.25f * hero.pointsInTalent(Talent.ARIS_ARMOR3_3)) {
			hero.spendAndNext(Actor.TICK);
		}
		if (hero.hasTalent(Talent.ARIS_ARMOR3_1)) {
			Buff.affect(hero, BatteryChangeCooldownBuff.class);
		}
		if (hero.hasTalent(Talent.ARIS_ARMOR3_2)) {
			Buff.affect(hero, BatteryChangeDamageBuff.class);
		}
		hero.sprite.operate(hero.pos);
	}

	@Override
	public int icon() {
		return HeroIcon.ARIS_3;
	}

	@Override
	public Talent[] talents() {
		return new Talent[]{Talent.ARIS_ARMOR3_1, Talent.ARIS_ARMOR3_2, Talent.ARIS_ARMOR3_3, Talent.HEROIC_ENERGY};
	}

	public static class BatteryChangeCooldownBuff extends Buff {}
	public static class BatteryChangeDamageBuff extends Buff {}
}

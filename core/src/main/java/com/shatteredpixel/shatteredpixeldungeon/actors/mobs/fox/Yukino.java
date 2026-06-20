package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.fox;

import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.AR.AR;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.sprites.YukinoSprite;

public class Yukino extends Fox {

	{
		spriteClass = YukinoSprite.class; //유키노 스프라이트 사용
		loot = new AR();
	}

	@Override
	protected boolean canAttack( Char enemy ) {
		Ballistica attack = new Ballistica( pos, enemy.pos, Ballistica.PROJECTILE);
		return attack.collisionPos == enemy.pos; //투사체가 닿을 수 있으면 공격 가능(근접 포함)
	}

	@Override
	public Fox findPartner() {
		if (nikoID != -1) return (Fox) Actor.findById(nikoID);
		if (kurumiID != -1) return (Fox) Actor.findById(kurumiID);
		if (otogiID != -1) return (Fox) Actor.findById(otogiID);
		return null;
	}
	
}

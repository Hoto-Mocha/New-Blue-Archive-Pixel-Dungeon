package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.fox;

import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.SMG.SMG;
import com.shatteredpixel.shatteredpixeldungeon.sprites.KurumiSprite;
import com.watabou.utils.Random;

public class Kurumi extends Fox {

	{
		spriteClass = KurumiSprite.class; //쿠루미 스프라이트 사용
		loot = new SMG();
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 1 + level/2, 1 + level + level/2 ); //낮은 공격력
	}

	@Override
	public int drRoll() {
		return super.drRoll() + Random.NormalIntRange(0, 3 + level); //높은 방어력
	}

	@Override
	protected boolean canAttack( Char enemy ) {
		return super.canAttack(enemy); //근접 공격만 가능
	}

	public void adjustStats( int level ) {
		HP = HT = (1 + level) * 8; //높은 체력
		defenseSkill = 1 + level/3; //낮은 회피
	}

	@Override
	public Fox findPartner() {
		if (otogiID != -1) return (Fox) Actor.findById(otogiID);
		if (yukinoID != -1) return (Fox) Actor.findById(yukinoID);
		if (nikoID != -1) return (Fox) Actor.findById(nikoID);
		return null;
	}
	
}

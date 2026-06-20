package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.fox;

import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.SG.SG;
import com.shatteredpixel.shatteredpixeldungeon.sprites.NikoSprite;
import com.watabou.utils.Random;

public class Niko extends Fox {

	{
		spriteClass = NikoSprite.class; //니코 스프라이트 사용
		loot = new SG();
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 1 + level, 3 + 3*level); //높은 공격력
	}

	@Override
	public int attackSkill( Char target ) {
		return 10 + level; //높은 정확성
	}

	@Override
	public float speed() {
		if (this.state == HUNTING) return super.speed() * 2; //이동 속도 2배
		else return super.speed();
	}

	@Override
	protected boolean canAttack( Char enemy ) {
		return super.canAttack(enemy); //근접에서만 공격 가능
	}

	@Override
	public Fox findPartner() {
		if (kurumiID != -1) return (Fox) Actor.findById(kurumiID);
		if (otogiID != -1) return (Fox) Actor.findById(otogiID);
		if (yukinoID != -1) return (Fox) Actor.findById(yukinoID);
		return null;
	}
}

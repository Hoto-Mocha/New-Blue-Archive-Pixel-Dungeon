package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AllyBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Amok;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Dread;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Drowsy;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Light;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.sprites.OtogiSprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class Otogi extends Mob {

	public int level;

	{
		spriteClass = OtogiSprite.class; //오토기 스프라이트 사용

		viewDistance = Light.DISTANCE; //영웅 시야와 동일

		properties.add(Property.LARGE);

		immunities.add(Terror.class); 	//공포 면역
		immunities.add(Dread.class); 	//두려움 면역
		immunities.add(Amok.class); 	//광란 면역
		immunities.add(Drowsy.class); 	//졸림 면역
		immunities.add(AllyBuff.class); //아군으로 만들 수 없음
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 2 + level, 3 + 3*level); //원거리 공격 피해량. 유키노보다 50% 높음, 현재 층에 따라 증가
	}

	@Override
	public int drRoll() {
		return super.drRoll() + Random.NormalIntRange(0, level/3); //방어력. 유키노보다 낮음
	}

	@Override
	public int attackSkill( Char target ) {
		return 6 + level; //정확성
	}

	@Override
	protected boolean canAttack( Char enemy ) {
		return !Dungeon.level.adjacent( pos, enemy.pos )
				&& (super.canAttack(enemy) || new Ballistica( pos, enemy.pos, Ballistica.PROJECTILE).collisionPos == enemy.pos); //원거리에서만 공격 가능. 투사체가 닿을 수 있는 곳에만 공격 가능
	}

	@Override
	protected boolean getCloser( int target ) { //가까이 가면 거리를 벌림
		if (state == HUNTING) {
			return enemySeen && getFurther( target );
		} else {
			return super.getCloser( target );
		}
	}

	public static Otogi spawnAt( int pos ){ //오토기 생성 시 사용. 층에 따라 레벨이 증가
		Otogi otogi = new Otogi();

		otogi.setLevel( Dungeon.scalingDepth() );
		otogi.pos = pos;

		return otogi;
	}

	public void setLevel( int level ){ //레벨 결정
		this.level = level;
		adjustStats(level);
	}

	public void adjustStats( int level ) { //레벨에 따른 스테이터스 설정
		HP = HT = (1 + level) * 3; //유키노보다 체력이 낮음
		defenseSkill = 2 + level/2;
	}

	private static final String LEVEL	= "level";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( LEVEL, level );
	}

	@SuppressWarnings("unchecked")
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		level = bundle.getInt( LEVEL );
		adjustStats(level);
		super.restoreFromBundle(bundle);
	}
	
}

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AllyBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Amok;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Dread;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Drowsy;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Light;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.shatteredpixeldungeon.sprites.NikoSprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class Niko extends Mob {

	public int level;

	{
		state = HUNTING;
		spriteClass = NikoSprite.class; //니코 스프라이트 사용

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
		return Random.NormalIntRange( 1 + level, 3 + 3*level); //산탄총 공격 피해량. 유키노보다 높음, 현재 층에 따라 증가
	}

	@Override
	public int drRoll() {
		return super.drRoll() + Random.NormalIntRange(0, 1 + level/2); //방어력
	}

	@Override
	public int attackSkill( Char target ) {
		return 10 + level; //정확성. 유키노보다 약간 높음
	}

	@Override
	public float speed() {
		return super.speed() * 2; //이동 속도 2배
	}

	@Override
	protected boolean canAttack( Char enemy ) {
		return super.canAttack(enemy); //근접에서만 공격 가능
	}

	public static Niko spawnAt( int pos ){ //니코 생성 시 사용. 층에 따라 레벨이 증가
		Niko niko = new Niko();

		niko.setLevel( Dungeon.scalingDepth() );
		niko.pos = pos;

		return niko;
	}

	public void setLevel( int level ){ //레벨 결정
		this.level = level;
		adjustStats(level);
	}

	public void adjustStats( int level ) { //레벨에 따른 스테이터스 설정. 유키노와 동일
		HP = HT = (1 + level) * 6;
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

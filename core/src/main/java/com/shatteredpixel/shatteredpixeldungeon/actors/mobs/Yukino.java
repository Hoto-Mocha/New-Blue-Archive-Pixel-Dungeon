package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AllyBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Amok;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Dread;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Drowsy;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Light;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.AR.AR;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.sprites.YukinoSprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class Yukino extends Mob {

	public int level;

	{
		state = HUNTING;
		spriteClass = YukinoSprite.class; //유키노 스프라이트 사용

		viewDistance = Light.DISTANCE; //영웅 시야와 동일

		properties.add(Property.LARGE);

		immunities.add(Terror.class); 	//공포 면역
		immunities.add(Dread.class); 	//두려움 면역
		immunities.add(Amok.class); 	//광란 면역
		immunities.add(Drowsy.class); 	//졸림 면역
		immunities.add(AllyBuff.class); //아군으로 만들 수 없음

		lootChance = 1f;
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 1 + level, 2 + 2*level); //근접 및 원거리 공격 피해량. 미믹과 동일하며, 현재 층에 따라 증가
	}

	@Override
	public int drRoll() {
		return super.drRoll() + Random.NormalIntRange(0, 1 + level/2); //방어력
	}

	@Override
	public int attackSkill( Char target ) {
		return 6 + level; //정확성
	}

	@Override
	protected boolean canAttack( Char enemy ) {
		Ballistica attack = new Ballistica( pos, enemy.pos, Ballistica.PROJECTILE);
		return attack.collisionPos == enemy.pos; //투사체가 닿을 수 있으면 공격 가능(근접 포함)
	}

	public static Yukino spawnAt( int pos ){ //유키노 생성 시 사용. 층에 따라 레벨이 증가
		Yukino yukino = new Yukino();

		yukino.setLevel( Dungeon.scalingDepth() );
		yukino.pos = pos;

		return yukino;
	}

	public void setLevel( int level ){ //레벨 결정
		this.level = level;
		adjustStats(level);
	}

	public void adjustStats( int level ) { //레벨에 따른 스테이터스 설정
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

	@Override
	public Item createLoot() {
		int tier = 1+level/5;
		AR ar = AR.getAR(tier);
		ar.identify();
		return ar;
	}
	
}

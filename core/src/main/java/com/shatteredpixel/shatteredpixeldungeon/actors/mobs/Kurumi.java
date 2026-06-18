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
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.SMG.SMG;
import com.shatteredpixel.shatteredpixeldungeon.sprites.KurumiSprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class Kurumi extends Mob {

	public int level;

	{
		state = HUNTING;
		spriteClass = KurumiSprite.class; //쿠루미 스프라이트 사용

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
		return Random.NormalIntRange( 1 + level/2, 1 + level + level/2 ); //근접 공격 피해량. 유키노보다 낮으며, 현재 층에 따라 증가
	}

	@Override
	public int drRoll() {
		return super.drRoll() + Random.NormalIntRange(0, 3 + level); //방어력. 유키노보다 높음
	}

	@Override
	public int attackSkill( Char target ) {
		return 6 + level; //정확성
	}

	@Override
	protected boolean canAttack( Char enemy ) {
		return super.canAttack(enemy); //근접 공격만 가능
	}

	public static Kurumi spawnAt(int pos ){ //쿠루미 생성 시 사용. 층에 따라 레벨이 증가
		Kurumi kurumi = new Kurumi();

		kurumi.setLevel( Dungeon.scalingDepth() );
		kurumi.pos = pos;

		return kurumi;
	}

	public void setLevel( int level ){ //레벨 결정
		this.level = level;
		adjustStats(level);
	}

	public void adjustStats( int level ) { //레벨에 따른 스테이터스 설정
		HP = HT = (1 + level) * 8; //유키노보다 체력이 높음
		defenseSkill = 1 + level/3; //유키노보다 회피가 낮음
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
		SMG smg = SMG.getSMG(tier);
		smg.identify();
		return smg;
	}
	
}

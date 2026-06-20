package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.fox;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AllyBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Amok;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ChampionEnemy;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Dread;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Drowsy;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Light;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfAggression;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.Gun;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class Fox extends Mob {

    public int level;
    public boolean hit = false;
    public int spawnPos;

    public int yukinoID;
    public int nikoID;
    public int kurumiID;
    public int otogiID;

    {
        state = HUNTING;

        viewDistance = Light.DISTANCE; //영웅 시야와 동일
        WANDERING = new Wandering();

        immunities.add(Terror.class); 	                        //공포 면역
        immunities.add(Dread.class); 	                        //두려움 면역
        immunities.add(Amok.class); 	                        //광란 면역
        immunities.add(StoneOfAggression.Aggression.class); 	//목표 지정 면역
        immunities.add(Drowsy.class); 	                        //졸림 면역
        immunities.add(AllyBuff.class);                         //아군으로 만들 수 없음

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
    public String info(){
        String desc = super.info();

        desc += "\n\n";
        desc += Messages.get(this, "stats_desc");

        return desc;
    }

    public void setLevel( int level ){ //레벨 결정
        this.level = level;
        adjustStats(level);
    }

    public void adjustStats( int level ) { //레벨에 따른 스테이터스 설정
        HP = HT = (1 + level) * 6;
        defenseSkill = 2 + level/2;
    }

    public static Fox spawnAt( int pos, Fox fox ){ //층에 따라 레벨이 증가
        fox.setLevel( Dungeon.scalingDepth() );
        fox.pos = pos;
        fox.spawnPos = pos;

        return fox;
    }

    public void setAlly( Yukino yukino, Niko niko, Kurumi kurumi, Otogi otogi ) { //아군 설정
        if (yukino != null && yukino != this)   this.yukinoID = yukino.id();
        if (niko != null && niko != this)       this.nikoID = niko.id();
        if (kurumi != null && kurumi != this)   this.kurumiID = kurumi.id();
        if (otogi != null && otogi != this)     this.otogiID = otogi.id();
    }

    public boolean isAlly( Mob enemy ) {
        return enemy instanceof Yukino
                || enemy instanceof Niko
                || enemy instanceof Kurumi
                || enemy instanceof Otogi;
    }

    public void allyDie( Fox fox ) {
        if (fox instanceof Yukino)  this.yukinoID = -1;
        if (fox instanceof Niko)    this.nikoID = -1;
        if (fox instanceof Kurumi)  this.kurumiID = -1;
        if (fox instanceof Otogi)   this.otogiID = -1;
    }

    @Override
    public void die(Object cause) {
        yell(Messages.get(this, "die"));
        if (yukinoID != -1 && !(this instanceof Yukino))    ((Fox) Actor.findById(yukinoID)).allyDie(this );
        if (nikoID != -1 && !(this instanceof Niko))        ((Fox) Actor.findById(nikoID)).allyDie(this );
        if (kurumiID != -1 && !(this instanceof Kurumi))    ((Fox) Actor.findById(kurumiID)).allyDie(this );
        if (otogiID != -1 && !(this instanceof Otogi))      ((Fox) Actor.findById(otogiID)).allyDie(this );
        super.die(cause);
    }

    @Override
    public int defenseProc(Char enemy, int damage) {
        if (!hit) {
            yell(Messages.get(this, "hit"));
            hit = true;
        }

        return super.defenseProc(enemy, damage);
    }

    private static final String LEVEL	= "level";
    private static final String HIT	= "hit";
    private static final String SPAWN_POS	= "spawnPos";
    private static final String YUKINO_ID  	= "yukinoID";
    private static final String NIKO_ID    	= "nikoID";
    private static final String KURUMI_ID  	= "kurumiID";
    private static final String OTOGI_ID   	= "otogiID";

    @Override
    public void storeInBundle( Bundle bundle ) {
        super.storeInBundle( bundle );
        bundle.put( LEVEL, level );
        bundle.put( HIT, hit );
        bundle.put( SPAWN_POS, spawnPos );
        bundle.put( YUKINO_ID, yukinoID );
        bundle.put( NIKO_ID, nikoID );
        bundle.put( KURUMI_ID, kurumiID );
        bundle.put( OTOGI_ID, otogiID );
    }

    @SuppressWarnings("unchecked")
    @Override
    public void restoreFromBundle( Bundle bundle ) {
        level = bundle.getInt( LEVEL );
        adjustStats(level);
        super.restoreFromBundle(bundle);
        hit = bundle.getBoolean( HIT );
        spawnPos = bundle.getInt( SPAWN_POS );
        yukinoID = bundle.getInt( YUKINO_ID );
        nikoID = bundle.getInt( NIKO_ID );
        kurumiID = bundle.getInt( KURUMI_ID );
        otogiID = bundle.getInt( OTOGI_ID );
    }

    @Override
    public Item createLoot() {
        if (!(loot instanceof Gun)) return null;

        int tier = 1+level/5;
        Gun gun = Gun.getGun(((Gun)loot).getClass(), tier, 0); //총 생성
        gun.identify();
        return gun;
    }

    //needs to be overridden
    public Fox findPartner() {
        return null;
    }

    public ArrayList<Fox> allies() {
        ArrayList<Fox> allies = new ArrayList<>();
        if (yukinoID != -1 && !(this instanceof Yukino)) allies.add((Fox)Actor.findById(yukinoID));
        if (nikoID != -1 && !(this instanceof Niko)) allies.add((Fox)Actor.findById(nikoID));
        if (kurumiID != -1 && !(this instanceof Kurumi)) allies.add((Fox)Actor.findById(kurumiID));
        if (otogiID != -1 && !(this instanceof Otogi)) allies.add((Fox)Actor.findById(otogiID));
        return allies;
    }

    public static class TalkTracker extends FlavourBuff {}

    public class Wandering extends Mob.Wandering {

        @Override
        protected boolean continueWandering() {
            enemySeen = false;

            Fox partner = findPartner();

            if (partner != null && (partner.state != partner.WANDERING || partner.target != partner.spawnPos)) {
                target = partner.pos;
            } else {
                target = spawnPos;
            }
            int oldPos = pos;
            if (getCloser( target )){
                spend( 1 / speed() );
                return moveSprite( oldPos, pos );
            } else {
                target = spawnPos;
                spend( TICK );
                return true;
            }
        }

        @Override
        protected boolean noticeEnemy() {
            boolean result = super.noticeEnemy();
            ArrayList<Fox> allies = allies();
            if (!allies.isEmpty()) {
                boolean needTalk = false;
                for (Fox fox : allies) {
                    if (fox.enemy == null) {
                        fox.beckon(enemy.pos);
                        fox.aggro(enemy);
                        Buff.prolong(fox, TalkTracker.class, 30f);
                        needTalk = true;
                    }
                }
                if (needTalk && Fox.this.buff(TalkTracker.class) == null) {
                    Buff.prolong(Fox.this, TalkTracker.class, 30f);
                    yell(Messages.get(Fox.this, "aggro"));
                }
            }
            return result;
        }
    }
}

package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.Stasis;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.DirectableAlly;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShaftParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.KindOfWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.Gun;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.SMG.SMG;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MissileSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.TextureFilm;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class RabbitSquadBuff extends Buff implements ActionIndicator.Action {
    {
        type = buffType.NEUTRAL;

        revivePersists = true;
    }

    protected final float SAKI_COOLDOWN_MAX = 500f;
    protected final float MIYU_COOLDOWN_MAX = 50f;
    protected final float MOE_COOLDOWN_MAX = 200f;

    private Saki saki = null;
    private int sakiID = 0;

    private static final String SAKI_ID = "sakiID";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(SAKI_ID, sakiID);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        sakiID = bundle.getInt(SAKI_ID);
    }

    public void findSaki() {
        Actor a = Actor.findById(sakiID);
        if (a instanceof Saki){
            saki = (Saki)a;
        } else {
            if (Stasis.getStasisAlly() instanceof Saki){
                saki = (Saki) Stasis.getStasisAlly();
                sakiID = saki.id();
            } else {
                sakiID = 0;
            }
        }
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc");
    }

    @Override
    public boolean act() {
        spend(TICK);
        if (saki == null && sakiID != 0) {
            findSaki();
        }
        return super.act();
    }

    @Override
    public String actionName() {
        return Messages.get(this, "action_name");
    }

    @Override
    public int actionIcon() {
        return ActionIndicator.Action.super.actionIcon();
    }

    @Override
    public int indicatorColor() {
        return 0xFFFFFF;
    }

    @Override
    public boolean attachTo(Char target) {
        ActionIndicator.setAction(this);
        return super.attachTo(target);
    }

    @Override
    public void detach() {
        ActionIndicator.clearAction();
        super.detach();
    }

    @Override
    public void doAction() {
        if (saki == null && sakiID != 0) {
            findSaki();
        }
        if (saki == null) {
            summonSaki((Hero)target);
        }
    }

    public void attack(Char enemy) {
        if (saki == null && sakiID != 0) {
            findSaki();
        }
        if (saki != null) { //미야코가 적을 공격할 경우 사키는 해당 적을 우선으로 공격함
            saki.targetChar(enemy);
            if (Random.Float() < 0.1f) { //10% 확률로 대사를 함
                Dungeon.hero.yellI(Messages.get(Hero.class, "miyako_attack_saki"));
                GLog.newLine();
                GLog.i( "%s: \"%s\" ", Messages.titleCase(saki.name()), Messages.get(this, "saki_react") );
                GLog.newLine();
            }
        }
    }

    public void summonSaki(Hero hero) {
        ArrayList<Integer> spawnPoints = new ArrayList<>();
        for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
            int p = hero.pos + PathFinder.NEIGHBOURS8[i];
            if (Actor.findChar(p) == null && (Dungeon.level.passable[p] || Dungeon.level.avoid[p])) {
                spawnPoints.add(p);
            }
        }
        if (spawnPoints.size() > 0) {
            saki = new Saki(this);
            sakiID = saki.id();
            saki.pos = Random.element(spawnPoints);

            GameScene.add(saki, 1f);
            Dungeon.level.occupyCell(saki);

            CellEmitter.get(saki.pos).start(ShaftParticle.FACTORY, 0.3f, 4);
            CellEmitter.get(saki.pos).start(Speck.factory(Speck.LIGHT), 0.2f, 3);

            hero.spend(1f);
            hero.busy();
            hero.sprite.operate(hero.pos);
        } else {
            GLog.w(Messages.get(this, "no_space"));
        }
    }

    public static class Saki extends DirectableAlly {
        {
            spriteClass = SakiSprite.class;

            state = WANDERING;
        }

        private KindOfWeapon heroOldWeapon = null;
        private Gun sakiGun = null;
        private Gun.Bullet nextBullet = null;
        private RabbitSquadBuff buff = null;
        private int round = 0;
        private int maxRound = 0;

        public Saki() {
            super();
        }

        public Saki (RabbitSquadBuff buff) {
            this();
            this.buff = buff;
            updateBuff();
            getGun();
            round = maxRound;
            HP = HT;
        }

        private void updateBuff(){ //사키가 가지고 있는 버프의 정보를 갱신하고 영웅의 정보를 가져옴
            if (buff == null) {
                buff = Dungeon.hero.buff(RabbitSquadBuff.class);
            }

            //same dodge as the hero
            defenseSkill = (Dungeon.hero.lvl+4);
            HT = Dungeon.hero.HT/2;
        }

        private Gun getGun() { //영웅의 무기에 기반한 총기를 생성하여 제공한다.
            //영웅의 무기를 가져와서, 영웅의 무기가 변경되었는지 체크한다. 만약 변경되었다면 기존에 생성해서 가지고 있던 총기의 정보는 제거한다.
            KindOfWeapon heroWep = Dungeon.hero.belongings.weapon();
            if (heroOldWeapon != heroWep) {
                heroOldWeapon = heroWep;
                sakiGun = null;
            }
            //만약 기존에 생성한 총기의 정보가 없다면, 영웅의 무기에 기반한 총기를 생성하여 저장한다.
            if (sakiGun == null) {
                int tier;
                int lvl;
                if (heroWep instanceof MeleeWeapon) {
                    tier = ((MeleeWeapon)heroWep).tier();
                    lvl = heroWep.buffedLvl();
                } else {
                    tier = 1;
                    lvl = 0;
                }
                sakiGun = Gun.getGun(SMG.class, tier, lvl);
                if (sakiGun != null) maxRound = sakiGun.maxRound();
            }
            //저장된 총기의 정보를 제공한다.
            return sakiGun;
        }

        private boolean willingToShoot(Char enemy) { //총을 발사할 수 있는 조건이 만족되어 있으면 참, 아니면 거짓.
            return !Dungeon.level.adjacent( this.pos, enemy.pos );
        }

        @Override
        public int drRoll() { //방어력
            return Dungeon.hero.drRoll()/2;
        }

        @Override
        public int damageRoll() { //공격력. 발사한 탄환이 있다면 탄환의 데미지를 가지며, 아닐 경우 영웅의 근접 데미지의 절반을 취한다.
            if (nextBullet != null) {
                return getGun().bulletDamage();
            } else {
                return Dungeon.hero.damageRoll()/2;
            }
        }

        @Override
        public float speed() { //이동 속도
            float speed = super.speed();

            //moves 2 tiles at a time when returning to the hero
            if (state == WANDERING
                    && defendingPos == -1
                    && Dungeon.level.distance(pos, Dungeon.hero.pos) > 1){
                speed *= 2;
            }

            return speed;
        }

        public void tryReload(boolean shouldEmpty) { //총기 재장전을 시도한다. shouldEmpty가 참이면 탄환이 0이어야 장전하고, 거짓이면 탄환이 0이 아니어도 장전한다.
            if (shouldEmpty) {
                if (round <= 0) {
                    reload();
                }
            } else {
                if (round < maxRound) {
                    reload();
                }
            }
        }

        public void reload() { //총기를 재장전한다.
            Callback callback = new Callback() {
                @Override
                public void call() {
                    round = maxRound;
                }
            };
            spend(getGun().reloadTime());
            this.sprite.showStatus( CharSprite.POSITIVE, Messages.get(RabbitSquadBuff.class, "saki_reloading") );
            callback.call();
        }

        @Override
        protected boolean act() {
            updateBuff(); //항상 영웅의 스펙과 버프 정보를 갱신한다.

            //움직이지 않을 경우 스프라이트의 움직임을 멈춘다.
            int oldPos = pos;
            boolean result = super.act();
            //partially simulates how the hero switches to idle animation
            if ((pos == target || oldPos == pos) && sprite.looping()){
                sprite.idle();
            }

            //만약 사키의 시야에 적이 없을 경우 영웅을 따라간다.
            boolean enemyInFOV = enemy != null && enemy.isAlive() && fieldOfView[enemy.pos] && enemy.invisible <= 0;
            if (!enemyInFOV) {
                followHero();
            }
            return result;
        }

        @Override
        public void die(Object cause) {
            super.die(cause);
            buff.saki = null;
            buff.sakiID = 0;
        }

        @Override
        protected boolean canAttack( Char enemy ) { //공격 가능 여부. 총을 발사할 수 있으면 탄환의 궤적이 적에게 닿는지 체크하고, 만약 닿는다면 공격 가능으로 처리. 아닐 경우 근접 공격과 똑같다.
            //만약 탄환 발사 조건이 만족한 경우
            if (willingToShoot(enemy)) {
                if (round > 0) {
                    boolean canShoot = new Ballistica( pos, enemy.pos, Ballistica.PROJECTILE).collisionPos == enemy.pos;
                    if (canShoot) {
                        //발사할 탄환 생성
                        nextBullet = getGun().knockBullet();
                    }
                    return canShoot;
                } else {
                    tryReload(true);
                    return false;
                }
            } else {
                return super.canAttack(enemy);
            }
        }

        @Override
        public boolean attack(Char enemy, float dmgMulti, float dmgBonus, float accMulti) { //탄환을 발사한 경우 탄환의 명중 횟수만큼 공격을 반복. 아닐 경우 근접 공격과 동일
            if (nextBullet != null && getGun().shotPerShoot() > 1) {
                for (int i = 0; i < getGun().shotPerShoot() - 1; i++) { //이 코드는 한 발에 여러 번 타격하는 총기에 한해서 발동할 것
                    super.attack(enemy, dmgMulti, dmgBonus, accMulti);
                }
            }
            return super.attack(enemy, dmgMulti, dmgBonus, accMulti);
        }

        @Override
        public int attackSkill(Char target) { //탄환을 발사한 경우 탄환의 명중률을 반영한다.
            int acc = Dungeon.hero.lvl+9;
            if (nextBullet != null) {
                acc *= nextBullet.accuracyFactor(this, target);
            }
            return acc;
        }

        @Override
        public float attackDelay() { //탄환을 발사한 경우 총기의 공격 속도를 반영한다.
            float delay = super.attackDelay();
            if (nextBullet != null) {
                delay *= nextBullet.delayFactor(this); //탄환 공격 속도 적용
            }
            return delay;
        }

        @Override
        public void onAttackComplete() { //공격 완료 시 작동한다.
            //탄환을 발사한 경우 장탄수를 1 소모한다.
            if (nextBullet != null) {
                round--;
            }

            //장탄수가 0일 경우 재장전.
            tryReload(true);

            super.onAttackComplete();
            //탄환을 발사했든 안 했든 탄환을 제거한다.
            nextBullet = null;
        }

        private static final String ROUND = "round";
        private static final String MAX_ROUND = "maxRound";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(ROUND, round);
            bundle.put(MAX_ROUND, maxRound);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            round = bundle.getInt(ROUND);
            maxRound = bundle.getInt(MAX_ROUND);
        }

        public String description() {
            return Messages.get(this, "desc", getGun().tier(), getGun().buffedLvl(), round, maxRound, Messages.decimalFormat("#.##", getGun().reloadTime()));
        }
    }

    public static class SakiSprite extends MobSprite {

        private int cellToAttack;

        public SakiSprite() {
            super();

            texture(Assets.Sprites.SAKI);

            TextureFilm film = new TextureFilm( texture, 12, 17 );

            idle = new Animation( 1, true );
            idle.frames( film, 0, 0, 0, 1, 0, 0, 1, 1 );

            run = new Animation( 20, true );
            run.frames( film, 2, 3, 4, 5, 6, 7 );

            die = new Animation( 20, false );
            die.frames( film, 0 );

            attack = new Animation( 15, false );
            attack.frames( film, 13, 14, 15, 0 );

            zap = attack.clone();

            idle();
            resetColor();
        }

        @Override
        public void attack( int cell ) {
            if (!Dungeon.level.adjacent( cell, ch.pos )) {

                cellToAttack = cell;
                zap(cell);

            } else {

                super.attack( cell );

            }
        }

        @Override
        public void onComplete( Animation anim ) {
            if (anim == zap) {
                idle();
                Gun sakiGun = null;
                if (Dungeon.hero.buff(RabbitSquadBuff.class) != null) {
                    sakiGun = ((Saki)ch).getGun();
                }
                if (sakiGun != null) {
                    ((MissileSprite)parent.recycle( MissileSprite.class )).
                            reset( this, cellToAttack, sakiGun.knockBullet(), new Callback() {
                                @Override
                                public void call() {
                                    //sakiGun.knockBullet().shoot()을 사용하는 경우 **영웅이** 적을 공격한 것으로 처리되기 때문에 사용하지 않는다.
                                    //탄환 발사 메커니즘 자체가 hero.shoot()을 사용하기 때문.
                                    //따라서 탄환을 생성하고 그 탄환의 공격력, 명중률, 공격 속도 정보를 사키의 스펙에 적용시켜 사용한다.
                                    //이렇게 하면 탄환으로 공격하지 않으면서 탄환으로 공격한 것처럼 작동하게 할 수 있다.
                                    ch.onAttackComplete();
                                }
                            } );
                } else {
                    super.onComplete( anim );
                }
            } else {
                super.onComplete( anim );
            }
        }
    }
}

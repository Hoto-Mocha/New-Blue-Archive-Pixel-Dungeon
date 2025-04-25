package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.Stasis;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.DirectableAlly;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.AirSupportParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.BlastParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SnipeParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.KindOfWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.GL.GL;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.Gun;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.SMG.SMG;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MissileSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.Visual;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.tweeners.Tweener;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.GameMath;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class RabbitSquadBuff extends Buff implements ActionIndicator.Action {
    {
        type = buffType.NEUTRAL;

        revivePersists = true;
    }

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
    public boolean act() {
        spend(TICK);
        if (saki == null && sakiID != 0) {
            findSaki();
        }
        return super.act();
    }

    @Override
    public String actionName() {
        findSaki();
        if (Dungeon.hero.buff(SakiCooldown.class) == null && saki == null) {
            return Messages.get(this, "action_name_summon");
        } else if (Dungeon.hero.buff(MiyuCooldown.class) == null) {
            return Messages.get(this, "action_name_snipe");
        } else if (Dungeon.hero.buff(MoeCooldown.class) == null) {
            return Messages.get(this, "action_name_airsupport");
        } else {
            return Messages.get(this, "action_name");
        }
    }

    @Override
    public int actionIcon() {
        return HeroIcon.RABBIT_SQUAD_ACTION;
    }

    @Override
    public Visual secondaryVisual() {
        findSaki();
        if (Dungeon.hero.buff(SakiCooldown.class) == null && saki == null) {
            return Icons.ACTION_SAKI.get();
        } else if (Dungeon.hero.buff(MiyuCooldown.class) == null) {
            return Icons.ACTION_MIYU.get();
        } else if (Dungeon.hero.buff(MoeCooldown.class) == null) {
            return Icons.ACTION_MOE.get();
        } else {
            return null;
        }
    }

    @Override
    public int indicatorColor() {
        return 0x85A2C4;
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
        if (Dungeon.hero.buff(SakiCooldown.class) == null && saki == null) {
            if (sakiID != 0) {
                findSaki();
            }
            if (saki == null) {
                summonSaki((Hero)target);
                return;
            }
        }
        if (Dungeon.hero.buff(MiyuCooldown.class) == null) {
            GameScene.selectCell(snipeCellSelector);
        } else if (Dungeon.hero.buff(MoeCooldown.class) == null) {
            GameScene.selectCell(airSupportCellSelector);
        } else {
            GLog.w(Messages.get(this, "no_action"));
        }
    }

    public void snipe(int cell) {
        if (!Dungeon.level.heroFOV[cell]) {
            Dungeon.hero.yellW(Messages.get(Hero.class, "miyako_cannot_see"));
            return;
        }
        Char ch = Actor.findChar(cell);
        KindOfWeapon heroWep = Dungeon.hero.belongings.weapon();
        int tier, lvl;
        if (heroWep instanceof MeleeWeapon) {
            tier = ((MeleeWeapon)heroWep).tier();
            lvl = heroWep.buffedLvl();
        } else {
            tier = 1;
            lvl = 0;
        }
        if (ch != null && ch.alignment == Char.Alignment.ENEMY) {
            Sample.INSTANCE.play(Assets.Sounds.BEACON);
            Dungeon.hero.yellI(Messages.get(Hero.class, "miyako_attack_miyu"));
            Dungeon.hero.busy();
            Dungeon.hero.sprite.operate(Dungeon.hero.pos, new Callback() {
                @Override
                public void call() {
                    GLog.newLine();
                    GLog.i( "%s: \"%s\" ", Messages.titleCase(Messages.get(RabbitSquadBuff.class, "miyu")), Messages.get(RabbitSquadBuff.class, "miyu_react") );
                    GLog.newLine();
                    Dungeon.hero.sprite.idle();
                    Callback callback = new Callback() {
                        @Override
                        public void call() {
                            Dungeon.hero.spendAndNext(0); //턴을 소모하지 않음
                            Buff.affect(Dungeon.hero, MiyuCooldown.class, MiyuCooldown.DURATION-1); //턴을 소모하지 않기 때문에 1턴을 빼줌
                        }
                    };
                    CellEmitter.center(ch.pos).burst(SnipeParticle.factory(ch, tier, lvl, callback), 1);
                }
            });
        } else {
            Dungeon.hero.yellW(Messages.get(Hero.class, "miyako_no_char"));
        }
    }

    public void callAirSupport(int cell) {
        Sample.INSTANCE.play(Assets.Sounds.BEACON);
        Dungeon.hero.yellI(Messages.get(Hero.class, "miyako_attack_moe"));
        Dungeon.hero.busy();
        Dungeon.hero.sprite.operate(Dungeon.hero.pos, new Callback() {
            @Override
            public void call() {
                Dungeon.hero.sprite.idle();
                //대사 출력
                GLog.newLine();
                GLog.i( "%s: \"%s\" ", Messages.titleCase(Messages.get(RabbitSquadBuff.class, "moe")), Messages.get(RabbitSquadBuff.class, "moe_react") );
                GLog.newLine();

                //먼저, 반복문에 사용될 변수를 선언한다.
                float delay = 0; //Tweener에 들어갈 시간 값. 루프가 한 바퀴 돌 때마다 0.1씩 추가된다.
                int blastAmount = 6+3*Dungeon.hero.pointsInTalent(Talent.MIYAKO_EX1_3); //폭발 횟수
                
                //blastAmount 횟수만큼 반복하는 반복문 작성
                while (blastAmount > 0) {
                    //지정한 위치 주변 3x3 지역 중 하나의 좌표를 무작위로 선정. 최종 폭발 지점이 된다.
                    int finalCell = cell + PathFinder.NEIGHBOURS9[Random.Int(9)];

                    //폭발 횟수를 감소시킨다. 0이 되면 밑의 콜백에 있는 Dungeon.hero.next();를 실행하고 반복문을 종료한다.
                    blastAmount--;

                    //지연 시간에 처음부터 0.1을 더함으로써 Dungeon.hero.operate() 이후 0.1초가 흐른 시점부터 폭격이 떨어진다.
                    delay += 0.1f;

                    //Callback에 값을 넣기 위해 상수화
                    final int finalBlastAmount = blastAmount;
                    
                    //마찬가지로 Tweener에 값을 넣기 위해 상수화
                    final float finalDelay = delay;
                    
                    //지연 시간이 흐른 후에 작동하는 코드를 만들기 위해 Tweener를 생성해서 추가한다.
                    Dungeon.hero.sprite.parent.add(new Tweener(Dungeon.hero.sprite.parent, finalDelay) {
                        @Override
                        protected void updateValues(float progress) {} //인터페이스의 메서드를 구현하기 위해 넣은 코드. 시간이 지남에 따른 아무런 작동도 필요로 하지 않기 때문에 공백.

                        @Override
                        protected void onComplete() { //finalDelay초가 흐른 후에 실행되는 함수
                            super.onComplete();
                            //폭격 파티클을 최종 위치에 생성한다.
                            //콜백은 폭격 파티클이 목표 지점에 완전히 떨어지고 나서 작동하며, blastAmount가 0 이하가 되었을 때 Dungeon.hero.next();를 실행함으로써 마지막 폭발 이후부터 영웅이 행동할 수 있게 한다.
                            CellEmitter.heroCenter(finalCell).burst(AirSupportParticle.factory(new Callback() {
                                @Override
                                public void call() {
                                    //2~(현재 계층)~5의 티어를 가지고, 보스 층에서 다음 티어를 가지는 것을 방지하기 위해 1을 뺌. 강화 수치는 영웅 레벨을 5로 나누어 소수점을 버린 값에 3을 더한 값을 취함.
                                    Gun gun = Gun.getGun(GL.class, (int) GameMath.gate(2, 1+(Dungeon.scalingDepth()-1)/5f, 5), Dungeon.hero.lvl/5+3);
                                    Gun.Bullet bullet = gun.knockBullet();

                                    bullet.shoot(finalCell, false);
                                    CellEmitter.center(finalCell).burst(BlastParticle.FACTORY, 4);

                                    if (finalBlastAmount <= 0) { //마지막 폭격임을 체크
                                        Dungeon.hero.spendAndNext(1f); //영웅이 대기 상태에서 벗어나게 함
                                        Buff.affect(Dungeon.hero, MoeCooldown.class, MoeCooldown.DURATION);
                                    }
                                }
                            }), 1);
                        }
                    });
                }
            }
        });
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

            hero.spend(1f);
            hero.busy();
            hero.yellI(Messages.get(Hero.class, "miyako_summon_saki"));
            Sample.INSTANCE.play(Assets.Sounds.BEACON);
            Sample.INSTANCE.play(Assets.Sounds.TELEPORT);
            CellEmitter.get(saki.pos).burst(Speck.factory(Speck.LIGHT), 4);
            hero.sprite.operate(hero.pos);
            ActionIndicator.refresh();
        } else {
            GLog.w(Messages.get(this, "no_space"));
        }
    }

    private CellSelector.Listener snipeCellSelector = new CellSelector.Listener() {
        @Override
        public void onSelect( Integer target ) {
            if (target != null) {
                snipe(target);
            }
        }
        @Override
        public String prompt() {
            return Messages.get(SpiritBow.class, "prompt");
        }
    };

    private CellSelector.Listener airSupportCellSelector = new CellSelector.Listener() {
        @Override
        public void onSelect( Integer target ) {
            if (target != null) {
                callAirSupport(target);
            }
        }
        @Override
        public String prompt() {
            return Messages.get(SpiritBow.class, "prompt");
        }
    };

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
            buff.saki = null;
            buff.sakiID = 0;
            Buff.affect(Dungeon.hero, SakiCooldown.class, SakiCooldown.DURATION - 50 * Dungeon.hero.pointsInTalent(Talent.MIYAKO_EX1_1));
            super.die(cause);
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
            updateBuff();
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
                    Gun finalSakiGun = sakiGun;
                    ((MissileSprite)parent.recycle( MissileSprite.class )).
                            reset( this, cellToAttack, sakiGun.knockBullet(), new Callback() {
                                @Override
                                public void call() {
                                    finalSakiGun.knockBullet().throwSound();
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

    public static class SakiCooldown extends FlavourBuff {
        {
            type = buffType.NEUTRAL;
        }

        public static final float DURATION	= 250f;

        @Override
        public boolean attachTo(Char target) {
            ActionIndicator.refresh();
            return super.attachTo(target);
        }

        @Override
        public int icon() {
            return BuffIndicator.TIME;
        }

        @Override
        public void tintIcon(Image icon) {
            icon.hardlight(0x93869E);
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0, (DURATION - visualcooldown()) / DURATION);
        }
    }

    public static class MiyuCooldown extends FlavourBuff {
        {
            type = buffType.NEUTRAL;
        }

        public static final float DURATION	= 50f;

        @Override
        public boolean attachTo(Char target) {
            ActionIndicator.refresh();
            return super.attachTo(target);
        }

        @Override
        public int icon() {
            return BuffIndicator.TIME;
        }

        @Override
        public void tintIcon(Image icon) {
            icon.hardlight(0xEE6C93);
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0, (DURATION - visualcooldown()) / DURATION);
        }
    }

    public static class MoeCooldown extends FlavourBuff {
        {
            type = buffType.NEUTRAL;
        }

        public static final float DURATION	= 100f;

        @Override
        public boolean attachTo(Char target) {
            ActionIndicator.refresh();
            return super.attachTo(target);
        }

        @Override
        public int icon() {
            return BuffIndicator.TIME;
        }

        @Override
        public void tintIcon(Image icon) {
            icon.hardlight(0xF2D9B4);
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0, (DURATION - visualcooldown()) / DURATION);
        }
    }
}

package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import static com.shatteredpixel.shatteredpixeldungeon.Dungeon.hero;

import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.NPC;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.BulletParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SmokeParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.KindOfWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.MasterThievesArmband;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.Gun;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.tweeners.Tweener;
import com.watabou.utils.Bundle;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class BankRobber extends Buff implements ActionIndicator.Action {

    @Override
    public String actionName() {
        return Messages.get(this, "action_name");
    }

    @Override
    public int actionIcon() {
        return HeroIcon.BANK_ROBBER_ACTION;
    }

    @Override
    public int indicatorColor() {
        return 0xC7C4C9;
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
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);

        ActionIndicator.setAction(this);
    }

    @Override
    public void doAction() {
        Hero hero = Dungeon.hero; //영웅 인스턴스
        if (hero == null) return; //영웅 null 체크

        KindOfWeapon weapon = hero.belongings.weapon(); //현재 장착한 무기를 가져옴
        Gun gun; //장착한 무기가 총이라면 이곳에 저장
        if (!(weapon instanceof Gun)) {
            hero.yellW("need_gun"); //무기가 없거나 총이 아닐 경우 대사 출력
            return;
        } else {
            gun = (Gun) weapon; //장착한 총기 인스턴스 저장
        }

        if (hero.STR() < gun.STRReq()) {
            hero.yellW("low_str");
            return;
        }

        if (gun.round() <= 0) {
            hero.yellW("no_ammo");
            return;
        }

        float delay = 0; //이펙트 딜레이
        final int rounds = gun.round(); //초기 탄창 수
        final Gun.Bullet bullet = gun.knockBullet(); //발사음 재생을 위한 인스턴스
        final int amount = Math.round(gun.tier()*rounds*1.5f);
        for (int i = 0; i < rounds; i++) {
            Dungeon.hero.sprite.parent.add(new Tweener(Dungeon.hero.sprite.parent, delay) {
                @Override
                protected void updateValues(float progress) {}

                @Override
                protected void onComplete() {
                    super.onComplete();
                    gun.useRound(); //탄환 사용
                    bullet.throwSound(); //발사음 재생
                    //탄환 파티클을 위쪽 방향으로 생성. 기본 3개, 발사 개수가 증가하면 발사 수당 3개
                    CellEmitter.heroCenter(hero.pos).burst(BulletParticle.factory(new PointF(DungeonTilemap.tileCenterToWorld(hero.pos).x, DungeonTilemap.tileCenterToWorld(hero.pos).y-40)), 3*gun.shotPerShoot());
                    //연기 파티클 생성
                    CellEmitter.get(hero.pos).burst(SmokeParticle.FACTORY, gun.shotPerShoot());
                    if (gun.round() == 0) { //마지막 발사일 때 능력 효과 발동
                        onAction(hero, amount);
                    }
                }
            });
            //장탄수가 얼마나 되든 총 0.4초가 소모됨. 장탄수가 많을수록 발사 속도가 빠르고, 적을수록 발사 속도가 느려짐.
            delay += 0.4f/rounds;
        }

        hero.sprite.zap(hero.pos);
        hero.spendAndNext(1);
        Invisibility.dispel();
    }

    private void onAction(Hero hero, int amount) {
        ArrayList<Mob> targets = new ArrayList<>();

        //calculate targets first, in case damaging/blinding a target affects hero vision
        for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
            if (Dungeon.level.heroFOV[mob.pos]) {
                if (mob instanceof NPC) continue; //NPC에게는 영향을 주지 않음
                targets.add(mob);
            }
        }

        for (Mob mob : targets){
            int finalAmount = Math.round(amount*(1-mob.HP/(float)mob.HT));
            Buff.affect(mob, Terror.class, finalAmount);

            if (mob.buff(Terror.class) != null && mob.buff(Terror.class).cooldown() >= 20 && !mob.isImmune(Dread.class)) {
                mob.buff(Terror.class).detach();
                Buff.affect( mob, Dread.class ).object = hero.id();

                createRoot(hero, mob);
            }
        }
    }

    private void createRoot(Hero hero, Mob mob) {
        float lootMultiplier = 1f + hero.pointsInTalent(Talent.SHIROKO_EX2_2);
        float lootChance = mob.lootChance() * lootMultiplier;

        if (Dungeon.hero.lvl > mob.maxLvl + 2) {
            lootChance = 0;
        } else if (mob.buff(MasterThievesArmband.StolenTracker.class) != null){
            lootChance = 0;
        }

        if (Random.Float() <= lootChance) {
            Item loot = mob.createLoot();
            if (Challenges.isItemBlocked(loot)) {
                Buff.affect(mob, MasterThievesArmband.StolenTracker.class).setItemStolen(false);
            } else {
                Dungeon.level.drop(loot, mob.pos).sprite.drop();
                Buff.affect(mob, MasterThievesArmband.StolenTracker.class).setItemStolen(true);
            }
        }
    }
}

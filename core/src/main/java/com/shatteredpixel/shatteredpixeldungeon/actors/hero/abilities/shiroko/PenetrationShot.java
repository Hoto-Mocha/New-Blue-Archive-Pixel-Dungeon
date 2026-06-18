package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.shiroko;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.Gun;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MissileSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Callback;

import java.util.ArrayList;

public class PenetrationShot extends ArmorAbility {
    {
        baseChargeUse = 25f;
    }

    @Override
    public int icon() {
        return HeroIcon.SHIROKO_1;
    }

    public boolean useTargeting(){
        return true;
    }

    @Override
    public String targetingPrompt() {
        return Messages.get(this, "prompt");
    }

    @Override
    public int targetedPos(Char user, int dst) {
        return dst;
    }

    @Override
    protected void activate(ClassArmor armor, Hero hero, Integer target) {
        if (target == null) return;

        if (!(hero.belongings.weapon instanceof Gun)) {
            GLog.w(Messages.get(this, "need_gun"));
            return;
        }

        Gun gun = (Gun)hero.belongings.weapon; //장착한 총의 인스턴스
        Gun tempGun = ((Gun)(hero.belongings.weapon).duplicate()); //장착한 총의 사본 생성. 여러 개의 탄환을 발사하면서 현재 장착한 총의 장탄수가 감소하지 않게 하기 위함


        if (tempGun.round() <= 0) {
            GLog.w(Messages.get(this, "no_rounds"));
            return;
        }

        Gun.Bullet bullet = tempGun.knockBullet(); //사본 총의 총알 생성
        hero.belongings.thrownWeapon = bullet;

        bullet.setIgnoreWall(true); //총알이 벽을 무시하여 도달하도록 설정

        Ballistica aim = new Ballistica(hero.pos, target, Ballistica.STOP_TARGET); //현재 위치에서 지정한 위치에 벽을 무시하고 도달하는 직선 경로
        int finalPos = finalPos(aim, 2*hero.pointsInTalent(Talent.SHIROKO_ARMOR1_1)); //벽 관통을 계산한 탄환의 최종 도달 위치

        bullet.ACC *= 1+0.25f*hero.pointsInTalent(Talent.SHIROKO_ARMOR1_1);
        bullet.cast(hero, finalPos); //탄환 발사

        gun.useRound(); //장착한 총의 장탄수 감소

        ArrayList<Integer> chCells = getCharPositions(aim, finalPos);

        for (int cell : chCells) {
            InvisibleShot invisibleShot = new InvisibleShot();
            //보이지 않는 아이템을 각 위치로 던져, 아이템이 도착한 시점에 공격. 탄환과 속도가 동일하므로 탄환이 지나가며 공격하는 것처럼 보임
            invisibleShot.cast(hero, cell);
        }

        hero.belongings.thrownWeapon = null;

        Invisibility.dispel();
        armor.charge -= chargeUse(hero);
        armor.updateQuickslot();
    }

    private static ArrayList<Integer> getCharPositions(Ballistica aim, int finalPos) {
        Ballistica trail = new Ballistica(aim.sourcePos, finalPos, Ballistica.STOP_TARGET); //발사한 탄환이 이동한 직선 경로
        ArrayList<Integer> chCells = new ArrayList<>(); //캐릭터가 있는 위치 저장 배열
        for (int cell : trail.path) { //발사한 탄환이 이동한 경로의 각 셀에 대해 계산
            //만약 캐릭터가 있다면 배열에 그 위치를 추가하지만, 최종 위치는 제외. 최종 위치는 투명 탄환이 아닌 처음에 발사한 탄환이 명중하기 때문
            if (Actor.findChar(cell) != null && !(Actor.findChar(cell) instanceof Hero) && (cell != trail.collisionPos)) {
                chCells.add(cell);
            }
        }
        return chCells;
    }

    private int finalPos(Ballistica path, int wallPenetration) { //벽 관통을 계산하고 최종 도달 위치를 반환
        int prevPos = path.sourcePos;

        for (int cell : path.path) {
            if (Dungeon.level.solid[cell]){
                wallPenetration--;
                if (wallPenetration < 0){
                    if (Dungeon.level.map[cell] == Terrain.DOOR) { //최종 도달 위치가 문일 경우 이전 타일이 아닌 문 타일 위치를 반환
                        return cell;
                    } else { //최종 도달 위치가 벽일 경우 이전 위치를 반환
                        return prevPos;
                    }
                }
            } else {
                prevPos = cell; //지금까지의 경로 상 벽을 제외한 가장 마지막 타일을 저장
            }
            //만약 현재 위치가 발사 시 지정한 위치와 같을 경우 연산을 중단하고 해당 위치를 반환. 탄환이 지정한 위치를 넘어서 이동하는 것을 방지함
            if (cell == path.collisionPos) {
                //만약 이 위치가 단단한 벽이지만 문이 아닐 경우 이전 위치를 반환함
                if (Dungeon.level.solid[cell] && Dungeon.level.map[cell] != Terrain.DOOR){
                    return prevPos;
                } else {
                    return cell;
                }
            }
        }

        //반복문 중간에 발사 시 지정한 위치를 반드시 지나기 때문에 일반적으로 이 곳에는 도달하지 않음
        return prevPos;
    }

    public static class InvisibleShot extends Item {

        {
            image = ItemSpriteSheet.NO_BULLET;
        }

        @Override
        public int throwPos(Hero user, int dst) {
            return dst;
        }

        @Override
        protected void onThrow(int cell) {
            if (Actor.findChar(cell) != null) curUser.attack(Actor.findChar(cell), 1, 0, 1+0.25f*curUser.pointsInTalent(Talent.SHIROKO_ARMOR1_1));
        }

        @Override
        public void cast( final Hero user, final int dst ) {
            final int cell = throwPos( user, dst );

            Char enemy = Actor.findChar( cell );

            Callback callback = new Callback() {
                @Override
                public void call() {
                    curUser = user;
                    onThrow(cell);
                }
            };

            if (enemy != null) {
                ((MissileSprite) user.sprite.parent.recycle(MissileSprite.class)).
                        reset(user.sprite, enemy.sprite, this, callback);
            } else {
                ((MissileSprite) user.sprite.parent.recycle(MissileSprite.class)).
                        reset(user.sprite, cell, this, callback);
            }
        }
    }

    @Override
    public Talent[] talents() {
        return new Talent[]{Talent.SHIROKO_ARMOR1_1, Talent.SHIROKO_ARMOR1_2, Talent.SHIROKO_ARMOR1_3, Talent.HEROIC_ENERGY};
    }
}

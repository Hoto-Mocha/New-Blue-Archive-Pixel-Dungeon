package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.miyako;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TalismanOfForesight;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.Gun;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.tweeners.Tweener;
import com.watabou.utils.BArray;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class Helicopter extends ArmorAbility {
    {
        baseChargeUse = 35f;
    }

    @Override
    public boolean useTargeting(){
        return false;
    }

    @Override
    protected void activate(ClassArmor armor, Hero hero, Integer target) {
        if (target != null) {
            if (hero.rooted){
                PixelScene.shake( 1, 1f );
                return;
            }

            PathFinder.buildDistanceMap(target, BArray.or(Dungeon.level.passable, Dungeon.level.avoid, null));
            if (PathFinder.distance[hero.pos] == Integer.MAX_VALUE || !Dungeon.level.passable[target]){
                GLog.w(Messages.get(this, "cannot_reach"));
                return;
            }

            Char ch = Actor.findChar(target);
            if (ch != null) {
                if (!Dungeon.level.heroFOV[target]) {
                    Buff.affect(hero, TalismanOfForesight.CharAwareness.class, 3f).charID = ch.id();
                    float amount = chargeUse( hero );
                    if (hero.hasTalent(Talent.MIYAKO_ARMOR2_3)) {
                        amount *= 1-(0.25f*hero.pointsInTalent(Talent.MIYAKO_ARMOR2_3));
                    }
                    armor.charge -= amount;
                    armor.updateQuickslot();

                    hero.next();
                    Dungeon.observe();
                    return;
                } else {
                    GLog.w(Messages.get(this, "already_char"));
                    hero.next();
                    return;
                }
            }

            Ballistica route = new Ballistica(hero.pos, target, Ballistica.STOP_TARGET);
            int cell = target;

            //위에서 캐릭터가 있을 경우 예외처리를 해 놓았으나 만약을 위해 같은 위치에 캐릭터가 있을 경우 착지 위치를 바꾸는 코드를 추가
            int backTrace = route.dist-1;
            while (Actor.findChar( cell ) != null && cell != hero.pos) {
                cell = route.path.get(backTrace);
                backTrace--;
            }

            armor.charge -= chargeUse( hero );
            armor.updateQuickslot();

            Sample.INSTANCE.play(Assets.Sounds.BEACON);
            final int dest = cell;
            final int startCell = hero.pos;
            hero.sprite.operate(hero.pos, new Callback() {
                @Override
                public void call() {
                    if (hero.hasTalent(Talent.MIYAKO_ARMOR2_2) && Random.Float() < 0.25f*hero.pointsInTalent(Talent.MIYAKO_ARMOR2_2)) {
                        System.out.println("---Jump---");
                        System.out.println("startCell: "+startCell);
                        System.out.println("dest: "+dest);
                        Ballistica finalRoute = new Ballistica(startCell, dest, Ballistica.STOP_TARGET);
                        float delay = 0f;
                        Gun gun = (Gun) hero.belongings.weapon().duplicate(); //현재 장착한 총기와 똑같은 무기를 생성한다. 영웅이 장착한 무기를 직접 사용할 경우 탄환을 소모하기 때문
                        System.out.println("path dist: "+finalRoute.dist);
                        System.out.println("---for---");
                        for (int c : finalRoute.subPath(1, finalRoute.dist)) {
                            System.out.println("path pos: "+c);
                            Char enemy = Actor.findChar(c);
                            if (enemy != null
                                    && enemy.alignment == Char.Alignment.ENEMY
                                    && hero.belongings.weapon() instanceof Gun) {
                                hero.sprite.parent.add(new Tweener(Dungeon.hero.sprite.parent, delay) {
                                    @Override
                                    protected void updateValues(float progress) {

                                    }

                                    @Override
                                    protected void onComplete() {
                                        super.onComplete();
                                        Gun.Bullet bullet = gun.knockBullet();
                                        bullet.setIgnoreWall(true);
                                        bullet.cast(hero, enemy.pos);
                                        hero.spend(-bullet.delayFactor(hero)); //투척에 필요한 턴 수를 뺀다.
                                        System.out.println("enemy pos: "+enemy.pos);
                                    }
                                });
                            }
                            delay += 1/(float)finalRoute.path.size();
                        }
                    }


                    hero.busy();
                    hero.sprite.jump(hero.pos, dest, 100, 1, new Callback() {
                        @Override
                        public void call() {
                            hero.move(dest);
                            Dungeon.level.occupyCell(hero);
                            Dungeon.observe();
                            GameScene.updateFog();
                            Invisibility.dispel();

                            hero.spendAndNext(5f-hero.pointsInTalent(Talent.MIYAKO_ARMOR2_1));
                        }
                    });
                }
            });
        }
    }

    @Override
    public int icon() {
        return HeroIcon.MIYAKO_2;
    }

    @Override
    public String targetingPrompt() {
        return Messages.get(this, "prompt");
    }

    @Override
    public Talent[] talents() {
        return new Talent[]{Talent.MIYAKO_ARMOR2_1, Talent.MIYAKO_ARMOR2_2, Talent.MIYAKO_ARMOR2_3, Talent.HEROIC_ENERGY};
    }
}

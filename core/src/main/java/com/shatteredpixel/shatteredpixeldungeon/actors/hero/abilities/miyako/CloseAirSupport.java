package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.miyako;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.AirSupportParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.active.Claymore;
import com.shatteredpixel.shatteredpixeldungeon.items.active.HandGrenade;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.tweeners.Tweener;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.List;

public class CloseAirSupport extends ArmorAbility {
    {
        baseChargeUse = 50f;
    }

    @Override
    public boolean useTargeting(){
        return false;
    }

    @Override
    protected void activate(ClassArmor armor, Hero hero, Integer target) {
        if (target != null) {
            Ballistica aim = new Ballistica(hero.pos, target, Ballistica.DASH);
            if (aim.dist < 2) {
                GLog.w(Messages.get(this, "does_nothing"));
                return;
            }
            hero.busy();
            Sample.INSTANCE.play(Assets.Sounds.BEACON);
            hero.sprite.operate(hero.pos, new Callback() {
                @Override
                public void call() {
                    hero.sprite.idle();
                    Ballistica aim = new Ballistica(hero.pos, target, Ballistica.DASH);
                    float delay = 0f;
                    List<Integer> route = aim.subPath(2, aim.dist);
                    for (int cell : route) { //경로의 3번째 타일부터 시작
                        float finalDelay = delay; //폭발 이펙트 지연 시간. 0에서 시작해 루프 한 번을 돌 때마다 0.05가 추가된다.
                        hero.sprite.parent.add(new Tweener(hero.sprite.parent, finalDelay) { //finalDelay초 후에 폭발 이펙트가 작동하도록 설정한 Tweener
                            @Override
                            protected void updateValues(float progress) { //시간이 지남에 따라 실행되는 함수

                            }

                            @Override
                            protected void onComplete() { //시간이 다 지나면 실행되는 함수
                                super.onComplete();
                                CellEmitter.heroCenter(cell).burst(AirSupportParticle.factory(new Callback() {
                                    @Override
                                    public void call() {
                                        new CASBomb().explode(cell);
                                        if (cell == route.get(route.size()-1)) { //마지막 폭격임을 체크
                                            Dungeon.hero.spendAndNext(1f); //영웅이 대기 상태에서 벗어나게 함
                                        }
                                        if (hero.hasTalent(Talent.MIYAKO_ARMOR3_1)) {
                                            for (int offset : PathFinder.NEIGHBOURS9){
                                                if (!Dungeon.level.solid[cell+offset] && Blob.volumeAt(cell+offset, Fire.class) <= 0) {
                                                    GameScene.add(Blob.seed(cell+offset, 1+2*hero.pointsInTalent(Talent.MIYAKO_ARMOR3_1), Fire.class)); //영웅이 1턴을 소모하기 때문에 1을 더함
                                                }
                                            }
                                        }
                                    }
                                }), 1);
                            }
                        });
                        delay += 0.05f; //0.05초마다 1번 터진다.
                    }

                    if (hero.hasTalent(Talent.MIYAKO_ARMOR3_2)) {
                        switch (hero.pointsInTalent(Talent.MIYAKO_ARMOR3_2)) {
                            case 1:
                                if (hero.belongings.getItem(HandGrenade.class) != null) {
                                    hero.belongings.getItem(HandGrenade.class).reload(1);
                                }
                                break;
                            case 2:
                                if (hero.belongings.getItem(HandGrenade.class) != null) {
                                    hero.belongings.getItem(HandGrenade.class).reload(1+Random.Int(1));
                                }
                                break;
                            case 3:
                                if (hero.belongings.getItem(HandGrenade.class) != null) {
                                    hero.belongings.getItem(HandGrenade.class).reload(2);
                                }
                                break;
                            case 4:
                                if (hero.belongings.getItem(HandGrenade.class) != null) {
                                    hero.belongings.getItem(HandGrenade.class).reload(2);
                                }
                                if (hero.belongings.getItem(Claymore.class) != null) {
                                    hero.belongings.getItem(Claymore.class).reload(1);
                                }
                                break;
                        }
                    }

                    armor.charge -= chargeUse(hero);
                    armor.updateQuickslot();
                    Invisibility.dispel();
                }
            });
        }
    }

    @Override
    public int icon() {
        return HeroIcon.MIYAKO_3;
    }

    @Override
    public String targetingPrompt() {
        return Messages.get(this, "prompt");
    }

    @Override
    public Talent[] talents() {
        return new Talent[]{Talent.MIYAKO_ARMOR3_1, Talent.MIYAKO_ARMOR3_2, Talent.MIYAKO_ARMOR3_3, Talent.HEROIC_ENERGY};
    }

    public static class CASBomb extends Bomb {
        { //이 곳에서 폭발의 데미지를 조절 가능하다.
            minDamage = Math.round((4 + Dungeon.scalingDepth())*(1+0.25f*Dungeon.hero.pointsInTalent(Talent.MIYAKO_ARMOR3_3)));
            maxDamage = Math.round((12 + 3*Dungeon.scalingDepth())*(1+0.25f*Dungeon.hero.pointsInTalent(Talent.MIYAKO_ARMOR3_3)));
        }
    }
}

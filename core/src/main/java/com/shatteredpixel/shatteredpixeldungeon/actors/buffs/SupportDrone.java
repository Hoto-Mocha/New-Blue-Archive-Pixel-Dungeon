package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.BlastParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SparkParticle;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class SupportDrone extends Buff {
    {
        type = buffType.POSITIVE;
    }

    private int drone = 0;

    public int getDrone() {
        return drone;
    }

    //적 처치 시 작동하는 메서드
    public void kill() {
        int add = 1;
        if (Dungeon.hero.hasTalent(Talent.MIYAKO_EX2_1) && Random.Float() < 0.2f) add++;
        drone = Math.min(drone+add, MaxDrone());
        Dungeon.observe();
    }

    public int MaxDrone() {
        return 3 + (Dungeon.hero.pointsInTalent(Talent.MIYAKO_EX2_1) == 3 ? 1 : 0);
    }

    //물리 피격을 받았을 때 작동하는 메서드
    public int hit(Hero hero, int damage) {
        if (!(Dungeon.hero.pointsInTalent(Talent.MIYAKO_EX2_1) > 1 && Random.Float() < 0.2f)) {
            drone--;
            Dungeon.observe();
        }
        CellEmitter.heroCenter(hero.pos).burst(BlastParticle.FACTORY, 1);
        if (drone <= 0) detach();
        return Math.round(damage*0.5f);
    }

    //적을 물리 공격했을 때 작동하는 메서드
    public void attackProc(Char enemy) {
        enemy.sprite.centerEmitter().burst(SparkParticle.FACTORY, drone);
        Sample.INSTANCE.play( Assets.Sounds.LIGHTNING );
        for (int i = 0; i < drone; i++) {
            enemy.damage(1+(Dungeon.scalingDepth()-1)/5+Dungeon.hero.pointsInTalent(Talent.MIYAKO_EX2_2), this); //1~(현재 계층)의 피해를 입힘. 보스 층에서 다음 계층 취급이 되는 것을 방지하기 위해 층수에서 1을 뺀다.
        }
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", drone);
    }

    @Override
    public String iconTextDisplay() {
        return Integer.toString(drone);
    }

    @Override
    public int icon() {
        return BuffIndicator.SUPPORT_DRONE;
    }

    @Override
    public float iconFadePercent() {
        return Math.max(0, (MaxDrone() - drone) / (float)MaxDrone());
    }

    public static final String DRONE = "drone";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(DRONE, drone);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        drone = bundle.getInt(DRONE);
    }
}

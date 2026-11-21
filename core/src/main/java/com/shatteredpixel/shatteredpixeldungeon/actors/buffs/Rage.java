package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;
import com.watabou.utils.GameMath;

public class Rage extends Buff {
    {
        type = buffType.POSITIVE;
    }

    public int powerLossBuffer = 0;
    private float power = 0;

    private static final String POWER = "power";
    private static final String POWER_BUFFER = "power_buffer";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(POWER, power);
        bundle.put(POWER_BUFFER, powerLossBuffer);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);

        power = bundle.getFloat(POWER);
        powerLossBuffer = bundle.getInt(POWER_BUFFER);
    }

    @Override
    public boolean act() {
        if (powerLossBuffer > 0){
            powerLossBuffer--;
        } else {
            power -= GameMath.gate(0.1f, power, 1f) * 0.05f * Math.pow((target.HP / (float) target.HT), 2);

            if (power <= 0) {
                detach();
            }
        }
        spend(TICK);
        return true;
    }

    public float damageFactor(float dmg){
        return dmg * Math.min(1.5f, 1f + (power / 2f));
    }

    public void damage(int damage){
        float maxPower = 1f + 0.1667f*((Hero)target).pointsInTalent(Talent.KURUMI_EX2_1);
        power = Math.min(maxPower, power + (damage/(float)target.HT)/4f );
        BuffIndicator.refreshHero();
        powerLossBuffer = 3;
    }

    @Override
    public int icon() {
        return BuffIndicator.BERSERK;
    }

    @Override
    public void tintIcon(Image icon) {
        if (power < 1f) icon.hardlight(1f, 0.5f, 0f);
        else            icon.hardlight(1f, 0f, 0f);
    }

    @Override
    public float iconFadePercent() {
        float maxPower = 1f + 0.1667f*((Hero)target).pointsInTalent(Talent.KURUMI_EX2_1);
        return (maxPower - power)/maxPower;
    }

    public String iconTextDisplay() {
        return (int)(power*100) + "%";
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", (int)Math.floor(power * 100f), (int)(((int)damageFactor(10000) / 100f) - 100f));
    }
}

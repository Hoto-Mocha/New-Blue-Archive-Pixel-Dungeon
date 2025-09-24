package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;

public class ArmorBreak extends Buff {
    {
        type = buffType.NEGATIVE;
        announced = true;
    }

    int drDown = 0;
    float time = 6f;
    public static final float DURATION = 6f;

    @Override
    public boolean act() {
        time -= TICK;

        if (time <= 0) {
            detach();
        }

        return true;
    }

    public void set(int dr) {
        drDown = dr;
    }

    public int getDrDown() {
        return drDown;
    }

    private static final String DR_DOWN = "drDown";
    private static final String TIME = "time";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(DR_DOWN, drDown);
        bundle.put(TIME, time);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        drDown = bundle.getInt(DR_DOWN);
        time = bundle.getFloat(TIME);
    }

    @Override
    public int icon() {
        return BuffIndicator.VULNERABLE;
    }

    @Override
    public void tintIcon(Image icon) {
        icon.hardlight(0x007EFF);
    }

    @Override
    public float iconFadePercent() {
        return (DURATION - time) / DURATION;
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", drDown, Messages.format("#.##", time));
    }

}

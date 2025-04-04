package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;

public class ShootAllBuff extends Buff implements ActionIndicator.Action {
    {
        revivePersists = true;
    }

    private boolean shootAll = false;

    public static String SHOOT_ALL = "shootAll";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(SHOOT_ALL, shootAll);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        shootAll = bundle.getBoolean(SHOOT_ALL);
    }

    @Override
    public boolean attachTo(Char target) {
        ActionIndicator.setAction(this);
        return super.attachTo(target);
    }

    @Override
    public void detach() { //이것은 일반적으로 작동하지 않음
        ActionIndicator.clearAction();
        super.detach();
    }

    @Override
    public String actionName() {
        return Messages.get(this, "action_name");
    }

    @Override
    public int actionIcon() {
        return HeroIcon.SHOOT_ALL_ACTION;
    }

    @Override
    public int indicatorColor() {
        return 0xCBB994;
    }

    @Override
    public void doAction() {
        Hero hero = Dungeon.hero;
        shootAll = !shootAll;
        hero.sprite.operate(hero.pos);
        if (shootAll) {
            GLog.p(Messages.get(this, "on"));
        } else {
            GLog.i(Messages.get(this, "off"));
        }
    }

    public boolean shootAll() {
        return shootAll;
    }

    public static class OverHeat extends Buff {

        {
            type = buffType.NEGATIVE;
        }

        private int duration = 1;
        private int maxDuration = 0;

        @Override
        public int icon() {
            return BuffIndicator.TIME;
        }

        public void add(int duration) {
            this.duration += duration;
            this.maxDuration += duration;
        }

        public void hit() {
            duration--;
            if (duration <= 0) {
                detach();
            }
        }

        @Override
        public boolean act() {
            duration--;
            if (duration <= 0) {
                detach();
            }
            spend(TICK);
            return true;
        }

        public static String DURATION = "duration";
        public static String MAX_DURATION = "max_duration";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(DURATION, duration);
            bundle.put(MAX_DURATION, maxDuration);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            duration = bundle.getInt(DURATION);
            maxDuration = bundle.getInt(MAX_DURATION);
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0, (maxDuration - duration) / (float)maxDuration);
        }

        @Override
        public String iconTextDisplay() {
            return String.valueOf(this.duration);
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc", duration);
        }
    }
}

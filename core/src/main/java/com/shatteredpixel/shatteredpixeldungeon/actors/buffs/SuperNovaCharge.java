package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.SuperNova;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class SuperNovaCharge extends CounterBuff implements ActionIndicator.Action {
    {
        type = buffType.NEUTRAL;
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
    public int icon() {
        return BuffIndicator.RECHARGING;
    }

    @Override
    public void tintIcon(Image icon) {
        icon.hardlight(0x5CDAFF);
    }

    public float getDamageBonus() {
        return 1+count()/100f;
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", Messages.decimalFormat("#.##", 100*getDamageBonus()));
    }

    public void hit() {
        int cnt = 1;
        if (Dungeon.hero.hasTalent(Talent.ARIS_EX2_1)) {
            int point = Dungeon.hero.pointsInTalent(Talent.ARIS_EX2_1);
            switch (point) {
                case 1: default:
                    cnt = Random.IntRange(-1, 2);
                    break;
                case 2:
                    cnt = Random.IntRange(0, 2);
                    break;
                case 3:
                    cnt = Random.IntRange(1, 3);
                    break;
            }
        }
        countUp(cnt);
    }

    @Override
    public String actionName() {
        return Messages.get(this, "action_name");
    }

    @Override
    public int actionIcon() {
        return HeroIcon.CHARGE;
    }

    @Override
    public int indicatorColor() {
        return 0x5CDAFF;
    }

    @Override
    public void doAction() {
        Hero hero = Dungeon.hero;

        if (hero.buff(SuperNova.SuperNovaCooldown.class) == null) {
            hero.yellW(Messages.get(Hero.class, "aris_no_cooldown"));
        } else {
            float duration = hero.buff(SuperNova.SuperNovaCooldown.class).duration();
            countDown((int)duration);
            hero.buff(SuperNova.SuperNovaCooldown.class).detach();
        }
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        ActionIndicator.setAction(this);
    }
}

package com.shatteredpixel.shatteredpixeldungeon.actors.hero.shops;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.yuzu.VIPMembership;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;

public class InfiniteAmmo extends YuzuShopContent {
    public static final InfiniteAmmo INSTANCE = new InfiniteAmmo();

    @Override
    public int icon() {
        return HeroIcon.SHOP_12;
    }

    @Override
    public void onSelect(Hero hero) {
        hero.busy();
        Buff.prolong(hero, InfiniteAmmoBuff.class, 5*hero.pointsInTalent(Talent.YUZU_ARMOR3_2));
        Sample.INSTANCE.play(Assets.Sounds.UNLOCK);
        hero.sprite.operate(hero.pos, new Callback() {
            @Override
            public void call() {
                hero.sprite.idle();
                hero.next();
            }
        });
    }

    @Override
    public int creditUse(Hero hero) {
        return 300*inflationParameter();
    }

    @Override
    public boolean hideWindow() {
        return true;
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", 5*Dungeon.hero.pointsInTalent(Talent.YUZU_ARMOR3_2)) + "\n\n" + Messages.get(this, "credit_cost", creditUse(Dungeon.hero));
    }

    @Override
    public boolean canSelect(Hero hero) {
        return super.canSelect(hero) && hero.buff(VIPMembership.VIPBuff.class) != null;
    }

    public static class InfiniteAmmoBuff extends FlavourBuff {
        {
            announced = true;
        }

        public static final float DURATION = 20f;

        @Override
        public float iconFadePercent() {
            return Math.max(0, (DURATION - visualcooldown()) / DURATION);
        }

        @Override
        public int icon() {
            return BuffIndicator.INFINITE_BULLET;
        }
    }
}

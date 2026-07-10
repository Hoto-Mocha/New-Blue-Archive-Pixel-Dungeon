package com.shatteredpixel.shatteredpixeldungeon.actors.hero.shops;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.watabou.noosa.audio.Sample;

public class InfiniteAmmo extends YuzuShopContent {
    public static final InfiniteAmmo INSTANCE = new InfiniteAmmo();

    @Override
    public int icon() {
        return HeroIcon.SHOP_12;
    }

    @Override
    public void onSelect(Hero hero) {
        Buff.prolong(hero, InfiniteAmmoBuff.class, 5*hero.pointsInTalent(Talent.YUZU_ARMOR3_2));
        hero.sprite.operate(hero.pos);
        Sample.INSTANCE.play(Assets.Sounds.UNLOCK);
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

    public static class InfiniteAmmoBuff extends FlavourBuff {
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

package com.shatteredpixel.shatteredpixeldungeon.actors.hero.shops;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.YuzuStatus;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;

public class PayToWin extends YuzuShopContent {
    public static final PayToWin INSTANCE = new PayToWin();

    @Override
    public int icon() {
        return HeroIcon.SHOP_4;
    }

    @Override
    public void onSelect(Hero hero) {
        Buff.affect(hero, YuzuStatus.CertainCritBuff.class).countUp(1);
        Buff.affect(hero, YuzuStatus.PayToWinBuff.class);
    }

    @Override
    public int creditUse(Hero hero) {
        return 100*inflationParameter();
    }

    @Override
    public boolean canSelect(Hero hero) {
        return super.canSelect(hero) && hero.buff(YuzuStatus.PayToWinBuff.class) == null;
    }

    @Override
    public boolean hideWindow() {
        return true;
    }
}

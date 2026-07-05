package com.shatteredpixel.shatteredpixeldungeon.actors.hero.shops;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.YuzuStatus;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;

public class BuyCritDmgMulti extends YuzuShopContent {
    public static final BuyCritDmgMulti INSTANCE = new BuyCritDmgMulti();

    @Override
    public int icon() {
        return HeroIcon.SHOP_7;
    }

    @Override
    public void onSelect(Hero hero) {
        Buff.affect(hero, YuzuStatus.class).buyStat(YuzuStatus.CRIT_DMG);
        CellEmitter.center( hero.pos ).burst( Speck.factory( Speck.STAR ), 1 );
        CellEmitter.get( hero.pos ).burst( Speck.factory( Speck.FORGE ), 1 );
    }

    @Override
    public boolean canSelect(Hero hero) {
        return super.canSelect(hero)
                && YuzuStatus.yuzuCritDmgMulti(hero) - YuzuStatus.yuzuBaseCritDmgMulti(hero) < YuzuStatus.CRIT_DMG_INCREMENT*YuzuStatus.MAX_LEVEL;
    }

    @Override
    public String shortDesc() {
        return Messages.get(this, "short_desc", Messages.decimalFormat("#", 100*YuzuStatus.CRIT_DMG_INCREMENT))
                + ".\n" + Messages.get(this, "credit_cost", creditUse(Dungeon.hero));
    }

    public String desc(){
        return Messages.get(this, "desc",
                Messages.decimalFormat("#", 100*YuzuStatus.CRIT_DMG_INCREMENT),
                Messages.decimalFormat("#", 100*YuzuStatus.CRIT_DMG_INCREMENT*YuzuStatus.MAX_LEVEL),
                Messages.decimalFormat("#", 100*YuzuStatus.yuzuBaseCritDmgMulti(Dungeon.hero)),
                Messages.decimalFormat("#", 100*(YuzuStatus.yuzuCritDmgMulti(Dungeon.hero) - YuzuStatus.yuzuBaseCritDmgMulti(Dungeon.hero))))
                + "\n\n" + Messages.get(this, "credit_cost", creditUse(Dungeon.hero));
    }

    @Override
    public int creditUse(Hero hero) {
        return 1000*(1+(int)((YuzuStatus.yuzuCritDmgMulti(hero)-YuzuStatus.yuzuBaseCritDmgMulti(hero))/YuzuStatus.CRIT_DMG_INCREMENT));
    }
}

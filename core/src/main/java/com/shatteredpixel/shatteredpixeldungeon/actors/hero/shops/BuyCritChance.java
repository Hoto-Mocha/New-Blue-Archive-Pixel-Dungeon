package com.shatteredpixel.shatteredpixeldungeon.actors.hero.shops;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.YuzuStatus;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;

public class BuyCritChance extends YuzuShopContent {
    public static final BuyCritChance INSTANCE = new BuyCritChance();

    @Override
    public int icon() {
        return HeroIcon.SHOP_6;
    }

    @Override
    public void onSelect(Hero hero) {
        Buff.affect(hero, YuzuStatus.class).buyStat(YuzuStatus.CRIT_CHANCE);
        CellEmitter.center( hero.pos ).burst( Speck.factory( Speck.STAR ), 1 );
        CellEmitter.get( hero.pos ).burst( Speck.factory( Speck.FORGE ), 1 );
    }

    @Override
    public boolean canSelect(Hero hero) {
        return super.canSelect(hero) && YuzuStatus.yuzuCritChance(hero)-YuzuStatus.yuzuBaseCritChance(hero) < YuzuStatus.CRIT_CHANCE_INCREMENT*YuzuStatus.MAX_LEVEL;
    }

    @Override
    public String shortDesc() {
        return Messages.get(this, "short_desc", Messages.decimalFormat("#", 100*YuzuStatus.CRIT_CHANCE_INCREMENT)) + ".\n" + Messages.get(this, "credit_cost", creditUse(Dungeon.hero));
    }

    public String desc(){
        return Messages.get(this, "desc",
                Messages.decimalFormat("#", 100*YuzuStatus.CRIT_CHANCE_INCREMENT),
                Messages.decimalFormat("#", 100*YuzuStatus.CRIT_CHANCE_INCREMENT*YuzuStatus.MAX_LEVEL),
                Messages.decimalFormat("#", 100*YuzuStatus.yuzuBaseCritChance(Dungeon.hero)),
                Messages.decimalFormat("#", 100*(YuzuStatus.yuzuCritChance(Dungeon.hero)-YuzuStatus.yuzuBaseCritChance(Dungeon.hero))))
                + "\n\n" + Messages.get(this, "credit_cost", creditUse(Dungeon.hero));
    }

    @Override
    public int creditUse(Hero hero) {
        return 1000*(1+(int)((YuzuStatus.yuzuCritChance(Dungeon.hero)-YuzuStatus.yuzuBaseCritChance(Dungeon.hero))/YuzuStatus.CRIT_CHANCE_INCREMENT));
    }
}

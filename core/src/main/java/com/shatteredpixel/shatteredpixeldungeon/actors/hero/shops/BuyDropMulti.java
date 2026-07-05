package com.shatteredpixel.shatteredpixeldungeon.actors.hero.shops;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.YuzuStatus;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;

public class BuyDropMulti extends YuzuShopContent {
    public static final BuyDropMulti INSTANCE = new BuyDropMulti();
    private final float INCREMENT = 0.1f;
    private final int MAX_LEVEL = 20;

    @Override
    public int icon() {
        return HeroIcon.SHOP_9;
    }

    @Override
    public void onSelect(Hero hero) {
        Buff.affect(hero, YuzuStatus.class).dropMulti += INCREMENT;
        CellEmitter.center( hero.pos ).burst( Speck.factory( Speck.STAR ), 1 );
        CellEmitter.get( hero.pos ).burst( Speck.factory( Speck.FORGE ), 1 );
    }

    @Override
    public boolean canSelect(Hero hero) {
        return super.canSelect(hero) && YuzuStatus.yuzuDropMulti(hero)-1 < INCREMENT*MAX_LEVEL;
    }

    @Override
    public String shortDesc() {
        return Messages.get(this, "short_desc", Messages.decimalFormat("#", 100*INCREMENT))
                + ".\n" + Messages.get(this, "credit_cost", creditUse(Dungeon.hero));
    }

    public String desc(){
        return Messages.get(this, "desc",
                Messages.decimalFormat("#", 100*INCREMENT),
                Messages.decimalFormat("#", 100*INCREMENT*MAX_LEVEL),
                Messages.decimalFormat("#", 100),
                Messages.decimalFormat("#", 100*(YuzuStatus.yuzuDropMulti(Dungeon.hero)-1)))
                + "\n\n" + Messages.get(this, "credit_cost", creditUse(Dungeon.hero));
    }

    @Override
    public int creditUse(Hero hero) {
        return 1000*(1+(int)((YuzuStatus.yuzuDropMulti(hero)-1)/INCREMENT));
    }
}

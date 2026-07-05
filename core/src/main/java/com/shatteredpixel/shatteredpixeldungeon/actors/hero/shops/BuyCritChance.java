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
    private final float INCREMENT = 0.02f;
    private final int MAX_LEVEL = 20;

    @Override
    public int icon() {
        return HeroIcon.SHOP_6;
    }

    @Override
    public void onSelect(Hero hero) {
        Buff.affect(hero, YuzuStatus.class).critChanceBonus += INCREMENT;
        CellEmitter.center( hero.pos ).burst( Speck.factory( Speck.STAR ), 1 );
        CellEmitter.get( hero.pos ).burst( Speck.factory( Speck.FORGE ), 1 );
    }

    @Override
    public boolean canSelect(Hero hero) {
        return super.canSelect(hero) && YuzuStatus.yuzuCritChanceMulti(hero) < INCREMENT*MAX_LEVEL;
    }

    @Override
    public String shortDesc() {
        return Messages.get(this, "short_desc", (int)(100*INCREMENT)) + ".\n" + Messages.get(this, "credit_cost", creditUse(Dungeon.hero));
    }

    public String desc(){
        return Messages.get(this, "desc", (int)(100*INCREMENT), (int)(100*INCREMENT*MAX_LEVEL), (int)(Math.ceil(100*YuzuStatus.yuzuCritChanceMulti(Dungeon.hero)))) + "\n\n" + Messages.get(this, "credit_cost", creditUse(Dungeon.hero));
    }

    @Override
    public int creditUse(Hero hero) {
        return 1000*(1+(int)(YuzuStatus.yuzuCritChanceMulti(hero)/INCREMENT));
    }
}

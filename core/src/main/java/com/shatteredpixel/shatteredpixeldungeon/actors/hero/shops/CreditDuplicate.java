package com.shatteredpixel.shatteredpixeldungeon.actors.hero.shops;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;

public class CreditDuplicate extends YuzuShopContent {
    public static final CreditDuplicate INSTANCE = new CreditDuplicate();

    @Override
    public int icon() {
        return HeroIcon.SHOP_13;
    }

    @Override
    public void onSelect(Hero hero) {
        new Gold(50*(1+hero.pointsInTalent(Talent.YUZU_ARMOR3_3))).doPickUp(hero, hero.pos);
    }

    @Override
    public int creditUse(Hero hero) {
        return 0;
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", 50*(1+Dungeon.hero.pointsInTalent(Talent.YUZU_ARMOR3_3))) + "\n\n" + Messages.get(this, "credit_cost", creditUse(Dungeon.hero));
    }
}

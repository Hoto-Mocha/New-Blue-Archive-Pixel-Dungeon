package com.shatteredpixel.shatteredpixeldungeon.actors.hero.shops;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.yuzu.VIPMembership;
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
        if (hero.buff(VIPMembership.VIPBuff.class) == null) return;
        int turnsRemain = (int)hero.buff(VIPMembership.VIPBuff.class).visualcooldown();
        new Gold(turnsRemain*50*(1+hero.pointsInTalent(Talent.YUZU_ARMOR3_3))).doPickUp(hero, hero.pos);
        hero.buff(VIPMembership.VIPBuff.class).detach();
    }

    @Override
    public int creditUse(Hero hero) {
        return 0;
    }

    @Override
    public boolean hideWindow() {
        return true;
    }

    @Override
    public String desc() {
        int turnsRemain = 1;
        if (Dungeon.hero.buff(VIPMembership.VIPBuff.class) != null) turnsRemain = (int)Dungeon.hero.buff(VIPMembership.VIPBuff.class).visualcooldown();
        return Messages.get(this, "desc", turnsRemain*50*(1+Dungeon.hero.pointsInTalent(Talent.YUZU_ARMOR3_3))) + "\n\n" + Messages.get(this, "credit_cost", creditUse(Dungeon.hero));
    }

    @Override
    public boolean canSelect(Hero hero) {
        return super.canSelect(hero) && hero.buff(VIPMembership.VIPBuff.class) != null;
    }
}

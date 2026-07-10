package com.shatteredpixel.shatteredpixeldungeon.actors.hero.shops;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.yuzu.VVIPMembership;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.watabou.noosa.audio.Sample;

public class Invulnerable extends YuzuShopContent {
    public static final Invulnerable INSTANCE = new Invulnerable();

    @Override
    public int icon() {
        return HeroIcon.SHOP_11;
    }

    @Override
    public void onSelect(Hero hero) {
        Sample.INSTANCE.play(Assets.Sounds.CHARGEUP, 1f, 1.2f);
        Buff.affect(hero, InvulnerableTracker.class);
        Buff.affect(hero, Barrier.class).incShield(30*hero.pointsInTalent(Talent.YUZU_ARMOR3_1));
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
        return Messages.get(this, "desc", 30 * Dungeon.hero.pointsInTalent(Talent.YUZU_ARMOR3_2)) + "\n\n" + Messages.get(this, "credit_cost", creditUse(Dungeon.hero));
    }

    @Override
    public boolean canSelect(Hero hero) {
        return super.canSelect(hero) && hero.buff(VVIPMembership.VVIPBuff.class) != null && hero.buff(InvulnerableTracker.class) == null;
    }

    public static class InvulnerableTracker extends Buff {}
}

package com.shatteredpixel.shatteredpixeldungeon.actors.hero.miracles;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CrossNecklace;

public class PunishmentBullet extends MariMiracle {
    public static PunishmentBullet INSTANCE = new PunishmentBullet();

    @Override
    public int icon() {
        return super.icon();
    }

    @Override
    public float chargeUse(Hero hero) {
        return super.chargeUse(hero);
    }

    @Override
    public String desc() {
        return super.desc();
    }

    @Override
    public boolean canCast(Hero hero) {
        return super.canCast(hero);
    }

    @Override
    public void onCast(CrossNecklace cross, Hero hero) {

    }
}

package com.shatteredpixel.shatteredpixeldungeon.actors.hero.miracles;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.mari.IdolConcert;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CrossNecklace;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;

public class Encore extends MariMiracle {
    public static Encore INSTANCE = new Encore();

    @Override
    public int icon() {
        return HeroIcon.ENCORE;
    }

    @Override
    public float chargeUse(Hero hero) {
        return 16-4*hero.pointsInTalent(Talent.MARI_ARMOR3_1);
    }

    @Override
    public boolean canCast(Hero hero) {
        return super.canCast(hero) && hero.hasTalent(Talent.MARI_ARMOR3_1) && hero.buff(IdolConcert.Encore.class) != null;
    }

    @Override
    public void onCast(CrossNecklace cross, Hero hero) {
        IdolConcert.concert(hero);
        hero.buff(IdolConcert.Encore.class).detach();
        onMiracleCast(cross, hero);
    }
}

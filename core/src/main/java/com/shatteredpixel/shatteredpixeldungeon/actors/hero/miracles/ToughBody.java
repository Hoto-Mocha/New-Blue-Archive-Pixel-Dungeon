package com.shatteredpixel.shatteredpixeldungeon.actors.hero.miracles;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;

public class ToughBody extends TrinityMiracle {

    public static ToughBody INSTANCE = new ToughBody();

    @Override
    public int icon() {
        return HeroIcon.TOUGH_BODY;
    }

    @Override
    public boolean canCast(Hero hero) {
        return super.canCast(hero) && hero.hasTalent(Talent.MARI_ARMOR2_1);
    }

    @Override
    public String desc() {
        int min = Dungeon.hero.pointsInTalent(Talent.MARI_ARMOR2_1);
        int max = 5*Dungeon.hero.pointsInTalent(Talent.MARI_ARMOR2_1);
        return Messages.get(this, "desc", min, max) + "\n\n" + Messages.get(this, "charge_cost", (int)chargeUse(Dungeon.hero));
    }

    @Override
    public void castEffect(Hero hero) {
        getBuff(hero).defenseCast();
    }
}

package com.shatteredpixel.shatteredpixeldungeon.actors.hero.miracles;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.SpellSprite;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;

public class CrystalMind extends TrinityMiracle {

    public static CrystalMind INSTANCE = new CrystalMind();

    @Override
    public int icon() {
        return HeroIcon.CRYSTAL_MIND;
    }

    @Override
    public boolean canCast(Hero hero) {
        return super.canCast(hero) && hero.hasTalent(Talent.MARI_ARMOR2_2);
    }

    @Override
    public String desc() {
        int range = Dungeon.hero.pointsInTalent(Talent.MARI_ARMOR2_1);
        int maxRange = 12;
        return Messages.get(this, "desc", range, maxRange) + "\n\n" + Messages.get(this, "charge_cost", (int)chargeUse(Dungeon.hero));
    }

    @Override
    public void castEffect(Hero hero) {
        getBuff(hero).mindVisionCast(hero);
        SpellSprite.show(hero, SpellSprite.VISION);
    }
}

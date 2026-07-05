package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class YuzuStatus extends Buff {
    public float critChanceBonus = 0;
    public int critDmgBonus = 0;
    public float critDmgMulti = 1.2f;
    public float creditMulti = 1f;
    public float dropMulti = 1f;

    {
        revivePersists = true;
    }

    public float chance(Hero hero) {
        return 0.05f+(0.01f*hero.lvl)+ critChanceBonus;
    }

    public boolean isCritical(Hero hero) {
        return Random.Float() < chance(hero);
    }

    public float criticalDamage(Hero hero, Char enemy, float dmg) {
        if (isCritical(hero)) {
            dmg *= critDmgMulti;
            dmg += critDmgBonus;
            Buff.affect(hero, CriticalTracker.class);
        }

        return dmg;
    }

    private static final String CRIT_CHANCE_BONUS = "critChanceBonus";
    private static final String CRIT_DMG_BONUS = "critDmgBonus";
    private static final String CRIT_DMG_MULTI = "critDmgMulti";
    private static final String CREDIT_MULTI = "creditMulti";
    private static final String DROP_MULTI = "dropMulti";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(CRIT_CHANCE_BONUS, critChanceBonus);
        bundle.put(CRIT_DMG_BONUS, critDmgBonus);
        bundle.put(CRIT_DMG_MULTI, critDmgMulti);
        bundle.put(CREDIT_MULTI, creditMulti);
        bundle.put(DROP_MULTI, dropMulti);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        critChanceBonus = bundle.getFloat(CRIT_CHANCE_BONUS);
        critDmgBonus = bundle.getInt(CRIT_DMG_BONUS);
        critDmgMulti = bundle.getFloat(CRIT_DMG_MULTI);
        creditMulti = bundle.getFloat(CREDIT_MULTI);
        dropMulti = bundle.getFloat(DROP_MULTI);
    }

    public static class CriticalTracker extends Buff {}

    public static float yuzuCritChanceMulti(Hero hero) {
        if (hero.buff(YuzuStatus.class) == null) return 0f;
        else return hero.buff(YuzuStatus.class).critChanceBonus;
    }

    public static int yuzuCritDmgBonus(Hero hero) {
        if (hero.buff(YuzuStatus.class) == null) return 0;
        else return hero.buff(YuzuStatus.class).critDmgBonus;
    }

    public static float yuzuCritDmgMulti(Hero hero) {
        if (hero.buff(YuzuStatus.class) == null) return 1f;
        else return hero.buff(YuzuStatus.class).critDmgMulti;
    }

    public static float yuzuCreditMulti(Hero hero) {
        if (hero.buff(YuzuStatus.class) == null) return 1f;
        else return hero.buff(YuzuStatus.class).creditMulti;
    }

    public static float yuzuDropMulti(Hero hero) {
        if (hero.buff(YuzuStatus.class) == null) return 1f;
        else return hero.buff(YuzuStatus.class).dropMulti;
    }

}

package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class YuzuStatus extends Buff {
    int dmgBonus = 0;
    float dmgMulti = 1.2f;
    float creditMulti = 1f;
    float dropMulti = 1f;

    {
        revivePersists = true;
    }

    public float chance(Hero hero) {
        return 0.05f+0.01f*hero.lvl;
    }

    public boolean isCritical(Hero hero) {
        return Random.Float() < chance(hero);
    }

    public int damageBonus() {
        return this.dmgBonus;
    }

    public float damageMulti() {
        return this.dmgMulti;
    }

    public float creditMulti() {
        return this.creditMulti;
    }

    public float dropMulti() {
        return this.dropMulti;
    }

    public void addBonus(int amount) {
        this.dmgBonus += amount;
    }

    public void addMulti(float amount) {
        this.dmgMulti += amount;
    }

    public float criticalDamage(Hero hero, Char enemy, float dmg) {
        if (isCritical(hero)) {
            dmg *= damageMulti();
            dmg += damageBonus();
            Buff.affect(hero, CriticalTracker.class);
        }

        return dmg;
    }

    private static final String DMG_BONUS = "dmgBonus";
    private static final String DMG_MULTI = "dmgMulti";
    private static final String CREDIT_MULTI = "creditMulti";
    private static final String DROP_MULTI = "dropMulti";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(DMG_BONUS, dmgBonus);
        bundle.put(DMG_MULTI, dmgMulti);
        bundle.put(CREDIT_MULTI, creditMulti);
        bundle.put(DROP_MULTI, dropMulti);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        dmgBonus = bundle.getInt(DMG_BONUS);
        dmgMulti = bundle.getFloat(DMG_MULTI);
        creditMulti = bundle.getFloat(CREDIT_MULTI);
        dropMulti = bundle.getFloat(DROP_MULTI);
    }

    public static class CriticalTracker extends Buff {}

    public static float yuzuCreditBonus(Hero hero) {
        if (hero.buff(YuzuStatus.class) == null) return 1f;
        else return hero.buff(YuzuStatus.class).creditMulti();
    }

    public static float yuzuDropBonus(Hero hero) {
        if (hero.buff(YuzuStatus.class) == null) return 1f;
        else return hero.buff(YuzuStatus.class).dropMulti();
    }

}

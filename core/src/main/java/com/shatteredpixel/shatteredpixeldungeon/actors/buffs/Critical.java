package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.watabou.utils.Random;

public class Critical extends Buff {
    int dmgBonus = 0;
    float dmgMulti = 1.2f;

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

    public float criticalDamage(Hero hero, Char enemy, float dmg) {
        if (isCritical(hero)) {
            dmg *= damageMulti();
            dmg += damageBonus();
            Buff.affect(hero, CriticalTracker.class);
        }

        return dmg;
    }

    public static class CriticalTracker extends Buff {}

}

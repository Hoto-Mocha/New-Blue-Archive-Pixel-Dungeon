package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class YuzuStatus extends Buff {
    public static final int MAX_LEVEL = 20;

    public static final float CRIT_CHANCE_INCREMENT = 0.02f;
    public static final String CRIT_CHANCE = "critChance";
    private int critChanceCount = 0;

    public static final float CRIT_DMG_INCREMENT = 0.05f;
    public static final String CRIT_DMG = "critDmg";
    private int critDmgCount = 0;

    public static final float CREDIT_MULTI_INCREMENT = 0.1f;
    public static final String CREDIT_MULTI = "creditMulti";
    private int creditMultiCount = 0;

    public static final float DROP_MULTI_INCREMENT = 0.1f;
    public static final String DROP_MULTI = "dropMulti";
    private int dropMultiCount = 0;

    public static final float SEARCH_CHANCE_INCREMENT = 0.02f;
    public static final String SEARCH_CHANCE = "searchChance";
    private int searchChanceCount = 0;

    {
        revivePersists = true;
    }

    //크리티컬 확률 관련
    public float baseChance(Hero hero) {
        if (hero.buff(CertainCritBuff.class) != null) return 1;
        float chance = 0.05f+(0.01f*hero.lvl);
        if (hero.buff(SerialCritBuff.class) != null) chance += 0.1f*hero.pointsInTalent(Talent.YUZU_T2_4);
        return chance;
    }

    public float chance(Hero hero) {
        return baseChance(hero) + CRIT_CHANCE_INCREMENT * critChanceCount;
    }

    public boolean isCritical(Hero hero) {
        return Random.Float() < chance(hero);
    }

    //크리티컬 피해 관련
    public float baseCritDmgMulti(Hero hero) {
        float multi = 1.2f;
        if (hero.buff(PayToWinBuff.class) != null) {
            multi += 0.2f;
        }
        return multi;
    }

    private float critDmgMulti(Hero hero) {
        float multi = baseCritDmgMulti(hero);
        multi += CRIT_DMG_INCREMENT * critDmgCount;
        return multi;
    }

    private int critDmgBonus(Hero hero) {
        int bonus = 0;

        if (hero.hasTalent(Talent.YUZU_T1_3)) bonus += 1+2*hero.pointsInTalent(Talent.YUZU_T1_3);
        return bonus;
    }

    public float criticalDamage(Hero hero, Char enemy, float dmg) {
        if (hero.buff(SerialCritBuff.class) != null) hero.buff(SerialCritBuff.class).detach();
        if (isCritical(hero)) {
            dmg *= critDmgMulti(hero);
            dmg += critDmgBonus(hero);
            Buff.affect(hero, CriticalTracker.class);
            onCritical(hero);
        }

        return dmg;
    }

    //크리티컬 발동 시
    public void onCritical(Hero hero) {
        if (hero.buff(CertainCritBuff.class) != null) {
            hero.buff(CertainCritBuff.class).countDown(1);
        }
        if (hero.buff(PayToWinBuff.class) != null) {
            hero.buff(PayToWinBuff.class).detach();
        }
        if (hero.hasTalent(Talent.YUZU_T2_4)) {
            Buff.affect(hero, SerialCritBuff.class);
        }
        if (hero.hasTalent(Talent.YUZU_T3_2) && Random.Float() < 1/(float)(4-hero.pointsInTalent(Talent.YUZU_T3_2))) {
            //need to delay this to prevent consuming the buff from self attack with explosion at the attacking turn
            new FlavourBuff() {
                {
                    actPriority = VFX_PRIO;
                }

                public boolean act() {
                    Buff.affect(hero, UZQMode.class);
                    return super.act();
                }
            }.attachTo(hero);
        }
    }

    //크레딧 획득량 관련
    public float creditMulti(Hero hero) {
        return 1f + CREDIT_MULTI_INCREMENT * creditMultiCount;
    }

    //드랍률 증가량 관련
    public float dropMulti(Hero hero) {
        return 1f + DROP_MULTI_INCREMENT * dropMultiCount;
    }

    //탐색 확률 증가량 관련
    public float searchChanceBonus() {
        return SEARCH_CHANCE_INCREMENT * searchChanceCount;
    }

    //스테이터스 구매
    public void buyStat(String key) {
        switch (key) {
            case CRIT_CHANCE:
                critChanceCount++;
                return;
            case CRIT_DMG:
                critDmgCount++;
                return;
            case CREDIT_MULTI:
                creditMultiCount++;
                return;
            case DROP_MULTI:
                dropMultiCount++;
                return;
            case SEARCH_CHANCE:
                searchChanceCount++;
                return;
            default:
                return;
        }
    }

    private static final String CRIT_CHANCE_COUNT = "critChanceCount";
    private static final String CRIT_DMG_COUNT = "critDmgCount";
    private static final String CREDIT_MULTI_COUNT = "creditMultiCount";
    private static final String DROP_MULTI_COUNT = "dropMultiCount";
    private static final String SEARCH_CHANCE_COUNT = "searchChanceCount";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(CRIT_CHANCE_COUNT, critChanceCount);
        bundle.put(CRIT_DMG_COUNT, critDmgCount);
        bundle.put(CREDIT_MULTI_COUNT, creditMultiCount);
        bundle.put(DROP_MULTI_COUNT, dropMultiCount);
        bundle.put(SEARCH_CHANCE_COUNT, searchChanceCount);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        critChanceCount = bundle.getInt(CRIT_CHANCE_COUNT);
        critDmgCount = bundle.getInt(CRIT_DMG_COUNT);
        creditMultiCount = bundle.getInt(CREDIT_MULTI_COUNT);
        dropMultiCount = bundle.getInt(DROP_MULTI_COUNT);
        searchChanceCount = bundle.getInt(SEARCH_CHANCE_COUNT);
    }

    public static class CriticalTracker extends Buff {}

    public static float yuzuBaseCritChance(Hero hero) {
        if (hero.buff(YuzuStatus.class) == null) return 0f;
        else return hero.buff(YuzuStatus.class).baseChance(hero);
    }

    public static float yuzuCritChance(Hero hero) {
        if (hero.buff(YuzuStatus.class) == null) return 0f;
        else return hero.buff(YuzuStatus.class).chance(hero);
    }

    public static float yuzuBaseCritDmgMulti(Hero hero) {
        if (hero.buff(YuzuStatus.class) == null) return 1f;
        else return hero.buff(YuzuStatus.class).baseCritDmgMulti(hero);
    }

    public static float yuzuCritDmgMulti(Hero hero) {
        if (hero.buff(YuzuStatus.class) == null) return 1f;
        else return hero.buff(YuzuStatus.class).critDmgMulti(hero);
    }

    public static float yuzuCreditMulti(Hero hero) {
        if (hero.buff(YuzuStatus.class) == null) return 1f;
        else return hero.buff(YuzuStatus.class).creditMulti(hero);
    }

    public static float yuzuDropMulti(Hero hero) {
        if (hero.buff(YuzuStatus.class) == null) return 1f;
        else return hero.buff(YuzuStatus.class).dropMulti(hero);
    }

    public static float yuzuSearchChanceBonus(Hero hero) {
        if (hero.buff(YuzuStatus.class) == null) return 0f;
        else return hero.buff(YuzuStatus.class).searchChanceBonus();
    }

    public static class CertainCritBuff extends CounterBuff {
        private final int MAX_COUNT = 5;

        @Override
        public int icon() {
            return BuffIndicator.WEAPON;
        }

        @Override
        public void tintIcon(Image icon) {
            icon.hardlight(0xE2A865);
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0, (MAX_COUNT - count()) / MAX_COUNT);
        }

        @Override
        public void countDown(float inc) {
            super.countDown(inc);
            if (count() <= 0) {
                detach();
            }
        }

        @Override
        public void countUp(float inc) {
            super.countUp(inc);
            while (count() > MAX_COUNT) {
                countDown(1);
            }
        }
    }

    public static class PayToWinBuff extends Buff {}

    public static class SerialCritBuff extends Buff {}

    public static class UZQMode extends Buff {
        @Override
        public int icon() {
            return BuffIndicator.HASTE;
        }

        @Override
        public void tintIcon(Image icon) {
            icon.hardlight(0x00AAFF);
        }
    }

}

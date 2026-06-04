package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.hoshino;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.Gun;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

public class ShieldParry extends ArmorAbility {
    {
        baseChargeUse = 50f;
    }

    @Override
    protected void activate(ClassArmor armor, Hero hero, Integer target) {
        hero.sprite.operate(hero.pos);
        Sample.INSTANCE.play(Assets.Sounds.MISS);
        armor.charge -= chargeUse( hero );
        armor.updateQuickslot();
        Invisibility.dispel();
        hero.spendAndNext(1f);
    }

    public static class ParryBuff extends Buff {

        {
            actPriority = HERO_PRIO+1;
            type = buffType.POSITIVE;
        }

        @Override
        public boolean act() {
            detach();
            return super.act();
        }

        public void onParry(Hero hero, Char attacker) {
            if (hero.hasTalent(Talent.HOSHINO_ARMOR3_1)) {
                hero.heal(2*hero.pointsInTalent(Talent.HOSHINO_ARMOR3_1));
            }
            if (hero.hasTalent(Talent.HOSHINO_ARMOR3_2)) {
                if (Random.Float() < 0.25f*hero.pointsInTalent(Talent.HOSHINO_ARMOR3_2)) {
                    Buff.affect(attacker, Paralysis.class, 3f);
                }
            }
            if (hero.hasTalent(Talent.HOSHINO_ARMOR3_3)) {
                if (hero.belongings.weapon() instanceof Gun) {
                    float possibleRounds = 0.5f*hero.pointsInTalent(Talent.HOSHINO_ARMOR3_3);
                    int rounds = (int)possibleRounds;
                    if (Random.Float() < rounds % 1f){
                        rounds++;
                    }
                    ((Gun)hero.belongings.weapon()).manualReload(rounds, true);
                }
            }
        }
    }

    @Override
    public Talent[] talents() {
        return new Talent[]{Talent.HOSHINO_ARMOR3_1, Talent.HOSHINO_ARMOR3_2, Talent.HOSHINO_ARMOR3_3, Talent.HEROIC_ENERGY};
    }
}

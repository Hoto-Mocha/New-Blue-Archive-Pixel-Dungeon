package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.hoshino;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.items.active.IronHorus;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.Gun;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

public class ShieldParry extends ArmorAbility {
    {
        baseChargeUse = 50f;
    }

    @Override
    public int icon() {
        return HeroIcon.HOSHINO_3;
    }

    @Override
    protected void activate(ClassArmor armor, Hero hero, Integer target) {
        if (hero.buff(IronHorus.LightTacticalShieldBuff.class) == null && hero.buff(IronHorus.TacticalShieldBuff.class) == null) {
            GLog.w(Messages.get(this, "no_shield"));
            return;
        }

        Buff.affect(hero, ParryBuff.class, Actor.TICK);

        hero.sprite.operate(hero.pos);
        Sample.INSTANCE.play(Assets.Sounds.MISS);
        armor.charge -= chargeUse( hero );
        armor.updateQuickslot();
        Invisibility.dispel();
        hero.spendAndNext(1f);
    }

    public static class ParryBuff extends FlavourBuff {

        {
            actPriority = HERO_PRIO+1;
        }

        public void onParry(Hero hero, Char attacker) {
            if (hero.hasTalent(Talent.HOSHINO_ARMOR3_1)) {
                hero.heal(2*hero.pointsInTalent(Talent.HOSHINO_ARMOR3_1));
            }
            if (hero.hasTalent(Talent.HOSHINO_ARMOR3_2)) {
                if (Random.Float() < 0.25f*hero.pointsInTalent(Talent.HOSHINO_ARMOR3_2)) {
                    Sample.INSTANCE.play(Assets.Sounds.BLAST);
                    WandOfBlastWave.BlastWave.blast(hero.pos);
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

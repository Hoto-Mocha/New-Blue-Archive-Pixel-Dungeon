package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.kurumi;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vulnerable;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.PotionOfCleansing;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.Gun;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

public class DefensivePosition extends ArmorAbility {
    {
        baseChargeUse = 25f;
    }

    @Override
    public int icon() {
        return HeroIcon.KURUMI_1;
    }

    @Override
    protected void activate(ClassArmor armor, Hero hero, Integer target) {
        hero.busy();
        hero.sprite.operate(hero.pos);
        hero.spend(Actor.TICK);
        Buff.affect(hero, DamageImmunity.class, hero.cooldown());
        if (Random.Float() < 0.25f*hero.pointsInTalent(Talent.KURUMI_ARMOR1_3)) {
            PotionOfCleansing.cleanseDebuff(hero, 0);
        }
        hero.next();
        Sample.INSTANCE.play(Assets.Sounds.MISS);
    }

    @Override
    public Talent[] talents() {
        return new Talent[]{Talent.KURUMI_ARMOR1_1, Talent.KURUMI_ARMOR1_2, Talent.KURUMI_ARMOR1_3, Talent.HEROIC_ENERGY};
    }

    public static class DamageImmunity extends FlavourBuff {
        { actPriority = HERO_PRIO+1; }

        public void damage(Hero hero, Char attacker) {
            if (hero.hasTalent(Talent.KURUMI_ARMOR1_1)) {
                Buff.prolong(attacker, Vulnerable.class, hero.pointsInTalent(Talent.KURUMI_ARMOR1_1));
            }
            if (hero.belongings.weapon() instanceof Gun) {
                Gun gun = (Gun) hero.belongings.weapon();
                gun.manualOverLoad(1, hero.pointsInTalent(Talent.KURUMI_ARMOR1_2)-1);
            }
            if (hero.belongings.secondWep() instanceof Gun) {
                Gun gun = (Gun) hero.belongings.weapon();
                gun.manualOverLoad(1, hero.pointsInTalent(Talent.KURUMI_ARMOR1_2)-1);
            }
        }
    }
}
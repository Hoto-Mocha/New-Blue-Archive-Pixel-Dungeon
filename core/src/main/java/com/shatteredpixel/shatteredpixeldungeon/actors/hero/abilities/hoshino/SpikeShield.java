package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.hoshino;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.active.IronHorus;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

public class SpikeShield extends ArmorAbility {
    {
        baseChargeUse = 25f;
    }

    @Override
    public int icon() {
        return HeroIcon.HOSHINO_2;
    }

    @Override
    public float chargeUse(Hero hero) {
        float chargeUse = super.chargeUse(hero);

        if (hero.hasTalent(Talent.HOSHINO_ARMOR2_3) && hero.buff(IronHorus.TacticalShieldCooldown.class) != null) {
            chargeUse *= Math.pow(0.9f, hero.pointsInTalent(Talent.HOSHINO_ARMOR2_3));
        }

        return chargeUse;
    }

    @Override
    protected void activate(ClassArmor armor, Hero hero, Integer target) {
        if (hero.hasTalent(Talent.HOSHINO_ARMOR2_3) && hero.buff(IronHorus.TacticalShieldCooldown.class) != null) {
            hero.buff(IronHorus.TacticalShieldCooldown.class).detach();
            hero.sprite.operate(hero.pos);
            Sample.INSTANCE.play(Assets.Sounds.MISS);
            hero.sprite.emitter().burst(Speck.factory(Speck.JET), 5);
            armor.charge -= chargeUse( hero );
            armor.updateQuickslot();
        }

        if (hero.buff(IronHorus.LightTacticalShieldBuff.class) == null && hero.buff(IronHorus.TacticalShieldBuff.class) == null) {
            GLog.w(Messages.get(this, "no_shield"));
            return;
        }

        if (hero.buff(SpikeShieldBuff.class) != null) {
            GLog.w(Messages.get(this, "already_active"));
            return;
        }

        Buff.affect(hero, SpikeShieldBuff.class);

        hero.sprite.operate(hero.pos);
        Sample.INSTANCE.play(Assets.Sounds.HIT_SLASH, 1, 1+Random.Float());
        Sample.INSTANCE.playDelayed(Assets.Sounds.HIT_SLASH, 0.1f, 1, 1+Random.Float());
        Sample.INSTANCE.playDelayed(Assets.Sounds.HIT_SLASH, 0.2f, 1, 1+Random.Float());
        armor.charge -= chargeUse( hero );
        armor.updateQuickslot();
    }

    public static class SpikeShieldBuff extends Buff {

        {
            actPriority = HERO_PRIO+1;
            type = buffType.POSITIVE;
            announced = true;
        }

        @Override
        public boolean act() {
            spend(TICK);

            if (target.buff(IronHorus.LightTacticalShieldBuff.class) == null && target.buff(IronHorus.TacticalShieldBuff.class) == null) {
                detach();
            }

            return true;
        }

        @Override
        public int icon() {
            return BuffIndicator.THORNS;
        }

        public void onHit(Hero hero, Char attacker) {
            Buff.affect(attacker, Bleeding.class).set(attacker.HT*(0.1f+0.025f*hero.pointsInTalent(Talent.HOSHINO_ARMOR2_1)));
            Sample.INSTANCE.play(Assets.Sounds.HIT_PARRY);
        }

        public int drBonus(Hero hero) {
            return 2*hero.pointsInTalent(Talent.HOSHINO_ARMOR2_2);
        }

    }

    @Override
    public Talent[] talents() {
        return new Talent[]{Talent.HOSHINO_ARMOR2_1, Talent.HOSHINO_ARMOR2_2, Talent.HOSHINO_ARMOR2_3, Talent.HEROIC_ENERGY};
    }
}

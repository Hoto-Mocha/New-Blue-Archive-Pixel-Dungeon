package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.yuzu;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bless;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.shops.Invulnerable;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;

public class VIPMembership extends ArmorAbility {
    {
        baseChargeUse = 50f;
    }

    @Override
    public int icon() {
        return HeroIcon.YUZU_3;
    }

    @Override
    protected void activate(ClassArmor armor, Hero hero, Integer target) {
        if (hero.buff(VIPBuff.class) != null) {
            GLog.w(Messages.get(this, "already_active"));
            return;
        }

        Buff.affect(hero, Barrier.class).setShield(30);
        Buff.affect(hero, Bless.class, Bless.DURATION);
        if (Dungeon.level.heroFOV[hero.pos]){
            new Flare(6, 32).color(0xFFFF00, true).show(hero.sprite, 2f);
        }
        Buff.affect(hero, VIPBuff.class, VIPBuff.DURATION);
        Sample.INSTANCE.play(Assets.Sounds.CHARGEUP);
        hero.sprite.operate(hero.pos);
        hero.spendAndNext(1);

        armor.charge -= chargeUse( hero );
        armor.updateQuickslot();
        Invisibility.dispel();
    }

    @Override
    public Talent[] talents() {
        return new Talent[]{Talent.YUZU_ARMOR3_1, Talent.YUZU_ARMOR3_2, Talent.YUZU_ARMOR3_3, Talent.HEROIC_ENERGY};
    }

    public static class VIPBuff extends FlavourBuff {
        {
            announced = true;
        }

        public static final float DURATION = 10f;

        @Override
        public int icon() {
            return BuffIndicator.VIP;
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0, (DURATION - visualcooldown()) / DURATION);
        }

        @Override
        public void detach() {
            if (target.buff(Invulnerable.InvulnerableTracker.class) != null) target.buff(Invulnerable.InvulnerableTracker.class).detach();
            super.detach();
        }
    }
}

package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.mika;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.SuperNovaTracker;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.CursedWand;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

public class SuperMeteor extends ArmorAbility {
    {
        baseChargeUse = 35f;
    }

    @Override
    public int icon() {
        return super.icon();
    }

    @Override
    public int targetedPos(Char user, int dst) {
        return super.targetedPos(user, dst);
    }

    @Override
    public String targetingPrompt() {
        return Messages.get(this, "prompt");
    }

    @Override
    public float chargeUse(Hero hero) {
        float chargeUse = super.chargeUse(hero);

        if (hero.buff(SerialUseTracker.class) != null) {
            chargeUse *= (float) (Math.pow(0.8f, hero.pointsInTalent(Talent.MIKA_ARMOR2_3)));
        }

        return chargeUse;
    }

    @Override
    protected void activate(ClassArmor armor, Hero hero, Integer target) {
        if (target == null) return;
        if (!Dungeon.level.heroFOV[target]) {
            hero.yellW("out_of_sight");
            return;
        }
        if (!Dungeon.level.passable[target]) {
            hero.yellW("invalid");
            return;
        }

        boolean positiveOnly = Random.Float() < 0.25f*hero.pointsInTalent(Talent.MIKA_ARMOR2_1);

        SuperNovaTracker nova = Buff.append(Dungeon.hero, SuperNovaTracker.class);
        nova.pos = target;
        nova.harmsAllies = !positiveOnly;

        if (hero.hasTalent(Talent.MIKA_ARMOR2_2)) {
            Buff.affect(hero, Noise.class).set(target);
        }

        if (positiveOnly){
            hero.yellW("supernova_positive");
        } else {
            hero.yellW("supernova");
        }

        if (hero.hasTalent(Talent.MIKA_ARMOR2_3)) {
            Buff.affect(hero, SerialUseTracker.class, 1f);
        }

        Sample.INSTANCE.play(Assets.Sounds.CHARGEUP);
        hero.sprite.zap(target, new Callback() {
            @Override
            public void call() {
                hero.sprite.idle();
                hero.spendAndNext(1);
            }
        });
        armor.charge -= chargeUse(hero);
        Item.updateQuickslot();
        Invisibility.dispel();
    }

    @Override
    public Talent[] talents() {
        return new Talent[]{Talent.MIKA_ARMOR2_1, Talent.MIKA_ARMOR2_2, Talent.MIKA_ARMOR2_3, Talent.HEROIC_ENERGY};
    }

    public static class Noise extends Buff {
        int duration;
        int pos;
        int left;

        public void set(int pos) {
            this.duration = 10;
            this.pos = pos;
            this.left = 0;
        }

        @Override
        public boolean act() {
            spend(TICK);
            left--;
            duration--;

            if (left <= 0){
                CellEmitter.center( pos ).start( Speck.factory( Speck.SCREAM ), 0.3f, 3 );
                Sample.INSTANCE.play( Assets.Sounds.ALERT );

                for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
                    mob.beckon( pos );
                }
                left = 5-Dungeon.hero.pointsInTalent(Talent.MIKA_ARMOR2_2);
            }
            if (duration <= 0) detach();
            return true;
        }
    }

    public static class SerialUseTracker extends FlavourBuff {}
}

package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.mari;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Amok;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MindVision;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.miracles.HeartOverflow;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

public class IdolConcert extends ArmorAbility {
    {
        baseChargeUse = 50f;
    }

    @Override
    public int icon() {
        return HeroIcon.MARI_3;
    }

    @Override
    protected void activate(ClassArmor armor, Hero hero, Integer target) {
        hero.sprite.operate(hero.pos, new Callback() {
            @Override
            public void call() {
                hero.sprite.idle();
            }
        });
        concert(hero);
        armor.charge -= chargeUse(hero);
        armor.updateQuickslot();
    }

    @Override
    public Talent[] talents() {
        return new Talent[]{Talent.MARI_ARMOR3_1, Talent.MARI_ARMOR3_2, Talent.MARI_ARMOR3_3, Talent.HEROIC_ENERGY};
    }

    public static void concert(Hero hero) {
        Buff.affect(hero, ConcertTimeStasis.class, 50);
        Buff.affect(hero, ConcertBuff.class);
        Buff.affect(hero, MindVision.class, 50f);

        float beat = 0.3f;

        Sample.INSTANCE.play(Assets.Sounds.LULLABY, 1f, 1.1f);
        //background beat
//        Sample.INSTANCE.play(Assets.Sounds.BLAST, 0.6f, 0.6f);
//        Sample.INSTANCE.playDelayed(Assets.Sounds.BLAST, beat, 0.5f, 0.8f);
//        Sample.INSTANCE.playDelayed(Assets.Sounds.BLAST, beat*2.5f, 0.5f, 0.6f);
//        Sample.INSTANCE.playDelayed(Assets.Sounds.BLAST, beat*3f, 0.5f, 0.8f);
    }

    public static class ConcertTimeStasis extends FlavourBuff {
        {
            type = Buff.buffType.POSITIVE;
            actPriority = BUFF_PRIO-3; //acts after all other buffs, so they are prevented
        }

        @Override
        public boolean attachTo(Char target) {

            if (super.attachTo(target)) {

                target.invisible++;
                target.paralysed++;
                target.next();

                if (Dungeon.hero != null) {
                    Dungeon.observe();
                }

                return true;
            } else {
                return false;
            }
        }

        @Override
        public void detach() {
            super.detach();
            if (target.invisible > 0) target.invisible--;
            if (target.paralysed > 0) target.paralysed--;
            if (target.buff(ConcertBuff.class) != null) {
                target.buff(ConcertBuff.class).detach();
            }
            if (Dungeon.hero.hasTalent(Talent.MARI_ARMOR3_1)) {
                Buff.affect(target, Encore.class, Encore.DURATION);
            }
            if (target.buff(HeartOverflow.HeartOverflowBuff.class) != null) {
                target.buff(HeartOverflow.HeartOverflowBuff.class).detach();
            }
            Dungeon.observe();
        }

        @Override
        public void fx(boolean on) {
            if (!on) if (target.invisible == 0) target.sprite.remove( CharSprite.State.INVISIBLE );
        }
    }

    public static class ConcertBuff extends Buff {
        {
            type = Buff.buffType.POSITIVE;
            actPriority = BUFF_PRIO-4;
        }

        @Override
        public boolean act() {
            spend(5);
            target.sprite.centerEmitter().burst( Speck.factory( Speck.NOTE ), 1 );
            target.sprite.zap(target.pos);
            target.sprite.centerEmitter().start( Speck.factory( Speck.SCREAM ), 0.3f, 3 );
            for (Char ch : Actor.chars()) {
                if (ch.alignment == Dungeon.hero.alignment && ch != Dungeon.hero) {
                    ch.heal(5);
                }
                if (ch instanceof Mob && ch.alignment == Char.Alignment.ENEMY) {
                    Buff.affect(ch, Amok.class, 5f);
                    Sample.INSTANCE.playDelayed(Assets.Sounds.FALLING, Random.Float(0.3f),  0.4f, Random.Float(2f, 4f));
                    ((Mob) ch).beckon(target.pos);
                }
            }
            return true;
        }
    }

    public static class Encore extends FlavourBuff {

        public static final float DURATION = 5f;

        @Override
        public int icon() {
            return BuffIndicator.ENCORE;
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0, visualcooldown()/DURATION);
        }
    }
}

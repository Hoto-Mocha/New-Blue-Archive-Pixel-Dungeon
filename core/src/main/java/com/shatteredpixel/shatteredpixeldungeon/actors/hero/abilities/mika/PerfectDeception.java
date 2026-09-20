package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.mika;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Amok;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ChampionEnemy;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Doom;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

public class PerfectDeception extends ArmorAbility {
    {
        baseChargeUse = 50f;
    }

    @Override
    public int icon() {
        return super.icon();
    }

    @Override
    public String targetingPrompt() {
        if (Dungeon.hero != null && Dungeon.hero.buff(DeceptionBuff.class) != null && Dungeon.hero.hasTalent(Talent.MIKA_ARMOR3_3)) return Messages.get(this, "prompt");
        else return null;
    }

    @Override
    public float chargeUse(Hero hero) {
        float chargeUse = super.chargeUse(hero);

        if (hero.buff(DeceptionBuff.class) != null && hero.hasTalent(Talent.MIKA_ARMOR3_3)) {
            chargeUse *= (float) Math.pow(0.9, hero.pointsInTalent(Talent.MIKA_ARMOR3_3));
        }

        return chargeUse;
    }

    @Override
    protected void activate(ClassArmor armor, Hero hero, Integer target) {
        if (hero.buff(DeceptionBuff.class) != null && hero.hasTalent(Talent.MIKA_ARMOR3_3)) {
            if (target == null) return;
            Char ch = Actor.findChar(target);
            if (!Dungeon.level.heroFOV[target]) {
                hero.yellW("out_of_sight");
                return;
            }
            if (ch == null) {
                hero.yellW("no_char");
                return;
            }
            if (ch.alignment != Char.Alignment.ENEMY) {
                hero.yellW("no_enemy");
                return;
            }

            hero.sprite.zap(target, new Callback() {
                @Override
                public void call() {
                    hero.sprite.idle();
                    hero.spendAndNext(1);
                }
            });

            ch.sprite.emitter().start(ShadowParticle.UP, 0.05f, 10);
            Buff.affect(ch, Doom.class);
            Sample.INSTANCE.play(Assets.Sounds.BURNING);

        } else {
            if (hero.buff(DeceptionBuff.class) != null) {
                hero.yellW("already_have");
                return;
            }

            Sample.INSTANCE.play(Assets.Sounds.MISS);
            hero.sprite.operate(hero.pos);
            hero.spendAndNext(1);

            if (Random.Float() < hero.pointsInTalent(Talent.MIKA_ARMOR3_1)) {
                Buff.affect(hero, Invisibility.class, 1f);
                Sample.INSTANCE.play(Assets.Sounds.MELD);
            }

            Buff.affect(hero, DeceptionBuff.class, DeceptionBuff.DURATION);
        }

        armor.charge -= chargeUse(hero);
        Item.updateQuickslot();
    }

    @Override
    public Talent[] talents() {
        return new Talent[]{Talent.MIKA_ARMOR3_1, Talent.MIKA_ARMOR3_2, Talent.MIKA_ARMOR3_3, Talent.HEROIC_ENERGY};
    }

    public static class DeceptionBuff extends FlavourBuff {
        {
            type = buffType.POSITIVE;
        }

        public static final float DURATION = 100f;

        @Override
        public int icon() {
            return BuffIndicator.IMBUE;
        }

        @Override
        public void tintIcon(Image icon) {
            icon.hardlight(0xF284F1);
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0, (DURATION-visualcooldown())/DURATION);
        }

        public int onAttack(int damage, Char defender) {
            detach();

            if (Dungeon.hero.hasTalent(Talent.MIKA_ARMOR3_2)) {
                for (Char ch : Actor.chars()) {
                    if (ch.alignment == Char.Alignment.ENEMY && Dungeon.level.heroFOV[ch.pos]) {
                        Buff.affect(ch, Amok.class, 2f*Dungeon.hero.pointsInTalent(Talent.MIKA_ARMOR3_2));
                    }
                }
            }

            if (!(defender instanceof Mob && ((Mob) defender).surprisedBy(Dungeon.hero))) return damage;
            if (Char.hasProp(defender, Char.Property.BOSS)
                    || Char.hasProp(defender, Char.Property.MINIBOSS)
                    || defender.buff(ChampionEnemy.class) != null) {
                return damage;
            } else {
                defender.sprite.emitter().start(ShadowParticle.UP, 0.05f, 10);
                return Math.max(damage, defender.HP);
            }
        }
    }
}

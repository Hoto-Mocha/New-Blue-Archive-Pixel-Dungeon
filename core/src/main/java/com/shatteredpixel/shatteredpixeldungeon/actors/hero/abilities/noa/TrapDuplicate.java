package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.noa;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.LightSmokeParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRecharging;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.ReclaimTrap;
import com.shatteredpixel.shatteredpixeldungeon.journal.Bestiary;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.ArrayList;

public class TrapDuplicate extends ArmorAbility {

    {
        baseChargeUse = 35f;
    }

    @Override
    public float chargeUse(Hero hero) {
        float chargeUse = super.chargeUse(hero);
        if (hero.buff(ReclaimedTrap.class) != null) return 0;
        return chargeUse;
    }

    @Override
    public int icon() {
        return HeroIcon.NOA_1;
    }

    @Override
    public String targetingPrompt() {
        return Messages.get(this, "prompt");
    }

    @Override
    public int targetedPos(Char user, int dst) {
        return dst;
    }

    @Override
    protected void activate(ClassArmor armor, Hero hero, Integer target) {
        if (target == null) return;
        if (!Dungeon.level.heroFOV[target]) {
            GLog.w(Messages.get(this, "out_of_view"));
            return;
        }
        Class<?extends Trap> storedTrap = null;
        //pre-v3.0.0
        if (hero.buff(ReclaimedTrap.class) != null){
            storedTrap = hero.buff(ReclaimedTrap.class).trap;

            if (hero.buff(ReclaimedTrap.class) != null) {
                if (Random.Float() < 1f-0.1f*hero.pointsInTalent(Talent.NOA_ARMOR1_3) || target == hero.pos) {
                    hero.buff(ReclaimedTrap.class).detach();
                } else {
                    GLog.p(Messages.get(this, "trap_preserved"));
                    ScrollOfRecharging.charge(hero);
                }
            }
        }

        if (storedTrap == null) {
            Trap t = Dungeon.level.traps.get(target);
            if (t != null && t.active && t.visible) {
                t.disarm(); //even disarms traps that normally wouldn't be

                if (hero.hasTalent(Talent.NOA_ARMOR1_1)) {
                    int num = 2*hero.pointsInTalent(Talent.NOA_ARMOR1_1);
                    ArrayList<Trap> candidates = new ArrayList<>();
                    for (int i : PathFinder.NEIGHBOURS8) {
                        int c = target + i;
                        Trap otherTrap = Dungeon.level.traps.get(c);
                        if (otherTrap != null) {
                            candidates.add(otherTrap);
                        }
                    }
                    Random.shuffle(candidates);
                    for (int i = 0; i < Math.min(candidates.size(), num); i++) {
                        candidates.get(i).disarm();
                    }
                }

                hero.sprite.zap(target);
                armor.charge -= chargeUse(hero);
                armor.updateQuickslot();

                Sample.INSTANCE.play(Assets.Sounds.LIGHTNING);
                ScrollOfRecharging.charge(hero);
                Buff.affect(hero, ReclaimedTrap.class).trap = t.getClass();
                Bestiary.setSeen(t.getClass());

            } else {
                GLog.w(Messages.get(this, "no_trap"));
            }

        } else {
            if (target == hero.pos) {
                GLog.i(Messages.get(this, "trap_removed"));
                hero.sprite.operate(hero.pos);
                Sample.INSTANCE.play(Assets.Sounds.UNLOCK);
                CellEmitter.get(hero.pos).burst(LightSmokeParticle.FACTORY, 3);
            } else {
                Trap t = Reflection.newInstance(storedTrap);

                t.pos = target;
                t.reclaimed = true;
                Bestiary.countEncounter(t.getClass());
                t.activate();

                if (hero.hasTalent(Talent.NOA_ARMOR1_2)) new TrapBomb().explode(target);

                Invisibility.dispel();
                hero.sprite.zap(target);
                hero.spendAndNext( 1f );
            }
        }
    }

    @Override
    public Talent[] talents() {
        return new Talent[]{Talent.NOA_ARMOR1_1, Talent.NOA_ARMOR1_2, Talent.NOA_ARMOR1_3, Talent.HEROIC_ENERGY};
    }

    public static class ReclaimedTrap extends Buff {

        {
            revivePersists = true;
        }

        private Class<?extends Trap> trap;

        private static final String TRAP = "trap";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(TRAP, trap);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            trap = bundle.getClass(TRAP);
        }
    }

    public static class TrapBomb extends Bomb.ConjuredBomb {
        {
            Hero hero = Dungeon.hero;
            if (hero != null) {
                minDamage = 2*(1+hero.pointsInTalent(Talent.NOA_ARMOR1_2));
                maxDamage = 12*hero.pointsInTalent(Talent.NOA_ARMOR1_2);
            }
        }
    }
}

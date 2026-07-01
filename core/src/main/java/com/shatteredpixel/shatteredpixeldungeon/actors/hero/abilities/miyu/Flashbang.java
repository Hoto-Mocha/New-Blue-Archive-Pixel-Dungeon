package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.miyu;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Daze;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vertigo;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.FlashBangParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SparkParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.active.Grenade;
import com.shatteredpixel.shatteredpixeldungeon.items.active.HandGrenade;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.BArray;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class Flashbang extends ArmorAbility {
    {
        baseChargeUse = 25f;
    }

    @Override
    public int icon() {
        return HeroIcon.MIYU_1;
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

        new FlashBangGrenade().knockItem().cast(hero, target);

        //섬광탄이 터질 때 작동하므로 불필요
        //Invisibility.dispel();
        armor.charge -= chargeUse(hero);
        armor.updateQuickslot();
    }

    public static class FlashBangGrenade extends Grenade {
        @Override
        public int explodeMinDmg() {
            return 0;
        }

        @Override
        public int explodeMaxDmg() {
            return 0;
        }

        @Override
        public void explode(int cell) {
            Hero hero = Dungeon.hero;
            ArrayList<Char> affected = new ArrayList<>();

            //5x5 Area
            PathFinder.buildDistanceMap( cell, BArray.not( Dungeon.level.solid, null ), 2 );
            for (int i = 0; i < PathFinder.distance.length; i++) {
                if (PathFinder.distance[i] < Integer.MAX_VALUE) {
                    CellEmitter.center(i).burst(FlashBangParticle.FACTORY, Random.IntRange(1, 3));
                    if (Actor.findChar(i) != null) {
                        affected.add(Actor.findChar(i));
                    }
                }
            }
            for (Char c : affected) {
                Buff.affect(c, Blindness.class, 10f);
                if (Random.Float() < 0.25f*hero.pointsInTalent(Talent.MIYU_ARMOR1_1)) {
                    Buff.affect(c, Vertigo.class, 10f);
                    Buff.affect(c, Daze.class, 10f);
                }
            }

            if (hero.hasTalent(Talent.MIYU_ARMOR1_3) && !affected.isEmpty()) {
                Buff.affect(hero, Barrier.class).setShield(3*Math.min(affected.size(), 10)*hero.pointsInTalent(Talent.MIYU_ARMOR1_3));
            }

            //3x3 Area
            if (Random.Float() < 0.25f*hero.pointsInTalent(Talent.MIYU_ARMOR1_2)) {
                affected.clear();
                PathFinder.buildDistanceMap( cell, BArray.not( Dungeon.level.solid, null ), 1 );
                for (int i = 0; i < PathFinder.distance.length; i++) {
                    if (PathFinder.distance[i] < Integer.MAX_VALUE) {
                        CellEmitter.center(i).burst(FlashBangParticle.FACTORY, 10);
                        if (Actor.findChar(i) != null) {
                            affected.add(Actor.findChar(i));
                        }
                    }
                }
                for (Char c : affected) {
                    Buff.affect(c, Paralysis.class, 5f);
                }
            }

            Sample.INSTANCE.play(Assets.Sounds.BLAST);
            GameScene.flash(0x60FFFFFF);
        }

        @Override
        public Grenade.Boomer knockItem(){
            return new FlashBangGrenadeBoomer();
        }

        public class FlashBangGrenadeBoomer extends Boomer {

            {
                image = ItemSpriteSheet.FLASHBANG_GRENADE;
            }

            //needs to be overridden
            @Override
            protected void activate(int cell) {
                super.activate(cell);
                explode(cell);
            }
        }
    }

    @Override
    public Talent[] talents() {
        return new Talent[]{Talent.MIYU_ARMOR1_1, Talent.MIYU_ARMOR1_2, Talent.MIYU_ARMOR1_3, Talent.HEROIC_ENERGY};
    }
}

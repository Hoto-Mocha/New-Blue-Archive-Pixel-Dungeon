package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.kurumi;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ArmorBreak;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Daze;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.GreaterHaste;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vulnerable;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.warrior.HeroicLeap;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.AfterImageParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.active.IronHorus;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Door;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.HighGrass;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.BArray;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.Objects;

public class LineClear extends ArmorAbility {
    {
        baseChargeUse = 35f;
    }

    @Override
    public int icon() {
        return HeroIcon.KURUMI_2;
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
    protected void activate(ClassArmor armor, Hero hero, Integer target) {
        if (target == null || target == hero.pos) return;

        if (hero.rooted) {
            PixelScene.shake( 1, 1f );
            hero.yellW("rooted");
            return;
        }

        int distance;
        distance = 3;
        if (Dungeon.level.distance(hero.pos, target) > distance) {
            hero.yellW("bad_location");
            return;
        }
        if (!Dungeon.level.passable[target]) {
            hero.yellW("bad_location");
            return;
        }

        Ballistica route = new Ballistica(hero.pos, target, Ballistica.STOP_TARGET | Ballistica.STOP_SOLID);
        if (!Objects.equals(route.collisionPos, target)) {
            hero.yellW("bad_location");
            return;
        }

        for (int pathCell : route.subPath(0, route.dist)) {
            if (Dungeon.level.map[pathCell] == Terrain.DOOR) {
                Door.enter(pathCell);
            }
            if (Dungeon.level.map[pathCell] == Terrain.HIGH_GRASS || Dungeon.level.map[pathCell] == Terrain.FURROWED_GRASS) {
                HighGrass.trample(Dungeon.level, pathCell);
            }
        }

        int cell = route.collisionPos;

        //can't occupy the same cell as another char, so move back one.
        int backTrace = route.dist-1;
        while (Actor.findChar( cell ) != null && cell != hero.pos) {
            cell = route.path.get(backTrace);
            backTrace--;
        }

        armor.charge -= chargeUse( hero );
        armor.updateQuickslot();

        Dungeon.hero.busy();
        final int dest = cell;
        Emitter emitter = GameScene.floorEmitter();
        if (emitter != null) {
            emitter.pos(hero.sprite);
            emitter.start(
                    AfterImageParticle.rainbowFactory(),
                    0.005f,
                    Math.round(5 * (Dungeon.level.trueDistance(hero.pos, target)+2))
            );
        }
        Dungeon.hero.sprite.jump(Dungeon.hero.pos, cell, 0, 0.05f, new Callback() {
            @Override
            public void call() {
                hero.move(dest);
                Dungeon.level.occupyCell(hero);
                Dungeon.observe();
                GameScene.updateFog();

                for (int i : PathFinder.NEIGHBOURS8) {
                    Char mob = Actor.findChar(hero.pos + i);
                    if (mob != null && mob != hero && mob.alignment != Char.Alignment.ALLY) {
                        if (hero.hasTalent(Talent.KURUMI_ARMOR2_2)) {
                            int damage = Hero.heroDamageIntRange(0, 4*hero.pointsInTalent(Talent.KURUMI_ARMOR2_2));
                            damage += Math.round(hero.drRoll()*0.25f*hero.pointsInTalent(Talent.KURUMI_ARMOR2_2));
                            damage -= mob.drRoll();
                            mob.damage(damage, hero);
                        }
                        if (mob.pos == hero.pos + i){
                            Ballistica trajectory = new Ballistica(mob.pos, mob.pos + i, Ballistica.MAGIC_BOLT);
                            int strength = 1;
                            WandOfBlastWave.throwChar(mob, trajectory, strength, true, true, LineClear.this);
                            Buff.prolong(mob, Blindness.class, 3f*(1+hero.pointsInTalent(Talent.KURUMI_ARMOR2_1)));
                            Buff.prolong(mob, Paralysis.class, 1f*(1+hero.pointsInTalent(Talent.KURUMI_ARMOR2_1)));
                        }
                    }
                }

                WandOfBlastWave.BlastWave.blast(dest);
                PixelScene.shake(2, 0.5f);

                Invisibility.dispel();
                if (hero.hasTalent(Talent.KURUMI_ARMOR2_3)) {
                    if (Random.Float() < 0.5f * hero.pointsInTalent(Talent.KURUMI_ARMOR2_3)) {
                        hero.next();
                    } else {
                        hero.spendAndNext(Actor.TICK);
                    }
                    if (hero.pointsInTalent(Talent.KURUMI_ARMOR2_3) > 2) {
                        Buff.affect(hero, GreaterHaste.class).set(hero.pointsInTalent(Talent.KURUMI_ARMOR2_3)-2);
                    }
                } else {
                    hero.spendAndNext(Actor.TICK);
                }
            }
        });
    }

    @Override
    public Talent[] talents() {
        return new Talent[]{Talent.KURUMI_ARMOR2_1, Talent.KURUMI_ARMOR2_2, Talent.KURUMI_ARMOR2_3, Talent.HEROIC_ENERGY};
    }
}

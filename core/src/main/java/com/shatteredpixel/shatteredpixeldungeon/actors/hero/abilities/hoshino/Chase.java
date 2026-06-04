package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.hoshino;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.duelist.Challenge;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.rogue.DeathMark;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Brute;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Door;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class Chase extends ArmorAbility {
    {
        baseChargeUse = 35f;
    }

    @Override
    public int icon() {
        return HeroIcon.HOSHINO_1;
    }

    public boolean useTargeting(){
        return true;
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
    public float chargeUse( Hero hero ) {
        float chargeUse = super.chargeUse(hero);
        if (hero.buff(ChaseKillTracker.class) != null){
            //reduced charge use by 16%/30%/41%/50%
            chargeUse *= Math.pow(0.84, hero.pointsInTalent(Talent.HOSHINO_ARMOR1_3));
        }
        return chargeUse;
    }

    @Override
    protected void activate(ClassArmor armor, Hero hero, Integer target) {
        if (target == null){
            return;
        }

        if (target == hero.pos) {
            GLog.w(Messages.get(ArmorAbility.class, "self_target"));
            return;
        }

        if (Actor.findChar(target) == null) {
            GLog.w(Messages.get(ArmorAbility.class, "no_target"));
            return;
        }

        int distance = 2+hero.pointsInTalent(Talent.HOSHINO_ARMOR1_1);
        if (Dungeon.level.distance(hero.pos, target) > distance) {
            GLog.w(Messages.get(this, "too_far"));
            return;
        }

        hero.busy();
        Sample.INSTANCE.play(Assets.Sounds.MISS);
        Ballistica path = new Ballistica(hero.pos, target, Ballistica.PROJECTILE);
        Char enemy = Actor.findChar(target);
        hero.sprite.jump(hero.pos, target, 3, 0.1f, new Callback() {
            @Override
            public void call() {
                if (Dungeon.level.map[hero.pos] == Terrain.OPEN_DOOR) {
                    Door.leave( hero.pos );
                }

                if (enemy != null) {
                    if (enemy.HP <= enemy.HT/4 + 5*hero.pointsInTalent(Talent.HOSHINO_ARMOR1_1)) {
                        enemy.HP = 0;
                        if (enemy.buff(Brute.BruteRage.class) != null){
                            enemy.buff(Brute.BruteRage.class).detach();
                        }
                        if (!enemy.isAlive()) {
                            enemy.die(Chase.this);
                        } else {
                            //helps with triggering any on-damage effects that need to activate
                            enemy.damage(-1, Chase.this);
                            DeathMark.processFearTheReaper(enemy);
                        }
                        if (enemy.sprite != null) {
                            enemy.sprite.showStatus(CharSprite.NEGATIVE, Messages.get(Talent.CombinedLethalityAbilityTracker.class, "executed"));
                        }
                    } else {
                        enemy.sprite.flash();
                        enemy.damage(20, Chase.this);
                    }

                    if (enemy.isAlive()) {
                        ArrayList<Integer> candidates = new ArrayList<>();
                        for (int n : PathFinder.NEIGHBOURS8){
                            if (Dungeon.level.passable[target+n]){
                                candidates.add(target+n);
                            }
                        }
                        Actor.add(new Pushing(enemy, target, Random.element(candidates)));

                        if (hero.hasTalent(Talent.HOSHINO_ARMOR1_2)) {
                            Ballistica aim = new Ballistica(hero.pos, target, Ballistica.MAGIC_BOLT);

                            //do not push chars that are dieing over a pit, or that move due to the damage
                            if ((enemy.flying || !Dungeon.level.pit[enemy.pos])
                                    && aim.path.size() > aim.dist+1 && enemy.pos == aim.collisionPos) {
                                Ballistica trajectory = new Ballistica(enemy.pos, aim.path.get(aim.dist + 1), Ballistica.MAGIC_BOLT);
                                int strength = hero.pointsInTalent(Talent.HOSHINO_ARMOR1_2)*2;
                                WandOfBlastWave.throwChar(enemy, trajectory, strength, false, true, this);
                                Buff.affect(hero, Barrier.class).setShield(5*hero.pointsInTalent(Talent.HOSHINO_ARMOR1_2));
                            }
                        }
                    }

                    if (!enemy.isAlive()) {
                        Buff.affect(hero, ChaseKillTracker.class);
                    }
                }
                Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG);
                Sample.INSTANCE.play(Assets.Sounds.HIT_SLASH);
                hero.pos = target;
                hero.sprite.zap(target);
                Dungeon.level.occupyCell(hero);
                armor.charge -= chargeUse( hero );
                armor.updateQuickslot();
                Invisibility.dispel();
                hero.spendAndNext(1f);
            }
        });

        if (hero.buff(ChaseKillTracker.class) != null) {
            hero.buff(ChaseKillTracker.class).detach();
        }
    }

    @Override
    public Talent[] talents() {
        return new Talent[]{Talent.HOSHINO_ARMOR1_1, Talent.HOSHINO_ARMOR1_2, Talent.HOSHINO_ARMOR1_3, Talent.HEROIC_ENERGY};
    }

    public static class ChaseKillTracker extends Buff {}
}

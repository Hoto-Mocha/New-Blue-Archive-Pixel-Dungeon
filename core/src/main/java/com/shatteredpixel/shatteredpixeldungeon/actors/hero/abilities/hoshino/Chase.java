package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.hoshino;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.rogue.DeathMark;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Brute;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Door;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
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
        hero.sprite.jump(hero.pos, target, 5, 0.3f, new Callback() {
            @Override
            public void call() {
                if (Dungeon.level.map[hero.pos] == Terrain.OPEN_DOOR) {
                    Door.leave( hero.pos );
                }
                hero.sprite.zap(target);
                if (enemy != null) {
                    if (enemy.HP <= enemy.HT/4 + 5*hero.pointsInTalent(Talent.HOSHINO_ARMOR1_1)) {
                        enemy.HP = 0;
                        if (enemy.buff(Brute.BruteRage.class) != null){
                            enemy.buff(Brute.BruteRage.class).detach();
                        }
                        if (!enemy.isAlive()) {
                            enemy.die(this);
                        } else {
                            //helps with triggering any on-damage effects that need to activate
                            enemy.damage(-1, this);
                            DeathMark.processFearTheReaper(enemy);
                        }
                        if (enemy.sprite != null) {
                            enemy.sprite.showStatus(CharSprite.NEGATIVE, Messages.get(Talent.CombinedLethalityAbilityTracker.class, "executed"));
                        }
                    } else {
                        Sample.INSTANCE.play(Assets.Sounds.HIT);
                        enemy.sprite.flash();
                        enemy.damage(20, hero);
                    }

                    if (enemy.isAlive()) {
                        ArrayList<Integer> candidates = new ArrayList<>();
                        for (int n : PathFinder.NEIGHBOURS8){
                            if (Dungeon.level.passable[target+n]){
                                candidates.add(target+n);
                            }
                        }
                        Actor.add(new Pushing(hero, target, Random.element(candidates)));
                    }
                }
                hero.pos = target;
                Dungeon.level.occupyCell(hero);
                Invisibility.dispel();
                hero.spendAndNext(1f);
            }
        });

    }

    @Override
    public Talent[] talents() {
        return new Talent[]{Talent.HOSHINO_ARMOR1_1, Talent.HOSHINO_ARMOR1_2, Talent.HOSHINO_ARMOR1_3, Talent.HEROIC_ENERGY};
    }
}

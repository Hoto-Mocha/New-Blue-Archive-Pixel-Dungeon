package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.miyako;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Roots;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.effects.Chains;
import com.shatteredpixel.shatteredpixeldungeon.effects.Effects;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.levels.MiningLevel;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.BArray;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;

public class WireHook extends ArmorAbility {
    {
        baseChargeUse = 25f;
    }

    @Override
    protected void activate(ClassArmor armor, Hero hero, Integer target) {
        if (target != null && (Dungeon.level.visited[target] || Dungeon.level.mapped[target])){
            //chains cannot be used to go where it is impossible to walk to
            PathFinder.buildDistanceMap(target, BArray.or(Dungeon.level.passable, Dungeon.level.avoid, null));
            if (!(Dungeon.level instanceof MiningLevel) && PathFinder.distance[hero.pos] == Integer.MAX_VALUE){
                GLog.w( Messages.get(this, "cant_reach") );
                return;
            }

            int ballistica = Ballistica.PROJECTILE;

            final Ballistica chain = new Ballistica(hero.pos, target, ballistica);

            Char ch = Actor.findChar( chain.collisionPos );
            if (ch != null){
                if (chainEnemy( chain, hero, ch )) {
                    armor.charge -= chargeUse(hero);
                    armor.updateQuickslot();
                    Invisibility.dispel();

                    hero.sprite.zap(target);
                } else {
                    GLog.w( Messages.get(this, "failed") );
                }
            } else {
                GLog.w( Messages.get(this, "no_char") );
            }
        }
    }

    @Override
    public int icon() {
        return HeroIcon.MIYAKO_1;
    }

    @Override
    public String targetingPrompt() {
        return Messages.get(this, "prompt");
    }

    @Override
    public float chargeUse( Hero hero ) {
        float chargeUse = super.chargeUse(hero);
        if (hero.buff(ContinuousChainTracker.class) != null){
            //reduced charge use by 16%/30%/41%/50%
            chargeUse *= (float) Math.pow(0.9f, hero.pointsInTalent(Talent.MIYAKO_ARMOR1_2));
        }
        return chargeUse;
    }

    @Override
    public Talent[] talents() {
        return new Talent[]{Talent.MIYAKO_ARMOR1_1, Talent.MIYAKO_ARMOR1_2, Talent.MIYAKO_ARMOR1_3, Talent.HEROIC_ENERGY};
    }

    //returns true when pulling is successfully performed
    private boolean chainEnemy( Ballistica chain, final Hero hero, final Char enemy ){
        if (enemy.properties().contains(Char.Property.IMMOVABLE)) {
            GLog.w( Messages.get(this, "cant_pull") );
            return false;
        }

        int bestPos = -1;
        for (int i : chain.subPath(1, chain.dist)){
            //prefer to the earliest point on the path
            if (!Dungeon.level.solid[i]
                    && Actor.findChar(i) == null
                    && (!Char.hasProp(enemy, Char.Property.LARGE) || Dungeon.level.openSpace[i])){
                bestPos = i;
                break;
            }
        }

        if (bestPos == -1) {
            GLog.i(Messages.get(this, "does_nothing"));
            return false;
        }

        Sample.INSTANCE.play(Assets.Sounds.CHAINS);

        hero.busy();
        Sample.INSTANCE.play(Assets.Sounds.MISS);
        final int pulledPos = bestPos;
        hero.sprite.parent.add(new Chains(hero.sprite.center(),
                enemy.sprite.center(),
                Effects.Type.CHAIN,
            new Callback() {
                public void call() {
                    Actor.add(new Pushing(enemy, enemy.pos, pulledPos, new Callback() {
                        public void call() {
                            enemy.pos = pulledPos;
                            Dungeon.level.occupyCell(enemy);
                            Dungeon.observe();
                            GameScene.updateFog();
                            hero.spendAndNext(1f);

                            Buff.affect(enemy, Cripple.class, 5f);

                            if (hero.hasTalent(Talent.MIYAKO_ARMOR1_1)) {
                                Buff.affect(enemy, Roots.class, hero.pointsInTalent(Talent.MIYAKO_ARMOR1_1));
                            }
                            if (hero.hasTalent(Talent.MIYAKO_ARMOR1_2)) {
                                if (hero.buff(ContinuousChainTracker.class) == null) {
                                    Buff.affect(hero, ContinuousChainTracker.class, ContinuousChainTracker.DURATION);
                                } else {
                                    hero.buff(ContinuousChainTracker.class).detach();
                                }
                            }
                            if (hero.hasTalent(Talent.MIYAKO_ARMOR1_3)) {
                                Buff.affect(hero, PointBlankShot.class, 4f*hero.pointsInTalent(Talent.MIYAKO_ARMOR1_3));
                            }
                        }
                    }));
                    hero.next();
                }
            })
        );
        return true;
    }

    public static class ContinuousChainTracker extends FlavourBuff {
        {
            type = buffType.NEUTRAL;
        }
        public static final float DURATION = 3f;
    }

    public static class PointBlankShot extends FlavourBuff {
        {
            type = buffType.POSITIVE;
            announced = true;
        }

        public static final float DURATION = 16f;

        @Override
        public int icon() {
            return BuffIndicator.INVERT_MARK;
        }

        @Override
        public void tintIcon(Image icon) {
            icon.hardlight(0x4D4D4D);
        }
    }
}

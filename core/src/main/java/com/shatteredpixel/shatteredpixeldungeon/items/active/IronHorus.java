package com.shatteredpixel.shatteredpixeldungeon.items.active;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ArmorBreak;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Roots;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vertigo;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.KindOfWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.Gun;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.HG.HG;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Door;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.HighGrass;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class IronHorus extends Item {

    {
        image = ItemSpriteSheet.IRON_HORUS;
        levelKnown = true;

        defaultAction = AC_USE;
        usesTargeting = false;

        bones = false;
        unique = true;
    }

    private static final String AC_USE = "USE";

    @Override
    public ArrayList<String> actions(Hero hero ) {
        ArrayList<String> actions = super.actions( hero );
        actions.add( AC_USE );
        return actions;
    }

    @Override
    public void execute( Hero hero, String action ) {
        super.execute( hero, action );
        if (action.equals(AC_USE)) {
            if (Dungeon.hero.buff(TacticalShieldCooldown.class) == null) {
                shieldUse(hero);
                curUser.sprite.operate(curUser.pos);
                Sample.INSTANCE.play(Assets.Sounds.UNLOCK);
                curUser.spendAndNext(Actor.TICK);
            } else {
                Dungeon.hero.yellN(Messages.get(Hero.class, hero.heroClass.name() + "_shield_cooldown"));
            }
        }
    }

    public void shieldUse(Hero hero) {
        if (hero.belongings.weapon() instanceof HG) {
            Buff.affect(hero, LightTacticalShieldBuff.class).set(this, (HG) hero.belongings.weapon());
        } else {
            Buff.affect(hero, TacticalShieldBuff.class).set(this, hero.belongings.weapon());
        }
    }

    @Override
    public int level() {
        return Dungeon.hero == null ? 0 : Dungeon.hero.lvl/5;
    }

    @Override
    public int buffedLvl() {
        //level isn't affected by buffs/debuffs
        return level();
    }

    public int drMin(int lvl) {
        return lvl + 1;
    }

    public int drMin() {
        return drMin(buffedLvl());
    }

    public int drMax(int lvl) {
        return 3 * (lvl + 1);
    }

    public int drMax() {
        return drMax(buffedLvl());
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    @Override
    public int value() {
        return -1;
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", drMin(), drMax());
    }

    public static boolean hasBuff(Hero hero) { //다른 버프를 가지고 있어도 같은 작업을 수행하기 위함
        return hero.buff(TacticalShieldBuff.class) != null || hero.buff(LightTacticalShieldBuff.class) != null;
    }

    public static void detachBuff(Hero hero) { //다른 버프를 가지고 있어도 같은 작업으로 제거하기 위함
        if (hero.buff(TacticalShieldBuff.class) != null) {
            hero.buff(TacticalShieldBuff.class).detach();
        }
        if (hero.buff(LightTacticalShieldBuff.class) != null) {
            hero.buff(LightTacticalShieldBuff.class).detach();
        }
    }

    public static int drRoll(Hero hero) {
        if (hasBuff(hero)) {
            if (hero.buff(TacticalShieldBuff.class) != null) {
                return hero.buff(TacticalShieldBuff.class).drRoll();
            } else {
                return hero.buff(LightTacticalShieldBuff.class).drRoll();
            }
        } else {
            return 0;
        }
    }

    public static class TacticalShieldBuff extends Buff {

        {
            type = buffType.NEUTRAL;
            announced = false;
        }

        public IronHorus ironHorus;
        public KindOfWeapon gun;
        public int pos = -1;

        @Override
        public boolean act() {
            if (target instanceof Hero) {
                ironHorus = ((Hero)target).belongings.getItem(IronHorus.class);
                if (gun == null || !gun.isSimilar(((Hero)target).belongings.weapon())) {
                    detach();
                }
            }
            if (ironHorus == null) {
                detach();
            }
            if (pos != target.pos) {
                detach();
            } else {
                spend(TICK);
            }
            return true;
        }

        public boolean attachTo(Char target ) {
            if (target instanceof Hero && ((Hero)target).subClass == HeroSubClass.SHIELD_BASH) {
                Buff.affect(target, ShieldBashBuff.class);
            }
            if (super.attachTo(target)) {
                pos = target.pos;
                return true;
            } else {
                return false;
            }
        }

        public void set(IronHorus ironHorus, KindOfWeapon gun) {
            this.ironHorus = ironHorus;
            this.gun = gun;
        }

        @Override
        public int icon() {
            return BuffIndicator.IRON_HORUS;
        }

        @Override
        public void detach() {
            super.detach();
            Buff.affect(target, TacticalShieldCooldown.class, TacticalShieldCooldown.DURATION);
        }

        public int drRoll(){
            if (pos == target.pos && target instanceof Hero){
                return Random.NormalIntRange(ironHorus.drMin(), ironHorus.drMax());
            } else {
                detach();
                return 0;
            }
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc", ironHorus.drMin(), ironHorus.drMax());
        }

        private static final String POS = "pos";
        private static final String IRON_HORUS = "ironHorus";
        private static final String GUN = "gun";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(POS, pos);
            bundle.put(IRON_HORUS, ironHorus);
            bundle.put(GUN, gun);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            pos = bundle.getInt(POS);
            ironHorus = (IronHorus)bundle.get(IRON_HORUS);
            gun = (KindOfWeapon) bundle.get(GUN);
        }
    }

    public static class LightTacticalShieldBuff extends Buff {
        {
            type = buffType.NEUTRAL;
            announced = false;
        }

        public Gun gun;
        public IronHorus ironHorus;

        @Override
        public boolean act() {
            if (target instanceof Hero) {
                ironHorus = ((Hero)target).belongings.getItem(IronHorus.class);
                if (gun == null || !gun.isSimilar(((Hero)target).belongings.weapon())) {
                    detach();
                }
            }
            if (ironHorus == null) {
                detach();
            } else {
                spend(TICK);
            }
            return true;
        }

        public void set(IronHorus ironHorus, HG hg) {
            this.ironHorus = ironHorus;
            this.gun = hg;
        }

        @Override
        public int icon() {
            return BuffIndicator.LIGHT_IRON_HORUS;
        }

        @Override
        public boolean attachTo(Char target) {
            if (target instanceof Hero && ((Hero)target).subClass == HeroSubClass.SHIELD_BASH) {
                Buff.affect(target, ShieldBashBuff.class);
            }
            return super.attachTo(target);
        }

        @Override
        public void detach() {
            super.detach();
            Buff.affect(target, TacticalShieldCooldown.class, TacticalShieldCooldown.DURATION);
        }

        public int drRoll(){
            if (target instanceof Hero) {
                return Random.NormalIntRange(ironHorus.drMin()/2, ironHorus.drMax()/2);
            } else {
                detach();
                return 0;
            }
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc", ironHorus.drMin()/2, ironHorus.drMax()/2);
        }

        private static final String IRON_HORUS = "ironHorus";
        private static final String GUN = "gun";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(IRON_HORUS, ironHorus);
            bundle.put(GUN, gun);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            ironHorus = (IronHorus)bundle.get(IRON_HORUS);
            gun = (Gun)bundle.get(GUN);
        }
    }

    public static class TacticalShieldCooldown extends FlavourBuff {

        public static final float DURATION = 10;

        {
            type = buffType.NEUTRAL;
            announced = false;
        }

        @Override
        public int icon() {
            return BuffIndicator.TIME;
        }

        @Override
        public void tintIcon(Image icon) {
            icon.hardlight(0xFF9AB0);
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0, (DURATION - visualcooldown()) / DURATION);
        }

        @Override
        public String toString() {
            return Messages.get(this, "name");
        }
    }

    public static class ShieldBashBuff extends Buff implements ActionIndicator.Action {

        @Override
        public String actionName() {
            return Messages.get(this, "action_name");
        }

        @Override
        public int actionIcon() {
            return HeroIcon.SHIELD_BASH;
        }

        @Override
        public int indicatorColor() {
            return 0xFFFFFF;
        }

        @Override
        public void doAction() {
            GameScene.selectCell(selector);
        }

        @Override
        public boolean attachTo(Char target) {
            ActionIndicator.setAction(this);
            return super.attachTo(target);
        }

        @Override
        public void detach() {
            ActionIndicator.clearAction();
            super.detach();
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);

            ActionIndicator.setAction(this);
        }

        private CellSelector.Listener selector = new CellSelector.Listener() {
            @Override
            public void onSelect( Integer target ) {
                if (Dungeon.hero.buff(TacticalShieldBuff.class) == null && Dungeon.hero.buff(LightTacticalShieldBuff.class) == null) {
                    detach();
                    return;
                }
                if (Dungeon.hero.buff(Roots.class) != null) {
                    GLog.w(Messages.get(ShieldBashBuff.class, "cannot_move"));
                    return;
                }
                int distance;
                int dr;
                if (Dungeon.hero.buff(TacticalShieldBuff.class) != null) {
                    distance = 1;
                    dr = Dungeon.hero.buff(TacticalShieldBuff.class).drRoll();
                } else {
                    distance = 3;
                    dr = Dungeon.hero.buff(LightTacticalShieldBuff.class).drRoll();
                }
                if (target != null) {
                    if (Dungeon.level.distance(Dungeon.hero.pos, target) > distance) {
                        GLog.w(Messages.get(ShieldBashBuff.class, "cannot_reach"));
                        return;
                    }

                    if (!Dungeon.level.passable[target]) {
                        GLog.w(Messages.get(ShieldBashBuff.class, "cannot_reach"));
                        return;
                    }

                    Ballistica path = new Ballistica(Dungeon.hero.pos, target, Ballistica.PROJECTILE | Ballistica.IGNORE_SOFT_SOLID);
                    if (!Objects.equals(path.collisionPos, target)) {
                        GLog.w(Messages.get(ShieldBashBuff.class, "cannot_reach"));
                        return;
                    }

                    for (int pathCell : path.subPath(0, path.dist)) {
                        if (Dungeon.level.map[pathCell] == Terrain.DOOR) {
                            Door.enter(pathCell);
                        }
                        if (Dungeon.level.map[pathCell] == Terrain.HIGH_GRASS || Dungeon.level.map[pathCell] == Terrain.FURROWED_GRASS) {
                            HighGrass.trample(Dungeon.level, pathCell);
                        }
                    }

                    Dungeon.hero.busy();
                    Sample.INSTANCE.play(Assets.Sounds.MISS);
                    Dungeon.hero.sprite.jump(Dungeon.hero.pos, target, 0, 0.05f, new Callback() {
                        @Override
                        public void call() {
                            Char ch = Actor.findChar(target);
                            if (ch != null) {
                                int prevPos = ch.pos;
                                Ballistica trajectory = new Ballistica(ch.pos, path.path.get(path.dist + 1), Ballistica.MAGIC_BOLT);
                                if ((trajectory.dist == 1 && Actor.findChar(trajectory.collisionPos) != null) || trajectory.collisionPos == prevPos) {
                                    ArrayList<Integer> candidates = new ArrayList<>();
                                    for (int n : PathFinder.NEIGHBOURS8) {
                                        if (Dungeon.level.passable[prevPos+n] && Actor.findChar( prevPos+n ) == null) {
                                            candidates.add( prevPos+n );
                                        }
                                    }
                                    trajectory = new Ballistica(ch.pos, Random.element( candidates ), Ballistica.MAGIC_BOLT);
                                }
                                WandOfBlastWave.throwChar(ch, trajectory, 5-distance, false, true, Dungeon.hero);
                                int damage = dr;
                                if (Dungeon.hero.hasTalent(Talent.HOSHINO_EX1_2)) {
                                    damage = Math.round(damage*(1+0.5f*Dungeon.hero.pointsInTalent(Talent.HOSHINO_EX1_2)));

                                    int selfDamage = Random.NormalIntRange(4*Dungeon.hero.pointsInTalent(Talent.HOSHINO_EX1_2), 10*Dungeon.hero.pointsInTalent(Talent.HOSHINO_EX1_2));
                                    selfDamage -= Dungeon.hero.drRoll();
                                    Dungeon.hero.damage(selfDamage, IronHorus.class);
                                }
                                ch.damage(damage, Dungeon.hero);

                                if (Dungeon.hero.hasTalent(Talent.HOSHINO_EX1_1)) {
                                    Buff.affect(ch, Vertigo.class, 2f*Dungeon.hero.pointsInTalent(Talent.HOSHINO_EX1_1));
                                    Buff.affect(ch, ArmorBreak.class).set(Dungeon.hero.pointsInTalent(Talent.HOSHINO_EX1_1));
                                }

                                if (Dungeon.hero.hasTalent(Talent.HOSHINO_EX1_3)) {
                                    Buff.prolong(Dungeon.hero, Talent.InstantFocusingTracker.class, Dungeon.hero.cooldown()+4f);
                                }

                                if (ch.sprite != null) {
                                    ch.sprite.flash();
                                }
                            }

                            if (Dungeon.level.map[Dungeon.hero.pos] == Terrain.OPEN_DOOR) {
                                Door.leave( Dungeon.hero.pos );
                            }
                            Dungeon.hero.pos = target;
                            Dungeon.level.occupyCell(Dungeon.hero);
                            Dungeon.hero.next();
                            Dungeon.observe();
                            GameScene.updateFog();

                            Sample.INSTANCE.play(Assets.Sounds.BLAST, 0.6f, 1.2f);
                            Sample.INSTANCE.play(Assets.Sounds.ROCKS, 0.1f, 1.8f);
                            WandOfBlastWave.BlastWave.blast(target);
                            CellEmitter.center( target ).burst( Speck.factory( Speck.STAR ), 7 );
                            CellEmitter.get( target ).burst( Speck.factory( Speck.FORGE ), 5 );
                            PixelScene.shake(0.5f, 0.5f);

                            if (Dungeon.hero.buff(TacticalShieldBuff.class) != null) {
                                Dungeon.hero.buff(TacticalShieldBuff.class).detach();
                            } else {
                                Dungeon.hero.buff(LightTacticalShieldBuff.class).detach();
                            }
                            detach();
                        }
                    });
                }
            }
            @Override
            public String prompt() {
                return Messages.get(SpiritBow.class, "prompt");
            }
        };
    }
}

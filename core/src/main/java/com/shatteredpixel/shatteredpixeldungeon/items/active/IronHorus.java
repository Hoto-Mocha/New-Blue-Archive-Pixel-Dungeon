package com.shatteredpixel.shatteredpixeldungeon.items.active;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.KindOfWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.Gun;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.HG.HG;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.ArrayList;

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
}

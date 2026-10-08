package com.shatteredpixel.shatteredpixeldungeon.actors.hero.miracles;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CrossNecklace;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.Gun;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;

import java.util.ArrayList;
import java.util.HashSet;

public abstract class MariMiracle {

    public static final HashSet<Class> MIRACLE_DAMAGE = new HashSet<>();
    static {
        MIRACLE_DAMAGE.add( PunishmentThunder.class );
        MIRACLE_DAMAGE.add( LightBullet.class );
    }

    public abstract void onCast(CrossNecklace cross, Hero hero);

    public float chargeUse( Hero hero ){
        return 1;
    }

    public boolean canCast( Hero hero ){
        return true;
    }

    public String name(){
        return Messages.get(this, "name");
    }

    public String shortDesc(){
        return Messages.get(this, "short_desc") + " " + Messages.get(this, "charge_cost", (int)chargeUse(Dungeon.hero));
    }

    public String desc(){
        return Messages.get(this, "desc") + "\n\n" + Messages.get(this, "charge_cost", (int)chargeUse(Dungeon.hero));
    }

    public boolean usesTargeting(){
        return false;
    }

    public int targetingFlags(){
        return -1; //-1 for no targeting
    }

    public int icon(){
        return HeroIcon.NONE;
    }

    public void onMiracleCast(CrossNecklace cross, Hero hero){
        Invisibility.dispel();

        if (hero.hasTalent(Talent.MARI_T1_1) && hero.buff(Talent.BlessOfPlentyTracker.class) != null){
            int amount = 1 + 2*hero.pointsInTalent(Talent.MARI_T1_1);
            Buff.affect(hero, Barrier.class).setShield(amount);
            hero.buff(Talent.BlessOfPlentyTracker.class).detach();
        }

        cross.spendCharge(chargeUse(hero));
        Talent.onArtifactUsed(hero);
    }

    public Gun getEquippedGun(Hero hero) {
        if (!(hero.belongings.weapon instanceof Gun)) {
            return null;
        } else {
            return (Gun) hero.belongings.weapon;
        }
    }

    public static ArrayList<MariMiracle> getMiracleList(Hero mari, int tier){
        ArrayList<MariMiracle> miracles = new ArrayList<>();

        if (tier == 1) {
            miracles.add(LightBullet.INSTANCE);
            miracles.add(Protection.INSTANCE);
            miracles.add(Pray.INSTANCE);

            if (mari.hasTalent(Talent.MARI_T1_2)) {
                miracles.add(UnholyDetection.INSTANCE);
            }

        } else if (tier == 2) {
            if (mari.hasTalent(Talent.MARI_T2_3)) {
                miracles.add(SilverBullet.INSTANCE);
            }

        } else if (tier == 3){


        } else if (tier == 4){


        }

        return miracles;
    }

    public static ArrayList<MariMiracle> getAllMiracles() {
        ArrayList<MariMiracle> miracles = new ArrayList<>();
        miracles.add(LightBullet.INSTANCE);
        miracles.add(Protection.INSTANCE);
        miracles.add(Pray.INSTANCE);
        miracles.add(UnholyDetection.INSTANCE);
        miracles.add(SilverBullet.INSTANCE);

        return miracles;
    }

    //this is in the miracle class, but it is used for Mari's passive ability
    public static class PunishmentThunder {

        public static int thunderDamage(Hero hero) {

            int damage = (int)Math.ceil(hero.lvl/5f);

            if (hero.buff(Protection.ProtectionBuff.class) != null) {
                damage += hero.buff(Protection.ProtectionBuff.class).thunderDamageBonus(hero);
            }

            return damage;
        }
    }

}

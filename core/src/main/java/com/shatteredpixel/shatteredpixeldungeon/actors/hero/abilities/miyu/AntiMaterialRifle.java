package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.miyu;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.Gun;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.SR.SR_SP;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class AntiMaterialRifle extends ArmorAbility {
    {
        baseChargeUse = 50f;
    }

    @Override
    public int icon() {
        return HeroIcon.MIYU_3;
    }

    @Override
    protected void activate(ClassArmor armor, Hero hero, Integer target) {
        if (target == null) return;
        if (!(hero.belongings.weapon() instanceof Gun)) {
            hero.yellW("need_gun");
            return;
        }

        if (hero.buff(GotRifleTracker.class) != null) {
            hero.yellW("already_got");
            return;
        }

        Gun gun = (Gun) hero.belongings.weapon();
        SR_SP sr = new SR_SP();
        sr.upgrade(gun.level());
        sr.enchantment          = gun.enchantment;
        sr.curseInfusionBonus   = gun.curseInfusionBonus;
        sr.masteryPotionBonus   = gun.masteryPotionBonus;
        sr.levelKnown           = gun.levelKnown;
        sr.cursedKnown          = gun.cursedKnown;
        sr.cursed               = gun.cursed;
        sr.augment              = gun.augment;
        sr.enchantHardened      = gun.enchantHardened;
        sr.keptThoughLostInvent = gun.keptThoughLostInvent;
        sr.barrelMod = gun.barrelMod;
        sr.magazineMod = gun.magazineMod;
        sr.bulletMod = gun.bulletMod;
        sr.weightMod = gun.weightMod;
        sr.attachMod = gun.attachMod;
        sr.enchantMod = gun.enchantMod;
        sr.inscribeMod = gun.inscribeMod;
        sr.set(gun);
        if (hero.hasTalent(Talent.MIYU_ARMOR3_3)) {
            switch (hero.pointsInTalent(Talent.MIYU_ARMOR3_3)) {
                case 1: default:
                    if (Random.Float() < 0.5f) {
                        sr.manualReload(1, true);
                        gun.quickReload();
                    }
                    break;
                case 2:
                    sr.manualReload(1, true);
                    gun.quickReload();
                    break;
                case 3:
                    sr.manualReload(1, true);
                    gun.quickReload();
                    if (Random.Float() < 0.5f) {
                        sr.manualReload(2, true);
                        gun.manualReload(1, true);
                    }
                    break;
                case 4:
                    sr.manualReload(2, true);
                    gun.quickReload();
                    gun.manualReload(2, true);
                    break;
            }
        }
        hero.belongings.weapon = sr;
        int slot = Dungeon.quickslot.getSlot(gun);
        if (slot != -1
                && sr.defaultAction() != null){
            Dungeon.quickslot.setSlot(slot, sr);
        }
        hero.yellI("switching_new");
        Item.updateQuickslot();

        Buff.affect(hero, GotRifleTracker.class);

        hero.sprite.operate(hero.pos);
        Sample.INSTANCE.play(Assets.Sounds.UNLOCK);

        Invisibility.dispel();
        armor.charge -= chargeUse(hero);
        armor.updateQuickslot();
    }

    @Override
    public Talent[] talents() {
        return new Talent[]{Talent.MIYU_ARMOR3_1, Talent.MIYU_ARMOR3_2, Talent.MIYU_ARMOR3_3, Talent.HEROIC_ENERGY};
    }

    public static class GotRifleTracker extends Buff {
        {revivePersists = true;}
    }

    public static class QuickSlotSet extends FlavourBuff {
        Gun gun;
        int slot = -1;

        public void set(Gun gun, int slot) {
            this.gun = gun;
            this.slot = slot;
        }

        @Override
        public void detach() {
            if (slot != -1) Dungeon.quickslot.setSlot(slot, gun);
            super.detach();
        }

        private static final String GUN = "gun";
        private static final String SLOT = "slot";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(GUN, gun);
            bundle.put(SLOT, slot);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            gun = (Gun) bundle.get(GUN);
            slot = bundle.getInt(SLOT);
        }
    }
}

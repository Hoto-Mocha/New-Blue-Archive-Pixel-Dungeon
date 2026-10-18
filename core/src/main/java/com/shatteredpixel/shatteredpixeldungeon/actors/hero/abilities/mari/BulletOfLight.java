package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.mari;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.miracles.LightBullet;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.Gun;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.watabou.utils.Callback;

public class BulletOfLight extends ArmorAbility {
    {
        baseChargeUse = 25f;
    }

    @Override
    public int icon() {
        return HeroIcon.MARI_1;
    }

    @Override
    protected void activate(ClassArmor armor, Hero hero, Integer target) {
        if (!(hero.belongings.weapon instanceof Gun)) {
            hero.yellW("need_gun");
            return;
        }

        Gun gun = (Gun) hero.belongings.weapon;

        if (gun.round() == gun.maxRound()) {
            hero.yellW("already_loaded");
            return;
        }

        hero.busy();
        hero.sprite.operate(hero.pos, new Callback() {
            @Override
            public void call() {
                hero.sprite.idle();
                hero.next();
            }
        });

        int bulletsLoaded = gun.maxRound() - gun.round();
        Buff.affect(hero, LightBullet.LightMagazine.class).stack(gun, bulletsLoaded);
        gun.quickReload();

        armor.charge -= chargeUse(hero);
        armor.updateQuickslot();
    }

    @Override
    public Talent[] talents() {
        return new Talent[]{Talent.MARI_ARMOR1_1, Talent.MARI_ARMOR1_2, Talent.MARI_ARMOR1_3, Talent.HEROIC_ENERGY};
    }
}

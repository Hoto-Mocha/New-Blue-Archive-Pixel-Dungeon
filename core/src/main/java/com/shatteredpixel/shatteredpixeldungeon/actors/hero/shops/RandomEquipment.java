package com.shatteredpixel.shatteredpixeldungeon.actors.hero.shops;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.watabou.utils.Random;

public class RandomEquipment extends YuzuShopContent {
    public static RandomEquipment INSTANCE = new RandomEquipment();

    @Override
    public int icon() {
        return HeroIcon.SHOP_2;
    }

    @Override
    public void onSelect(Hero hero) {
        Item prize = prize();
        prize.cursedKnown = true;
        Dungeon.level.drop(prize, hero.pos).sprite.drop();
        if (prize.level() >= 2) {
            showFlareForBonusDrop(hero.sprite, 5);
        } else if (prize.level() >= 1) {
            showFlareForBonusDrop(hero.sprite, 4);
        } else {
            showFlareForBonusDrop(hero.sprite, 3);
        }

    }

    public Item prize() {
        switch (Random.Int(2)) {
            case 0: default:
                Weapon w = Generator.randomWeapon(Dungeon.depth, true);
                if (w.hasCurseEnchant()) w.enchant(null);
                return w;
            case 1:
                Armor a = Generator.randomArmor(Dungeon.depth);
                if (a.hasCurseGlyph()) a.inscribe(null);
                return a;
        }
    }

    @Override
    public int creditUse(Hero hero) {
        return 500 * (1+(hero.lvl/5));
    }
}

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.special;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class FlashShield extends MeleeWeapon {

    {
        image = ItemSpriteSheet.FLASH_SHIELD;

        tier = 1;
        unique = true;
        bones = false;
    }

    @Override
    public String statsInfo() {
        if (isIdentified()){
            return Messages.get(this, "stats_desc", tier()*buffedLvl()+1, 20, -2);
        } else {
            return Messages.get(this, "typical_stats_desc", tier()+1, 20, -2);
        }
    }

    @Override
    public int tier() {
        if (Dungeon.hero != null) return 1+Dungeon.hero.lvl/6;
        else return super.tier();
    }

    @Override
    public int defenseFactor(Char owner) {
        return tier()*buffedLvl()+1;
    }

    public float bulletAccuracyFactor(Hero hero) {
        if (isEquipped(hero)) return 1.2f;
        else return 1f;
    }

    public int gunSTRFactor(Hero hero) {
        if (isEquipped(hero)) return -2;
        else return 0;
    }
}

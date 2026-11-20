package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.special;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class FlashShield extends MeleeWeapon {

    {
        image = ItemSpriteSheet.FLASH_SHIELD;

        tier = 1;
        unique = true;
        bones = false;
    }

    @Override
    public int min(int lvl) {
        return  tier() +  //base
                lvl;    //level scaling
    }

    @Override
    public int max(int lvl) {
        return  4*(tier()+1) +    //base
                lvl*(tier()+1);   //level scaling
    }

    @Override
    public String statsInfo() {
        if (isIdentified()){
            return Messages.get(this, "stats_desc", DRMax(), 20, -2, tier());
        } else {
            return Messages.get(this, "typical_stats_desc", DRMax(0), 20, -2, tier());
        }
    }

    @Override
    public int tier() {
        if (Dungeon.hero != null) return (int) Math.ceil(Dungeon.hero.lvl/6f);
        else return super.tier();
    }



    @Override
    public int defenseFactor( Char owner ) {
        return DRMax();
    }

    public int DRMax(){
        return DRMax(buffedLvl());
    }

    //6 extra defence, plus 2 per level
    public int DRMax(int lvl){
        return tier()*lvl+1;
    }

    public static float bulletAccuracyFactor(Hero hero) {
        FlashShield shield = hero.belongings.getItem(FlashShield.class);
        if (shield != null && shield.isEquipped(hero)) return 1.2f;
        else return 1f;
    }

    public static int gunSTRFactor(Hero hero) {
        FlashShield shield = hero.belongings.getItem(FlashShield.class);
        if (shield != null && shield.isEquipped(hero)) return -2;
        else return 0;
    }
}

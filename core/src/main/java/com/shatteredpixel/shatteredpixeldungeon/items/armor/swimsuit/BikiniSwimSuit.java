package com.shatteredpixel.shatteredpixeldungeon.items.armor.swimsuit;

import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class BikiniSwimSuit extends Armor {
    {
        image = ItemSpriteSheet.BIKINI_SWIMSUIT;

        armorType = ArmorType.SWIMSUIT;
    }

    public BikiniSwimSuit() {
        super(5);
    }
}

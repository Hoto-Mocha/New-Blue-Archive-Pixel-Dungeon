package com.shatteredpixel.shatteredpixeldungeon.items.armor.swimsuit;

import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class FrilledSwimSuit extends SwimSuit {
    {
        image = ItemSpriteSheet.CAMISOLE_SWIMSUIT;

        armorType = ArmorType.SWIMSUIT;
    }

    public FrilledSwimSuit() {
        super(4);
    }
}

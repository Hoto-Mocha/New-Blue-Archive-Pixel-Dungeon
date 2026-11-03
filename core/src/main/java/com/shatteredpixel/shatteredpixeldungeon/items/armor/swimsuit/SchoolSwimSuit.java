package com.shatteredpixel.shatteredpixeldungeon.items.armor.swimsuit;

import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class SchoolSwimSuit extends SwimSuit {
    {
        image = ItemSpriteSheet.SCHOOL_SWIMSUIT;

        armorType = ArmorType.SWIMSUIT;
    }

    public SchoolSwimSuit() {
        super(2);
    }
}

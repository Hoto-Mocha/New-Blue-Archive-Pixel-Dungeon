package com.shatteredpixel.shatteredpixeldungeon.items.armor.swimsuit;

import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class CamisoleSwimSuit extends Armor {
    {
        image = ItemSpriteSheet.CAMISOLE_SWIMSUIT;

        armorType = ArmorType.SWIMSUIT;
    }

    public CamisoleSwimSuit() {
        super(4);
    }
}

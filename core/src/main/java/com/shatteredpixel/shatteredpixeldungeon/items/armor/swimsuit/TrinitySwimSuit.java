package com.shatteredpixel.shatteredpixeldungeon.items.armor.swimsuit;

import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class TrinitySwimSuit extends Armor {
    {
        image = ItemSpriteSheet.TRINITY_SWIMSUIT;

        armorType = ArmorType.SWIMSUIT;
    }

    public TrinitySwimSuit() {
        super(3);
    }
}

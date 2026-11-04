package com.shatteredpixel.shatteredpixeldungeon.items.armor.racing;

import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class LatexRacingSuit extends RacingSuit {

    {
        image = ItemSpriteSheet.LATEX_RACING_SUIT;

        armorType = ArmorType.RACING;
    }

    public LatexRacingSuit() {
        super(5);
    }
}

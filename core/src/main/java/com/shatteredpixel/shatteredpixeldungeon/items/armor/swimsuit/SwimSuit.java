package com.shatteredpixel.shatteredpixeldungeon.items.armor.swimsuit;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndImageDisplay;

public class SwimSuit extends Armor {

    public SwimSuit(int tier) {
        super(tier);
    }

    @Override
    public boolean doEquip(Hero hero) {

        if (SPDSettings.imageDisplay()) {
            try {
                WndImageDisplay.getImage(Assets.Arts.swimsuit(hero.heroClass.name(), tier));
            } catch (Exception e) {
                return super.doEquip(hero);
            }
            GameScene.show(new WndImageDisplay(Assets.Arts.swimsuit(hero.heroClass.name(), tier), name()));
        }

        return super.doEquip(hero);
    }
}

package com.shatteredpixel.shatteredpixeldungeon.items.armor.racing;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndImageDisplay;

public class RacingSuit extends Armor {

    public RacingSuit(int tier) {
        super(tier);
    }

    @Override
    public boolean doEquip(Hero hero) {

        if (SPDSettings.imageDisplay() && !Dungeon.customSeedText.contains("test")) {
            try {
                WndImageDisplay.getImage(Assets.Arts.image("racing", hero.heroClass.name(), tier));
            } catch (Exception e) {
                return super.doEquip(hero);
            }
            GameScene.show(new WndImageDisplay(Assets.Arts.image("racing", hero.heroClass.name(), tier), name()));
        }

        return super.doEquip(hero);
    }
}

package com.shatteredpixel.shatteredpixeldungeon.items.armor.pajamas;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndImageDisplay;

public class Pajamas extends Armor {

    {
        armorType = ArmorType.PAJAMAS;
    }

    public Pajamas(int tier) {
        super(tier);
    }

    @Override
    public boolean doEquip(Hero hero) {

        if (SPDSettings.imageDisplay() && !Dungeon.customSeedText.contains("test")) {
            try {
                WndImageDisplay.getImage(Assets.Arts.image("pajamas", hero.heroClass.name(), tier));
            } catch (Exception e) {
                return super.doEquip(hero);
            }
            GameScene.show(new WndImageDisplay(Assets.Arts.image("pajamas", hero.heroClass.name(), tier), name()));
        }

        return super.doEquip(hero);
    }

}

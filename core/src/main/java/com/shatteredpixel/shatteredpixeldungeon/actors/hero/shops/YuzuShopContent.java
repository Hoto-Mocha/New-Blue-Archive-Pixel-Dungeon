package com.shatteredpixel.shatteredpixeldungeon.actors.hero.shops;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.watabou.noosa.Visual;

import java.util.ArrayList;

public abstract class YuzuShopContent {
    public abstract void onSelect(Hero hero);

    public int creditUse(Hero hero ){
        return 1;
    }

    public boolean canSelect( Hero hero ){
        return Dungeon.gold >= creditUse(hero);
    }

    public String name(){
        return Messages.get(this, "name");
    }

    public String shortDesc(){
        return Messages.get(this, "short_desc") + " " + Messages.get(this, "credit_cost", creditUse(Dungeon.hero));
    }

    public String desc(){
        return Messages.get(this, "desc") + "\n\n" + Messages.get(this, "credit_cost", creditUse(Dungeon.hero));
    }

    public int icon(){
        return HeroIcon.NONE;
    }

    public void onContentSelect(Hero hero) {
        Dungeon.gold -= creditUse(hero);
    }

    public static ArrayList<YuzuShopContent> getContentList(Hero yuzu, int tier) {
        ArrayList<YuzuShopContent> contents = new ArrayList<>();

        if (tier == 1) {
            contents.add(RandomConsumable.INSTANCE);
            contents.add(RandomEquipment.INSTANCE);
            contents.add(SellItem.INSTANCE);
        } else if (tier == 2) {

        }

        return contents;
    }

    public static ArrayList<YuzuShopContent> getAllContents() {
        ArrayList<YuzuShopContent> contents = new ArrayList<>();

        return contents;
    }

    public static void showFlareForBonusDrop( Visual vis, int tier ){
        if (vis == null || vis.parent == null) return;
        switch (tier){
            default:
                break; //do nothing
            case 1:
                new Flare(6, 20).color(0x00FF00, true).show(vis, 3f);
                break;
            case 2:
                new Flare(6, 24).color(0x00AAFF, true).show(vis, 3.33f);
                break;
            case 3:
                new Flare(6, 28).color(0xAA00FF, true).show(vis, 3.67f);
                break;
            case 4:
                new Flare(6, 32).color(0xFFAA00, true).show(vis, 4f);
                break;
            case 5:
                new Flare(6, 36).color(0xFF0000, true).show(vis, 4.33f);
                break;
            case 6:
                new Flare(6, 40).color(0x0000FF, true).show(vis, 4.67f);
                break;
        }
    }
}

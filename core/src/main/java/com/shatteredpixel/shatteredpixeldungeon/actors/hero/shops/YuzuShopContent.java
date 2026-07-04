package com.shatteredpixel.shatteredpixeldungeon.actors.hero.shops;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;

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
        } else if (tier == 2) {

        }

        return contents;
    }

    public static ArrayList<YuzuShopContent> getAllContents() {
        ArrayList<YuzuShopContent> contents = new ArrayList<>();

        return contents;
    }
}

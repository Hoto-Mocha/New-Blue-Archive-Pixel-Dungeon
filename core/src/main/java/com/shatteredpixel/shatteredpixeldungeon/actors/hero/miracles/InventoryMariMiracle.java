package com.shatteredpixel.shatteredpixeldungeon.actors.hero.miracles;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.InventoryClericSpell;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CrossNecklace;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HolyTome;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;

public abstract class InventoryMariMiracle extends MariMiracle {

    @Override
    public void onCast(CrossNecklace cross, Hero hero) {
        GameScene.selectItem(new WndBag.ItemSelector() {

            @Override
            public String textPrompt() {
                return inventoryPrompt();
            }

            @Override
            public Class<? extends Bag> preferredBag() {
                return this.preferredBag();
            }

            @Override
            public boolean itemSelectable(Item item) {
                return usableOnItem(item);
            }

            @Override
            public void onSelect(Item item) {
                onItemSelected(cross, hero, item);
            }
        });
    }

    protected String inventoryPrompt(){
        return Messages.get(this, "prompt");
    }

    protected Class<? extends Bag> preferredBag() {
        return null; //defaults to no preference
    }

    protected boolean usableOnItem( Item item ){
        return true;
    }

    protected abstract void onItemSelected( CrossNecklace cross, Hero hero, Item item );
}

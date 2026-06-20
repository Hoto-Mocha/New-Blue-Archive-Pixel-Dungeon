package com.shatteredpixel.shatteredpixeldungeon.items.scrolls;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.ScrollHolder;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ExoticScroll;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class EmptyScroll extends InventoryScroll {

    {
        image = ItemSpriteSheet.SCROLL_EMPTY;
        preferredBag = ScrollHolder.class;
        handler.know(this);
        anonymous = false;

        bones = false;
        talentFactor = 0;
    }

    @Override
    protected boolean usableOnItem(Item item) {
        return item instanceof Scroll
                && !(item instanceof ExoticScroll)
                && !(item instanceof EmptyScroll)
                && !(item instanceof ScrollOfUpgrade);
    }

    @Override
    protected void onItemSelected(Item item) {
        this.detach(curUser.belongings.backpack);
        Item scroll = item.duplicate();
        if (!scroll.doPickUp(curUser)) {
            Dungeon.level.drop(scroll, curUser.pos).sprite.drop(curUser.pos);
        }
    }

    @Override
    public boolean isIdentified() {
        return true;
    }
}

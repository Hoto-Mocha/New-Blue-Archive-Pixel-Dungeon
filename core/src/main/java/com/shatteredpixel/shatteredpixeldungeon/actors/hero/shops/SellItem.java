package com.shatteredpixel.shatteredpixeldungeon.actors.hero.shops;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.EquipableItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

import java.util.LinkedList;

public class SellItem extends YuzuShopContent {
    public static SellItem INSTANCE = new SellItem();

    @Override
    public int icon() {
        return HeroIcon.SHOP_3;
    }

    @Override
    public void onSelect(Hero hero) {
        if (creditUse(hero) == 0) return;
        Heap heap = Dungeon.level.heaps.get(hero.pos);
        if (heap == null) return;
        sellItem(hero, heap);
    }

    public void sellItem(Hero hero, Heap heap) {
        int totalValue = 0;
        LinkedList<Item> items = heap.items;
        if (heap.type != Heap.Type.HEAP) {
            GLog.w(Messages.get(this, "no_items"));
            return;
        } else {
            for (Item item : items.toArray( new Item[0] )) {

                //doesn't sell unique items and items cannot sell
                if (item.unique || item.value() < 0){
                    continue;
                }

                totalValue += item.value();
                items.remove( item );
            }

            if (heap.isEmpty()){
                heap.destroy();
            } else if (heap.sprite != null) {
                heap.sprite.view(heap).place( heap.pos );
            }
        }

        if (totalValue == 0) {
            GLog.w(Messages.get(this, "no_items"));
        } else {
            new Gold(totalValue).doPickUp(hero, hero.pos);
        }
    }

    @Override
    public int creditUse(Hero hero) {
        return 100;
    }

    @Override
    public boolean canSelect(Hero hero) {
        Heap heap = Dungeon.level.heaps.get(hero.pos);
        return heap != null && heap.type == Heap.Type.HEAP;
    }
}

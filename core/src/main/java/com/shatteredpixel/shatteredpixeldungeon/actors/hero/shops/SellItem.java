package com.shatteredpixel.shatteredpixeldungeon.actors.hero.shops;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;

public class SellItem extends YuzuShopContent {
    public static SellItem INSTANCE = new SellItem();

    @Override
    public int icon() {
        return HeroIcon.SHOP_3;
    }

    @Override
    public void onSelect(Hero hero) {
        Heap heap = getHeap(hero);
        if (heap == null) return;
        sellItem(hero, heap);
    }

    public void sellItem(Hero hero, Heap heap) {
        int totalValue = 0;
        if (heap.type != Heap.Type.HEAP) {
            return;
        } else {
            for (Item item : heap.items.toArray( new Item[0] )) {

                //doesn't sell unique items and items cannot sell
                if (item.unique || item.value() <= 0){
                    continue;
                }

                totalValue += item.value();
                heap.items.remove( item );
            }

            if (heap.isEmpty()){
                heap.destroy();
            } else if (heap.sprite != null) {
                heap.sprite.view(heap).place( heap.pos );
            }
        }

        new Gold(totalValue).doPickUp(hero, hero.pos);
    }

    public int checkValue(Heap heap) {
        int totalValue = 0;
        for (Item item : heap.items.toArray( new Item[0] )) {
            //doesn't count unique items and items cannot sell
            if (item.unique || item.value() <= 0){
                continue;
            }
            totalValue += item.value();
        }
        return totalValue;
    }

    @Override
    public int creditUse(Hero hero) {
        return 100;
    }

    @Override
    public boolean canSelect(Hero hero) {
        Heap heap = getHeap(hero);
        return super.canSelect(hero) && heap != null && heap.type == Heap.Type.HEAP && checkValue(heap) > 0;
    }

    public Heap getHeap(Hero hero) {
        return Dungeon.level.heaps.get(hero.pos);
    }

    public String desc(){
        if (canSelect(Dungeon.hero)) {
            return Messages.get(this, "desc_value", checkValue(getHeap(Dungeon.hero))) + "\n\n" + Messages.get(this, "credit_cost", creditUse(Dungeon.hero));
        } else {
            return Messages.get(this, "desc") + "\n\n" + Messages.get(this, "credit_cost", creditUse(Dungeon.hero));
        }
    }
}

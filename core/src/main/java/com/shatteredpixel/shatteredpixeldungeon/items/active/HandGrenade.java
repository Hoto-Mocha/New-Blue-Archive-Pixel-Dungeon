package com.shatteredpixel.shatteredpixeldungeon.items.active;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class HandGrenade extends Grenade {
    {
        image = ItemSpriteSheet.GRENADE;

        max_amount = 1;
        amount = max_amount;
        dropChance = 0.125f;
    }

    @Override
    public Grenade.Boomer knockItem(){
        return new HandGrenadeBoomer();
    }

    public class HandGrenadeBoomer extends Boomer {

        {
            image = ItemSpriteSheet.GRENADE;
        }

        //needs to be overridden
        @Override
        protected void activate(int cell) {
            explode(cell);
        }
    }
}

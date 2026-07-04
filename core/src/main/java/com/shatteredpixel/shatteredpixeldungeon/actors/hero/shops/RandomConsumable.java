package com.shatteredpixel.shatteredpixeldungeon.actors.hero.shops;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.Honeypot;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfExperience;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.UnstableBrew;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.ExoticPotion;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.PotionOfDivineInspiration;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfWealth;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTransmutation;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ExoticScroll;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfTalentReset;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.UnstableSpell;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfEnchantment;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.ExoticCrystals;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

public class RandomConsumable extends YuzuShopContent {
    public static RandomConsumable INSTANCE = new RandomConsumable();
    public static int latestDropTier = 0;

    @Override
    public int icon() {
        return HeroIcon.SHOP_1;
    }

    @Override
    public void onSelect(Hero hero) {
        Item prize = genConsumableDrop(0);
        Dungeon.level.drop(prize, hero.pos).sprite.drop();
        showFlareForBonusDrop(hero.sprite, latestDropTier);
        latestDropTier = 0;
    }

    @Override
    public int creditUse(Hero hero) {
        return 150 * inflationParameter();
    }

    public static Item genConsumableDrop(int level) {
        float roll = Random.Float();
        //60% chance - 4% per level. Starting from +15: 0%
        if (roll < (0.6f - 0.04f * level)) {
            latestDropTier = 1;
            return genLowValueConsumable();
            //30% chance + 2% per level. Starting from +15: 60%-2%*(lvl-15)
        } else if (roll < (0.9f - 0.02f * level)) {
            latestDropTier = 2;
            return genMidValueConsumable();
            //10% chance + 2% per level. Starting from +15: 40%+2%*(lvl-15)
        } else {
            latestDropTier = 3;
            return genHighValueConsumable();
        }
    }

    private static Item genLowValueConsumable(){
        switch (Random.Int(4)){
            case 0: default:
                Item i = new Gold().random();
                return i.quantity(i.quantity()/2);
            case 1:
                return Generator.randomUsingDefaults(Generator.Category.STONE);
            case 2:
                return Generator.randomUsingDefaults(Generator.Category.POTION);
            case 3:
                return Generator.randomUsingDefaults(Generator.Category.SCROLL);
        }
    }

    private static Item genMidValueConsumable(){
        switch (Random.Int(6)){
            case 0: default:
                Item i = genLowValueConsumable();
                return i.quantity(i.quantity()*2);
            case 1:
                i = Generator.randomUsingDefaults(Generator.Category.POTION);
                if (!(i instanceof ExoticPotion)) {
                    return Reflection.newInstance(ExoticPotion.regToExo.get(i.getClass()));
                } else {
                    return Reflection.newInstance(i.getClass());
                }
            case 2:
                i = Generator.randomUsingDefaults(Generator.Category.SCROLL);
                if (!(i instanceof ExoticScroll)){
                    return Reflection.newInstance(ExoticScroll.regToExo.get(i.getClass()));
                } else {
                    return Reflection.newInstance(i.getClass());
                }
            case 3:
                return Random.Int(2) == 0 ? new UnstableBrew() : new UnstableSpell();
            case 4:
                return new Bomb();
            case 5:
                return new Honeypot();
        }
    }

    private static Item genHighValueConsumable(){
        switch (Random.Int(4)){
            case 0: default:
                Item i = genMidValueConsumable();
                if (i instanceof Bomb){
                    return new Bomb.DoubleBomb();
                } else {
                    return i.quantity(i.quantity()*2);
                }
            case 1:
                return new StoneOfEnchantment();
            case 2:
                return Random.Float() < ExoticCrystals.consumableExoticChance() ? new PotionOfDivineInspiration() : new PotionOfExperience();
            case 3:
                return Random.Float() < ExoticCrystals.consumableExoticChance() ? new ScrollOfTalentReset() : new ScrollOfTransmutation();
        }
    }
}

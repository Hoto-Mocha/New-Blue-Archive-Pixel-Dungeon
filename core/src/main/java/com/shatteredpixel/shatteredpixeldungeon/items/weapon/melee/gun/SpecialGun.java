package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun;

import com.shatteredpixel.shatteredpixeldungeon.items.GunSmithingTool;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.AR.AR_T1;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.AR.AR_T2;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.AR.AR_T3;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.AR.AR_T4;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.AR.AR_T5;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.AR.UniqueIdea;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public interface SpecialGun {
    class Recipe extends com.shatteredpixel.shatteredpixeldungeon.items.Recipe {
        public static final LinkedHashMap<Class<?extends Gun>, Class<?extends Gun>> validIngredients = new LinkedHashMap<>();
        static {
            validIngredients.put(AR_T1.class, UniqueIdea.class);
            validIngredients.put(AR_T2.class, UniqueIdea.class);
            validIngredients.put(AR_T3.class, UniqueIdea.class);
            validIngredients.put(AR_T4.class, UniqueIdea.class);
            validIngredients.put(AR_T5.class, UniqueIdea.class);

        }

        private static final HashMap<Class<?extends Gun>, Integer> gunCosts = new HashMap<>();
        static {
            gunCosts.put(UniqueIdea.class, 8);
        }

        @Override
        public boolean testIngredients(ArrayList<Item> ingredients) {
            boolean tool = false;
            boolean gun = false;

            for (Item i : ingredients){
                if (!i.isIdentified()) return false;
                if (i.getClass().equals(GunSmithingTool.class)){
                    tool = true;
                } else if (validIngredients.containsKey(i.getClass())){
                    gun = true;
                }
            }

            return tool && gun;
        }

        @Override
        public int cost(ArrayList<Item> ingredients) {
            for (Item i : ingredients){
                if (validIngredients.containsKey(i.getClass())){
                    return (gunCosts.get(validIngredients.get(i.getClass())));
                }
            }
            return 0;
        }

        @Override
        public Item brew(ArrayList<Item> ingredients) {
            Item result = null;

            for (Item i : ingredients){
                i.quantity(i.quantity()-1);
                if (validIngredients.containsKey(i.getClass())){
                    result = Reflection.newInstance(validIngredients.get(i.getClass()));
                }
            }

            if (result != null) {
                result.identify();
            }

            return result;
        }

        @Override
        public Item sampleOutput(ArrayList<Item> ingredients) {
            for (Item i : ingredients){
                if (validIngredients.containsKey(i.getClass())){
                    return Reflection.newInstance(validIngredients.get(i.getClass()));
                }
            }
            return null;
        }
    }
}

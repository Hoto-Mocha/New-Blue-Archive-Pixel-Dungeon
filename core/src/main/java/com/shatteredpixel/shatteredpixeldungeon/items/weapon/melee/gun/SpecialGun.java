package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun;

import com.shatteredpixel.shatteredpixeldungeon.items.GunSmithingTool;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.AR.AR_T1;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.AR.AR_T2;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.AR.AR_T3;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.AR.AR_T4;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.AR.AR_T5;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.AR.UniqueIdea;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.GL.FunnyFirework;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.GL.GL_T1;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.GL.GL_T2;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.GL.GL_T3;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.GL.GL_T4;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.GL.GL_T5;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.HG.HG_T1;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.HG.HG_T2;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.HG.HG_T3;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.HG.HG_T4;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.HG.HG_T5;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.HG.Piety;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.MG.MG_T1;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.MG.MG_T2;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.MG.MG_T3;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.MG.MG_T4;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.MG.MG_T5;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.MG.Mulligan;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.SG.SG_T1;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.SG.SG_T2;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.SG.SG_T3;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.SG.SG_T4;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.SG.SG_T5;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.SG.ShootingStar;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.SMG.SMG_T1;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.SMG.SMG_T2;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.SMG.SMG_T3;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.SMG.SMG_T4;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.SMG.SMG_T5;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.SMG.TwinDragon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.SR.JusticeIncarnate;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.SR.SR_T1;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.SR.SR_T2;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.SR.SR_T3;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.SR.SR_T4;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.SR.SR_T5;
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

            validIngredients.put(GL_T1.class, FunnyFirework.class);
            validIngredients.put(GL_T2.class, FunnyFirework.class);
            validIngredients.put(GL_T3.class, FunnyFirework.class);
            validIngredients.put(GL_T4.class, FunnyFirework.class);
            validIngredients.put(GL_T5.class, FunnyFirework.class);

            validIngredients.put(HG_T1.class, Piety.class);
            validIngredients.put(HG_T2.class, Piety.class);
            validIngredients.put(HG_T3.class, Piety.class);
            validIngredients.put(HG_T4.class, Piety.class);
            validIngredients.put(HG_T5.class, Piety.class);

            validIngredients.put(MG_T1.class, Mulligan.class);
            validIngredients.put(MG_T2.class, Mulligan.class);
            validIngredients.put(MG_T3.class, Mulligan.class);
            validIngredients.put(MG_T4.class, Mulligan.class);
            validIngredients.put(MG_T5.class, Mulligan.class);

            validIngredients.put(SG_T1.class, ShootingStar.class);
            validIngredients.put(SG_T2.class, ShootingStar.class);
            validIngredients.put(SG_T3.class, ShootingStar.class);
            validIngredients.put(SG_T4.class, ShootingStar.class);
            validIngredients.put(SG_T5.class, ShootingStar.class);

            validIngredients.put(SMG_T1.class, TwinDragon.class);
            validIngredients.put(SMG_T2.class, TwinDragon.class);
            validIngredients.put(SMG_T3.class, TwinDragon.class);
            validIngredients.put(SMG_T4.class, TwinDragon.class);
            validIngredients.put(SMG_T5.class, TwinDragon.class);

            validIngredients.put(SR_T1.class, JusticeIncarnate.class);
            validIngredients.put(SR_T2.class, JusticeIncarnate.class);
            validIngredients.put(SR_T3.class, JusticeIncarnate.class);
            validIngredients.put(SR_T4.class, JusticeIncarnate.class);
            validIngredients.put(SR_T5.class, JusticeIncarnate.class);

        }

        private static final HashMap<Class<?extends Gun>, Integer> gunCosts = new HashMap<>();
        static {
            gunCosts.put(UniqueIdea.class, 8);
            gunCosts.put(FunnyFirework.class, 8);
            gunCosts.put(Piety.class, 8);
            gunCosts.put(Mulligan.class, 8);
            gunCosts.put(ShootingStar.class, 8);
            gunCosts.put(TwinDragon.class, 8);
            gunCosts.put(JusticeIncarnate.class, 8);
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

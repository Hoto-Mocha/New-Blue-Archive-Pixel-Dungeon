package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun;

import com.shatteredpixel.shatteredpixeldungeon.items.GunSmithingTool;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.Recipe;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.AR.TacticalTherapy;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.AR.UniqueIdea;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.GL.FunnyFirework;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.HG.Chistka;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.HG.Piety;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.MG.Mulligan;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.MG.TrickOrTrick;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.MT.FancyLight;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.SG.MTET;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.SG.ShootingStar;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.SMG.BeyondTheLumination;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.SMG.TwinDragon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.SR.JusticeIncarnate;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.SR.WineRedAdmire;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickRecipe;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.Arrays;

public interface SpecialGun {
    abstract class BaseRecipe extends Recipe {
        public abstract Class<? extends Gun> ingredients();

        public abstract Class<? extends Gun> result();

        public boolean isIngredient(Item item) {
            return ingredients().isInstance(item) && !(item instanceof SpecialGun);
        }

        @Override
        public boolean testIngredients(ArrayList<Item> ingredients) {
            boolean tool = false;
            boolean gun = false;

            for (Item i : ingredients){
                if (!i.isIdentified()) return false;
                if (i.getClass().equals(GunSmithingTool.class)){
                    tool = true;
                } else if (isIngredient(i)){
                    gun = true;
                }
            }

            return tool && gun;
        }

        @Override
        public int cost(ArrayList<Item> ingredients) {
            return 8;
        }

        @Override
        public Item brew(ArrayList<Item> ingredients) {
            Item result = null;

            for (Item i : ingredients){
                i.quantity(i.quantity()-1);
                if (isIngredient(i)){
                    result = brewGun(i);
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
                if (isIngredient(i)){
                    return brewGun(i);
                }
            }
            return null;
        }

        public Gun brewGun(Item item) {
            if (!(item instanceof Gun)) return null;

            Gun n = Reflection.newInstance(result());
            if (n == null) return null;

            Gun g = (Gun)item;
            int level = Math.min((g.checkKit() != null && g.checkKit().level() > 0) ? 3:2, g.level());
            n.identify().upgrade(level);
            if (g.checkKit() != null) {
                //만약 키트가 강화되어 있다면 키트에 의해 추가로 증가한 강화수치를 1 감소시킴
                if (g.checkKit().level() > 0) {
                    n.degrade(g.checkKit().level());
                }
                n.affixKit(g.checkKit());
            }

            n.enchantment = g.enchantment;
            n.barrelMod = g.barrelMod;
            n.magazineMod = g.magazineMod;
            n.bulletMod = g.bulletMod;
            n.weightMod = g.weightMod;
            n.attachMod = g.attachMod;
            n.enchantMod = g.enchantMod;
            n.inscribeMod = g.inscribeMod;

            return n;
        }
    }

    Recipe[] gunRecipes = new Recipe[]{
        new UniqueIdea.Recipe(),
        new TacticalTherapy.Recipe(),
        new FunnyFirework.Recipe(),
        new Piety.Recipe(),
        new Chistka.Recipe(),
        new Mulligan.Recipe(),
        new TrickOrTrick.Recipe(),
        new FancyLight.Recipe(),
        new ShootingStar.Recipe(),
        new MTET.Recipe(),
        new TwinDragon.Recipe(),
        new BeyondTheLumination.Recipe(),
        new JusticeIncarnate.Recipe(),
        new WineRedAdmire.Recipe()
    };

    static ArrayList<QuickRecipe> quickRecipes() {
        ArrayList<QuickRecipe> result = new ArrayList<>();
        for (Recipe recipe : gunRecipes){
            result.add(new QuickRecipe( recipe,
                    new ArrayList<Item>(Arrays.asList(Reflection.newInstance(((BaseRecipe)recipe).ingredients()).getPlaceHolder(), new GunSmithingTool())),
                    Reflection.newInstance(((BaseRecipe)recipe).result())));
        }
        return result;
    }
}

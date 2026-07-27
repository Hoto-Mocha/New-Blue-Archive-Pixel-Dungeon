package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.MT;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.EquipableItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.MagicalHolster;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.Trinket;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.GL.GL;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.Gun;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.SpecialGun;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.CustomNoteButton;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class FancyLight extends MT implements SpecialGun {

    public static final String AC_BOMB    = "BOMB";

    private Bomb bomb = null;

    {
        image = ItemSpriteSheet.FACNY_LIGHT;
        tier = 4;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_BOMB);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);
        if (action.equals(AC_BOMB)) {
            GameScene.selectItem(itemSelector);
        }
    }

    @Override
    public String info() {
        String desc = super.info();

        if (bomb != null) {
            desc += "\n\n" + Messages.get(this, "bomb", bomb.name());
        }

        return desc;
    }

    @Override
    public void onReload() {
        super.onReload();
        bomb = null;
    }

    private WndBag.ItemSelector itemSelector = new WndBag.ItemSelector() {

        @Override
        public String textPrompt() {
            return	Messages.get(FancyLight.class, "inv_prompt");
        }

        @Override
        public Class<? extends Bag> preferredBag() {
            return MagicalHolster.class;
        }

        @Override
        public boolean itemSelectable(Item item) {
            return item instanceof Bomb;
        }

        @Override
        public void onSelect( Item item ) {
            if (item instanceof Bomb){
                reload(); //onReload()에서 bomb를 null로 만들기 때문에 반드시 먼저 행해져야 함
                manualReload(Random.IntRange(1, 4), true);
                bomb = (Bomb)item.detach(Dungeon.hero.belongings.backpack);
            }
        }
    };

    private static final String BOMB = "bomb";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(BOMB, bomb);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        bomb = (Bomb)bundle.get(BOMB);
    }

    @Override
    public Bullet knockBullet() {
        return new FancyLightBullet();
    }

    public class FancyLightBullet extends MTBullet {
        @Override
        public float projectileSpeed() {
            return 1.5f;
        }

        @Override
        public int image() {
            if (bomb != null){
                return bomb.image();
            } else {
                return super.image();
            }
        }

        @Override
        public ItemSprite.Glowing glowing() {
            if (bomb != null){
                return new ItemSprite.Glowing( 0xFF0000, 0.6f);
            } else {
                return super.glowing();
            }
        }

        @Override
        protected void onThrow(int cell) {
            super.onThrow(cell);
            //onThrow() 레벨에서 폭탄을 터뜨림으로써 각인 탄환의 영향을 받지 않게 함
            if (bomb != null) {
                ((Bomb)bomb.duplicate()).explode(cell);
                if (round() <= 0) bomb = null;
            }
        }
    }

    public static class Recipe extends BaseRecipe {

        @Override
        public Class<? extends Gun> ingredients() {
            return GL.class;
        }

        @Override
        public Class<? extends Gun> result() {
            return FancyLight.class;
        }
    }
}

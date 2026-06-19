package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Belongings;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.Gun;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndUseItem;
import com.watabou.noosa.audio.Sample;

import java.util.ArrayList;

public class ConversionKit extends Item {
    public static final String AC_AFFIX = "AFFIX";

    //only to be used from the quickslot, for tutorial purposes mostly.
    public static final String AC_INFO = "INFO_WINDOW";

    {
        image = ItemSpriteSheet.CONVERSION_KIT;

        cursedKnown = levelKnown = true;
        unique = true;
        bones = false;

        defaultAction = AC_INFO;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions =  super.actions(hero);
        actions.add(AC_AFFIX);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {

        super.execute(hero, action);

        if (action.equals(AC_AFFIX)){
            curItem = this;
            GameScene.selectItem(gunSelector);
        } else if (action.equals(AC_INFO)) {
            GameScene.show(new WndUseItem(null, this));
        }
    }

    public void affixToGun(Gun gun, Item outgoing) {
        if (gun != null) {
            if (!gun.cursedKnown) {
                GLog.w(Messages.get(ConversionKit.class, "unknown_gun"));

            } else if (gun.cursed) {
                GLog.w(Messages.get(ConversionKit.class, "cursed_gun"));

            } else {
                if (outgoing == this) {
                    detach(Dungeon.hero.belongings.backpack);
                } else if (outgoing instanceof Gun) {
                    ((Gun) outgoing).detachKit();
                }

                GLog.p(Messages.get(ConversionKit.class, "affix"));
                Dungeon.hero.sprite.operate(Dungeon.hero.pos);
                Sample.INSTANCE.play(Assets.Sounds.UNLOCK);
                gun.affixKit(this);
                Dungeon.hero.next();
            }
        }
    }

    @Override
    public boolean isUpgradable() {
        return level() == 0;
    }

    protected static WndBag.ItemSelector gunSelector = new WndBag.ItemSelector() {

        @Override
        public String textPrompt() {
            return  Messages.get(ConversionKit.class, "prompt");
        }

        @Override
        public Class<?extends Bag> preferredBag(){
            return Belongings.Backpack.class;
        }

        @Override
        public boolean itemSelectable(Item item) {
            return item instanceof Gun;
        }

        @Override
        public void onSelect( Item item ) {
            if (item instanceof Gun) {
                ConversionKit kit = (ConversionKit) curItem;
                kit.affixToGun((Gun)item, kit);
            }
        }
    };
}

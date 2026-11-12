package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Belongings;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Shopkeeper;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.pajamas.Pajamas;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.racing.RacingSuit;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.swimsuit.SwimSuit;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.Gun;
import com.shatteredpixel.shatteredpixeldungeon.journal.Gallery;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.ItemButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;

public class WndPresent extends Window {

    private static final int WIDTH		= 120;

    private static final int BTN_SIZE	= 32;
    private static final float GAP		= 2;
    private static final float BTN_GAP	= 5;

    private ItemButton btnPressed;

    private ItemButton btnItem;
    private RedButton btnPresent;

    public WndPresent(Shopkeeper shopkeeper, Window wndParent ) {
        super();

        IconTitle titlebar = new IconTitle();
        titlebar.icon( shopkeeper.sprite() );
        titlebar.label( Messages.titleCase( shopkeeper.name() ) );
        titlebar.setRect( 0, 0, WIDTH, 0 );
        add( titlebar );

        RenderedTextBlock message = PixelScene.renderTextBlock( Messages.get(this, "message"), 6 );
        message.maxWidth( WIDTH);
        message.setPos(0, titlebar.bottom() + GAP);
        add( message );

        btnItem = new ItemButton() {
            @Override
            protected void onClick() {
                btnPressed = btnItem;
                GameScene.selectItem( itemSelector );
            }
        };
        btnItem.setRect( (WIDTH - BTN_SIZE) / 2f, message.top() + message.height() + BTN_GAP, BTN_SIZE, BTN_SIZE );
        add(btnItem);

        btnPresent = new RedButton( Messages.get(this, "present") ) {
            @Override
            protected void onClick() {

                Item item = btnItem.item();
                item.detach(Dungeon.hero.belongings.backpack);
                Dungeon.presentedSora = true;

                if (item instanceof Armor) {
                    GameScene.show(new WndTitledMessage(shopkeeper.sprite(), Messages.get(Shopkeeper.class, "name"), Messages.get(WndPresent.class, "present_"+((Armor)item).armorType.name())) {
                        @Override
                        public void hide() {
                            super.hide();
                            if (SPDSettings.imageDisplay() && !Dungeon.customSeedText.contains("test")) {
                                try {
                                    WndImageDisplay.getImage(Assets.Arts.image(((Armor)item).armorType.name().toLowerCase(), "sora", ((Armor)item).tier));
                                } catch (Exception e) {
                                    return;
                                }
                                Gallery.setSeen(item.getClass(), "sora");
                                GameScene.show(new WndImageDisplay(Assets.Arts.image(((Armor)item).armorType.name().toLowerCase(), "sora", ((Armor)item).tier), item.name()));
                            }
                        }
                    });
                }

                hide();
                if (wndParent != null){
                    wndParent.hide();
                }
            }
        };
        btnPresent.enable( false );
        btnPresent.setRect( 0, btnItem.bottom() + BTN_GAP, WIDTH, 20 );
        add(btnPresent);

        resize( WIDTH, (int) btnPresent.bottom() );
    }

    protected WndBag.ItemSelector itemSelector = new WndBag.ItemSelector() {

        @Override
        public String textPrompt() {
            return Messages.get(WndPresent.class, "prompt");
        }

        @Override
        public Class<?extends Bag> preferredBag(){
            return Belongings.Backpack.class;
        }

        @Override
        public boolean itemSelectable(Item item) {
            return item.isIdentified() && !item.cursed && (item instanceof SwimSuit || item instanceof RacingSuit || item instanceof Pajamas);
        }

        @Override
        public void onSelect( Item item ) {
            if (item != null && btnPressed.parent != null) {
                btnPressed.item(item);

                Item item1 = btnItem.item();

                if (item1 == null) {
                    btnPresent.enable(false);

                } else if (!(item1 instanceof Armor)) {
                    btnPresent.enable(false);

                } else {
                    btnPresent.enable(true);
                }
            }
        }
    };

}

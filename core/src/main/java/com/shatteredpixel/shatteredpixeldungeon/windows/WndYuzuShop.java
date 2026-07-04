package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.shops.YuzuShopContent;
import com.shatteredpixel.shatteredpixeldungeon.items.active.Laptop;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.RightClickMenu;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.input.PointerEvent;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Image;
import com.watabou.noosa.NinePatch;
import com.watabou.utils.DeviceCompat;
import com.watabou.utils.PointF;

import java.util.ArrayList;

public class WndYuzuShop extends Window {

	protected static final int WIDTH    = 120;

	public static int BTN_SIZE = 20;

	public WndYuzuShop(Laptop laptop, Hero yuzu, boolean info){

		IconTitle title;
		if (!info){
			title = new IconTitle(new ItemSprite(laptop), Messages.titleCase(Messages.get(this, "cast_title")));
		} else {
			title = new IconTitle(Icons.INFO.get(), Messages.titleCase(Messages.get(this, "info_title")));
		}

		title.setRect(0, 0, WIDTH, 0);
		add(title);

		IconButton btnInfo = new IconButton(info ? new ItemSprite(laptop) : Icons.INFO.get()){
			@Override
			protected void onClick() {
				GameScene.show(new WndYuzuShop(laptop, yuzu, !info));
				hide();
			}
		};
		btnInfo.setRect(WIDTH-16, 0, 16, 16);
		add(btnInfo);

		RenderedTextBlock msg;
		if (info){
			msg = PixelScene.renderTextBlock( Messages.get( this, "info_desc"), 6);
		} else if (DeviceCompat.isDesktop()){
			msg = PixelScene.renderTextBlock( Messages.get( this, "cast_desc_desktop"), 6);
		} else {
			msg = PixelScene.renderTextBlock( Messages.get( this, "cast_desc_mobile"), 6);
		}
		msg.maxWidth(WIDTH);
		msg.setPos(0, title.bottom()+4);
		add(msg);

		int top = (int)msg.bottom()+4;

		for (int i = 1; i <= 2; i++) {

			ArrayList<YuzuShopContent> contents = YuzuShopContent.getContentList(yuzu, i);

			if (!contents.isEmpty() && i != 1){
				top += BTN_SIZE + 2;
				ColorBlock sep = new ColorBlock(WIDTH, 1, 0xFF000000);
				sep.y = top;
				add(sep);
				top += 3;
			}

			ArrayList<IconButton> contentBtns = new ArrayList<>();

			for (YuzuShopContent content : contents) {
				IconButton contentBtn = new ShopButton(content, info);
				add(contentBtn);
				contentBtns.add(contentBtn);
			}

			int left = 2 + (WIDTH - contentBtns.size() * (BTN_SIZE + 4)) / 2;
			for (IconButton btn : contentBtns) {
				btn.setRect(left, top, BTN_SIZE, BTN_SIZE);
				left += btn.width() + 4;
			}

		}

		resize(WIDTH, top + BTN_SIZE);

		//if we are on mobile, offset the window down to just above the toolbar
		if (SPDSettings.interfaceSize() != 2){
			offset(0, (int) (GameScene.uiCamera.height/2 - 30 - height/2));
		}

	}

	public class ShopButton extends IconButton {

		YuzuShopContent content;
		boolean info;

		NinePatch bg;

		public ShopButton(YuzuShopContent content, boolean info){
			super(new HeroIcon(content));

			this.content = content;
			this.info = info;

			bg = Chrome.get(Chrome.Type.TOAST);
			addToBack(bg);
		}

		@Override
		protected void onPointerDown() {
			super.onPointerDown();
		}

		@Override
		protected void onPointerUp() {
			super.onPointerUp();
		}

		@Override
		protected void layout() {
			super.layout();

			if (bg != null) {
				bg.size(width, height);
				bg.x = x;
				bg.y = y;
			}
		}

		@Override
		protected void onClick() {
			if (info){
				GameScene.show(new WndTitledMessage(new HeroIcon(content), Messages.titleCase(content.name()), content.desc()));
			} else {
				hide();
				content.onSelect(Dungeon.hero);
			}
		}

		@Override
		protected boolean onLongClick() {
			return super.onLongClick();
		}

		@Override
		protected void onRightClick() {
			super.onRightClick();
			RightClickMenu r = new RightClickMenu(new Image(icon),
					Messages.titleCase(content.name()),
					Messages.get(WndYuzuShop.class, "cast"),
					Messages.get(WndYuzuShop.class, "info")){
				@Override
				public void onSelect(int index) {
					switch (index){
						default:
							//do nothing
							break;
						case 0:
							hide();
							content.onSelect(Dungeon.hero);
							break;
						case 1:
							GameScene.show(new WndTitledMessage(new HeroIcon(content), Messages.titleCase(content.name()), content.desc()));
							break;
					}
				}
			};
			parent.addToFront(r);
			r.camera = camera();
			PointF mousePos = PointerEvent.currentHoverPos();
			mousePos = camera.screenToCamera((int)mousePos.x, (int)mousePos.y);
			r.setPos(mousePos.x-3, mousePos.y-3);
		}

		@Override
		protected String hoverText() {
			return "_" + Messages.titleCase(content.name()) + "_\n" + content.shortDesc();
		}
	}

}

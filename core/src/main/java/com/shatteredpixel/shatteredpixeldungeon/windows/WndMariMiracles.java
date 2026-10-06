package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.miracles.MariMiracle;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CrossNecklace;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.RightClickMenu;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.input.PointerEvent;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Image;
import com.watabou.noosa.NinePatch;
import com.watabou.utils.DeviceCompat;
import com.watabou.utils.PointF;

import java.util.ArrayList;

public class WndMariMiracles extends Window {

	protected static final int WIDTH    = 120;

	public static int BTN_SIZE = 20;

	public WndMariMiracles(CrossNecklace cross, Hero cleric, boolean info){

		IconTitle title;
		if (!info){
			title = new IconTitle(new ItemSprite(cross), Messages.titleCase(Messages.get(this, "cast_title")));
		} else {
			title = new IconTitle(Icons.INFO.get(), Messages.titleCase(Messages.get(this, "info_title")));
		}

		title.setRect(0, 0, WIDTH, 0);
		add(title);

		IconButton btnInfo = new IconButton(info ? new ItemSprite(cross) : Icons.INFO.get()){
			@Override
			protected void onClick() {
				GameScene.show(new WndMariMiracles(cross, cleric, !info));
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

		for (int i = 1; i <= Talent.MAX_TALENT_TIERS; i++) {

			ArrayList<MariMiracle> miracles = MariMiracle.getMiracleList(cleric, i);

			if (!miracles.isEmpty() && i != 1){
				top += BTN_SIZE + 2;
				ColorBlock sep = new ColorBlock(WIDTH, 1, 0xFF000000);
				sep.y = top;
				add(sep);
				top += 3;
			}

			ArrayList<IconButton> miracleBtns = new ArrayList<>();

			for (MariMiracle miracle : miracles) {
				IconButton miracleBtn = new MiracleButton(miracle, cross, info);
				add(miracleBtn);
				miracleBtns.add(miracleBtn);
			}

			int left = 2 + (WIDTH - miracleBtns.size() * (BTN_SIZE + 4)) / 2;
			for (IconButton btn : miracleBtns) {
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

	public class MiracleButton extends IconButton {

		MariMiracle miracle;
		CrossNecklace cross;
		boolean info;

		NinePatch bg;

		public MiracleButton(MariMiracle miracle, CrossNecklace cross, boolean info){
			super(new HeroIcon(miracle));

			this.miracle = miracle;
			this.cross = cross;
			this.info = info;

			if (!cross.canCast(Dungeon.hero, miracle)){
				icon.alpha( 0.3f );
			}
//			else if (miracle == GuidingLight.INSTANCE && miracle.chargeUse(Dungeon.hero) == 0){
//				icon.brightness(3);
//			}

			bg = Chrome.get(Chrome.Type.TOAST);
			addToBack(bg);
		}

		@Override
		protected void onPointerDown() {
			super.onPointerDown();
//			if (miracle == GuidingLight.INSTANCE && miracle.chargeUse(Dungeon.hero) == 0){
//				icon.brightness(4);
//			}
		}

		@Override
		protected void onPointerUp() {
			super.onPointerUp();
			if (!cross.canCast(Dungeon.hero, miracle)){
				icon.alpha( 0.3f );
			}
//			else if (miracle == GuidingLight.INSTANCE && miracle.chargeUse(Dungeon.hero) == 0){
//				icon.brightness(3);
//			}
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
				GameScene.show(new WndTitledMessage(new HeroIcon(miracle), Messages.titleCase(miracle.name()), miracle.desc()));
			} else {
				hide();


				if(!cross.canCast(Dungeon.hero, miracle)){
					GLog.w(Messages.get(CrossNecklace.class, "no_miracle"));
				} else {
					miracle.onCast(cross, Dungeon.hero);

					if (miracle.targetingFlags() != -1 && Dungeon.quickslot.contains(cross)){
						cross.targetingMiracle = miracle;
						QuickSlotButton.useTargeting(Dungeon.quickslot.getSlot(cross));
					}
				}

			}
		}

		@Override
		protected boolean onLongClick() {
			hide();
			cross.setQuickMiracle(miracle);
			return true;
		}

		@Override
		protected void onRightClick() {
			super.onRightClick();
			RightClickMenu r = new RightClickMenu(new Image(icon),
					Messages.titleCase(miracle.name()),
					Messages.get(WndMariMiracles.class, "cast"),
					Messages.get(WndMariMiracles.class, "info"),
					Messages.get(WndMariMiracles.class, "quick_cast")){
				@Override
				public void onSelect(int index) {
					switch (index){
						default:
							//do nothing
							break;
						case 0:
							hide();
							if(!cross.canCast(Dungeon.hero, miracle)){
								GLog.w(Messages.get(CrossNecklace.class, "no_miracle"));
							} else {
								miracle.onCast(cross, Dungeon.hero);

								if (miracle.targetingFlags() != -1 && Dungeon.quickslot.contains(cross)){
									cross.targetingMiracle = miracle;
									QuickSlotButton.useTargeting(Dungeon.quickslot.getSlot(cross));
								}
							}
							break;
						case 1:
							GameScene.show(new WndTitledMessage(new HeroIcon(miracle), Messages.titleCase(miracle.name()), miracle.desc()));
							break;
						case 2:
							hide();
							cross.setQuickMiracle(miracle);
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
			return "_" + Messages.titleCase(miracle.name()) + "_\n" + miracle.shortDesc();
		}
	}

}

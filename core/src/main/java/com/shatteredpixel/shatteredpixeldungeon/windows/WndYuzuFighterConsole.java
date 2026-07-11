package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.console.YuzuConsoleContent;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.active.console.Console;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RightClickMenu;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.input.PointerEvent;
import com.watabou.noosa.Image;
import com.watabou.noosa.NinePatch;
import com.watabou.utils.PointF;

import java.util.ArrayList;

public class WndYuzuFighterConsole extends Window {

	protected static final int WIDTH    = 126;

	public static int BTN_SIZE = 20;

	public WndYuzuFighterConsole(Console console, Hero yuzu, boolean info){
		ArrayList<YuzuConsoleContent> contents = YuzuConsoleContent.getContentList(yuzu, console);

		ArrayList<IconButton> contentBtns = new ArrayList<>();

		for (YuzuConsoleContent content : contents) {
			IconButton contentBtn = new ConsoleButton(content, yuzu, console, info);
			add(contentBtn);
			contentBtns.add(contentBtn);
		}

		final int SPACING = 4;
		final int ARROWS_LEFT = BTN_SIZE/2+(BTN_SIZE+SPACING)*2;
		int btnIndex = 0;
		for (IconButton btn : contentBtns) {
			if (btnIndex == 0) { //약공격
				btn.setRect((int)((BTN_SIZE+SPACING)/2), 0, BTN_SIZE, BTN_SIZE);
			}
			if (btnIndex == 1) { //강공격
				btn.setRect((int)((BTN_SIZE+SPACING)/2)+BTN_SIZE+SPACING, 0, BTN_SIZE, BTN_SIZE);
			}
			if (btnIndex == 2) { //기 모으기
				btn.setRect(0, BTN_SIZE+SPACING, BTN_SIZE, BTN_SIZE);
			}
//			if (btnIndex == 3) { //
//				btn.setRect(BTN_SIZE+SPACING, BTN_SIZE+SPACING, BTN_SIZE, BTN_SIZE);
//			}
			if (btnIndex == 3) { //왼쪽
				btn.setRect(ARROWS_LEFT, (int)((BTN_SIZE+SPACING)/2), BTN_SIZE, BTN_SIZE);
			}
			if (btnIndex == 4) { //위
				btn.setRect(ARROWS_LEFT + BTN_SIZE + SPACING, 0, BTN_SIZE, BTN_SIZE);
			}
			if (btnIndex == 5) { //아래
				btn.setRect(ARROWS_LEFT + BTN_SIZE + SPACING, BTN_SIZE + SPACING, BTN_SIZE, BTN_SIZE);
			}
			if (btnIndex == 6) { //오른쪽
				btn.setRect(ARROWS_LEFT + 2*(BTN_SIZE + SPACING), (int)((BTN_SIZE+SPACING)/2), BTN_SIZE, BTN_SIZE);
			}

			btnIndex++;
		}

		resize(WIDTH, BTN_SIZE*2);

		//if we are on mobile, offset the window down to just above the toolbar
		if (SPDSettings.interfaceSize() != 2){
			offset(0, (int) (GameScene.uiCamera.height/2 - 30 - height/2));
		}

	}

	public class ConsoleButton extends IconButton {

		YuzuConsoleContent content;
		Hero yuzu;
		Console console;
		boolean info;

		NinePatch bg;

		public ConsoleButton(YuzuConsoleContent content, Hero yuzu, Console console, boolean info){
			super(new HeroIcon(content));

			this.content = content;
			this.yuzu = yuzu;
			this.console = console;
			this.info = info;

			if (!content.canSelect(yuzu)){
				icon.alpha( 0.3f );
			} else if (content.isEnhanced(yuzu)) {
				icon.brightness(3);
			}

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
			if (!content.canSelect(yuzu)){
				icon.alpha( 0.3f );
			} else if (content.isEnhanced(yuzu)) {
				icon.brightness(3);
			}
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
			if (info) {
				GameScene.show(new WndTitledMessage(new HeroIcon(content), Messages.titleCase(content.name()), content.desc()));
			} else {
				if (!content.canSelect(Dungeon.hero)) {
					return;
				} else {
					executeContent();
				}
			}
		}

		@Override
		protected boolean onLongClick() {
			GameScene.show(new WndTitledMessage(new HeroIcon(content), Messages.titleCase(content.name()), content.desc()));
			return true;
		}

		@Override
		protected void onRightClick() {
			GameScene.show(new WndTitledMessage(new HeroIcon(content), Messages.titleCase(content.name()), content.desc()));
		}

		public void executeContent() {
			hide();
			content.onSelect(Dungeon.hero);
			content.onContentSelect(console, Dungeon.hero, info);
			Item.updateQuickslot();
		}
	}

}

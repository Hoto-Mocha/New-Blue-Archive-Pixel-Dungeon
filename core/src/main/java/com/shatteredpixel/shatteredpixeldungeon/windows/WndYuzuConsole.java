package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.console.YuzuConsoleContent;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.active.console.Console;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.noosa.NinePatch;

import java.util.ArrayList;

public class WndYuzuConsole extends Window {

	protected static final int WIDTH    = 120;

	public static int BTN_SIZE = 20;

	public WndYuzuConsole(Console console, Hero yuzu){
		ArrayList<YuzuConsoleContent> contents = YuzuConsoleContent.getContentList(yuzu, console);

		ArrayList<IconButton> contentBtns = new ArrayList<>();

		for (YuzuConsoleContent content : contents) {
			IconButton contentBtn = new ConsoleButton(content, yuzu, console);
			add(contentBtn);
			contentBtns.add(contentBtn);
		}
		int top = 0;

		int left = 2 + (WIDTH - contentBtns.size() * (BTN_SIZE + 4)) / 2;
		for (IconButton btn : contentBtns) {
			btn.setRect(left, 0, BTN_SIZE, BTN_SIZE);
			left += btn.width() + 4;
		}

		resize(WIDTH, top + BTN_SIZE);

		//if we are on mobile, offset the window down to just above the toolbar
		if (SPDSettings.interfaceSize() != 2){
			offset(0, (int) (GameScene.uiCamera.height/2 - 30 - height/2));
		}

	}

	public class ConsoleButton extends IconButton {

		YuzuConsoleContent content;
		Hero yuzu;
		Console console;

		NinePatch bg;

		public ConsoleButton(YuzuConsoleContent content, Hero yuzu, Console console){
			super(new HeroIcon(content));

			this.content = content;
			this.yuzu = yuzu;
			this.console = console;

			if (!content.canSelect(Dungeon.hero)){
				icon.alpha( 0.3f );
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
			if (!content.canSelect(Dungeon.hero)){
				icon.alpha( 0.3f );
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
			if (!content.canSelect(Dungeon.hero)) {
				return;
			} else {
				executeContent();
			}
		}

		public void executeContent() {
			hide();
			content.onSelect(Dungeon.hero);
			content.onContentSelect(console, Dungeon.hero);
			Item.updateQuickslot();
		}
	}

}

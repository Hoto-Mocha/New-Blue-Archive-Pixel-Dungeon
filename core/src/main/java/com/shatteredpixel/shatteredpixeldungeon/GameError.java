package com.shatteredpixel.shatteredpixeldungeon;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.watabou.noosa.Game;
import com.watabou.utils.FileUtils;

import java.io.PrintWriter;
import java.io.StringWriter;

public class GameError {
    public static void showError(Exception e) {
        String text = stackTrace(e);
        TextField textField;
        Skin skin = new Skin(FileUtils.getFileHandle(Files.FileType.Internal, "gdx/textfield.json"));
        TextField.TextFieldStyle style = skin.get(TextField.TextFieldStyle.class);
        style.font = Game.platform.getFont(6, "", false, false);
        style.background = null;
        textField = new TextField("", style);
        textField.setText(text);
        if (textField.getSelection().isEmpty()) {
            textField.selectAll();
        }
        textField.copy();

        GameScene.show(new WndTitledMessage(
                Icons.get(Icons.INFO),
                Messages.titleCase(title()),
                Messages.get(GameError.class, "desc")));
    }

    public static String title() {
        return Messages.get(GameError.class, "title");
    }

    public static String stackTrace(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }
}

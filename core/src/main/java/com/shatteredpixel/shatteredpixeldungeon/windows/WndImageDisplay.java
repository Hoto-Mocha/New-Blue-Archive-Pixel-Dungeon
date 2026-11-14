package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.input.PointerEvent;
import com.watabou.noosa.Image;
import com.watabou.noosa.PointerArea;

public class WndImageDisplay extends Window {

    private static final int MARGIN = 2;

    public static Image getImage(String imagePath) {
        return new Image(imagePath);
    }

    public WndImageDisplay(String imagePath, String titleText) {
        this(imagePath, titleText, true);
    }

    public WndImageDisplay(String imagePath, String titleText, boolean seen) {
        super();

        float currentY = 1 + MARGIN;
        Image image;

        image = getImage(imagePath);

        if (titleText != null && !titleText.isEmpty()) {
            RenderedTextBlock title = PixelScene.renderTextBlock(titleText, 9);
            title.hardlight(Window.TITLE_COLOR);
            title.setPos(MARGIN, currentY);
            add(title);
            currentY = title.bottom() + 2 + MARGIN;
        }

        float maxAllowedWidth = PixelScene.uiCamera.width - 20;
        float maxAllowedHeight = PixelScene.uiCamera.height - 30;

        float availableWidth = maxAllowedWidth;
        float availableHeight = maxAllowedHeight - currentY - MARGIN;

        float rawW = image.width();
        float rawH = image.height();

        float scaleX = availableWidth / rawW;
        float scaleY = availableHeight / rawH;
        float scale = Math.min(scaleX, scaleY);

        if (scale < 1.0f) {
            image.scale.set(scale);
        }

        float finalImgW = image.width();
        float finalImgH = image.height();

        float windowWidth = finalImgW;
        float windowHeight = currentY + finalImgH;

        image.x = (windowWidth - finalImgW) / 2f;
        image.y = currentY;

        if (!seen) {
            image.brightness(0);
        }
        add(image);

        resize((int) windowWidth, (int) windowHeight);

        PointerArea fullScreenTouch = new PointerArea(0, 0, PixelScene.uiCamera.width, PixelScene.uiCamera.height) {
            @Override
            protected void onClick(PointerEvent event) {
                hide();
            }
        };
        fullScreenTouch.camera = PixelScene.uiCamera;
        add(fullScreenTouch);
    }
}
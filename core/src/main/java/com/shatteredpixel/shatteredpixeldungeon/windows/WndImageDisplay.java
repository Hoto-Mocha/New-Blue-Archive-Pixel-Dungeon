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

        // 2. 제목 텍스트
        if (titleText != null && !titleText.isEmpty()) {
            RenderedTextBlock title = PixelScene.renderTextBlock(titleText, 9);
            title.hardlight(Window.TITLE_COLOR);
            title.setPos(MARGIN, currentY);
            add(title);
            currentY = title.bottom() + 2 + MARGIN;
        }

        // 3. 화면 크기(UI 카메라) 대비 최대 가용 영역 계산 (여백 고려)
        float maxAllowedWidth = PixelScene.uiCamera.width - 20;   // 화면 좌우 여백 20
        float maxAllowedHeight = PixelScene.uiCamera.height - 30; // 화면 상하 여백 30

        float availableWidth = maxAllowedWidth;
        float availableHeight = maxAllowedHeight - currentY - MARGIN;

        // 4. 원본 크기 대비 비율 계산 및 스케일링
        float rawW = image.width();  // 스케일 전 원본 너비
        float rawH = image.height(); // 스케일 전 원본 높이

        float scaleX = availableWidth / rawW;
        float scaleY = availableHeight / rawH;
        float scale = Math.min(scaleX, scaleY); // 가로/세로 중 더 많이 줄여야 하는 비율 선택

        // 이미지 크기가 가용 영역보다 클 경우 축소 (원본이 작다면 1.0 유지)
        if (scale < 1.0f) {
            image.scale.set(scale);
        }

        // 5. 스케일링이 적용된 최종 이미지 크기
        // (Visual.width() / height() 메서드는 scale이 적용된 크기를 반환합니다)
        float finalImgW = image.width();
        float finalImgH = image.height();

        // 6. 창 크기 계산
        float windowWidth = finalImgW;
        float windowHeight = currentY + finalImgH;

        // 7. 이미지 위치 지정 (창 내부 정중앙 정렬)
        image.x = (windowWidth - finalImgW) / 2f;
        image.y = currentY;

        if (!seen) {
            image.brightness(0);
        }
        add(image);

        // 8. 최종 창 크기 설정
        resize((int) windowWidth, (int) windowHeight);

        // 9. 화면 아무 곳이나 터치 시 창 닫기
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
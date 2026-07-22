package com.shatteredpixel.shatteredpixeldungeon.effects.particles;

import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.particles.PixelParticle;
import com.watabou.utils.PointF;

import java.util.ArrayList;
import java.util.List;

public class JusticeParticle extends PixelParticle {

    private static final List<PointF> POINTS_1 = createCirclePoints(3f);
    private static final List<PointF> POINTS_2 = createCirclePoints(4f);
    private static final List<PointF> POINTS_3 = createCirclePoints(5f);
    private static final List<PointF> POINTS_4 = createCirclePoints(6f);
    private static final List<PointF> POINTS_5 = createCirclePoints(7f);
    private static final List<PointF> POINTS_6 = createCirclePoints(8f);
    private static final List<PointF> POINTS_7 = createCirclePoints(9f);
    private static final List<PointF> POINTS_8 = createCirclePoints(10f);

    private static final List<PointF>[] pointGroups = new List[]{POINTS_1, POINTS_2, POINTS_3, POINTS_4, POINTS_5, POINTS_6, POINTS_7, POINTS_8};

    private static List<PointF> createCirclePoints(float radius) {
        int degree = 3;
        int repeatNum = 360/degree;
        int expectedCapacity = (int) (2 * radius + 1) * 2 + repeatNum;
        List<PointF> points = new ArrayList<>(expectedCapacity);
        final float stepRad = (float) Math.toRadians(3);

        for (int i = 0; i < repeatNum; i++) { // 360도 / 5도 = 120회 반복
            float angleRad = i * stepRad;
            float x = radius * (float) Math.cos(angleRad);
            float y = radius * (float) Math.sin(angleRad);
            points.add(new PointF(x, y));
        }

        return points;
    }

    // 픽셀의 위치와 크기를 함께 저장하는 클래스
    static class SizedPointF {
        PointF point;
        float size;

        SizedPointF(PointF point, float size) {
            this.point = point;
            this.size = size;
        }
    }

    private static final List<SizedPointF> CROSS_POINTS = createStarPoints();
    private static List<SizedPointF> createStarPoints() {
        int radius = 8;
        float size = 2f;
        float unit = 0.5f;
        int expectedCapacity = (int) Math.ceil((radius + 1) * 2 / unit);
        List<SizedPointF> points = new ArrayList<>(expectedCapacity);
        for (float i=-radius; i<=radius; i += 0.5f) {
            // 0.0 ~ 1.0 사이의 선형 비율
            float linearFactor = Math.abs(i) / (float) radius;

            float power = 1.5f; // 1.0이면 직선, 2.0 이상이면 곡선이 오목해져 뾰족한 별 모양이 됨
            float reductionFactor = (float) Math.pow(linearFactor, power);

            // 거리에 따라 크기를 줄여나감. 중심에서 멀어질수록 작아짐
            float dynamicSize = size * (1.0f - reductionFactor);

            // 중심점 (i=0)에서의 중복 생성을 방지하기 위한 조건문 추가
            if (i == 0) {
                // 중심점은 한 번만 추가하고, 크기는 원래 size로 설정
                points.add(new SizedPointF(new PointF(0, 0), size));
            } else {
                // 가로/세로 축에 대해 점을 추가하고, 계산된 dynamicSize 적용
                points.add(new SizedPointF(new PointF(0, i), dynamicSize));
                points.add(new SizedPointF(new PointF(i, 0), dynamicSize));
            }
        }
        return points;
    }

    public static Emitter.Factory factory() {
        return new Emitter.Factory() {
            @Override
            public void emit(Emitter emitter, int index, float x, float y) {
                final float unitDelay = 0.02f;
                final float duration = 0.15f;
                for (int i = 0; i < pointGroups.length; i++) {
                    List<PointF> points = pointGroups[i];
                    float delay = i * unitDelay;

                    for (PointF p : points) {
                        JusticeParticle particle = (JusticeParticle) emitter.recycle(JusticeParticle.class);
                        particle.reset(x + p.x, y + p.y, duration, delay);
                    }
                }
                for (SizedPointF p : CROSS_POINTS) {
                    JusticeParticle particle = (JusticeParticle) emitter.recycle(JusticeParticle.class);
                    particle.reset(x + p.point.x, y + p.point.y, duration+0.1f, duration, p.size);
                }
            }

            @Override
            public boolean lightMode() {
                return true;
            }
        };
    }

    public JusticeParticle() {
        super();
        color(0xFFFF00);
        am = 0;
    }

    float duration;

    public void reset( float x, float y, float duration, float delay) {
        reset(x, y, duration, delay, 1);
    }

    public void reset( float x, float y, float duration, float delay, float size) {
        revive();

        this.x = x;
        this.y = y;

        left = lifespan = duration + delay;
        this.duration = duration;

        size(size);
    }

    @Override
    public void update() {
        super.update();
        if (left <= duration) {
            am = calculateAlpha(left, duration);
        }
    }

    public static float calculateAlpha(float x, float a) {
        if (x < 0 || x > a || a <= 0) return 0f;

        // 최대 Y값이 1로 고정된 삼각 함수식
        return 1f - (2f / a) * Math.abs(x - (a / 2f));
    }
}

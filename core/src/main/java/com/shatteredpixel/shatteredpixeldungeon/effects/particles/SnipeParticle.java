package com.shatteredpixel.shatteredpixeldungeon.effects.particles;

import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.particles.PixelParticle;
import com.watabou.utils.PointF;

import java.util.ArrayList;

public class SnipeParticle extends PixelParticle {
    public static ArrayList<PointF> points = new ArrayList<>();
    static {
        points.add( new PointF( 0, 0 ) );

        float radius = 10f;
        for (float i=-radius; i<=radius; i++) {
            points.add( new PointF( 0, i) );
            points.add( new PointF( i, 0) );
        }

        for (int angleDeg = 0; angleDeg < 360; angleDeg += 5) {
            float angleRad = (float) Math.toRadians(angleDeg); // 도 → 라디안
            float x = radius * (float) Math.cos(angleRad);
            float y = radius * (float) Math.sin(angleRad);
            points.add( new PointF( x, y ) );
        }
    }

    private final float speedMulti = 1f;

    public static Emitter.Factory factory() {
        return new Emitter.Factory() {
            @Override
            public void emit(Emitter emitter, int index, float x, float y) {
                for (PointF p : points) {
                    ((SnipeParticle)emitter.recycle( SnipeParticle.class )).reset( x + p.x, y + p.y );
                }
            }
            @Override
            public boolean lightMode() {
                return false;
            }
        };
    }

    public SnipeParticle() {
        super();
        color(0x000000);
        am = 0;
    }

    public void reset( float x, float y ) {
        revive();

        this.x = x;
        this.y = y;

        left = lifespan = 3;

        size = 1f;
    }

    @Override
    public void update() {
        float first = 0.5f;
        float last = 0.5f;
        super.update();
        if (left >= lifespan-first) { //처음 0.5초
            am = 1f - (left - (lifespan - first)) / first;
        } else if (left >= last) { //중간 2초
            am = 1f;
        } else { //마지막 0.5초
            am = left / last;
        }
    }
}

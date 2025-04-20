package com.shatteredpixel.shatteredpixeldungeon.effects.particles;

import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.PointF;

import java.util.ArrayList;

public class SnipeOuterParticle extends SnipeParticle {
    private static final ArrayList<PointF> POINTS = new ArrayList<>();
    static {
        POINTS.add( new PointF( 0, 0 ) );

        float radius = 11f;
        for (int angleDeg = 0; angleDeg < 360; angleDeg += 3) {
            float angleRad = (float) Math.toRadians(angleDeg); // 도 → 라디안
            float x = radius * (float) Math.cos(angleRad);
            float y = radius * (float) Math.sin(angleRad);
            POINTS.add( new PointF( x, y ) );
        }
    }

    public static Emitter.Factory factory() {
        return new Emitter.Factory() {
            @Override
            public void emit(Emitter emitter, int index, float x, float y) {
                for (PointF p : POINTS) {
                    ((SnipeOuterParticle)emitter.recycle( SnipeOuterParticle.class )).reset( x + p.x, y + p.y );
                }
            }
        };
    }

    public SnipeOuterParticle() {
        super();
        color(0xCCCCCC);
        am = 0;
    }
}

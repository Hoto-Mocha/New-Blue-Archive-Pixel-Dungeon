package com.shatteredpixel.shatteredpixeldungeon.effects.particles;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.particles.PixelParticle;
import com.watabou.utils.Callback;
import com.watabou.utils.PointF;

import java.util.ArrayList;

public class SnipeParticle extends PixelParticle {
    private static final ArrayList<PointF> POINTS = new ArrayList<>();
    static {
        POINTS.add( new PointF( 0, 0 ) );

        float radius = 10f;
        for (float i=-radius; i<=radius; i++) {
            POINTS.add( new PointF( 0, i) );
            POINTS.add( new PointF( i, 0) );
        }

        for (int angleDeg = 0; angleDeg < 360; angleDeg += 3) {
            float angleRad = (float) Math.toRadians(angleDeg); // 도 → 라디안
            float x = radius * (float) Math.cos(angleRad);
            float y = radius * (float) Math.sin(angleRad);
            POINTS.add( new PointF( x, y ) );
        }
    }

    public static Emitter.Factory factory(Char target, int tier, int lvl, Callback callback) {
        return new Emitter.Factory() {
            @Override
            public void emit(Emitter emitter, int index, float x, float y) {
                for (PointF p : POINTS) {
                    ((SnipeParticle)emitter.recycle( SnipeParticle.class )).reset( x + p.x, y + p.y );
                }
                CellEmitter.center(target.pos).burst(ShootParticle.factory(target, tier, lvl, callback), 1);
                CellEmitter.center(target.pos).burst(SnipeOuterParticle.factory(), 1);
            }
        };
    }

    public SnipeParticle() {
        super();
        color(0x000000);
        am = 0;
    }

    protected final float SPEED_MULTI = 2f;

    protected final float FIRST = 0.5f/ SPEED_MULTI; //처음 구간
    protected final float LAST = 0.5f/ SPEED_MULTI; //마지막 구간
    protected final float MIDDLE_REST = 1f/ SPEED_MULTI; //처음 구간 이후 총 발사 전에 쉬는 구간
    protected final float MIDDLE_SHOOT_TIME = 0.1f/ SPEED_MULTI; //총 발사로 조준점이 올라가는 구간
    protected final float MIDDLE_SHOOT_REST = 0.4f/ SPEED_MULTI; //총 발사 후 조준점이 내려오는 구간
    //총 발사 후 쉬는 구간 이후 마지막 구간 사이는 아무것도 하지 않음

    public void reset( float x, float y) {
        revive();

        this.x = x;
        this.y = y;

        left = lifespan = 3/ SPEED_MULTI;

        size(1f);
    }

    @Override
    public void update() {
        float speedX = 0;
        float accX = 0;
        float speedY;
        float accY = 0;
        float time; //각 구간에서 소요되는 시간을 대입하여 사용
        float aimDistance = 3f; //처음과 마지막 구간에서 조준 시 이동 거리
        super.update();
        if (left > lifespan- FIRST) { //[3.0~2.5)초
            time = FIRST;
            am = 1f - (left - (lifespan - FIRST)) / FIRST; //0~first 구간에서 투명도가 0~1이 되도록 조절
            
            float incY = (aimDistance/time)/ SPEED_MULTI; //Y 좌표의 이동 거리

            speedY = (this.y - incY - this.y)* SPEED_MULTI; //현재 y 좌표에서 incY만큼 위로 올라감; -incY인 이유는 안드로이드 그래픽 좌표에서는 y가 증가할수록 아래로 가기 때문
            accY = -2*(this.y - incY - this.y)* SPEED_MULTI * SPEED_MULTI; //도착 지점에서 속도가 0이 됨

        } else if (left > LAST) { //[2.5~0.5)초
            am = 1f;

            float distance = 3;
            float incY = 0;
            if (left > lifespan- FIRST - MIDDLE_REST) { //[2.5~1.5)초
                //움직이지 않고 대기
            } else if (left > lifespan- FIRST - MIDDLE_REST - MIDDLE_SHOOT_TIME) { //[1.5~1.4)초
                time = MIDDLE_SHOOT_TIME;
                incY = ((distance/time)/ SPEED_MULTI); //Y 좌표의 이동 거리
            } else if (left > lifespan- FIRST - MIDDLE_REST - MIDDLE_SHOOT_TIME - MIDDLE_SHOOT_REST) { //[1.4~1.0)초
                time = MIDDLE_SHOOT_REST;
                incY = ((-distance/time)/ SPEED_MULTI); //Y 좌표의 이동 거리
                accY = -2*(this.y - incY - this.y)* SPEED_MULTI * SPEED_MULTI; //도착 지점에서 속도가 0이 됨
            }

            speedY = (this.y - incY - this.y)* SPEED_MULTI;
        } else { //[0.5~0.0]초
            time = LAST;
            am = left / LAST; //(lifespan-last)~lifespan 구간에서 투명도가 1~0이 되도록 조절

            //처음 구간과 동일하나 아래로 이동함
            float incY = (-aimDistance/time)/ SPEED_MULTI;

            speedY = (this.y - incY - this.y)* SPEED_MULTI;
            accY = -2*(this.y - incY - this.y)* SPEED_MULTI * SPEED_MULTI;

        }
        acc.set( accX, accY );
        speed.set( 2*speedX, 2*speedY );
    }
}

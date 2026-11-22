package com.shatteredpixel.shatteredpixeldungeon.effects.particles;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HeroSprite;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.particles.Emitter;

public class AfterImageParticle extends Image {

    public static Emitter.Factory factory() {
        return factory(-1, 0.2f, 0.5f);
    }

    public static Emitter.Factory factory(int color, float lifespan, float alpha) {
        return new Emitter.Factory() {
            @Override
            public void emit(Emitter emitter, int index, float x, float y) {
                ((AfterImageParticle)emitter.recycle( AfterImageParticle.class )).reset( x, y, color, lifespan, alpha );
            }

            @Override
            public boolean lightMode() {
                return true;
            }
        };
    }

    public static Emitter.Factory rainbowFactory() {
        return rainbowFactory(0.2f, 0.5f, 30f);
    }

    public static Emitter.Factory rainbowFactory(float lifespan, float alpha, float speed) {
        return new Emitter.Factory() {
            @Override
            public void emit(Emitter emitter, int index, float x, float y) {
                ((AfterImageParticle)emitter.recycle( AfterImageParticle.class )).resetRainbow( x, y, lifespan, alpha, speed );
            }

            @Override
            public boolean lightMode() {
                return true;
            }
        };
    }

    private float lifespan;
    private float left;

    public AfterImageParticle() {
        super();
    }

    public void reset(float x, float y, int color, float lifespan, float alpha) {
        revive();

        HeroSprite heroSprite = (HeroSprite) Dungeon.hero.sprite;
        if (heroSprite != null) {
            copy(heroSprite);

            this.x = heroSprite.x;
            this.y = heroSprite.y;
            this.flipHorizontal = heroSprite.flipHorizontal;

            updateFrame();
            updateVertices();
        } else {
            this.x = x;
            this.y = y;
        }

        this.lifespan = lifespan;
        left = this.lifespan;

        alpha(alpha);

        if (color != -1) {
            tint(color, 0.5f);
        }
    }

    public void resetRainbow(float x, float y, float lifespan, float alpha, float speed) {
        revive();

        HeroSprite heroSprite = (HeroSprite) Dungeon.hero.sprite;
        if (heroSprite != null) {
            copy(heroSprite);

            this.x = heroSprite.x;
            this.y = heroSprite.y;
            this.flipHorizontal = heroSprite.flipHorizontal;

            updateFrame();
            updateVertices();
        } else {
            this.x = x;
            this.y = y;
        }

        this.lifespan = lifespan;
        left = lifespan;

        alpha(alpha);

        float interval = (Game.timeTotal * speed) % 3f;
        tint(interval > 2 ? interval - 2 : Math.max(0, 1 - interval),
                interval > 1 ? Math.max(0, 2-interval): interval,
                interval > 2 ? Math.max(0, 3-interval): interval-1, 0.5f);
    }

    @Override
    public void update() {
        super.update();

        left -= Game.elapsed;
        if (left <= 0) {
            kill();
        } else {
            // 시간에 따라 0으로 페이드아웃
            alpha(0.5f * (left / lifespan));
        }
    }
}
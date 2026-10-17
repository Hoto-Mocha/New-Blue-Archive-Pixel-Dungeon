package com.shatteredpixel.shatteredpixeldungeon.effects;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.mari.MariTrinity;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.watabou.noosa.Game;
import com.watabou.noosa.Gizmo;

public class RGBBlock extends Gizmo {

	private static final float CYCLE_TIME = 3.0f;
	private final CharSprite target;
	private final MariTrinity.TrinityBuff buff;

	public RGBBlock(CharSprite target, MariTrinity.TrinityBuff buff) {
		this.target = target;
		this.buff = buff;
	}

	@Override
	public void update() {
		super.update();
		if (buff == null || target == null) return;

		float interval = (Game.timeTotal % CYCLE_TIME) / (CYCLE_TIME / 3f);

		float r0 = 1.0f;
		float g0 = buff.red() ? 0.0f : 1.0f;
		float b0 = buff.red() ? 0.0f : 1.0f;

		float r1 = buff.yellow() ? 0.0f : 1.0f;
		float g1 = 1.0f;
		float b1 = buff.yellow() ? 0.0f : 1.0f;

		float r2 = buff.blue() ? 0.0f : 1.0f;
		float g2 = buff.blue() ? 0.0f : 1.0f;
		float b2 = 1.0f;

		float r, g, b;

		if (interval < 1.0f) {
			float t = interval;
			r = r0 + (r1 - r0) * t;
			g = g0 + (g1 - g0) * t;
			b = b0 + (b1 - b0) * t;
		} else if (interval < 2.0f) {
			float t = interval - 1.0f;
			r = r1 + (r2 - r1) * t;
			g = g1 + (g2 - g1) * t;
			b = b1 + (b2 - b1) * t;
		} else {
			float t = interval - 2.0f;
			r = r2 + (r0 - r2) * t;
			g = g2 + (g0 - g2) * t;
			b = b2 + (b0 - b2) * t;
		}

		target.tint(r, g, b, 0.5f);
	}

	public void darken() {
		target.resetColor();
		killAndErase();
	}

	public static RGBBlock lighten(CharSprite sprite, MariTrinity.TrinityBuff buff) {
		RGBBlock glowBlock = new RGBBlock(sprite, buff);
		if (sprite.parent != null) {
			sprite.parent.add(glowBlock);
		}
		return glowBlock;
	}

}

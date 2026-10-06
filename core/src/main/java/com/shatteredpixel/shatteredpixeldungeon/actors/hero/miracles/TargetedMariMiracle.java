package com.shatteredpixel.shatteredpixeldungeon.actors.hero.miracles;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CrossNecklace;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;

public abstract class TargetedMariMiracle extends MariMiracle {

    @Override
    public void onCast(CrossNecklace cross, Hero hero ){
        GameScene.selectCell(new CellSelector.Listener() {
            @Override
            public void onSelect(Integer cell) {
                onTargetSelected(cross, hero, cell);
            }

            @Override
            public String prompt() {
                return targetingPrompt();
            }
        });
    }

    @Override
    public int targetingFlags(){
        return Ballistica.MAGIC_BOLT;
    }

    protected String targetingPrompt(){
        return Messages.get(this, "prompt");
    }

    protected abstract void onTargetSelected(CrossNecklace tome, Hero hero, Integer target);

}

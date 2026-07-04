package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;
import com.watabou.utils.Random;

public class QuestEnemy extends Buff {

    {
        type = buffType.POSITIVE;
    }

    protected int color = 0xFFFFFF;
    protected int rays = 6;

    @Override
    public int icon() {
        return BuffIndicator.CORRUPT;
    }

    @Override
    public void tintIcon(Image icon) {
        icon.hardlight(color);
    }

    @Override
    public void fx(boolean on) {
        if (on) target.sprite.aura( color, rays );
        else target.sprite.clearAura();
    }

    {
        immunities.add(AllyBuff.class);
    }

    public static void rollForQuest(Mob m){
        if (Dungeon.hero == null || Dungeon.hero.heroClass != HeroClass.YUZU) return;
        if (Dungeon.mobsToQuest <= 0) Dungeon.mobsToQuest = 8;
        if (m.buff(ChampionEnemy.class) != null) return;

        Dungeon.mobsToQuest--;

        if (Dungeon.mobsToQuest <= 0 && Dungeon.hero != null) {
            Buff.affect(m, QuestEnemy.class);
            m.state = m.WANDERING;
        }
    }

    @Override
    public void detach() {
        super.detach();
        Buff.affect(Dungeon.hero, QuestEnemyTracker.class);
        Dungeon.level.drop(new Gold().random(), target.pos).sprite.drop();
    }

    public static class QuestEnemyTracker extends Buff {}

}

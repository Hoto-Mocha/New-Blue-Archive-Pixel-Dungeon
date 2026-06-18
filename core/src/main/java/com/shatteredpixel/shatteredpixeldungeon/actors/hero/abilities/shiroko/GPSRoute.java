package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.shiroko;

import static com.shatteredpixel.shatteredpixeldungeon.Dungeon.branch;
import static com.shatteredpixel.shatteredpixeldungeon.Dungeon.hero;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AscensionChallenge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.YellowParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TalismanOfForesight;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfExperience;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfStrength;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMagicMapping;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTransmutation;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Door;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;

import java.util.ArrayList;

public class GPSRoute extends ArmorAbility {
    {
        baseChargeUse = 50f;
    }

    @Override
    public int icon() {
        return HeroIcon.SHIROKO_3;
    }

    @Override
    public float chargeUse(Hero hero) {
        float chargeUse = super.chargeUse(hero);
        if (hero.buff(RouteBuff.class) != null && hero.buff(RouteBuff.class).onPath() && hero.hasTalent(Talent.SHIROKO_ARMOR3_1)) {
            switch (hero.pointsInTalent(Talent.SHIROKO_ARMOR3_1)) {
                case 1: default:
                    chargeUse *= 0.7f;
                    break;
                case 2:
                    chargeUse *= 0.49f;
                    break;
                case 3:
                    chargeUse *= 0.34f;
                    break;
                case 4:
                    chargeUse *= 0.24f;
                    break;
            }
        }
        return chargeUse;
    }

    @Override
    protected void activate(ClassArmor armor, Hero hero, Integer target) {
        if (Dungeon.branch != 0) {
            Dungeon.hero.yellN("cant_find_path");
            return; //브랜치 층에서는 작동하지 않음
        }

        if (hero.buff(RouteBuff.class) != null) {
            if (hero.hasTalent(Talent.SHIROKO_ARMOR3_1)) {
                jump(armor, hero, hero.buff(RouteBuff.class).startPos());
            } else {
                GLog.w(Messages.get(this, "already_active"));
            }
            return;
        }

        int len = Dungeon.level.length();
        boolean[] p = Dungeon.level.passable;
        boolean[] s = Dungeon.level.secret;
        boolean[] a = Dungeon.level.avoid;
        boolean[] passable = new boolean[len];
        for (int i = 0; i < len; i++) {
            passable[i] = (p[i] || s[i]) && !a[i];
        }

        int destination = Dungeon.level.exit();
        if (hero.buff(AscensionChallenge.class) != null) {
            destination = Dungeon.level.entrance();
        }
        PathFinder.Path path = Dungeon.findPath(hero, destination, passable, passable, false);
        if (PathFinder.distance[destination] == Integer.MAX_VALUE || path == null){
            Dungeon.hero.yellN("cant_find_path");
        } else {
            useAbility(armor, hero);
            hero.busy();

            int[] map = Dungeon.level.map;
            boolean[] mapped = Dungeon.level.mapped;
            boolean[] discoverable = Dungeon.level.discoverable;
            ArrayList<Integer> way = new ArrayList<>();
            way.add(hero.pos);

            for (int i : path) {
                if (discoverable[i]) {
                    int terr = map[i];

                    mapped[i] = true;
                    way.add(i);
                    if ((Terrain.flags[terr] & Terrain.SECRET) != 0) {

                        Dungeon.level.discover( i );

                        if (Dungeon.level.heroFOV[i]) {
                            GameScene.discoverTile( i, terr );
                            ScrollOfMagicMapping.discover( i );
                        }
                    }
                }
            }
            Buff.affect(hero, RouteBuff.class).setup(way, hero.pos, 8f*(1+0.25f*hero.pointsInTalent(Talent.SHIROKO_ARMOR3_2)));
            GameScene.updateFog();
            hero.yellP("found_path");

            hero.sprite.operate(hero.pos);
            Sample.INSTANCE.play(Assets.Sounds.BEACON);
            hero.next();
        }
    }

    public void jump(ClassArmor armor, Hero hero, Integer target) {
        if (hero.pos == target) {
            GLog.w(Messages.get(this, "cannot_use"));
            return;
        }
        hero.busy();
        Sample.INSTANCE.play(Assets.Sounds.MISS);
        hero.sprite.emitter().start(Speck.factory(Speck.JET), 0.01f, Math.round(4 + 2*Dungeon.level.trueDistance(hero.pos, target)));
        hero.sprite.jump(hero.pos, target, 0, 0.1f, new Callback() {
            @Override
            public void call() {
                if (Dungeon.level.map[hero.pos] == Terrain.OPEN_DOOR) {
                    Door.leave( hero.pos );
                }
                hero.pos = target;
                Dungeon.level.occupyCell(hero);
                useAbility(armor, hero);
                hero.next();
            }
        });
    }

    public void useAbility(ClassArmor armor, Hero hero) {
        int[] map = Dungeon.level.map;

        for (int i = 0; i < map.length; i++) {
            Heap h = Dungeon.level.heaps.get(i);
            if (h != null){
                boolean discoverable = false;
                for (Item item : h.items) {
                    if (item instanceof PotionOfStrength        && hero.pointsInTalent(Talent.SHIROKO_ARMOR3_3) >= 1) {
                        discoverable = true;
                        break;
                    }
                    if (item instanceof ScrollOfUpgrade         && hero.pointsInTalent(Talent.SHIROKO_ARMOR3_3) >= 2) {
                        discoverable = true;
                        break;
                    }
                    if (item instanceof PotionOfExperience      && hero.pointsInTalent(Talent.SHIROKO_ARMOR3_3) >= 3) {
                        discoverable = true;
                        break;
                    }
                    if (item instanceof ScrollOfTransmutation   && hero.pointsInTalent(Talent.SHIROKO_ARMOR3_3) >= 4) {
                        discoverable = true;
                        break;
                    }
                }
                if (discoverable) {
                    Buff.append(hero, TalismanOfForesight.HeapAwareness.class, 5).pos = h.pos;
                }
            }
        }

        Invisibility.dispel();
        armor.charge -= chargeUse(hero);
        armor.updateQuickslot();
    }

    public static class RouteBuff extends Buff {

        public int floor;
        public int startPos;
        public float left = 0;
        public float max = 0;

        private ArrayList<Integer> pathPositions = new ArrayList<>();
        private ArrayList<Emitter> pathEmitters = new ArrayList<>();

        {
            type = buffType.POSITIVE;
        }

        @Override
        public int icon() {
            return BuffIndicator.GPS_ROUTE;
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0, (max - left) / max);
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc", Messages.decimalFormat("#.##", left));
        }

        @Override
        public void fx(boolean on) {
            if (on){
                for (int i : pathPositions){
                    Emitter e = CellEmitter.get(i);
                    e.pour(YellowParticle.FACTORY, 0.05f);
                    pathEmitters.add(e);
                }
            } else {
                for (Emitter e : pathEmitters){
                    e.on = false;
                }
                pathEmitters.clear();
            }
        }

        public void setup(ArrayList<Integer> path, int from, float duration){
            this.pathPositions = path;
            this.startPos = from;

            if (target != null) {
                fx(false);
                fx(true);
            }

            this.left = this.max = duration;
            floor = Dungeon.depth;
        }

        public int startPos() {
            return startPos;
        }

        @Override
        public boolean act() {
            if (Dungeon.depth != floor){
                detach();

                spend(TICK);
                return true;
            }

            if (!pathPositions.contains(target.pos)){
                left--;
            }

            BuffIndicator.refreshHero();
            if (left <= 0){
                detach();
            }

            spend(TICK);
            return true;
        }

        private static final String PATH_POSITIONS = "path_positions";
        private static final String FLOOR = "floor";
        private static final String START_POS = "startPos";
        private static final String LEFT = "left";
        private static final String MAX = "max";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);

            int[] values = new int[pathPositions.size()];
            for (int i = 0; i < values.length; i ++)
                values[i] = pathPositions.get(i);
            bundle.put(PATH_POSITIONS, values);

            bundle.put(FLOOR, floor);
            bundle.put(START_POS, startPos);
            bundle.put(LEFT, left);
            bundle.put(MAX, max);
        }

        public boolean onPath() {
            return pathPositions.contains(target.pos);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);

            int[] values = bundle.getIntArray( PATH_POSITIONS );
            for (int value : values) {
                pathPositions.add(value);
            }

            floor = bundle.getInt(FLOOR);
            startPos = bundle.getInt(START_POS);
            left = bundle.getFloat(LEFT);
            max = bundle.getFloat(MAX);
        }
    }

    @Override
    public Talent[] talents() {
        return new Talent[]{Talent.SHIROKO_ARMOR3_1, Talent.SHIROKO_ARMOR3_2, Talent.SHIROKO_ARMOR3_3, Talent.HEROIC_ENERGY};
    }
}

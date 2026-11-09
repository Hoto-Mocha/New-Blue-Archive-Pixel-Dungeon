package com.shatteredpixel.shatteredpixeldungeon.journal;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.pajamas.AnimalPajamas;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.pajamas.PinkPajamas;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.racing.JacketRacingSuit;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.racing.LatexRacingSuit;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.swimsuit.BikiniSwimSuit;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.swimsuit.FrilledSwimSuit;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.swimsuit.SchoolSwimSuit;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.swimsuit.TrinitySwimSuit;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.utils.Bundle;
import com.watabou.utils.DeviceCompat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;

public enum Gallery {

    ARIS,
    NONOMI,
    MIYAKO,
    HOSHINO,
    SHIROKO,
    NOA,
    MIYU,
    YUZU,
    IZUNA,
    MIKA,
    MARI;

    private final LinkedHashMap<Class<?>, Boolean> seen = new LinkedHashMap<>();

    public static boolean isSeen(Class<?> cls, Gallery gallery){
        if (DeviceCompat.isDebug()) return true;
        for (Gallery cat : values()) {
            if (cat.name().equalsIgnoreCase(gallery.name()) && cat.seen.containsKey(cls)) {
                return cat.seen.get(cls);
            }
        }
        return false;
    }

    public static void setSeen(Class<?> cls, HeroClass heroClass){
        for (Gallery cat : values()) {
            if (cat.name().equalsIgnoreCase(heroClass.name()) && cat.seen.containsKey(cls) && !cat.seen.get(cls)) {
                cat.seen.put(cls, true);
                Journal.saveNeeded = true;
            }
        }
        Badges.validateCatalogBadges();
    }

    private void addArmors(Class<?>... classes ){
        for (Class<?> cls : classes){
            seen.put(cls, false);
        }
    }

    public Collection<Class<?>> armors(){
        return seen.keySet();
    }

    public String title(){
        return Messages.get(HeroClass.class, name());
    }

    public int totalArmors() {
        return seen.size();
    }

    public int totalSeen(){
        int seenTotal = 0;
        for (boolean itemSeen : seen.values()){
            if (itemSeen) seenTotal++;
        }
        return seenTotal;
    }

    static {
        for (Gallery gallery : values()) {
            gallery.addArmors(SchoolSwimSuit.class, TrinitySwimSuit.class, FrilledSwimSuit.class, BikiniSwimSuit.class,
                    JacketRacingSuit.class, LatexRacingSuit.class,
                    PinkPajamas.class, AnimalPajamas.class);
        }
    }

    private static final String ARMOR_SEEN_PREFIX = "armor_seen_";

    public static void store( Bundle bundle ){
        for (Gallery gallery : values()) {
            ArrayList<Class<?>> seenArmors = new ArrayList<>();
            for (Class<?> armor : gallery.armors()) {
                if (gallery.seen.get(armor)) {
                    seenArmors.add(armor);
                }
            }
            Class<?>[] storeCls = seenArmors.toArray(new Class[0]);
            bundle.put(ARMOR_SEEN_PREFIX + gallery.name(), storeCls);
        }
    }

    public static void restore( Bundle bundle ){
        for (Gallery gallery : values()) {
            String key = ARMOR_SEEN_PREFIX + gallery.name();
            if (bundle.contains(key)) {
                Class<?>[] classes = bundle.getClassArray(key);
                if (classes != null) {
                    for (Class<?> cls : classes) {
                        if (gallery.seen.containsKey(cls)) {
                            gallery.seen.put(cls, true);
                        }
                    }
                }
            }
        }
    }
}

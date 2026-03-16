package com.javarush.island.domain.factory;

import com.javarush.island.domain.aggregate.animal.Animal;
import com.javarush.island.domain.aggregate.animal.predator.*;
import com.javarush.island.domain.aggregate.animal.herbivore.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class AnimalFactory {

    public record AnimalPrototype(String name, Supplier<Animal> factory) {}

    private static final List<AnimalPrototype> PROTOTYPES = List.of(
            new AnimalPrototype("Wolf", Wolf::new),
            new AnimalPrototype("Snake", Snake::new),
            new AnimalPrototype("Fox", Fox::new),
            new AnimalPrototype("Bear", Bear::new),
            new AnimalPrototype("Eagle", Eagle::new),
            new AnimalPrototype("Horse", Horse::new),
            new AnimalPrototype("Deer", Deer::new),
            new AnimalPrototype("Rabbit", Rabbit::new),
            new AnimalPrototype("Mouse", Mouse::new),
            new AnimalPrototype("Goat", Goat::new),
            new AnimalPrototype("Sheep", Sheep::new),
            new AnimalPrototype("Boar", Boar::new),
            new AnimalPrototype("Buffalo", Buffalo::new),
            new AnimalPrototype("Duck", Duck::new),
            new AnimalPrototype("Caterpillar", Caterpillar::new)
    );

    public static Animal create(String species) {
        return PROTOTYPES.stream()
                .filter(p -> p.name().equals(species))
                .findFirst()
                .map(p -> p.factory().get())
                .orElseThrow(() -> new IllegalArgumentException("Неизвестный вид: " + species));
    }

    public static List<AnimalPrototype> getAllPrototypes() {
        return PROTOTYPES;
    }

    // кэш иконок чтоб не создавать объект каждый раз ради getIcon()
    private static final Map<String, String> ICON_CACHE = new HashMap<>();
    static {
        for (AnimalPrototype p : PROTOTYPES) {
            ICON_CACHE.put(p.name(), p.factory().get().getIcon());
        }
    }

    public static String getIcon(String species) {
        return ICON_CACHE.getOrDefault(species, "?");
    }
}

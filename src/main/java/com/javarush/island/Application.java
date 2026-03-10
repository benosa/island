package com.javarush.island;

import com.javarush.island.application.handler.AnimalLifecycleHandler;
import com.javarush.island.application.handler.PlantGrowthHandler;
import com.javarush.island.application.handler.StatisticsHandler;
import com.javarush.island.domain.SimulationServiceImpl;
import com.javarush.island.domain.aggregate.island.Island;
import com.javarush.island.domain.ports.out.StatisticsPort;
import com.javarush.island.infrastructure.adapters.ConsoleStatisticsAdapter;
import com.javarush.island.infrastructure.configuration.SimulationConfig;
import com.javarush.island.infrastructure.configuration.SimulationScheduler;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class Application {

    public static void main(String[] args) {
        SimulationConfig config = new SimulationConfig();
        Island island = new Island(config.getIslandRows(), config.getIslandCols());
        SimulationServiceImpl simulationService = new SimulationServiceImpl(island, config);
        StatisticsPort statisticsPort = new ConsoleStatisticsAdapter();

        simulationService.initialize();

        AnimalLifecycleHandler lifecycleHandler = new AnimalLifecycleHandler(simulationService);
        PlantGrowthHandler plantHandler = new PlantGrowthHandler(simulationService);
        StatisticsHandler statisticsHandler = new StatisticsHandler(simulationService, statisticsPort);

        SimulationScheduler scheduler = new SimulationScheduler();
        AtomicBoolean running = new AtomicBoolean(true);
        int tickMs = config.getTickDurationMs();

        System.out.println("=== Симуляция острова ===");
        System.out.printf("Размер: %dx%d клеток%n", config.getIslandRows(), config.getIslandCols());
        System.out.printf("Такт: %d мс, максимум тактов: %d%n", tickMs, config.getMaxTicks());
        System.out.println("Потоки: Project Loom (virtual threads)");
        System.out.println("Запуск...");

        scheduler.scheduleAtFixedRate(() -> {
            if (!running.get()) return;
            plantHandler.run();
            lifecycleHandler.run();
        }, 0, tickMs, TimeUnit.MILLISECONDS);

        scheduler.scheduleAtFixedRate(() -> {
            if (!running.get()) return;
            statisticsHandler.run();
            if (simulationService.isSimulationOver()) {
                System.out.println("\nСимуляция завершена.");
                running.set(false);
                scheduler.shutdown();
            }
        }, tickMs / 2, tickMs, TimeUnit.MILLISECONDS);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nОстановка симуляции...");
            running.set(false);
            scheduler.shutdown();
        }));
    }
}

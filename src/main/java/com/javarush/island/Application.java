package com.javarush.island;

import com.javarush.island.application.handler.AnimalLifecycleHandler;
import com.javarush.island.application.handler.PlantGrowthHandler;
import com.javarush.island.application.handler.StatisticsHandler;
import com.javarush.island.domain.SimulationServiceImpl;
import com.javarush.island.domain.aggregate.island.Island;
import com.javarush.island.domain.ports.in.SimulationUseCase;
import com.javarush.island.domain.ports.out.SimulationConfigPort;
import com.javarush.island.domain.ports.out.StatisticsPort;
import com.javarush.island.infrastructure.adapters.ConsoleMenuAdapter;
import com.javarush.island.infrastructure.adapters.ConsoleStatisticsAdapter;
import com.javarush.island.infrastructure.configuration.SimulationConfig;
import com.javarush.island.infrastructure.configuration.SimulationScheduler;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class Application {

    public static void main(String[] args) {
        SimulationConfig config = new SimulationConfig();
        Island island = new Island(config.getIslandRows(), config.getIslandCols());

        SimulationConfigPort configPort = config;
        StatisticsPort statisticsPort = new ConsoleStatisticsAdapter();

        SimulationUseCase simulationUseCase = new SimulationServiceImpl(island, configPort);
        simulationUseCase.initialize();

        AnimalLifecycleHandler lifecycleHandler = new AnimalLifecycleHandler(simulationUseCase);
        PlantGrowthHandler plantHandler = new PlantGrowthHandler(simulationUseCase);
        StatisticsHandler statisticsHandler = new StatisticsHandler(simulationUseCase, statisticsPort);

        AtomicBoolean running = new AtomicBoolean(true);

        // меню для live-настроек
        ConsoleMenuAdapter menu = new ConsoleMenuAdapter(config, running);
        menu.startInputListener();

        SimulationScheduler scheduler = new SimulationScheduler();
        int tickMs = config.getTickDurationMs();

        System.out.println("=== Симуляция острова ===");
        System.out.printf("Размер: %dx%d, такт: %d мс%n", config.getIslandRows(), config.getIslandCols(), tickMs);
        System.out.println("Нажмите Enter для паузы и меню настроек");
        System.out.println();

        scheduler.scheduleAtFixedRate(() -> {
            if (!running.get() || menu.getPaused().get()) return;
            Thread.ofVirtual().start(() -> {
                plantHandler.run();
                lifecycleHandler.run();
            });
        }, 0, tickMs, TimeUnit.MILLISECONDS);

        scheduler.scheduleAtFixedRate(() -> {
            if (!running.get() || menu.getPaused().get()) return;
            statisticsHandler.run();
            if (simulationUseCase.isSimulationOver()) {
                System.out.println("\nСимуляция завершена.");
                running.set(false);
                scheduler.shutdown();
            }
        }, tickMs / 2, tickMs, TimeUnit.MILLISECONDS);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            running.set(false);
            scheduler.shutdown();
        }));
    }
}

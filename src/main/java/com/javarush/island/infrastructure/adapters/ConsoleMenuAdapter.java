package com.javarush.island.infrastructure.adapters;

import com.javarush.island.infrastructure.configuration.SimulationConfig;

import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class ConsoleMenuAdapter {

    private final SimulationConfig config;
    private final AtomicBoolean running;
    private final AtomicBoolean paused = new AtomicBoolean(false);

    public ConsoleMenuAdapter(SimulationConfig config, AtomicBoolean running) {
        this.config = config;
        this.running = running;
    }

    public AtomicBoolean getPaused() {
        return paused;
    }

    // запускаем слушатель ввода в отдельном потоке
    public void startInputListener() {
        Thread.ofVirtual().name("menu-listener").start(() -> {
            Scanner scanner = new Scanner(System.in);
            while (running.get()) {
                try {
                    if (scanner.hasNextLine()) {
                        scanner.nextLine(); // Enter нажат
                        paused.set(true);
                        showMenu(scanner);
                        paused.set(false);
                    }
                } catch (Exception e) {
                    // терминал закрыт
                    break;
                }
            }
        });
    }

    private void showMenu(Scanner scanner) {
        System.out.println();
        System.out.println("=== МЕНЮ НАСТРОЕК (симуляция на паузе) ===");
        System.out.println("1. Изменить скорость (текущий такт: " + config.getTickDurationMs() + " мс)");
        System.out.println("2. Изменить макс. тактов (текущий: " + config.getMaxTicks() + ")");
        System.out.println("3. Изменить рост растений (текущий: " + config.getPlantGrowthRate() + ")");
        System.out.println("0. Продолжить симуляцию");
        System.out.print("> ");

        String input = scanner.nextLine().trim();
        switch (input) {
            case "1" -> {
                System.out.print("Новая длительность такта (мс): ");
                String val = scanner.nextLine().trim();
                try {
                    int ms = Integer.parseInt(val);
                    if (ms > 0) {
                        config.setTickDurationMs(ms);
                        System.out.println("Установлено: " + ms + " мс");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Неверный формат");
                }
            }
            case "2" -> {
                System.out.print("Новый макс. тактов: ");
                String val = scanner.nextLine().trim();
                try {
                    int ticks = Integer.parseInt(val);
                    if (ticks > 0) {
                        config.setMaxTicks(ticks);
                        System.out.println("Установлено: " + ticks);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Неверный формат");
                }
            }
            case "3" -> {
                System.out.print("Новый коэффициент роста (напр. 1.5): ");
                String val = scanner.nextLine().trim();
                try {
                    double rate = Double.parseDouble(val);
                    if (rate > 0) {
                        config.setPlantGrowthRate(rate);
                        System.out.println("Установлено: " + rate);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Неверный формат");
                }
            }
            case "0" -> System.out.println("Продолжаем...");
            default -> System.out.println("Продолжаем...");
        }
    }
}

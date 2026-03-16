# Island Simulation

Симуляция экосистемы острова с животными и растениями. Животные едят, размножаются, перемещаются и умирают от голода. Остров представляет собой сетку клеток с рекой посередине.

## Запуск

```bash
JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 ./gradlew run --console=plain
```

Требуется Java 21+ (virtual threads). Управление: `Enter` для паузы и меню настроек, `Ctrl+C` для выхода.

## Структура проекта

Проект построен на **шестигранной архитектуре** (Ports & Adapters):

```
src/main/java/com/javarush/island/
├── Application.java                         # Точка входа, сборка зависимостей
│
├── domain/                                  # ЯДРО - бизнес-логика, ноль внешних зависимостей
│   ├── ports/
│   │   ├── in/
│   │   │   └── SimulationUseCase.java       # Входной порт (что можно делать с симуляцией)
│   │   └── out/
│   │       ├── StatisticsPort.java          # Куда выводить статистику
│   │       └── SimulationConfigPort.java    # Откуда брать настройки
│   │
│   ├── aggregate/
│   │   ├── animal/
│   │   │   ├── Organism.java                # Интерфейс всего живого
│   │   │   ├── Animal.java                  # Абстрактный класс с общим поведением
│   │   │   ├── Direction.java               # Направления перемещения
│   │   │   ├── predator/                    # Хищники (Wolf, Snake, Fox, Bear, Eagle)
│   │   │   └── herbivore/                   # Травоядные (Horse, Deer, Rabbit, Mouse, ...)
│   │   ├── island/
│   │   │   ├── Island.java                  # Сетка клеток, генерация реки
│   │   │   ├── Cell.java                    # Одна клетка с животными и растениями
│   │   │   └── TerrainType.java             # PLAIN / RIVER
│   │   └── plant/
│   │       ├── Plant.java                   # Абстрактное растение
│   │       ├── Grass.java                   # Трава (1 кг)
│   │       └── Bush.java                    # Кустарник (3 кг)
│   │
│   ├── config/
│   │   └── FeedingMatrix.java               # Таблица вероятностей "кто кого ест"
│   │
│   ├── factory/
│   │   └── AnimalFactory.java               # Создание животных + кэш иконок
│   │
│   └── SimulationServiceImpl.java           # Реализация SimulationUseCase
│
├── application/                             # ВХОДНЫЕ АДАПТЕРЫ (кто вызывает домен)
│   └── handler/
│       ├── AnimalLifecycleHandler.java      # Запуск тика жизненного цикла
│       ├── PlantGrowthHandler.java          # Запуск роста растений
│       └── StatisticsHandler.java           # Сбор и вывод статистики
│
└── infrastructure/                          # ВЫХОДНЫЕ АДАПТЕРЫ (реализации портов)
    ├── adapters/
    │   ├── ConsoleStatisticsAdapter.java    # Реализация StatisticsPort (вывод в консоль)
    │   └── ConsoleMenuAdapter.java          # Интерактивное меню для смены параметров
    └── configuration/
        ├── SimulationConfig.java            # Реализация SimulationConfigPort (читает properties)
        └── SimulationScheduler.java         # ScheduledExecutorService для тиков
```

## Архитектура

### Шестигранная архитектура

Домен ничего не знает про инфраструктуру. Зависимости идут внутрь:

```
[Application.java]
    │
    ├── создаёт infrastructure (SimulationConfig, ConsoleStatisticsAdapter, SimulationScheduler)
    ├── создаёт domain (SimulationServiceImpl) через порты
    └── создаёт application (handlers) которые связывают infrastructure с domain
```

**Поток вызовов каждый такт:**
```
SimulationScheduler (infrastructure)
  → AnimalLifecycleHandler (application)
    → SimulationUseCase.processLifecycleTick() (domain port)
      → SimulationServiceImpl (domain) обходит клетки через virtual threads
        → каждая клетка: eat → reproduce → move → consumeEnergy
```

### Иерархия животных

```
Organism (interface)
├── Animal (abstract)
│   ├── Predator (abstract)
│   │   ├── Wolf      50кг  скорость:3  стайная охота
│   │   ├── Snake     15кг  скорость:1
│   │   ├── Fox        8кг  скорость:2
│   │   ├── Bear     500кг  скорость:2  не пересекает реку
│   │   └── Eagle      6кг  скорость:3
│   └── Herbivore (abstract)
│       ├── Horse    400кг  скорость:4  не пересекает реку
│       ├── Deer     300кг  скорость:4
│       ├── Rabbit     2кг  скорость:2
│       ├── Mouse   0.05кг  скорость:1  ест гусениц
│       ├── Goat      60кг  скорость:3
│       ├── Sheep     70кг  скорость:3
│       ├── Boar     400кг  скорость:2  ест мышей и гусениц
│       ├── Buffalo  700кг  скорость:3  не пересекает реку
│       ├── Duck       1кг  скорость:4  ест гусениц
│       └── Caterpillar 0.01кг  скорость:0  не двигается
└── Plant (abstract)
    ├── Grass   1кг   70% при росте
    └── Bush    3кг   30% при росте
```

### Многопоточность

- **ScheduledExecutorService** (2 platform-потока) -тикает по расписанию
- **Virtual threads (Project Loom)** -обработка клеток параллельно через `Executors.newVirtualThreadPerTaskExecutor()`
- **ReentrantLock** в каждой клетке -защита списков животных и растений
- **AtomicBoolean/AtomicInteger** -контроль состояния симуляции и глобальный лимит популяции
- **volatile boolean processed** -защита от двойной обработки при перемещении между клетками

### Рельеф

Река генерируется вертикально посередине острова, слегка виляя. Тяжёлые животные (Buffalo, Horse, Bear) не могут её пересечь. На реке не растут растения и не спавнятся животные.

### Стайная охота волков

Когда на клетке несколько волков, каждый получает +15% к вероятности поймать добычу за каждого дополнительного волка в стае.

## Настройки

Все параметры в `src/main/resources/simulation.properties`:

- `island.rows`, `island.cols` -размер острова
- `simulation.tickDurationMs` -длительность такта (мс)
- `simulation.maxTicks` -максимум тактов
- `simulation.stopCondition` -`ALL_DEAD` или `MAX_TICKS`
- `simulation.maxAnimals` -глобальный лимит популяции
- `plant.growthRate`, `plant.maxPerCell` -рост растений
- `animal.initial.*` -стартовое количество каждого вида
- `animal.offspring.*` -количество детёнышей
- `animal.maxPerCell.*` -максимум особей вида на клетке

Параметры можно менять на лету через меню (Enter во время симуляции).

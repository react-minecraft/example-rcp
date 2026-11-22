# API для RCP

RCP (Runtime Communication Protocol) — это jar в `plugins/React/.rcp/providers/`, который плагин загружает в свой
загрузчик классов. Всё нужное лежит в модуле `react-api`, в пакете `com.g4vrk.react.api.rcp`.

## rcp-metadata.yml

```yaml
name: example        # имя папки RCP: plugins/React/.rcp/example/
version: 1.0.0
mainClass: com.example.rcp.ExampleRCP
api-version: 2       # должен совпадать с RCPAPI.VERSION плагина, иначе RCP не загрузится
```

`name` должен быть одним именем папки, без `/`, `.` и `..`.

## Жизненный цикл

Главный класс реализует `RCPLifecycle` или наследует `RCPProvider`. Конструктор принимает `RCPEnvironment`
или не принимает ничего.

| Метод         | Когда вызывается                                         |
|---------------|----------------------------------------------------------|
| `onEnable()`  | После загрузки: на старте сервера и после `/react reboot` |
| `onReload()`  | После `/react reload`; здесь перечитывают свои файлы     |
| `onDisable()` | Перед выгрузкой                                          |

Всё, что зарегистрировано через `RCPEnvironment`, плагин снимает сам при выгрузке.

## RCPEnvironment

| Метод                            | Что даёт                                                                 |
|----------------------------------|--------------------------------------------------------------------------|
| `directory()`                    | Папку `.rcp/<name>/`, созданную заранее                                  |
| `logger()`, `platform()`         | Логгер RCP и доступ к текущей серверной платформе                         |
| `scheduler()`                    | Schedula: глобальные, асинхронные и привязанные к игроку задачи           |
| `players()`                      | `RCPPlayer` по UUID, по id сущности и всех онлайн                         |
| `listen(RCPPlayerListener)`      | Повороты, сброс состояния, выход игрока, reload                          |
| `listeners().register(Object)`   | Слушатель React, Bukkit или PacketEvents (`PacketListenerCommon`)       |
| `commands()`                     | Команды Cloud: `command(label)` даёт билдер с правом `react.command.<label>`, `register(label, builder)` регистрирует |
| `actions()`                      | Парсер действий FunctionalActions (`[msg]`, `[sound]`, …) со всеми встроенными действиями плагина |
| `text()`                         | Форматтер текста плагина (MiniMessage/legacy)                            |
| `fire(player, InferenceResult)`  | Передачу результата модели в проверку плагина: алерты, наказания, история, события |
| `status(String)`                 | Строку в `/react rcps` и `%react_model%`                                 |

Команды регистрируются прямо в Cloud. Можно создать свой корень или добавить подкоманду в `/react`
(вместе с его алиасами). При выгрузке плагин снимает только команды этого RCP, а команды плагина и
других расширений остаются.

```java
final CommandRegistrar commands = environment.commands();
commands.register("snap", commands.command("snap")
        .handler(context -> context.sender().sendMessage(Component.text("snap"))));
commands.register("react", commands.command("react")
        .literal("snapinfo")
        .handler(context -> context.sender().sendMessage(Component.text("info"))));
```

## Кадры моделей

RCP может публиковать строки датасета через `environment.listeners().post(new ModelFrameEvent(...))`.
Аддон подписывается через `environment.listeners().subscribe(ModelFrameEvent.class, ...)`, проверяет
`model()` и `columns()`, и при этом не зависит от классов RCP. Событие приходит в потоке производителя
(обычно это поток сущности игрока), поэтому обработчик должен быть быстрым.

## Повороты

`onRotation(player, float[] sample)` получает плоский массив из `RotationSample.STRIDE` чисел: `pitch`, `yaw`,
дельты, рывки и ошибки GCD. Читайте его через `RotationSample.deltaYaw(sample, 0)` и соседние методы.
Плагин переиспользует массив, поэтому копируйте его, если значения нужны после вызова.
`RCPPlayer.rotations()` отдаёт историю в том же виде: `RotationSample.count(...)` поворотов подряд.

## Потоки

- `onRotation` вызывается в потоке источника события, обычно Netty. Для работы с миром планируйте задачу
  через `environment.scheduler().scheduleEntity(player.platform(), task, TickSchedule.instant())`.
- Слушатель игроков через `listen` можно регистрировать с любого потока.
- Снимки данных `RCPPlayer` и `fire` доступны из любого потока. Нативный объект получается через
  `player.unwrap(Player.class)` и подчиняется правилам потоков своей платформы.

## InferenceResult

```java
new InferenceResult(
        "example/snap",  // стабильный id модели
        "Example Snap",  // имя для игроков и персонала
        0.93,            // вероятность чита, от 0 до 1
        0.8,             // порог флага; Double.NaN берёт порог из конфига проверки
        true             // false: только наблюдать, без флагов и наказаний
);
```

## Версии

В метаданных RCP указывайте `api-version`, совпадающий с `RCPAPI.VERSION` плагина. Для сборок React 2.0.0
в разработке пересобирайте RCP против той же ревизии React, которую устанавливаете на сервер.

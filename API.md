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
| `logger()`, `plugin()`           | Логгер RCP и сам плагин React                                            |
| `scheduler()`                    | Планировщик Schedula, работает и на Folia                                |
| `players()`                      | `RCPPlayer` по UUID, по id сущности и всех онлайн                         |
| `listen(RCPPlayerListener)`      | Повороты, сброс состояния, выход игрока, reload                          |
| `registerPacketListener(Object)` | Слушатель PacketEvents (`PacketListenerCommon`)                          |
| `commands()`                     | Команды Cloud: `command(label)` даёт билдер с правом `react.command.<label>`, `register(label, builder)` регистрирует |
| `fire(player, InferenceResult)`  | Передачу результата модели в проверку плагина: алерты, наказания, история, события |
| `status(String)`                 | Строку в `/react rcps` и `%react_model%`                                 |

Корневую команду, которая уже есть на сервере (`react` и чужие), взять нельзя. Плагин снимает команды RCP
при выгрузке.

```java
final CommandRegistrar commands = environment.commands();
commands.register("snap", commands.command("snap")
        .handler(context -> context.sender().sendMessage("snap")));
```

## Повороты

`onRotation(player, float[] sample)` получает плоский массив из `RotationSample.STRIDE` чисел: `pitch`, `yaw`,
дельты, рывки и ошибки GCD. Читайте его через `RotationSample.deltaYaw(sample, 0)` и соседние методы.
Плагин переиспользует массив, поэтому копируйте его, если значения нужны после вызова.
`RCPPlayer.rotations()` отдаёт историю в том же виде: `RotationSample.count(...)` поворотов подряд.

## Потоки

- `onRotation` вызывается на потоке netty. Не блокируйте его и не трогайте мир: на поток сервера переходите
  через `scheduler()`.
- Регистрировать можно с любого потока.
- `fire` и методы `RCPPlayer` безопасны с любого потока.

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

`RCPAPI.VERSION` растёт, когда меняется то, что ломает собранные раньше RCP. Плагин грузит только RCP
с точно таким же `api-version`, поэтому после такого обновления плагина RCP нужно пересобрать под новый api.

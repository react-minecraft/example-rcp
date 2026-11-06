# Example RCP

Минимальный RCP для плагина [React](https://github.com/react-minecraft/plugin). В нём есть своя «модель», свой результат
в проверке и своя подкоманда `/react snap`. Это шаблон: склонируйте репозиторий и замените `SnapDetector` своей логикой.

Что делает пример:

- слушает повороты игроков в бою через `RCPPlayerListener`;
- считает резкие развороты по yaw (`SnapDetector`, порог 40°) в окне из 20 поворотов;
- отдаёт результат плагину через `RCPEnvironment.fire` как модель `example/snap`. Дальше плагин сам показывает алерты,
  хранит историю и шлёт события, как для своих моделей;
- помечает результат `enforcement = false`: он виден и сохраняется, но никого не наказывает;
- добавляет `/react snap` и `/react snap clear` (право `react.command.snap`);
- пишет статус в `/react rcps` и в плейсхолдер `%react_model%`.

## Сборка и установка

```bash
./gradlew build
```

1. Возьмите `build/libs/example-rcp-1.0.0.jar`.
2. Положите его в `plugins/React/.rcp/providers/`.
3. Перезапустите сервер или выполните `/react reboot`.
4. Проверьте `/react rcps`: там появится `example` с версией и `api-version`.

Свои файлы RCP хранит в `plugins/React/.rcp/example/`, это `RCPEnvironment.directory()`.

## Структура

| Файл                | Что внутри                                         |
|---------------------|----------------------------------------------------|
| `rcp-metadata.yml`  | Имя, версия, главный класс и `api-version`         |
| `ExampleRCP.java`   | Точка входа: регистрирует слушатель и команду      |
| `SnapDetector.java` | «Модель»: окно поворотов превращается в вероятность |
| `SnapListener.java` | Передаёт повороты в детектор и отдаёт `InferenceResult` |
| `SnapCommand.java`  | Подкоманда `/react snap`                           |

Подробно про API написано в [API.md](API.md).

## Зависимость

Плагин отдаёт api через JitPack. Версия задаётся в `gradle.properties` в ключе `reactApiVersion`: коммит или ветка плагина.
Если рядом лежит чекаут плагина (`../react-vector`), сборка берёт api из него.

```kotlin
repositories {
    maven("https://jitpack.io/")
}

dependencies {
    compileOnly("com.github.react-minecraft.plugin:react-api:VERSION")
}
```

<div align="center">

<a href="https://git.io/typing-svg"><img src="https://readme-typing-svg.demolab.com?font=Manrope&weight=700&size=38&pause=1000&color=EB2544&background=FFFFFF00&center=true&vCenter=true&width=700&lines=Example+RCP;Your+own+model+for+React" alt="Example RCP Typing SVG"/></a>

<p>Шаблон своего RCP для React</p>

<p>
  <a href="https://github.com/react-minecraft/example-rcp/blob/main/LICENSE">
    <img src="https://img.shields.io/github/license/react-minecraft/example-rcp?style=flat&logo=opensourceinitiative&logoColor=white" alt="License">
  </a>
</p>

<p>
  <a href="https://react-ac.space">Сайт</a>
  &nbsp;•&nbsp;
  <a href="https://t.me/react_ac">Канал</a>
  &nbsp;•&nbsp;
  <a href="https://t.me/react_ac_support">Поддержка</a>
</p>

</div>

---

## Что это?

- Минимальный RCP для плагина [React](https://github.com/react-minecraft/plugin): своя «модель», свой результат
  в проверке и своя подкоманда `/react snap`.
- Это шаблон: клонируете, меняете `SnapDetector` на свою логику и всё.

Что делает пример:

- слушает повороты игроков в бою через `RCPPlayerListener`;
- считает резкие развороты по yaw (`SnapDetector`, порог 40°) в окне из 20 поворотов;
- отдаёт результат плагину через `RCPEnvironment.fire` как модель `example/snap`, а дальше плагин сам показывает
  алерты, хранит историю и шлёт события, как для своих моделей;
- помечает результат `enforcement = false`: он виден и сохраняется, но никого не наказывает;
- добавляет `/react snap` и `/react snap clear` (право `react.command.snap`);
- пишет статус в `/react rcps` и в плейсхолдер `%react_model%`.

---

## Сборка & Установка

1. `./gradlew build`
2. Возьмите `build/libs/example-rcp-1.0.0.jar`
3. Положите его в `plugins/React/.rcp/providers/`
4. Перезапустите сервер или выполните `/react reboot`
5. Проверьте `/react rcps`: там появится `example` с версией и `api-version`

Свои файлы RCP хранит в `plugins/React/.rcp/example/`, это `RCPEnvironment.directory()`.

---

## Структура

| Файл                | Что внутри                                              |
|---------------------|---------------------------------------------------------|
| `rcp-metadata.yml`  | Имя, версия, главный класс и `api-version`              |
| `ExampleRCP.java`   | Точка входа: регистрирует слушатель и аргумент команды  |
| `SnapDetector.java` | «Модель»: окно поворотов превращается в вероятность     |
| `SnapListener.java` | Передаёт повороты в детектор и отдаёт `InferenceResult` |
| `SnapCommand.java`  | Подкоманда `/react snap`                                |

Подробно про API написано в [API.md](API.md).

---

## Зависимость

> [!IMPORTANT]
> RCP собирается только против `react-api` и грузится, пока совпадает `api-version`. Версия api задаётся в
> `gradle.properties` в ключе `reactApiVersion`: коммит или ветка плагина. Если рядом лежит чекаут плагина
> (`../react-vector`), сборка берёт api из него.

```kotlin
repositories {
    maven("https://jitpack.io/")
}

dependencies {
    compileOnly("com.github.react-minecraft.plugin:react-api:VERSION")
}
```

---

## Лицензия

Пример распространяется под MIT — см. [LICENSE](LICENSE).

<div align="center">

<a href="https://git.io/typing-svg"><img src="https://readme-typing-svg.demolab.com?font=Manrope&weight=700&size=38&pause=1000&color=EB2544&background=FFFFFF00&center=true&vCenter=true&width=700&lines=Example+RCP;Your+own+model+for+React" alt="Example RCP"/></a>

<p>Шаблон RCP для собственной логики анализа</p>

<p>
  <a href="https://react-ac.space"><img src="https://img.shields.io/badge/Website-EB2544?style=plastic&logo=googlechrome&logoColor=white" alt="Website"/></a>
  <a href="https://t.me/react_ac"><img src="https://img.shields.io/badge/Telegram-26A5E4?style=plastic&logo=telegram&logoColor=white" alt="Telegram"/></a>
  <a href="https://t.me/react_ac_support"><img src="https://img.shields.io/badge/Support-555555?style=plastic&logo=telegram&logoColor=white" alt="Support"/></a>
</p>

<p>
  <a href="https://github.com/react-minecraft/example-rcp/blob/main/LICENSE"><img src="https://img.shields.io/github/license/react-minecraft/example-rcp?style=plastic&logo=opensourceinitiative&logoColor=white" alt="License"/></a>
</p>

</div>

## Что делает пример

- Слушает повороты игроков в бою через `RCPPlayerListener`.
- Считает резкие развороты от 40° в окне из 20 поворотов и возвращает вероятность модели `example/snap`.
- Отдаёт результат через `RCPEnvironment.fire` с `enforcement = false`. React показывает и сохраняет такие результаты в режиме наблюдения.
- Добавляет команды `/snap` и `/snap clear`. Статус RCP виден в `/react rcps`.

## Сборка и установка

1. Используйте JDK 21. Запустите `./gradlew build` или `.\gradlew.bat build` на Windows.
2. Положите `build/libs/example-rcp-1.0.0.jar` в `plugins/React/.rcp/providers/`.
3. Выполните `/react reboot example` или перезапустите сервер. Проверьте `/react rcps`, где должен появиться `example`.
4. Настройки действий лежат в `plugins/React/.rcp/example/actions.yml`.

Пример собирается с `react-api` из JitPack. Коммит или тег зависимости задаётся ключом `reactApiVersion` в `gradle.properties`. Значение `api-version: 2` в `rcp-metadata.yml` должно совпадать с версией API плагина.

См. [API RCP](API.md) и [плагин React](https://github.com/react-minecraft/plugin).


## Лицензия

[MIT](LICENSE).

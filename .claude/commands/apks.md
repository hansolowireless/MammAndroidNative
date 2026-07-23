---
description: Compila los APK de release de todos los flavors y los deja listos para enviar a testers
---

Genera los APK de **release firmados de TODOS los flavors** (masmedia, fibrazo, eligetv) para enviar a probar.

Pasos:

1. Desde la raíz del proyecto, compila los tres a la vez:
   `./gradlew assembleMasmediaRelease assembleFibrazoRelease assembleEligetvRelease`
2. Localiza los APK en `app/build/outputs/apk/<flavor>/release/`. El nombre es `<flavor>-release-<versionName>-<versionCode>.apk` (definido en `app/build.gradle.kts`).
3. Copia los tres a una carpeta nueva `~/Desktop/MammApps-APKs-<versionName>/` (crea la carpeta; usa el versionName actual del build.gradle.kts).
4. Reporta la ruta final y el tamaño de cada APK.

Recuerda al usuario:
- Son APK de **release** (firmados, con R8/minify): para instalarlos directamente hay que permitir "instalar apps de orígenes desconocidos" en el dispositivo.
- Si el tester ya tiene la versión de Play instalada (misma firma) se actualiza sin desinstalar; si viene de otra firma, hay que desinstalar primero.

No subas nada a Google Play en este comando: solo compilar y copiar.

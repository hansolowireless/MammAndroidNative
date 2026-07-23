---
description: Flujo completo de release a Google Play (sube versión, commit propio, push develop, fastlane deploy al 20%)
argument-hint: <flavor> [flavor...]   (masmedia | fibrazo | eligetv)
---

Despliega a Google Play los flavors indicados en: **$ARGUMENTS**
Si no se indica ningún flavor, pregunta cuál(es) antes de continuar.

Flujo:

1. **Subir versión** en `app/build.gradle.kts` (~líneas 106-107): incrementa `versionCode` en +1 y pregunta al usuario el nuevo `versionName` (muéstrale el actual). El `versionCode` DEBE subir siempre o Play rechaza el AAB por duplicado.
2. **Commit propio** solo del bump de versión: `chore: subir versión a <versionName> (versionCode <N>)`.
3. **Push** a `develop`.
4. Para cada flavor indicado: `fastlane deploy target:<flavor>` desde la raíz. Sube a producción con **rollout progresivo al 20%**.
5. Al terminar, recuerda al usuario que la release queda **pendiente de publicación manual** en Play Console (publicación gestionada) y que allí se confirma y se avanza el rollout (20% → 50% → 100%).

Notas importantes:
- Las notas de "Novedades" se toman de `fastlane/metadata/<flavor>/...` automáticamente. **No las cambies**: masmedia/fibrazo usan "Correcciones y mejoras" y **eligetv usa un texto de marketing propio**.
- Si es la primera vez que un flavor sube un build con anuncios (IMA SDK), Play puede rechazar por la declaración de **Advertising ID**: hay que declararlo en Play Console → Contenido de la app → ID de publicidad. Ver la memoria `release-fastlane`.
- **No publiques ni avances el rollout automáticamente**: eso es manual en la consola. Un deploy sube binario a producción, así que confirma el/los flavor(s) con el usuario antes de lanzarlo si hay ambigüedad.

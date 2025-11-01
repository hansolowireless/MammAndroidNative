package com.mamm.mammapps.ui.component.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val MovieRoll: ImageVector
    get() {
        if (_MovieRoll != null) {
            return _MovieRoll!!
        }
        _MovieRoll = ImageVector.Builder(
            name = "MovieRoll",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(fill = SolidColor(Color(0xFF000000))) {
                // Marco exterior
                moveTo(160f, 840f)
                verticalLineTo(120f)
                horizontalLineTo(240f)
                verticalLineTo(200f)
                horizontalLineTo(320f)
                verticalLineTo(120f)
                horizontalLineTo(640f)
                verticalLineTo(200f)
                horizontalLineTo(720f)
                verticalLineTo(120f)
                horizontalLineTo(800f)
                verticalLineTo(840f)
                horizontalLineTo(720f)
                verticalLineTo(760f)
                horizontalLineTo(640f)
                verticalLineTo(840f)
                horizontalLineTo(320f)
                verticalLineTo(760f)
                horizontalLineTo(240f)
                verticalLineTo(840f)
                close()

                // Cuadrados pequeños izquierda (3)
                moveTo(240f, 680f)
                horizontalLineTo(160f)
                verticalLineTo(600f)
                horizontalLineTo(240f)
                close()

                moveTo(240f, 520f)
                horizontalLineTo(160f)
                verticalLineTo(440f)
                horizontalLineTo(240f)
                close()

                moveTo(240f, 360f)
                horizontalLineTo(160f)
                verticalLineTo(280f)
                horizontalLineTo(240f)
                close()

                // Cuadrados pequeños derecha (3)
                moveTo(720f, 680f)
                horizontalLineTo(800f)
                verticalLineTo(600f)
                horizontalLineTo(720f)
                close()

                moveTo(720f, 520f)
                horizontalLineTo(800f)
                verticalLineTo(440f)
                horizontalLineTo(720f)
                close()

                moveTo(720f, 360f)
                horizontalLineTo(800f)
                verticalLineTo(280f)
                horizontalLineTo(720f)
                close()

                // Pantalla central
                moveTo(400f, 760f)
                horizontalLineTo(560f)
                verticalLineTo(200f)
                horizontalLineTo(400f)
                close()
            }
        }.build()
        return _MovieRoll!!
    }

private var _MovieRoll: ImageVector? = null
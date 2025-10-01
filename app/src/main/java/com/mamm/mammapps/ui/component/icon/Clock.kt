package drawable

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Clock: ImageVector
    get() {
        if (_Clock != null) return _Clock!!
        
        _Clock = ImageVector.Builder(
            name = "Clock",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color(0xFF0F172A)),
                strokeLineWidth = 1.5f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(12f, 6f)
                verticalLineTo(12f)
                horizontalLineTo(16.5f)
                moveTo(21f, 12f)
                curveTo(21f, 16.9706f, 16.9706f, 21f, 12f, 21f)
                curveTo(7.02944f, 21f, 3f, 16.9706f, 3f, 12f)
                curveTo(3f, 7.02944f, 7.02944f, 3f, 12f, 3f)
                curveTo(16.9706f, 3f, 21f, 7.02944f, 21f, 12f)
                close()
            }
        }.build()
        
        return _Clock!!
    }

private var _Clock: ImageVector? = null


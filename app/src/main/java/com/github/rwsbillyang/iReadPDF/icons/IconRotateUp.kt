package com.github.rwsbillyang.iReadPDF.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

//text_rotate_up
@Suppress("CheckReturnValue")
public val IconRotateUp: ImageVector
    get() {
        if (_text_rotate_up != null) {
            return _text_rotate_up!!
        }
        _text_rotate_up =
            ImageVector.Builder(
                name = "text_rotate_up",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f,
            )
                .apply {
                    path(
                        fill = SolidColor(Color.Black),
                        fillAlpha = 1f,
                        stroke = null,
                        strokeAlpha = 1f,
                        strokeLineWidth = 1f,
                        strokeLineCap = StrokeCap.Butt,
                        strokeLineJoin = StrokeJoin.Bevel,
                        strokeLineMiter = 1f,
                        pathFillType = PathFillType.Companion.NonZero,
                    ) {
                        moveTo(19f, 20f)
                        horizontalLineTo(17f)
                        verticalLineTo(6.8f)
                        lineTo(15.95f, 7.85f)
                        lineToRelative(-1.4f, -1.4f)
                        lineTo(18f, 3f)
                        lineToRelative(3.5f, 3.45f)
                        lineToRelative(-1.45f, 1.4f)
                        lineTo(19f, 6.8f)
                        verticalLineTo(20f)
                        close()
                        moveTo(14f, 17.1f)
                        lineTo(3f, 13f)
                        verticalLineTo(11f)
                        lineTo(14f, 6.9f)
                        verticalLineTo(8.8f)
                        lineTo(11.2f, 9.75f)
                        verticalLineTo(14.2f)
                        lineToRelative(2.8f, 1f)
                        verticalLineToRelative(1.9f)
                        close()
                        moveTo(9.6f, 13.65f)
                        verticalLineToRelative(-3.3f)
                        lineToRelative(-4.55f, 1.6f)
                        verticalLineToRelative(0.1f)
                        lineToRelative(4.55f, 1.6f)
                        close()
                    }
                }
                .build()
        return _text_rotate_up!!
    }

private var _text_rotate_up: ImageVector? = null

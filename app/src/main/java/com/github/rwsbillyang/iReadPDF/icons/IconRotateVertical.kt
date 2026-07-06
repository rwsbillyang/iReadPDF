package com.github.rwsbillyang.iReadPDF.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

//text_rotate_vertical
@Suppress("CheckReturnValue")
public val IconRotateVertical: ImageVector
    get() {
        if (_text_rotate_vertical != null) {
            return _text_rotate_vertical!!
        }
        _text_rotate_vertical =
            ImageVector.Builder(
                name = "text_rotate_vertical",
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
                        moveTo(10.9f, 16f)
                        lineTo(15f, 5f)
                        horizontalLineToRelative(2f)
                        lineToRelative(4.1f, 11f)
                        horizontalLineTo(19.2f)
                        lineToRelative(-1f, -2.8f)
                        horizontalLineTo(13.8f)
                        lineToRelative(-1f, 2.8f)
                        horizontalLineTo(10.9f)
                        close()
                        moveToRelative(3.45f, -4.4f)
                        horizontalLineToRelative(3.3f)
                        lineTo(16.05f, 7.05f)
                        horizontalLineToRelative(-0.1f)
                        lineToRelative(-1.6f, 4.55f)
                        close()
                        moveTo(6f, 20f)
                        lineTo(2.5f, 16.5f)
                        lineTo(3.9f, 15.1f)
                        lineTo(5f, 16.15f)
                        verticalLineTo(3f)
                        horizontalLineTo(7f)
                        verticalLineTo(16.15f)
                        lineTo(8.1f, 15.1f)
                        lineToRelative(1.4f, 1.4f)
                        lineTo(6f, 20f)
                        close()
                    }
                }
                .build()
        return _text_rotate_vertical!!
    }

private var _text_rotate_vertical: ImageVector? = null

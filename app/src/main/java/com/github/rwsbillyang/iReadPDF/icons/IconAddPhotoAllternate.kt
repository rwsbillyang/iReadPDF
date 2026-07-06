package com.github.rwsbillyang.iReadPDF.icons


import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

//add_photo_alternate
@Suppress("CheckReturnValue")
public val IconAddPhotoAlternate: ImageVector
    get() {
        if (_add_photo_alternate != null) {
            return _add_photo_alternate!!
        }
        _add_photo_alternate =
            ImageVector.Builder(
                name = "add_photo_alternate",
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
                        moveTo(12f, 12f)
                        close()
                        moveTo(5f, 21f)
                        quadTo(4.18f, 21f, 3.59f, 20.41f)
                        reflectiveQuadTo(3f, 19f)
                        verticalLineTo(5f)
                        quadTo(3f, 4.17f, 3.59f, 3.59f)
                        reflectiveQuadTo(5f, 3f)
                        horizontalLineToRelative(8f)
                        quadToRelative(0f, 0.42f, 0f, 0.92f)
                        reflectiveQuadTo(13f, 5f)
                        horizontalLineTo(5f)
                        verticalLineTo(19f)
                        horizontalLineTo(19f)
                        verticalLineTo(11f)
                        quadToRelative(0.58f, 0f, 1.08f, 0f)
                        reflectiveQuadTo(21f, 11f)
                        verticalLineToRelative(8f)
                        quadToRelative(0f, 0.82f, -0.59f, 1.41f)
                        reflectiveQuadTo(19f, 21f)
                        horizontalLineTo(5f)
                        close()
                        moveTo(6f, 17f)
                        horizontalLineTo(18f)
                        lineTo(14.25f, 12f)
                        lineToRelative(-3f, 4f)
                        lineTo(9f, 13f)
                        lineTo(6f, 17f)
                        close()
                        moveTo(17f, 9f)
                        verticalLineTo(7f)
                        horizontalLineTo(15f)
                        verticalLineTo(5f)
                        horizontalLineToRelative(2f)
                        verticalLineTo(3f)
                        horizontalLineToRelative(2f)
                        verticalLineTo(5f)
                        horizontalLineToRelative(2f)
                        verticalLineTo(7f)
                        horizontalLineTo(19f)
                        verticalLineTo(9f)
                        horizontalLineTo(17f)
                        close()
                    }
                }
                .build()
        return _add_photo_alternate!!
    }

private var _add_photo_alternate: ImageVector? = null

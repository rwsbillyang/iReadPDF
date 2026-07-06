package com.github.rwsbillyang.iReadPDF.icons


import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

//swipe
@Suppress("CheckReturnValue")
public val IconSwipe: ImageVector
    get() {
        if (_swipe != null) {
            return _swipe!!
        }
        _swipe =
            ImageVector.Builder(
                name = "swipe",
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
                        moveTo(11.83f, 22f)
                        quadToRelative(-0.6f, 0f, -1.15f, -0.23f)
                        reflectiveQuadTo(9.7f, 21.13f)
                        lineTo(4.6f, 16f)
                        lineTo(5.35f, 15.23f)
                        quadToRelative(0.4f, -0.4f, 0.94f, -0.54f)
                        reflectiveQuadTo(7.35f, 14.7f)
                        lineTo(9f, 15.18f)
                        verticalLineTo(7f)
                        quadTo(9f, 6.57f, 9.29f, 6.29f)
                        quadTo(9.58f, 6f, 10f, 6f)
                        reflectiveQuadToRelative(0.71f, 0.29f)
                        reflectiveQuadTo(11f, 7f)
                        verticalLineTo(17.83f)
                        lineTo(8.58f, 17.15f)
                        lineToRelative(2.55f, 2.55f)
                        quadToRelative(0.13f, 0.13f, 0.31f, 0.21f)
                        reflectiveQuadTo(11.83f, 20f)
                        horizontalLineTo(16f)
                        quadToRelative(0.82f, 0f, 1.41f, -0.59f)
                        reflectiveQuadTo(18f, 18f)
                        verticalLineTo(14f)
                        quadToRelative(0f, -0.43f, 0.29f, -0.71f)
                        reflectiveQuadTo(19f, 13f)
                        reflectiveQuadToRelative(0.71f, 0.29f)
                        reflectiveQuadTo(20f, 14f)
                        verticalLineToRelative(4f)
                        quadToRelative(0f, 1.65f, -1.18f, 2.82f)
                        reflectiveQuadTo(16f, 22f)
                        horizontalLineTo(11.83f)
                        close()
                        moveTo(12f, 15f)
                        verticalLineTo(11f)
                        quadToRelative(0f, -0.43f, 0.29f, -0.71f)
                        reflectiveQuadTo(13f, 10f)
                        reflectiveQuadToRelative(0.71f, 0.29f)
                        reflectiveQuadTo(14f, 11f)
                        verticalLineToRelative(4f)
                        horizontalLineTo(12f)
                        close()
                        moveToRelative(3f, 0f)
                        verticalLineTo(12f)
                        quadToRelative(0f, -0.43f, 0.29f, -0.71f)
                        reflectiveQuadTo(16f, 11f)
                        quadToRelative(0.43f, 0f, 0.71f, 0.29f)
                        reflectiveQuadTo(17f, 12f)
                        verticalLineToRelative(3f)
                        horizontalLineTo(15f)
                        close()
                        moveToRelative(-0.5f, 2f)
                        close()
                        moveTo(22f, 7f)
                        horizontalLineTo(17f)
                        verticalLineTo(5.5f)
                        horizontalLineToRelative(2.9f)
                        quadTo(18.25f, 4.05f, 16.23f, 3.27f)
                        reflectiveQuadTo(12f, 2.5f)
                        reflectiveQuadTo(7.78f, 3.27f)
                        reflectiveQuadTo(4.1f, 5.5f)
                        horizontalLineTo(7f)
                        verticalLineTo(7f)
                        horizontalLineTo(2f)
                        verticalLineTo(2f)
                        horizontalLineTo(3.5f)
                        verticalLineTo(4.02f)
                        quadTo(5.3f, 2.55f, 7.48f, 1.77f)
                        reflectiveQuadTo(12f, 1f)
                        reflectiveQuadToRelative(4.53f, 0.77f)
                        reflectiveQuadTo(20.5f, 4.02f)
                        verticalLineTo(2f)
                        horizontalLineTo(22f)
                        verticalLineTo(7f)
                        close()
                    }
                }
                .build()
        return _swipe!!
    }

private var _swipe: ImageVector? = null

package com.asta.cherryweather.ui.components.canvas

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import com.asta.cherryweather.ui.theme.BackgroundDark
import com.asta.cherryweather.ui.theme.CherryBlossomPetalColor
import com.asta.cherryweather.ui.theme.CloudBubbleColor
import com.asta.cherryweather.ui.theme.FogLayerColor
import com.asta.cherryweather.ui.theme.LightningFlashColor
import com.asta.cherryweather.ui.theme.MoonGlowColor
import com.asta.cherryweather.ui.theme.RainDropColor
import com.asta.cherryweather.ui.theme.RainSplashColor
import com.asta.cherryweather.ui.theme.SnowflakeColor
import com.asta.cherryweather.ui.theme.StarTwinkleColor
import com.asta.cherryweather.ui.theme.SunGlowColor
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

/**
 * Main container for custom Canvas particle weather animations.
 * Maps Open-Meteo WMO weather codes to particle systems.
 */
@Composable
fun CherryWeatherCanvasAnimation(
    weatherCode: Int?,
    modifier: Modifier = Modifier,
    isDay: Boolean = true,
    animationsEnabled: Boolean = true
) {
    if (!animationsEnabled) {
        // Static ambient dark gradient when animations are disabled
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            BackgroundDark,
                            Color(0xFF140E1B),
                            BackgroundDark
                        )
                    )
                )
        )
        return
    }

    val code = weatherCode ?: 0

    Box(modifier = modifier.fillMaxSize()) {
        when (code) {
            // Rain / Rain Showers
            51, 53, 55, 56, 57, 61, 63, 65, 66, 67, 80, 81, 82 -> {
                RainCanvasAnimation(intensity = getRainIntensity(code))
            }
            // Thunderstorm
            95, 96, 99 -> {
                ThunderstormCanvasAnimation()
            }
            // Snow
            71, 73, 75, 77, 85, 86 -> {
                SnowCanvasAnimation()
            }
            // Fog / Mist
            45, 48 -> {
                FogCanvasAnimation()
            }
            // Clouds / Overcast
            2, 3 -> {
                CloudCanvasAnimation()
            }
            // Clear or Mainly Clear (0, 1)
            else -> {
                ClearSkyCanvasAnimation(isDay = isDay)
            }
        }
    }
}

private fun getRainIntensity(code: Int): Float {
    return when (code) {
        51, 61, 80 -> 0.4f // Light
        53, 63, 81 -> 0.7f // Moderate
        55, 65, 82, 67 -> 1.0f // Heavy
        else -> 0.6f
    }
}

// ==========================================
// 1. RAIN CANVAS ANIMATION
// ==========================================

private data class RainDrop(
    val xRatio: Float,
    val initialYRatio: Float,
    val speed: Float,
    val lengthPx: Float,
    val strokeWidthPx: Float,
    val alpha: Float
)

@Composable
private fun RainCanvasAnimation(intensity: Float = 0.7f) {
    val dropCount = (100 * intensity).toInt().coerceIn(60, 120)

    val rainDrops = remember(dropCount) {
        val rand = Random(42)
        List(dropCount) {
            RainDrop(
                xRatio = rand.nextFloat(),
                initialYRatio = rand.nextFloat(),
                speed = 0.8f + rand.nextFloat() * 1.2f,
                lengthPx = 25f + rand.nextFloat() * 45f,
                strokeWidthPx = 1.8f + rand.nextFloat() * 1.5f,
                alpha = 0.4f + rand.nextFloat() * 0.5f
            )
        }
    }

    val transition = rememberInfiniteTransition(label = "RainTransition")
    val animProgress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "RainAnim"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        rainDrops.forEach { drop ->
            val totalY = (drop.initialYRatio + animProgress * drop.speed) % 1f
            val startX = drop.xRatio * w
            val startY = totalY * h
            val endX = startX - 8f // Slanted rain drop
            val endY = startY + drop.lengthPx

            // Draw rain line
            drawLine(
                color = RainDropColor.copy(alpha = drop.alpha),
                start = Offset(startX, startY),
                end = Offset(endX, endY),
                strokeWidth = drop.strokeWidthPx
            )

            // Splash ripple near bottom
            if (totalY > 0.85f) {
                val splashRatio = (totalY - 0.85f) / 0.15f
                val radius = splashRatio * 20f
                val splashAlpha = (1f - splashRatio) * 0.6f
                drawCircle(
                    color = RainSplashColor.copy(alpha = splashAlpha.coerceIn(0f, 1f)),
                    radius = radius,
                    center = Offset(startX, h - 10f),
                    style = Stroke(width = 1.5f)
                )
            }
        }
    }
}

// ==========================================
// 2. THUNDERSTORM CANVAS ANIMATION
// ==========================================

@Composable
private fun ThunderstormCanvasAnimation() {
    val transition = rememberInfiniteTransition(label = "ThunderTransition")
    val flashAlpha by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "FlashAlpha"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // Base rain
        RainCanvasAnimation(intensity = 1.0f)

        // Lightning flash overlay
        Canvas(modifier = Modifier.fillMaxSize()) {
            val pulseAlpha = when {
                flashAlpha in 0.20f..0.24f -> 0.45f
                flashAlpha in 0.26f..0.28f -> 0.65f
                flashAlpha in 0.70f..0.73f -> 0.35f
                else -> 0f
            }

            if (pulseAlpha > 0f) {
                drawRect(color = LightningFlashColor.copy(alpha = pulseAlpha))

                // Stylized lightning bolt path
                val w = size.width
                val h = size.height
                val path = Path().apply {
                    moveTo(w * 0.6f, 0f)
                    lineTo(w * 0.52f, h * 0.35f)
                    lineTo(w * 0.58f, h * 0.35f)
                    lineTo(w * 0.45f, h * 0.75f)
                    lineTo(w * 0.50f, h * 0.75f)
                    lineTo(w * 0.42f, h * 1.0f)
                }
                drawPath(
                    path = path,
                    color = Color.White.copy(alpha = pulseAlpha * 0.8f),
                    style = Stroke(width = 4f)
                )
            }
        }
    }
}

// ==========================================
// 3. SNOW CANVAS ANIMATION
// ==========================================

private data class Snowflake(
    val xRatio: Float,
    val initialYRatio: Float,
    val speed: Float,
    val radiusPx: Float,
    val swayAmountPx: Float,
    val phaseOffset: Float,
    val alpha: Float
)

@Composable
private fun SnowCanvasAnimation() {
    val flakeCount = 90

    val flakes = remember {
        val rand = Random(88)
        List(flakeCount) {
            Snowflake(
                xRatio = rand.nextFloat(),
                initialYRatio = rand.nextFloat(),
                speed = 0.3f + rand.nextFloat() * 0.5f,
                radiusPx = 3f + rand.nextFloat() * 6f,
                swayAmountPx = 15f + rand.nextFloat() * 25f,
                phaseOffset = rand.nextFloat() * (2f * PI.toFloat()),
                alpha = 0.5f + rand.nextFloat() * 0.5f
            )
        }
    }

    val transition = rememberInfiniteTransition(label = "SnowTransition")
    val animProgress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "SnowAnim"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        flakes.forEach { flake ->
            val totalY = (flake.initialYRatio + animProgress * flake.speed) % 1f
            val sway = sin(animProgress * 2f * PI.toFloat() + flake.phaseOffset) * flake.swayAmountPx
            val cx = (flake.xRatio * w + sway).coerceIn(0f, w)
            val cy = totalY * h

            drawCircle(
                color = SnowflakeColor.copy(alpha = flake.alpha),
                radius = flake.radiusPx,
                center = Offset(cx, cy)
            )
        }
    }
}

// ==========================================
// 4. CLEAR DAY / NIGHT & PETALS ANIMATION
// ==========================================

private data class BlossomPetal(
    val xRatio: Float,
    val initialYRatio: Float,
    val speedY: Float,
    val sizePx: Float,
    val rotationStart: Float,
    val rotationSpeed: Float,
    val swayPx: Float,
    val alpha: Float
)

private data class Star(
    val xRatio: Float,
    val yRatio: Float,
    val radiusPx: Float,
    val pulsePhase: Float
)

@Composable
private fun ClearSkyCanvasAnimation(isDay: Boolean) {
    val petalCount = 45

    val petals = remember {
        val rand = Random(123)
        List(petalCount) {
            BlossomPetal(
                xRatio = rand.nextFloat(),
                initialYRatio = rand.nextFloat(),
                speedY = 0.25f + rand.nextFloat() * 0.4f,
                sizePx = 8f + rand.nextFloat() * 10f,
                rotationStart = rand.nextFloat() * 360f,
                rotationSpeed = 60f + rand.nextFloat() * 120f,
                swayPx = 20f + rand.nextFloat() * 35f,
                alpha = 0.6f + rand.nextFloat() * 0.35f
            )
        }
    }

    val stars = remember(isDay) {
        if (isDay) emptyList() else {
            val rand = Random(999)
            List(35) {
                Star(
                    xRatio = rand.nextFloat(),
                    yRatio = rand.nextFloat() * 0.6f, // upper night sky
                    radiusPx = 1.5f + rand.nextFloat() * 2.5f,
                    pulsePhase = rand.nextFloat() * (2f * PI.toFloat())
                )
            }
        }
    }

    val transition = rememberInfiniteTransition(label = "ClearTransition")
    val animProgress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ClearAnim"
    )

    val sunMoonPulse by transition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "SunMoonPulse"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        if (isDay) {
            // Glowing Cherry Sun
            val sunCenter = Offset(w * 0.8f, h * 0.18f)
            val baseRadius = 60f

            // Radial Glow Rings
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        SunGlowColor.copy(alpha = 0.45f),
                        SunGlowColor.copy(alpha = 0.15f),
                        Color.Transparent
                    ),
                    center = sunCenter,
                    radius = baseRadius * 2.8f * sunMoonPulse
                ),
                radius = baseRadius * 2.8f * sunMoonPulse,
                center = sunCenter
            )

            drawCircle(
                color = SunGlowColor,
                radius = baseRadius * sunMoonPulse,
                center = sunCenter
            )
        } else {
            // Glowing Cherry Moon & Stars
            stars.forEach { star ->
                val twinkle = (sin(animProgress * 4f * PI.toFloat() + star.pulsePhase) * 0.4f + 0.6f)
                drawCircle(
                    color = StarTwinkleColor.copy(alpha = twinkle.coerceIn(0.1f, 1f)),
                    radius = star.radiusPx,
                    center = Offset(star.xRatio * w, star.yRatio * h)
                )
            }

            val moonCenter = Offset(w * 0.8f, h * 0.18f)
            val moonRadius = 50f

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        MoonGlowColor.copy(alpha = 0.40f),
                        MoonGlowColor.copy(alpha = 0.10f),
                        Color.Transparent
                    ),
                    center = moonCenter,
                    radius = moonRadius * 2.2f * sunMoonPulse
                ),
                radius = moonRadius * 2.2f * sunMoonPulse,
                center = moonCenter
            )

            // Crescent Moon shape
            drawCircle(
                color = MoonGlowColor,
                radius = moonRadius,
                center = moonCenter
            )
            drawCircle(
                color = BackgroundDark,
                radius = moonRadius * 0.85f,
                center = Offset(moonCenter.x - 18f, moonCenter.y - 10f)
            )
        }

        // Floating Cherry Blossom Petals
        petals.forEach { petal ->
            val totalY = (petal.initialYRatio + animProgress * petal.speedY) % 1f
            val sway = sin(animProgress * 2f * PI.toFloat() + petal.xRatio * 10f) * petal.swayPx
            val cx = (petal.xRatio * w + sway).coerceIn(0f, w)
            val cy = totalY * h
            val rotation = petal.rotationStart + animProgress * petal.rotationSpeed

            withTransform({
                rotate(degrees = rotation, pivot = Offset(cx, cy))
            }) {
                // Draw Sakura Petal Path (oval / teardrop)
                val petalPath = Path().apply {
                    moveTo(cx, cy - petal.sizePx)
                    cubicTo(
                        cx + petal.sizePx * 0.6f, cy - petal.sizePx * 0.5f,
                        cx + petal.sizePx * 0.6f, cy + petal.sizePx * 0.5f,
                        cx, cy + petal.sizePx
                    )
                    cubicTo(
                        cx - petal.sizePx * 0.6f, cy + petal.sizePx * 0.5f,
                        cx - petal.sizePx * 0.6f, cy - petal.sizePx * 0.5f,
                        cx, cy - petal.sizePx
                    )
                }
                drawPath(
                    path = petalPath,
                    color = CherryBlossomPetalColor.copy(alpha = petal.alpha)
                )
            }
        }
    }
}

// ==========================================
// 5. CLOUDS & FOG CANVAS ANIMATION
// ==========================================

@Composable
private fun CloudCanvasAnimation() {
    val transition = rememberInfiniteTransition(label = "CloudTransition")
    val cloudOffset by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "CloudOffset"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        val xShift1 = (cloudOffset * w) % w
        val xShift2 = ((cloudOffset + 0.5f) * w) % w

        // Drifting Cloud Cluster 1
        drawCloudBubble(this, Offset(xShift1 - 100f, h * 0.15f), 120f)
        drawCloudBubble(this, Offset(xShift1, h * 0.12f), 150f)
        drawCloudBubble(this, Offset(xShift1 + 100f, h * 0.16f), 110f)

        // Drifting Cloud Cluster 2
        drawCloudBubble(this, Offset(xShift2 - 80f, h * 0.30f), 140f)
        drawCloudBubble(this, Offset(xShift2 + 40f, h * 0.28f), 160f)
    }
}

private fun drawCloudBubble(drawScope: DrawScope, center: Offset, radius: Float) {
    drawScope.drawCircle(
        color = CloudBubbleColor,
        radius = radius,
        center = center
    )
}

@Composable
private fun FogCanvasAnimation() {
    val transition = rememberInfiniteTransition(label = "FogTransition")
    val fogProgress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "FogProgress"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        val shift = (fogProgress * w) % w

        // Translucent cherry/purple mist layers
        drawRect(
            color = FogLayerColor,
            topLeft = Offset(shift - w, h * 0.2f),
            size = Size(w * 1.5f, h * 0.15f)
        )
        drawRect(
            color = FogLayerColor.copy(alpha = 0.2f),
            topLeft = Offset(w - shift, h * 0.4f),
            size = Size(w * 1.5f, h * 0.2f)
        )
        drawRect(
            color = FogLayerColor.copy(alpha = 0.15f),
            topLeft = Offset(shift - w * 0.5f, h * 0.65f),
            size = Size(w * 2f, h * 0.25f)
        )
    }
}

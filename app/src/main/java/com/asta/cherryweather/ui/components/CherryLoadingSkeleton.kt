package com.asta.cherryweather.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.asta.cherryweather.ui.theme.BorderCrimson
import com.asta.cherryweather.ui.theme.CardDarkPlum

@Composable
fun CherryLoadingSkeleton(
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "SkeletonShimmer")
    val shimmerTranslateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ShimmerAnim"
    )

    val shimmerColors = listOf(
        CardDarkPlum,
        Color(0xFF381B2B),
        CardDarkPlum
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(shimmerTranslateAnim - 300f, shimmerTranslateAnim - 300f),
        end = Offset(shimmerTranslateAnim, shimmerTranslateAnim)
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Skeleton
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = CardDarkPlum,
            shape = RoundedCornerShape(24.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderCrimson)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Icon circle skeleton
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(brush)
                )

                // Large Temp skeleton
                Box(
                    modifier = Modifier
                        .width(140.dp)
                        .height(64.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(brush)
                )

                // Description text skeleton
                Box(
                    modifier = Modifier
                        .width(180.dp)
                        .height(20.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(brush)
                )

                // Badges row
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(
                        modifier = Modifier
                            .width(100.dp)
                            .height(28.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(brush)
                    )
                    Box(
                        modifier = Modifier
                            .width(120.dp)
                            .height(28.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(brush)
                    )
                }
            }
        }

        // Hourly Skeleton Row
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = CardDarkPlum,
            shape = RoundedCornerShape(20.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderCrimson)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(160.dp)
                        .height(16.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(brush)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    repeat(4) {
                        Box(
                            modifier = Modifier
                                .width(68.dp)
                                .height(96.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(brush)
                        )
                    }
                }
            }
        }

        // 7-Day Forecast Skeleton Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = CardDarkPlum,
            shape = RoundedCornerShape(24.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderCrimson)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(140.dp)
                        .height(16.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(brush)
                )

                repeat(5) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .width(60.dp)
                                .height(16.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(brush)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(brush)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(8.dp)
                                .clip(CircleShape)
                                .background(brush)
                        )
                    }
                }
            }
        }
    }
}

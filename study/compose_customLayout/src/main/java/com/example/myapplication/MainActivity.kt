package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.StudyTheme
import java.util.Collections.max
import kotlin.math.max
import kotlin.system.measureNanoTime

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StudyTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
//                    StaggeredGrid("Android")
                }
            }
        }
    }
}

@Composable
fun StaggeredGrid(modifier: Modifier = Modifier,
                  rows: Int = 3,
                  content: @Composable () -> Unit) {
    Layout(modifier = modifier, content = content) { measurables, constraints ->
        // 각 row의 전체 width 저장 변수
        val rowWidths = IntArray(rows) { 0 }

        // 각 row의 최대 height 저장 변수
        val rowHeights = IntArray(rows) { 0 }

        // Don't constrain child views further, measure them with given constraints
        // List of measured children
        val placeables = measurables.mapIndexed { index, measurable ->
            // Measure each child
            val placeable = measurable.measure(constraints)
            // Track the width and max height of each row
            val row = index % rows
            //child elements의 각 width를 누적
            rowWidths[row] += placeable.width
            // 해당 row에 저장되는 child elements중 최대 height를 갖는 값을 저장
            rowHeights[row] = max(rowHeights[row], placeable.height)
            placeable
        }

        //  row 3개의 width중에 가장 큰값을 constraints의 min/max width 범위안에 들어오도록 맞춘다.
        val width = rowWidths.maxOrNull()
            ?.coerceIn(constraints.minWidth.rangeTo(constraints.maxWidth))
            ?: constraints.minWidth

        // 3개 row의 height를 모두 합치고 constraints의 min/max height 범위안에 들어오도록 맞춘다.
        val height = rowHeights.sumOf { it }
            .coerceIn(constraints.minHeight.rangeTo(constraints.maxHeight))

        // 각 row의 y position을 저장한다. 각 row의 y 값은 이전 row의 eight의 누적값이다.
        val rowY = IntArray(rows) { 0 }
        for (i in 1 until rows) {
            rowY[i] = rowY[i-1] + rowHeights[i-1]
        }

        // 3개의 row중 가장 긴 width와 3개의 row height를 합쳐서 이 layout의 크기를 확정시킨다.
        layout(width, height) {
            // 각 row별로 child의 width를 누적하면서 child element의 x 값으로 사용한다.
            val rowX = IntArray(rows) { 0 }

            placeables.forEachIndexed { index, placeable ->
                val row = index % rows
                placeable.placeRelative(x = rowX[row], y = rowY[row])
                rowX[row] += placeable.width
            }
        }
    }
}

@Composable
fun Chip(modifier: Modifier = Modifier, text: String) {
    Card(
        modifier = modifier,
        border = BorderStroke(color = Color.Black, width = Dp.Hairline),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(start = 8.dp, top = 4.dp, end = 8.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(16.dp, 16.dp)
                    .background(color = MaterialTheme.colors.secondary)
            )
            Spacer(Modifier.width(4.dp))
            Text(text = text)
        }
    }
}

@Preview
@Composable
fun ChipPreview() {
    StudyTheme() {
        Chip(text = "Hi there")
    }
}

val topics = listOf(
    "Arts & Crafts", "Beauty", "Books", "Business", "Comics", "Culinary",
    "Design", "Fashion", "Film", "History", "Maths", "Music", "People", "Philosophy",
    "Religion", "Social sciences", "Technology", "TV", "Writing"
)

@Composable
fun BodyContent(modifier: Modifier = Modifier) {
    StaggeredGrid(modifier = modifier) {
        for (topic in topics) {
            Chip(modifier = Modifier.padding(8.dp), text = topic)
        }
    }
}

@Preview
@Composable
fun LayoutsCodelabPreview() {
    StudyTheme() {
        BodyContent()
    }
}
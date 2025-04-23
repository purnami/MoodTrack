package com.example.moodtrack.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp

@Composable
fun RecommendationList(recommendations: List<AnnotatedString>) {
    Column {
        recommendations.forEach { item ->
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = item,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

fun parseInsightText(text: String): List<Pair<String, String>> {
    val sections = text.split("###")
        .map { it.trim() }
        .filter { it.isNotBlank() }

    return sections.map { section ->
        val lines = section.lines()
        val title = lines.firstOrNull() ?: ""
        val content = lines.drop(1).joinToString("\n").trim()
        title to content
    }
}

fun parseBulletPoints(text: String): List<AnnotatedString> {
    return text.lines()
        .map { it.trim() }
        .filter { it.startsWith("- ") }
        .map { it.removePrefix("- ").trim() }
        .map { bullet ->
            buildAnnotatedString {
                val regex = Regex("""\*\*(.*?)\*\*""")
                var lastIndex = 0
                for (match in regex.findAll(bullet)) {
                    val start = match.range.first
                    val end = match.range.last + 1
                    append(bullet.substring(lastIndex, start))
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(match.groupValues[1])
                    }
                    lastIndex = end
                }
                if (lastIndex < bullet.length) {
                    append(bullet.substring(lastIndex))
                }
            }
        }
}

fun parseNumberedPoints(text: String): List<AnnotatedString> {
    val regex = Regex("""\d+\.\s+\*\*(.*?)\*\*:?\s*(.+)""")

    return regex.findAll(text).map { match ->
        val title = match.groupValues[1].trim()
        val description = match.groupValues[2].trim()

        buildAnnotatedString {
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append("$title: ")
            }
            append(description)
        }
    }.toList()
}

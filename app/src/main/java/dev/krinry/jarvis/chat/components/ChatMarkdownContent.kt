package dev.krinry.jarvis.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.krinry.jarvis.ui.theme.GlassBorder
import dev.krinry.jarvis.ui.theme.GlassWhite

@Composable
fun ChatMarkdownContent(
    text: String,
    isStreaming: Boolean,
    modifier: Modifier = Modifier
) {
    val blocks = parseMarkdownBlocks(text)
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        for (block in blocks) {
            when (block) {
                is MarkdownBlock.Text -> MarkdownParagraph(block.content)
                is MarkdownBlock.Code -> MarkdownCodeBlock(block.language, block.code)
                is MarkdownBlock.Heading -> MarkdownHeading(block.level, block.content)
            }
        }
    }
}

private sealed interface MarkdownBlock {
    data class Text(val content: String) : MarkdownBlock
    data class Code(val language: String, val code: String) : MarkdownBlock
    data class Heading(val level: Int, val content: String) : MarkdownBlock
}

private fun parseMarkdownBlocks(text: String): List<MarkdownBlock> {
    val blocks = mutableListOf<MarkdownBlock>()
    val lines = text.split("\n")
    var i = 0
    while (i < lines.size) {
        val line = lines[i]
        when {
            line.startsWith("```") -> {
                val lang = line.removePrefix("```").trim()
                val code = mutableListOf<String>()
                i++
                while (i < lines.size && !lines[i].startsWith("```")) {
                    code.add(lines[i])
                    i++
                }
                i++ // skip closing ```
                blocks.add(MarkdownBlock.Code(lang, code.joinToString("\n")))
            }
            line.startsWith("#") -> {
                val level = line.takeWhile { it == '#' }.length
                val content = line.drop(level).trim()
                blocks.add(MarkdownBlock.Heading(level, content))
                i++
            }
            else -> {
                val textLines = mutableListOf(line)
                i++
                while (i < lines.size &&
                    !lines[i].startsWith("```") &&
                    !lines[i].startsWith("#") &&
                    lines[i].isNotEmpty()
                ) {
                    textLines.add(lines[i])
                    i++
                }
                blocks.add(MarkdownBlock.Text(textLines.joinToString("\n")))
            }
        }
    }
    return blocks
}

@Composable
private fun MarkdownParagraph(text: String) {
    val parts = parseInlineMarkdown(text)
    BasicText(
        text = buildAnnotatedString {
            for ((content, style) in parts) {
                withStyle(style) { append(content) }
            }
        },
        style = TextStyle(
            fontSize = 15.sp,
            lineHeight = 22.sp,
            color = Color.White.copy(alpha = 0.9f)
        )
    )
}

@Composable
private fun MarkdownHeading(level: Int, text: String) {
    val fontSize = when (level) {
        1 -> 22.sp; 2 -> 19.sp; 3 -> 17.sp; else -> 15.sp
    }
    BasicText(
        text = text,
        style = TextStyle(
            fontSize = fontSize,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            lineHeight = fontSize * 1.3f
        )
    )
}

@Composable
private fun MarkdownCodeBlock(language: String, code: String) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFF1A1A2E),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            if (language.isNotEmpty()) {
                BasicText(
                    text = language,
                    style = TextStyle(
                        fontSize = 11.sp,
                        color = Color(0xFF6C6C8A),
                        fontFamily = FontFamily.Monospace
                    ),
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
            Box(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                BasicText(
                    text = code,
                    style = TextStyle(
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        color = Color(0xFFE0E0E0),
                        fontFamily = FontFamily.Monospace
                    )
                )
            }
        }
    }
}

private data class InlinePart(val text: String, val style: SpanStyle)

private fun parseInlineMarkdown(text: String): List<InlinePart> {
    val parts = mutableListOf<InlinePart>()
    var remaining = text

    val patterns = listOf(
        Regex("\\*\\*(.+?)\\*\\*") to SpanStyle(fontWeight = FontWeight.Bold),
        Regex("\\*(.+?)\\*") to SpanStyle(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
        Regex("`(.+?)`") to SpanStyle(
            fontFamily = FontFamily.Monospace,
            color = Color(0xFF00D4FF)
        ),
        Regex("\\[([^]]+)]\\(([^)]+)\\)") to SpanStyle(
            color = Color(0xFF00D4FF),
            textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline
        )
    )

    while (remaining.isNotEmpty()) {
        var earliestIndex = remaining.length
        var earliestMatch: MatchResult? = null
        var earliestStyle: SpanStyle? = null

        for ((regex, style) in patterns) {
            val match = regex.find(remaining)
            if (match != null && match.range.first < earliestIndex) {
                earliestIndex = match.range.first
                earliestMatch = match
                earliestStyle = style
            }
        }

        if (earliestMatch != null && earliestStyle != null) {
            if (earliestIndex > 0) {
                parts.add(InlinePart(remaining.substring(0, earliestIndex), SpanStyle()))
            }
            val content = earliestMatch.groupValues[1]
            parts.add(InlinePart(content, earliestStyle))
            remaining = remaining.substring(earliestMatch.range.last + 1)
        } else {
            parts.add(InlinePart(remaining, SpanStyle()))
            remaining = ""
        }
    }

    return parts
}
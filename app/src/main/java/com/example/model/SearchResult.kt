package com.example.model

/**
 * Command palette item for developer and system control.
 */
data class CommandItem(
    val id: String,
    val command: String,
    val description: String,
    val category: String = "System",
    val shortcut: String? = null,
    val action: GestureAction = GestureAction.NONE,
    val directAction: (() -> Unit)? = null
)

/**
 * Universal search result item.
 */
sealed class SearchResult {
    data class AppItem(val app: AppInfo) : SearchResult()
    data class Command(val item: CommandItem) : SearchResult()
    data class MathCalculation(val expression: String, val result: String) : SearchResult()
    data class SystemAction(val title: String, val subtitle: String, val action: () -> Unit) : SearchResult()
    data class GeminiInsight(val query: String, val answer: String, val isLoading: Boolean = false) : SearchResult()
}

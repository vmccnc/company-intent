package com.app.intent.api.model

import com.squareup.moshi.JsonClass



@JsonClass(generateAdapter = true)
data class RepoResponse(
    val total_count: Int = 0,
    val incomplete_results: Boolean = false,
    val items: List<Repo> = listOf()
)


class Repo(
    val id: Long = 0,
    val node_id: String = "",
    val html_url: String = "",
    val description: String? = "",
)



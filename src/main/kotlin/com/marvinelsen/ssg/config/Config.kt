package com.marvinelsen.ssg.config

import java.net.URI
import java.nio.file.Path
import kotlin.io.path.Path
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

typealias URIAsString = @Serializable(with = URISerializer::class) URI
typealias PathAsString = @Serializable(with = PathSerializer::class) Path

@Serializable
data class Config(
    val site: Site,
    val license: License,
    val author: Author,
    val pages: List<Page>,
    val postProcessing: PostProcessing = PostProcessing(),
)

@Serializable
data class PostProcessing(
    val tidy: Tidy = Tidy(),
    val deployment: Deployment = Deployment(),
)

@Serializable
data class Author(val name: String, val email: String, val website: String)

@Serializable
data class Site(
    val title: String,
    val description: String = "",
    val baseUrl: String,
    val outputDirectory: String = "site",
)

@Serializable
data class Page(
    val title: String,
    val description: String = "",
    val relativeUrl: String = "",
    val source: String,
    val keywords: List<String> = emptyList(),
    val language: String,
    @Transient var isCurrentPage: Boolean = false,
)

@Serializable
data class License(val name: String, val url: String)

@Serializable
data class Tidy(@SerialName("enable") val isEnabled: Boolean = false, val configFile: String = "")


@Serializable
data class Deployment(@SerialName("enable") val isEnabled: Boolean = false, val command: String = "")

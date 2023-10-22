package com.marvinelsen.ssg

import com.github.jknack.handlebars.Context
import com.github.jknack.handlebars.Handlebars
import com.github.jknack.handlebars.io.FileTemplateLoader
import com.marvinelsen.ssg.config.YamlConfigLoader
import com.marvinelsen.ssg.helpers.Helpers
import java.io.File
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.createFile
import kotlin.io.path.div
import kotlin.io.path.writeText

fun main() {
    val config = YamlConfigLoader.loadConfig(File("config.yaml").inputStream())

    val templateLoader = FileTemplateLoader("templates", ".html")
    val handlebars = Handlebars(templateLoader)
    handlebars.registerHelpers(Helpers())
    val template = handlebars.compile("base")

    val outputFiles = mutableListOf<Path>()
    config.pages.forEach {
        it.isCurrentPage = true
        val content = File("pages/", it.sourceFile).readText()
        val context: Context = Context
            .newBuilder(config)
            .combine("page", it)
            .combine("content", content)
            .build()
        val renderedTemplate = template.apply(context)

        val outputDirectory = Path(config.destinationDirectory.toString(), it.relativeUrl)
        val outputFile = outputDirectory / "index.html"
        outputDirectory.createDirectories()
        outputFile.createFile()
        outputFile.writeText(renderedTemplate)
        it.isCurrentPage = false
        outputFiles.add(outputFile)
    }

    if (config.postProcessing.tidy.isEnabled) {
        ProcessBuilder(
            listOf(
                "tidy",
                "-modify",
                "-quiet",
                "-config",
                config.postProcessing.tidy.configFile.toString(),
            ) + outputFiles.map { it.toString() }
        )
            .redirectOutput(ProcessBuilder.Redirect.INHERIT)
            .redirectError(ProcessBuilder.Redirect.INHERIT)
            .start()
            .waitFor()
    }
}

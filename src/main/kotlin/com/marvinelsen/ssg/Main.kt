package com.marvinelsen.ssg

import com.github.jknack.handlebars.Context
import com.github.jknack.handlebars.Handlebars
import com.github.jknack.handlebars.io.FileTemplateLoader
import com.marvinelsen.ssg.config.YamlConfigLoader
import com.marvinelsen.ssg.helpers.Helpers
import java.io.File

fun main() {
    val config = YamlConfigLoader.loadConfig(File("config.yaml").inputStream())

    val templateLoader = FileTemplateLoader("templates", ".html")
    val handlebars = Handlebars(templateLoader)
    handlebars.registerHelpers(Helpers())
    val template = handlebars.compile("base")

    val outputFiles = mutableListOf<File>()
    config.pages.forEach {
        it.isCurrentPage = true
        val content = File("pages/", it.source).readText()
        val context: Context = Context
            .newBuilder(config)
            .combine("page", it)
            .combine("content", content)
            .build()
        val renderedTemplate = template.apply(context)

        val outputDirectory = File(config.site.outputDirectory, it.relativeUrl)
        val outputFile = File(outputDirectory, "index.html")
        outputDirectory.mkdirs()
        outputFile.createNewFile()
        outputFile.writeText(renderedTemplate)
        it.isCurrentPage = false
        outputFiles += outputFile
    }

    if (config.postProcessing.tidy.isEnabled) {
        ProcessBuilder(
            listOf(
                "tidy",
                "-modify",
                "-quiet",
                "-config",
                config.postProcessing.tidy.configFile,
            ) + outputFiles.map { it.toString() }
        )
            .redirectOutput(ProcessBuilder.Redirect.INHERIT)
            .redirectError(ProcessBuilder.Redirect.INHERIT)
            .start()
            .waitFor()
    }
}

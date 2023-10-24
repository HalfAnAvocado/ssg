package com.marvinelsen.ssg

import com.github.jknack.handlebars.Context
import com.github.jknack.handlebars.Handlebars
import com.github.jknack.handlebars.io.FileTemplateLoader
import com.marvinelsen.ssg.config.Config
import com.marvinelsen.ssg.config.YamlConfigLoader
import com.marvinelsen.ssg.helpers.Helpers
import java.io.File
import java.nio.file.Path
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.Path
import kotlin.io.path.copyToRecursively
import kotlin.io.path.createDirectories
import kotlin.io.path.createFile
import kotlin.io.path.createParentDirectories
import kotlin.io.path.div
import kotlin.io.path.inputStream
import kotlin.io.path.notExists
import kotlin.io.path.writeText

fun initHandlebars(config: Config): Handlebars {
    val templateLoader = FileTemplateLoader(config.templatesDirectory.toFile(), ".html")
    val handlebars = Handlebars(templateLoader)
    handlebars.registerHelpers(Helpers())
    return handlebars
}

@OptIn(ExperimentalPathApi::class)
fun main() {
    val config = YamlConfigLoader.loadConfig(Path("config.yaml").inputStream().buffered())
    val handlebars = initHandlebars(config)

    val outputFiles = mutableListOf<Path>()
    config.pages.forEach {
        it.isCurrentPage = true
        val content = File("pages/", it.sourceFile).readText()
        val context: Context = Context
            .newBuilder(config)
            .combine("page", it)
            .combine("content", content)
            .build()
        val template = handlebars.compile(it.template)
        val renderedTemplate = template.apply(context)

        val outputDirectory = Path(config.destinationDirectory.toString(), it.relativeUrl)
        val outputFile = outputDirectory / "index.html"
        outputDirectory.createDirectories()
        if (outputFile.notExists()) outputFile.createFile()
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

    val stylesheetsDestinationDirectory = config.destinationDirectory / "css"
    config.stylesheetsDirectory.copyToRecursively(
        stylesheetsDestinationDirectory.createParentDirectories(),
        followLinks = false,
        overwrite = true
    )

    val fontsDestinationDirectory = config.destinationDirectory / "fonts"
    config.fontsDirectory.copyToRecursively(
        fontsDestinationDirectory.createParentDirectories(),
        followLinks = false,
        overwrite = true
    )

    val configDestinationDirectory = config.destinationDirectory
    config.configDirectory.copyToRecursively(
        configDestinationDirectory.createParentDirectories(),
        followLinks = false,
        overwrite = true
    )

    val imagesDestinationDirectory = config.destinationDirectory / "images"
    config.imagesDirectory.copyToRecursively(
        imagesDestinationDirectory.createParentDirectories(),
        followLinks = false,
        overwrite = true
    )

    val faviconsDestinationDirectory = config.destinationDirectory
    config.faviconsDirectory.copyToRecursively(
        faviconsDestinationDirectory.createParentDirectories(),
        overwrite = true,
        followLinks = false,
    )
}

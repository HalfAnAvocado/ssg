package com.marvinelsen.ssg.config

import com.charleskorn.kaml.Yaml
import java.io.InputStream

private interface ConfigLoader {
    fun loadConfig(inputStream: InputStream): Config
}

object YamlConfigLoader : ConfigLoader {
    override fun loadConfig(inputStream: InputStream) = Yaml.default.decodeFromStream(Config.serializer(), inputStream)
}

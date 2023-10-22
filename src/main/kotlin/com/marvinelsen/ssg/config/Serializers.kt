package com.marvinelsen.ssg.config

import java.net.URI
import java.nio.file.Path
import kotlin.io.path.Path
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object URISerializer : KSerializer<URI> {
    override val descriptor = PrimitiveSerialDescriptor("URI", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder) = URI(decoder.decodeString())

    override fun serialize(encoder: Encoder, value: URI) {
        encoder.encodeString(value.toString())
    }
}

object PathSerializer : KSerializer<Path> {
    override val descriptor = PrimitiveSerialDescriptor("Path", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder) = Path(decoder.decodeString())

    override fun serialize(encoder: Encoder, value: Path) {
        encoder.encodeString(value.toString())
    }
}
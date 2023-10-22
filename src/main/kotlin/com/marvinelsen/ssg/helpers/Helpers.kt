package com.marvinelsen.ssg.helpers

@Suppress("Unused")
class Helpers {
    fun join(strings: List<String>, separator: String?) = strings.joinToString(separator ?: ", ")
}

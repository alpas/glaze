package dev.alpas.glaze

import dev.alpas.Config
import dev.alpas.Environment

open class GlazeConfig(env: Environment) : Config {
    open val baseUrl = env("GLAZE_BASE_URL", "APP_URL")
}

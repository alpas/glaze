package dev.alpas.glaze

import dev.alpas.JsonSerializer
import dev.alpas.routing.Router

internal class GlazeRouteGenerator(private val router: Router) {
    @OptIn(ExperimentalStdlibApi::class)
    fun compile(): String {
        val routes = router.namedRoutes.map { (name, route) ->
            val methods = route.methods().map { it.toString() }
            name to mapOf("uri" to route.path, "methods" to methods)
        }.toMap()
        return JsonSerializer.serialize(routes)
    }
}

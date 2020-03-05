package dev.alpas.glaze

import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import dev.alpas.asGray
import dev.alpas.console.Command
import dev.alpas.routing.Router
import dev.alpas.routing.url
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths

class GlazePublishCommand(private val router: Router) : Command(name = "glaze:publish", help = "Publish Glaze JS files") {
    private val defaultPath get() = Paths.get(resourcesDir.path, "js").toAbsolutePath().toString()
    private val defaultName = "glaze.js"
    val url by option("--url", help = "The base URL. Default is ${"/".asGray()}").default("/")
    val path by option("--path", help = "The full output path. Defaults to ${"resources/js".asGray()}").default(defaultPath)
    val name by option("--name", help = "The name of the script. Default is ${defaultName.asGray()}").default(defaultName)

    override fun run() {
        File(path).mkdirs()
        println()
        writeGlazeScript().also { printSuccessMessage(it.toPath()) }
        writeRouteScript().also { printSuccessMessage(it.toPath()) }
    }

    private fun printSuccessMessage(path: Path) {
        withColors {
            echo("${green(" ✓")} ${brightGreen(relativeToSrc(path))}")
        }
    }

    private fun writeRouteScript(): File {
        val routeScript = this.javaClass.classLoader.getResource("js/route.js")?.readText() ?: ""
        return File(path, "route.js").apply { writeText(routeScript) }
    }

    private fun writeGlazeScript(): File {
        val json = GlazeRouteGenerator(router).compile()
        val baseUri = url("", this.url)
        val glazeScript = """
                 var Glaze = {
                   namedRoutes: $json,
                   baseUrl : '$baseUri',
                   baseProtocol: '${baseUri.scheme}',
                   baseDomain: '${baseUri.host}',
                   basePort: ${baseUri.port},
                   defaultParameters: []
                 };
                 
                 if (typeof window.Glaze !== 'undefined') {
                   for (var name in window.Glaze.namedRoutes) {
                     Glaze.namedRoutes[name] = window.Glaze.namedRoutes[name];
                   }
                 }
    
                 export { Glaze }
            """.trimIndent()

        return File(path, name).apply { writeText(glazeScript) }
    }
}

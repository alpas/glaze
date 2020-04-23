package dev.alpas.glaze

import dev.alpas.*
import dev.alpas.console.Command
import dev.alpas.view.addCustomFunction
import java.net.URI

@Suppress("unused")
open class GlazeServiceProvider : ServiceProvider {
    open val glazeFunctionName = "glaze"

    override fun boot(app: Application) {
        if (!app.env.inConsoleMode) {
            addGlazeFunction(app)
        }
    }

    override fun commands(app: Application): List<Command> {
        return listOf(GlazePublishCommand(app.make(), app.make()))
    }

    private fun addGlazeFunction(app: Application) {
        val glazeBaseUrl = app.config { GlazeConfig(app.env) }.baseUrl.let { URI(it) }
        val glazeScript = if (app.env.isProduction) {
            glazeScript(app, glazeBaseUrl)
        } else {
            null
        }
        app.addCustomFunction(glazeFunctionName) {
            val uri = if (call.url.startsWith("http://localhost")) {
                call.uri("/")
            } else {
                glazeBaseUrl
            }
            glazeScript ?: glazeScript(call, uri)
        }
    }

    private fun glazeScript(container: Container, baseUri: URI): String {
        val json = GlazeRouteGenerator(container.make()).compile()
        val baseUrl = baseUri.toString()
        val routeFunction = routeFunction(container.make())
        return """
               <script type="text/javascript">
                 var Glaze = {
                   namedRoutes: $json,
                   baseUrl : '$baseUrl',
                   baseProtocol: '${baseUri.scheme}',
                   baseDomain: '${baseUri.host}',
                   basePort: ${baseUri.port},
                   defaultParameters: []
                 };

                 $routeFunction
               </script>
            """.trimIndent()
    }

    private fun routeFunction(env: Environment): String {
        val min = if (env.isDev) "" else ".min"
        return this.javaClass.classLoader.getResource("js/route${min}.js")?.readText() ?: ""
    }
}

package dev.alpas.glaze

import dev.alpas.Application
import dev.alpas.Environment
import dev.alpas.ServiceProvider
import dev.alpas.console.Command
import dev.alpas.make
import dev.alpas.view.addCustomFunction

@Suppress("unused")
open class GlazeServiceProvider : ServiceProvider {
    open val glazeFunctionName = "glaze"

    override fun boot(app: Application) {
        if (!app.env.inConsoleMode) {
            addGlazeFunction(app)
        }
    }

    override fun commands(app: Application): List<Command> {
        return listOf(GlazePublishCommand(app.make()))
    }

    private fun addGlazeFunction(app: Application) {
        app.addCustomFunction(glazeFunctionName) {
            val json = GlazeRouteGenerator(app.make()).compile()
            val baseUri = call.uri("/")
            val routeFunction = routeFunction(call.env)
            """
               <script type="text/javascript">
                 var Glaze = {
                   namedRoutes: $json,
                   baseUrl : '$baseUri',
                   baseProtocol: '${baseUri.scheme}',
                   baseDomain: '${baseUri.host}',
                   basePort: ${baseUri.port},
                   defaultParameters: []
                 };
                 
                 $routeFunction
               </script> 
            """.trimIndent()
        }
    }

    private fun routeFunction(env: Environment): String {
        val min = if (env.isDev) "" else ".min"
        return this.javaClass.classLoader.getResource("js/route${min}.js")?.readText() ?: ""
    }
}

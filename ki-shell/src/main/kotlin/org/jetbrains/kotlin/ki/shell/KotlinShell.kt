package org.jetbrains.kotlin.ki.shell

import org.jetbrains.kotlin.ki.shell.configuration.CachedInstance
import org.jetbrains.kotlin.ki.shell.configuration.ReplConfiguration
import org.jetbrains.kotlin.ki.shell.configuration.ReplConfigurationImpl
import kotlin.script.experimental.api.ScriptCompilationConfiguration
import kotlin.script.experimental.api.ScriptEvaluationConfiguration
import kotlin.script.experimental.jvm.baseClassLoader
import kotlin.script.experimental.jvm.defaultJvmScriptingHostConfiguration
import kotlin.script.experimental.jvm.jvm
import kotlin.script.experimental.jvm.util.scriptCompilationClasspathFromContext

object KotlinShell {
    @JvmStatic
    fun main(args: Array<String>) {
        val repl =
                Shell(
                        configuration(),
                        defaultJvmScriptingHostConfiguration,
                        ScriptCompilationConfiguration {
                            jvm {
                                // TODO: replJars here
                                scriptCompilationClasspathFromContext(wholeClasspath = true)
                            }
                        },
                        ScriptEvaluationConfiguration {
                            jvm {
                                baseClassLoader(Shell::class.java.classLoader)
                            }
                        }
                )

//        repl.addClasspathRoots(replJars())
        Runtime.getRuntime().addShutdownHook(Thread {
            println("\nBye!")
            repl.cleanUp()
        })

        repl.doRun()
    }

    fun configuration(): ReplConfiguration {
        val instance = CachedInstance<ReplConfiguration>()
        val klassName: String? = System.getProperty("config.class")

        return if (klassName != null) {
            instance.load(klassName, ReplConfiguration::class)
        } else {
            instance.get { ReplConfigurationImpl() }
        }
    }
}
package cc.gxstudio.anative.pluginmain

import cc.gxstudio.anative.pluginmain.data.Plugindata
import kotlinx.coroutines.delay
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.BotLeaveEvent
import net.mamoe.mirai.event.events.BotOnlineEvent
import net.mamoe.mirai.utils.error
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.lang.Thread.sleep
import java.nio.file.Paths


/**
 * 使用 kotlin 版请把
 * `src/main/resources/META-INF.services/net.mamoe.mirai.console.plugin.jvm.JvmPlugin`
 * 文件内容改成 `cc.gxstudio.anative.pluginmain.PluginMain` 也就是当前主类全类名
 *
 * 使用 kotlin 可以把 java 源集删除不会对项目有影响
 *
 * 在 `settings.gradle.kts` 里改构建的插件名称、依赖库和插件版本
 *
 * 在该示例下的 [JvmPluginDescription] 修改插件名称，id和版本，etc
 *
 * 可以使用 `src/test/kotlin/RunMirai.kt` 在 ide 里直接调试，
 * 不用复制到 mirai-console-loader 或其他启动器中调试
 */

object PluginMain : KotlinPlugin(
    JvmPluginDescription(
        id = "cc.gxstudio.anative-loader",
        name = "AnotherNative启动器",
        version = "1.0.0"
                        ) {
        author("YmggDEV")
        info(
            """
            这是一个测试插件, 
            在这里描述插件的功能和用法等.
        """.trimIndent()
            )
        dependsOn("net.mamoe.mirai-api-http")
        // author 和 info 可以删除.
    }
                                ) {
    override fun onEnable() {
        
        
        val file = File("${PluginMain.dataFolder}/AnotherMiraiNative.exe")
        if (!file.exists()) file.writeBytes(
            javaClass.getResource("/another-mirai-native-release/AnotherMiraiNative.exe")
                .readBytes()
                                           )
        
        val eventchannel = GlobalEventChannel.parentScope(this)
        eventchannel.subscribeAlways<BotOnlineEvent> {
            
            
            val projectDirAbsolutePath = Paths.get("").toAbsolutePath().toString()
            logger.warning("projectDirAbsolutePath: $projectDirAbsolutePath")
            val yaml = Yaml()
            val objectMap = yaml
                .load(File("config/net.mamoe.mirai-api-http/setting.yml").reader())
                as Map<String, Any>
            val keys = objectMap["verifyKey"] as String
            val ws = (objectMap["adapterSettings"] as LinkedHashMap<String, LinkedHashMap<String, Any>>)["ws"]
            val wshost = (ws!!.get("host") as String)
            val wsport = (ws!!.get("port") as Int).toString()
            delay(1000)
            Plugindata.reload()
            val ProcessBuilder = ProcessBuilder().apply{
                command(
                    "${PluginMain.dataFolder}\\AnotherMiraiNative.exe",
                    "-i",
                    "-q",
                    "${bot.id}",
                    "-ws",
                    "\"ws://$wshost:$wsport\"",
                    "-wsk",
                    "$keys"
                       )
                directory(File("${PluginMain.dataFolder}"))
            }
            
            ProcessBuilder.start()
        }
        eventchannel.subscribeAlways<BotLeaveEvent> { }
        
    }
}

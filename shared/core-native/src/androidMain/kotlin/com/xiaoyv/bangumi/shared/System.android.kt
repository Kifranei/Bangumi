package com.xiaoyv.bangumi.shared

import android.app.Application
import android.content.ClipData
import android.content.ContentValues
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import androidx.compose.ui.platform.AndroidUiDispatcher
import androidx.compose.ui.platform.ClipEntry
import androidx.core.net.toUri
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.xiaoyv.bangumi.shared.database.DatabaseDriverFactory
import com.xiaoyv.bangumi.shared.native.AppDatabase
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.okhttp.OkHttp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.Path.Companion.toPath
import java.lang.System
import kotlin.coroutines.CoroutineContext
import kotlin.time.ExperimentalTime

lateinit var application: Application

actual object System {
    actual val isDebugType: Boolean
        get() = (application.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0

    actual val uiDispatcher: CoroutineContext = AndroidUiDispatcher.Main

    actual val database: AppDatabase by lazy {
        AppDatabase(DatabaseDriverFactory.createDriver())
    }

    actual val datastore: DataStore<Preferences> by lazy {
        PreferenceDataStoreFactory.createWithPath(
            produceFile = {
                application.filesDir.resolve("bgm.preferences_pb").absolutePath.toPath()
            }
        )
    }

    @OptIn(ExperimentalTime::class)
    actual fun currentTimeMillis() = kotlin.time.Clock.System.now().toEpochMilliseconds()

    actual fun log(tag: String, message: String) {
        Log.d(tag, message)
    }

    actual fun launchDeeplinkSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val intent = Intent(
                Settings.ACTION_APP_OPEN_BY_DEFAULT_SETTINGS,
                "package:com.xiaoyv.bangumi.multiplatform".toUri()
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            application.startActivity(intent)
        }
    }

    actual fun userAgent(): String {
        return System.getProperty("http.agent").orEmpty()
    }

    actual fun createClipEntry(text: String): ClipEntry {
        return ClipEntry(ClipData.newPlainText("Copy", text))
    }

    actual fun shareText(text: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            putExtra(Intent.EXTRA_TEXT, text)
        }
        val chooser = Intent.createChooser(intent, "分享内容")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        application.startActivity(chooser)
    }

    actual fun createHttpClient(block: HttpClientConfig<*>.() -> Unit): HttpClient {
        return HttpClient(OkHttp) {
            block()
        }
    }

    actual suspend fun cleanCache(): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            runCatching { application.cacheDir.deleteRecursively() }
        }
    }

    actual suspend fun saveImageFromUrl(url: String): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val bytes = java.net.URL(url).openStream().use { it.readBytes() }
            val name = "bgm_${currentTimeMillis()}.jpg"
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, name)
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Bangumi")
                    put(MediaStore.Images.Media.IS_PENDING, 1)
                }
            }
            val resolver = application.contentResolver
            val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                ?: error("insert failed")
            resolver.openOutputStream(uri)?.use { it.write(bytes) } ?: error("write failed")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                values.clear()
                values.put(MediaStore.Images.Media.IS_PENDING, 0)
                resolver.update(uri, values, null, null)
            }
        }
    }
}
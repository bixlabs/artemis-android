package com.chemicalwedding.artemis.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.net.toUri
import java.io.File


class ArtemisFileUtils {
    companion object {
        private val TAG = ArtemisFileUtils::class.simpleName
        private const val ArtemisPathChild = "Artemis"
        private var externalDir: String = ""

        @Suppress("SameParameterValue")
        private fun grantPermissions(context: Context, uri: Uri) {
            val flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION

            context.grantUriPermission(context.packageName, uri, flags)
        }

        fun ensureArtemisDir(context: Context): String {
            var savePath = File(Environment.getExternalStorageDirectory(), ArtemisPathChild)
            if(externalDir.isNotEmpty()){
                savePath = File(Environment.getExternalStorageDirectory(), externalDir)
            }

            if (!savePath.exists()) {
                savePath.mkdirs()
            }

            grantPermissions(context, savePath.toUri())

            Log.d(TAG, "ensuring save path: ${savePath.absolutePath}")

            return savePath.absolutePath
        }

        private fun ensureExternalDir(context: Context, name: String): String {
            val savePath = File(context.getExternalFilesDir(null), name)
            if (!savePath.exists()) {
                savePath.mkdirs()
            }

            grantPermissions(context, savePath.toUri())

            Log.d(TAG, "ensuring save path: ${savePath.absolutePath}")

            return savePath.absolutePath
        }

        fun newFile(context: Context, name: String): File {
            val file = File(File(ensureSaveDir(context)), name)

            Log.d(TAG, "newFile ${file.absolutePath}")

            return file
        }

        fun hasExternalDir(): Boolean {
            return externalDir.isNotEmpty();
        }

        fun ensureSaveDir(context: Context): String {
            return ensureArtemisDir(context)
//            if (externalDir.isEmpty()) {
//            }
//
//            return ensureExternalDir(context, externalDir)
        }

        fun setExternalDir(name: String) {
            if (name.isEmpty()) {
                return;
            }
            Log.d(TAG, "Setting external dir to : $name")
            externalDir = name
        }
    }
}

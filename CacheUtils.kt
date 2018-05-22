package com.eetrust.doshare.presentation.ui.viewutils

import android.content.Context
import android.os.Environment
import android.util.LruCache
import com.example.y.kotlindemo.cache.DiskLruCache
import com.example.y.kotlindemo.friends
import com.google.gson.Gson
import java.io.File


/**
 * Created by y on 2018/5/18.
 */
class CacheUtils {
    private constructor()

    val g: Gson = Gson()

    companion object {
        val CacheName: String = "ContactCache"//保存文件名
        val CacheDir: String = "diskCache" //保存路径
        val maxDiskSpace = 10 * 1024 * 1024L
        var edit: DiskLruCache.Editor? = null
        var diskCache: DiskLruCache? = null
        var lruCache: LruCache<String, friends>? = null
        var cacheUtils: CacheUtils? = null
        fun getInstance(context: Context): CacheUtils {
            if (cacheUtils == null) {
                synchronized(CacheUtils::class) {
                    cacheUtils = CacheUtils()
                    //内存缓存
                    val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
                    lruCache = LruCache<String, friends>(maxMemory / 16)
                    //磁盘缓存
                    val cacheFile = getDiskCacheDir(context, CacheDir)
                    diskCache = DiskLruCache.open(cacheFile, 1, 1, maxDiskSpace)

                }
            }
            return cacheUtils!!
        }

        //获取磁盘保存地址
        private fun getDiskCacheDir(context: Context, uniqueName: String): File {
            val cachePath: String
            if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()
                    || !Environment.isExternalStorageRemovable()) {
                cachePath = context.externalCacheDir.path
            } else {
                cachePath = context.cacheDir.path
            }
            return File(cachePath + File.separator + uniqueName)
        }
    }

    //将数据写入磁盘
    fun putToDisk(key: String, values: friends) {
        edit = diskCache!!.edit(key)
        val outputStream = edit!!.newOutputStream(0)
        val json = g.toJson(values)
        outputStream.write(json.toByteArray())
        edit!!.commit()
        outputStream.flush()
        outputStream.close()
    }

    //从磁盘中获取List的对象集合
    fun  getFromDisk(key: String): friends {
        val snapShot = diskCache!!.get(key)
        val inputStream = snapShot.getInputStream(0)
        inputStream.skip(12)

        var num: Int = 0
        var byteArray = ByteArray(1024)
        var stringBuffer = StringBuilder()

        while (num != -1) {
            num = inputStream.read(byteArray)
            if (num != -1) {
                stringBuffer.append(String(byteArray, 0, num))
            }
        }
        val fromJson: friends = g.fromJson<friends>(stringBuffer.toString(), friends::class.java)
        inputStream.close()
        return fromJson
    }

    //将List对象写入内存，使用LruCache管理
    fun put(contactId: String, values: friends) {
        lruCache!!.put(contactId, values)
    }

    //用LruChche获取对象数据
    fun get(contactId: String): friends {
        return lruCache!!.get(contactId)
    }
}
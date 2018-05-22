package com.example.y.kotlindemo.cache

import android.content.Context
import android.os.Environment
import android.text.TextUtils
import java.io.File

/**
 * Created by y on 2018/5/21.
 */
class DiskCache : BaseCache {
    //    val g: Gson = Gson()
    private var CacheDir: String = "" //保存路径
    private var maxDiskSpace = 10 * 1024 * 1024L
    private var edit: DiskLruCache.Editor? = null
    private var diskCache: DiskLruCache? = null

    private constructor() {
    }

    /**
     * 设置缓存文件路径   全路径
     *
     */
    fun cacheDir(cacheDir: String): DiskCache {
        this.CacheDir = cacheDir
        return mDiskCache!!
    }

    /**
     * 设置缓存文件最大保存量
     * 默认10M
     */
    fun maxDiskSpace(maxDiskSpace: Long): DiskCache {
        this.maxDiskSpace = maxDiskSpace
        return mDiskCache!!
    }

    fun build(): DiskCache {
        if (TextUtils.isEmpty(CacheDir)) {
            //文件的保存地址（File）
//            CacheDir = getDiskCacheDir(context, CacheDir)
        }
        val cacheFile = File(CacheDir)
        //使用open获得DiskLruCache对象，直接new不出来
        //参数：1、文件的保存地址（文件夹File）2、App版本号3、缓存条目的值  2、3都可以设置为1 4、文件保存SD卡最大的大小
        diskCache = DiskLruCache.open(cacheFile, 1, 1, maxDiskSpace)
        return mDiskCache!!
    }

    companion object {
        //单例
        var mDiskCache: DiskCache? = null

        fun getInstance(): DiskCache {
            if (mDiskCache == null) {
                synchronized(DiskCache::class.java) {
                    mDiskCache = DiskCache()
                }
            }
            return mDiskCache!!
        }
    }

    //value：json串
    override fun put(key: String, value: String) {
        //插入时，要获取DiskCache的Editor
        //参数key：文件名
        edit = diskCache!!.edit(key)
        //Editor用来获取输出流outputStream,进行写文件
        val outputStream = edit!!.newOutputStream(0)
        //Gson将对象转换为json串保存
//        val json = g.toJson(value)
        outputStream.write(value.toString().toByteArray())
        //别忘了把 Editor.commit()
        edit!!.commit()
        outputStream.flush()
        outputStream.close()
    }

    override fun get(key: String): String {
        //获取SnapShot对象 参数key: 文件名   和写文件的key相同时才能获取你想获取的值
        val snapShot = diskCache!!.get(key)
        val inputStream = snapShot.getInputStream(0)
        var num: Int = 0
        var byteArray = ByteArray(1024)
        var stringBuffer = StringBuilder()
        while (num != -1) {
            num = inputStream.read(byteArray)
            if (num != -1) {
                stringBuffer.append(String(byteArray, 0, num))
            }
        }
//        val fromJson: T = g.fromJson<T>(stringBuffer.toString()
//                , friends::class.java)
        inputStream.close()
        return stringBuffer.toString()
    }

    //获取磁盘保存地址
    private fun getDiskCacheDir(context: Context, uniqueName: String): String {
        val cachePath: String
        if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.externalCacheDir.path
        } else {
            cachePath = context.cacheDir.path
        }
        return cachePath + File.separator + uniqueName
    }
}
package com.example.y.kotlindemo.cache

import android.util.LruCache

/**
 * Created by y on 2018/5/21.
 */
class MemoryCache : BaseCache {

    private var lruCache: LruCache<String, String>? = null

    private constructor() {
        val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
        lruCache = LruCache<String, String>(maxMemory / 16)
    }

    companion object {
        var mMemoryCache: MemoryCache? = null
        fun getInstance(): MemoryCache {
            if (mMemoryCache == null) {
                mMemoryCache = MemoryCache()
            }
            return mMemoryCache!!
        }
    }

    override fun put(key: String, value: String) {
        lruCache!!.put(key, value)
    }

    override fun get(key: String): String {
        return lruCache!!.get(key)
    }

}
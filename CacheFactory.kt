package com.example.y.kotlindemo.cache

import android.content.Context

/**
 * Created by y on 2018/5/21.
 */
class CacheFactory {

    private constructor()

    companion object {
        var mCacheFactory: CacheFactory? = null
        var mContext: Context? = null
        fun getFactory(mContext: Context): CacheFactory {
            if (mCacheFactory == null) {
                synchronized(CacheFactory::class.java) {
                    this.mContext = mContext
                    mCacheFactory = CacheFactory()
                }
            }
            return mCacheFactory!!
        }
    }

    fun createMemoeryUtil(): MemoryCache{
        return MemoryCache.getInstance()
    }

    fun createDiskUtil(): DiskCache {
        return DiskCache.getInstance()
    }

}
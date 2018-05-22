package com.example.y.kotlindemo.cache

/**
 * Created by y on 2018/5/21.
 */
interface BaseCache {
    fun put(key: String, value: String)
    fun get(key: String): String?
}
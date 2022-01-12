package ru.drankin.dev.dreamteam.utils

import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream

fun getRandomFileName():String{
        val allowedChars = ('a'..'z')+('A'..'Z')+('0'..'9')
        return (1..16)
            .map { allowedChars.random() }
            .joinToString("")
    }

fun downloadImageFromUrl(url : String, path: String){
    val okHttpClient = OkHttpClient()
    val request = Request.Builder().url(url).build()
    val response = okHttpClient.newCall(request).execute()
    val inputStream = response.body?.byteStream()
    inputStream.use { input ->
        FileOutputStream(File(path)).use { output ->
            input?.copyTo(output)
        }
    }
}
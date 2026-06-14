package com.nastechai.nasmusic.expect

actual fun getDownloadFolderPath(): String = System.getProperty("user.home") + "/Downloads"
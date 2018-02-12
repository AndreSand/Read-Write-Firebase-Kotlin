package com.kotlinmap.andres.database_firebase_kotlin.Model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
class UploadInfo {

    var name: String = ""
    var url: String = ""

    constructor() {}

    constructor(name: String, url: String) {
        this.name = name
        this.url = url
    }
}
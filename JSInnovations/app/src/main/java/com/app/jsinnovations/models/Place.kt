package com.app.jsinnovations.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Place:RealmObject() {

    @PrimaryKey
    var _id: String? = null
    var name: String? = null
}
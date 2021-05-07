package com.app.jsinnovations.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Total :RealmObject(){

    @PrimaryKey
    var _id: String? = null
    var water = 0
    var liquid = 0
    var electricity = 0
    var minutes = 0
}
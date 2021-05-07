package com.app.jsinnovations.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey


open class Board: RealmObject(){

    @PrimaryKey
    var _id: String? = null
    var current: Int? = null
    var total: Int? = null
    var status: Int? = null
    var name: String? = null
    var user: String? = null
    var nominal: Int? = null
    var updated: String? = null
    var v: Int? = null
    var place: String? = null
    var income: Int? = 10
    var coasts: Int? = 60
    var revenue: Int? = 50
}
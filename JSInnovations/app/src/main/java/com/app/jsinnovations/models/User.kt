package com.app.jsinnovations.models

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey


open class User: RealmObject(){

    @PrimaryKey
    var _id: String? = null
    var name: String? = null
    var surname: String? = null
    var email: String? = null
    var paid: Boolean? = null
    var boards: RealmList<String?>? = null
    var places: RealmList<String?>? = null
}
package com.app.jsinnovations.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Module:RealmObject() {

    @PrimaryKey
    var _id: String? = null
    var editable: Boolean? = null
    var active: Boolean? = null
    var name: String? = null
    var time: Int? = null
    var nominal: Int? = null
    var waterValue: Int? = null
    var electricityValue: Int? = null
    var liquidValue: Int? = null
    var boardId: String? = null
}
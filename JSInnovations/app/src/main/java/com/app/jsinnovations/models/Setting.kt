package com.app.jsinnovations.models

import io.realm.RealmObject

open class Setting:RealmObject() {

    var value: Int? = null
    var mid: Int? = null
    var name: Name? = null
}
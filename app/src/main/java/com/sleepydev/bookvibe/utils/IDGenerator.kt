package com.sleepydev.bookvibe.utils

import java.util.*

object IDGenerator {
    private val generatedIDs = mutableSetOf<String>()

    fun generateUniqueID(): String {
        var newID: String
        do {
            newID = UUID.randomUUID().toString()
        } while (generatedIDs.contains(newID))
        generatedIDs.add(newID)
        return newID
    }
}

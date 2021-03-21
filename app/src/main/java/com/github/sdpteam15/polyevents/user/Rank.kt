package com.github.sdpteam15.polyevents.user

enum class Rank {
    Admin,
    ActivityProvider,
    Staff,
    RegisteredVisitor,
    Visitor
}

inline fun <reified T : Enum<T>> printAllValues() {
    print(enumValues<T>().joinToString { it.name })
}
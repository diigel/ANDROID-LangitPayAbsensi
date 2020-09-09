package com.absensi.langitpay.abstraction

fun <T> MutableList<T>.removeDuplicatesItem(): MutableList<T> {
    val set = LinkedHashSet<T>()
    set.addAll(this)
    this.clear()
    this.addAll(set)
    return this
}
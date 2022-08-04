package com.jstockley.bsn.twitch

class TwitchLiveStream(private var name: String, private var title: String, private var category: String) {

    fun getName(): String {
        return this.name
    }

    fun getTitle(): String {
        return this.title
    }

    fun getCategory(): String {
        return this.category
    }

    override fun toString(): String {
        return "TwitchLiveStream(name='$name', title='$title', category='$category')"
    }

}
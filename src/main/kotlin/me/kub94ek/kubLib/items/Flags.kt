package me.kub94ek.kubLib.items


enum class Flags {
    CRAFTING_USABLE {
        override fun isValidData(data: Any) = true
    };

    abstract fun isValidData(data: Any): Boolean
}
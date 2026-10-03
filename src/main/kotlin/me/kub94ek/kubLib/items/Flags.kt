package me.kub94ek.kubLib.items


enum class Flags {
    CRAFTING_USABLE {
        override fun isValidData(data: Any) = true
    },
    CUSTOM_FLAGS {
        override fun isValidData(data: Any): Boolean {
            return data is Set<*> && data.all { element ->
                element is Pair<*, *> && element.first is String
            }
        }
    };

    abstract fun isValidData(data: Any): Boolean
}
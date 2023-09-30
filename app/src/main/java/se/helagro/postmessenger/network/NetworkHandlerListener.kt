package se.helagro.postmessenger.network

interface NetworkHandlerListener {
    fun onUpdate(code: Int)
}
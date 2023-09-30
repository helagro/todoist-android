package se.helagro.postmessenger.network

interface NetworkHandlerListener {
    fun onPostItemUpdate(code: Int)
}
package se.helagro.postmessenger.network

interface NetworkCallback {
    fun onUpdate(code: Int, body: String?)
}
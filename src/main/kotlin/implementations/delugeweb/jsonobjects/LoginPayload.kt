package implementations.delugeweb.jsonobjects

data class LoginPayload(val password: String) {
    override fun toString(): String {
        return """{"id":1,"method":"auth.login","params":["$password"]}"""
    }
}
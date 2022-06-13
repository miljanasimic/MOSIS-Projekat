package elfak.mosis.petfinder.ui.register

//sealed class RegisterResult {
//    data class Success(val value: String? = null): RegisterResult()
//    data class Failure(val message: String? = null): RegisterResult()
//}

data class RegisterResult(
    val success: String? = null,
    val error: String? = null
)

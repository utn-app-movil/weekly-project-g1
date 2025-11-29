package Entity

data class PersonGetResponse(val data: List<DTOPerson>,
                             val message: String,
                             val responseCode: Int)

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import java.io.Serializable

data class Space(
    @DocumentId
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val address: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val capacity: Int = 0,
    val startTime: String, // horario de abertura do espaço
    val endTime: String, // horario de fechamento
    val images: List<String> = listOf(),
    var isReserved: Boolean = false
)


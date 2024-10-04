import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject

class SpaceDAO {
    val db = FirebaseFirestore.getInstance()

    fun add(space: Space, callback: (Boolean) -> Unit) {
        db.collection("spaces").add(space)
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                callback(false)
            }
    }

    fun findAll(callback: (List<Space>) -> Unit) {
        db.collection("spaces").get()
            .addOnSuccessListener { documents ->
                val spaces = documents.mapNotNull { document ->
                    val spaceData = document.data

                    // Verificação de nulos no campo capacity
                    val capacity = (spaceData["capacity"] as? Long)?.toInt() ?: 0  // Usa 0 se for null



                    // Criação do objeto Space manualmente
                    Space(
                        id = document.id,
                        name = spaceData["name"] as? String ?: "",
                        description = spaceData["description"] as? String ?: "",
                        address = spaceData["address"] as? String ?: "",
                        latitude = (spaceData["latitude"] as? Double) ?: 0.0,
                        longitude = (spaceData["longitude"] as? Double) ?: 0.0,
                        capacity = capacity,
                        startTime = spaceData["startTime"] as? String ?: "",
                        endTime = spaceData["endTime"] as? String ?: "",
                        images = (spaceData["images"] as? List<String>)?.map { it.trim() } ?: emptyList()
                    )
                }
                callback(spaces)
            }
            .addOnFailureListener {
                callback(emptyList())
            }
    }

    fun findByName(name: String, callback: (Space?) -> Unit) {
        db.collection("spaces").whereEqualTo("name", name).get()
            .addOnSuccessListener { document ->
                if (!document.isEmpty) {
                    val space = document.documents[0].toObject<Space>()
                    callback(space)
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener {
                callback(null)
            }
    }

    fun deleteById(id: String, callback: (Boolean) -> Unit) {
        db.collection("spaces").document(id).delete()
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                callback(false)
            }
    }

    fun edit(id: String, newData: Map<String, Any>, callback: (Boolean) -> Unit) {
        db.collection("spaces").document(id).update(newData)
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                callback(false)
            }
    }

    fun findById(id: String, callback: (Space?) -> Unit) {
        db.collection("spaces").document(id).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val space = document.toObject<Space>()
                    callback(space)
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener {
                callback(null)
            }
    }
}

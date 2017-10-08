import com.google.gson.JsonObject

class Comment : Item {
    var treeAddress = ""

    constructor(commentNode: JsonObject, treeAddress: String) : super(commentNode) {
        this.treeAddress = treeAddress
    }
}
package com.pzpg.ogr.api.request

/**
 * Enum class to represent formats of graph which we use in the project
 * @author github.com/Graidaris
 */
enum class GraphFormat(val suffix: String){
    RawImage(".jpg"),
    GraphML(".graphml"),
    Graph6(".g6")
}
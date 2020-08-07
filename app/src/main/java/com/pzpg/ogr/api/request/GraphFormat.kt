package com.pzpg.ogr.api.request

/**
 * Enum class to represent formats of graph which we use in the project
 *
 * @property[suffix]
 *
 * @author Władyław Jakołcewicz
 */
enum class GraphFormat(val suffix: String){
    RawImage(".jpg"),
    GraphML(".graphml"),
    Graph6(".g6")
}
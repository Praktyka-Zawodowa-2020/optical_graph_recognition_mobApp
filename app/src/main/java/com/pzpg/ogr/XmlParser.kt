package com.pzpg.ogr

import android.util.Xml
import de.blox.graphview.Graph
import de.blox.graphview.Node
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream

data class Edge(val source: String, val target: String)

class XmlParser{

    private val ns: String? = null

    @Throws(XmlPullParserException::class, IOException::class)
    fun parse(inputStream: InputStream): Graph {
        inputStream.use { inputStream ->
            val parser: XmlPullParser = Xml.newPullParser()
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(inputStream, null)
            parser.nextTag()
            return readGraph(parser)
        }
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readGraph(parser: XmlPullParser): Graph {
        parser.require(XmlPullParser.START_TAG, ns, "graph")
        val graph = Graph()
        val nodes = ArrayList<Node>()
        val edges = ArrayList<Edge>()

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }

            when (parser.name) {
                "node" -> {
                    val id = parser.getAttributeValue(null, "id")
                    nodes.add(id.toInt(), Node(id))
                }
                "edge" -> {
                    val source = parser.getAttributeValue(null, "source")
                    val target = parser.getAttributeValue(null, "target")
                    edges.add(Edge(source, target))
                }
                else -> skip(parser)
            }
        }

        edges.forEach {
            graph.addEdge(nodes[it.source.toInt()], nodes[it.target.toInt()])
        }

        return graph
    }


    @Throws(XmlPullParserException::class, IOException::class)
    private fun skip(parser: XmlPullParser) {
        if (parser.eventType != XmlPullParser.START_TAG) {
            throw IllegalStateException()
        }
        var depth = 1
        while (depth != 0) {
            when (parser.next()) {
                XmlPullParser.END_TAG -> depth--
                XmlPullParser.START_TAG -> depth++
            }
        }
    }
}
package com.pzpg.ogr.graph

import android.util.Log
import android.util.Xml
import de.blox.graphview.Graph
import de.blox.graphview.Node
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream


class XmlParser{

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
    private fun readGraph(parser: XmlPullParser): Graph{

        val graph = Graph()
        val arrNode = ArrayList<Node>()

        var curNode: Node? = null

        while (parser.next() != XmlPullParser.END_DOCUMENT) {
            when(parser.eventType){
                XmlPullParser.START_TAG ->{
                    when(parser.name){
                        "node" -> {
                            val id = parser.getAttributeValue(null, "id")
                            curNode = Node(id)
                        }
                        "y:Geometry"->
                        {
                            val x = parser.getAttributeValue(null, "x")
                            val y = parser.getAttributeValue(null, "y")
                            curNode!!.setPosition(x.toFloat(), y.toFloat())
                        }
                        "edge" -> {
                            val sourceId = parser.getAttributeValue(null, "source")
                            val targetId = parser.getAttributeValue(null, "target")

                            val s = arrNode.indexOf(arrNode.filter{ it.data == sourceId}[0])
                            val d = arrNode.indexOf(arrNode.filter{ it.data == targetId}[0])

                            graph.addEdge(arrNode[s], arrNode[d])
                        }
                    }
                }
                XmlPullParser.END_TAG -> {
                    when(parser.name){
                        "node" -> {
                            if (curNode != null) {
                                arrNode.add(curNode)
                            }
                        }
                    }
                }
            }
        }

        return graph
    }
}
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
        inputStream.use {
            val parser: XmlPullParser = Xml.newPullParser()
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(it, null)
            parser.nextTag()
            return readGraph(parser)
        }
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readGraph(parser: XmlPullParser): Graph{

        val graph = Graph()
        val arrNode = ArrayList<Node>()

        var curNode: Node? = null
        var dataKey: String? = null

        while (parser.next() != XmlPullParser.END_DOCUMENT) {
            when(parser.eventType){
                XmlPullParser.START_TAG ->{
                    when(parser.name){
                        "node" -> {
                            val id = parser.getAttributeValue(null, "id")
                            curNode = Node(id)
                        }
                        // tag supported yEd
                        "y:Geometry"->
                        {
                            if (curNode != null){
                                val x = parser.getAttributeValue(null, "x")
                                val y = parser.getAttributeValue(null, "y")
                                curNode.setPosition(x.toFloat(), y.toFloat())
                            }
                        }
                        // tag supported http://koala.os.niwa.gda.pl/zgred/zgred.html
                        "data" -> {
                            dataKey = parser.getAttributeValue(null, "key")
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
                XmlPullParser.TEXT ->{
                    val value = parser.text
                    if( curNode != null) {
                        when(dataKey){
                            "x" -> curNode.x = value.toFloat()
                            "y" -> curNode.y = value.toFloat()
                        }
                    }
                    dataKey = null
                }
                XmlPullParser.END_TAG -> {
                    when(parser.name){
                        "node" -> {
                            if (curNode != null) {
                                arrNode.add(curNode)
                            }
                            curNode = null
                        }
                    }
                }
            }
        }

        return graph
    }
}
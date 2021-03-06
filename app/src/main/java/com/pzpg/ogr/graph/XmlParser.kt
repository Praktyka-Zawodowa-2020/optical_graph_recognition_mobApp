package com.pzpg.ogr.graph

import android.util.Log
import android.util.Xml
import de.blox.graphview.Edge
import de.blox.graphview.Graph
import de.blox.graphview.Node
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream
import java.util.*
import kotlin.collections.ArrayList

/**
 * Parser of the GraphMl format.
 */
class XmlParser{


    /**
     * Create parse and parse file
     *
     * @param[inputStream] input stream of the file
     *
     * @return[Graph]
     */
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

    /**
     * Parse file body
     *
     * @param[parser]
     *
     * @return[Graph]
     */
    @Throws(XmlPullParserException::class, IOException::class)
    private fun readGraph(parser: XmlPullParser): Graph{

        val graph = Graph()
        val arrNode = ArrayList<Node>()
        val arrFillData = ArrayList<Pair<String, String>>()

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
                        "y:Fill"->{
                            if (curNode != null) {
                                val isFill = parser.getAttributeValue(null, "transparent")
                                Log.i("isFill", "${curNode.data} - $isFill")
                                isFill?.let {
                                    arrFillData.add(Pair(curNode!!.data.toString(), it))
                                }
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
                        "graphml" -> {
                            arrNode.forEach { localNode ->
                                if(!graph.nodes.contains(localNode)){
                                    graph.addNode(localNode)
                                }
                            }

                            arrFillData.forEach {nodeIsFill ->
                                graph.nodes.forEach { node->
                                    val (id, isFill) = nodeIsFill
                                    if (node.data == id) {
                                        if (isFill.toLowerCase(Locale.ROOT) == "true") {
                                            node.data = "E ${node.data}"
                                        }else{
                                            node.data = "F ${node.data}"
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return graph
    }
}
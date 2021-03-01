package gd.filesystem;

import gd.geometry.*;
import gd.graph.*;
import org.w3c.dom.*;
import org.xml.sax.*;

import javax.xml.parsers.*;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public final class GraphReader
{
    public static Graph<DspVertex, DspEdge> readGraphMl(String path) throws GraphReaderException
    {
        Document document;

        try {
            var file = new File(path);
            var documentBuilderFactory = DocumentBuilderFactory.newInstance();
            var documentBuilder = documentBuilderFactory.newDocumentBuilder();
            document = documentBuilder.parse(file);
        }
        catch (ParserConfigurationException e)
        {
            throw new RuntimeException(e);
        }
        catch (IOException | SAXException e)
        {
            throw new GraphReaderException("An error occurred when parsing file: " + path);
        }

        document.getDocumentElement().normalize();

        final var graph = new Graph<DspVertex, DspEdge>();
        final var graphAttributes = new HashMap<String, String>();

        var graphElements = document.getElementsByTagName("graph");
        if (graphElements.getLength() == 0)
        {
            throw new GraphReaderException("No graph elements defined");
        }

        //We only read the first <graph> element
        var graphElement = (Element) graphElements.item(0);

        //"edgedefault" must be equal to 'directed'
        if (!graphElement.getAttribute("edgedefault").equals("directed"))
        {
            throw new GraphReaderException("only value 'directed' supported for graph attribute 'edgedefault'");
        }

        Double xDefault = null;
        Double yDefault = null;
        String labelDefault = null;
        Double weightDefault = null;

        var graphAttributeKeys = document.getElementsByTagName("key");
        for (int i = 0; i < graphAttributeKeys.getLength(); ++i)
        {
            var keyNodeElement = (Element) graphAttributeKeys.item(i);

            var id = keyNodeElement.getAttribute("id");
            var name = keyNodeElement.getAttribute("attr.name");

            Node defaultNode = keyNodeElement.getElementsByTagName("default").item(0);
            if (defaultNode != null)
            {
                var defaultValue = defaultNode.getTextContent();
                switch (name)
                {
                    case "x" -> xDefault = Double.parseDouble(defaultValue);
                    case "y" -> yDefault = Double.parseDouble(defaultValue);
                    case "label" -> labelDefault = defaultValue;
                    case "weight" -> weightDefault = Double.parseDouble(defaultValue);
                }
            }

            graphAttributes.put(id, name);
        }

        //Check if all required attributes are defined
        for (var requiredAttribute : Set.of("x", "y", "label", "weight"))
        {
            if (!graphAttributes.containsValue(requiredAttribute))
            {
                throw new GraphReaderException("Attribute definition missing: " + requiredAttribute + " not found");
            }
        }

        var nodes = document.getElementsByTagName("node");
        for (int i = 0; i < nodes.getLength(); ++i)
        {
            var nodeElement = (Element) nodes.item(i);

            var nodeId = nodeElement.getAttribute("id");

            Double xCoordinate = xDefault;
            Double yCoordinate = yDefault;
            String label = labelDefault;

            if (nodeElement.hasChildNodes())
            {
                var attributes = nodeElement.getElementsByTagName("data");
                for (int j = 0; j < attributes.getLength(); ++j)
                {
                    var vertexAttribute = (Element) attributes.item(j);
                    var attributeKey = vertexAttribute.getAttribute("key");

                    //If we don't recognize the key, skip this attribute
                    if (!graphAttributes.containsKey(attributeKey))
                    {
                        continue;
                    }

                    var attributeName = graphAttributes.get(attributeKey);
                    var attributeValue = vertexAttribute.getTextContent();

                    switch (attributeName)
                    {
                        case "x" -> xCoordinate = Double.parseDouble(attributeValue);
                        case "y" -> yCoordinate = Double.parseDouble(attributeValue);
                        case "label" -> label = attributeValue;
                    }
                }

                if (xCoordinate == null) throw new GraphReaderException("Node description incomplete: X coordinate not defined");
                if (yCoordinate == null) throw new GraphReaderException("Node description incomplete: Y coordinate not defined");
                if (label == null) throw new GraphReaderException("Node description incomplete: Label not defined");

                var vertex = new DspVertex(nodeId, new Vector2D(xCoordinate, yCoordinate), label);
                graph.addVertex(vertex);
            }
        }

        final var vertexKeyMap = graph.getVertices()
                .stream()
                .collect(Collectors.toMap(DspVertex::key, v -> v));

        var edges = document.getElementsByTagName("edge");
        for (int i = 0; i < edges.getLength(); ++i)
        {
            var edgeElement = (Element) edges.item(i);

            var edgeSource = edgeElement.getAttribute("source");
            var edgeTarget = edgeElement.getAttribute("target");
            if (edgeElement.hasAttribute("directed") && edgeElement.getAttribute("directed").equals("false"))
            {
                throw new GraphReaderException("Undirected edges are not supported");
            }
            if (edgeSource.equals(edgeTarget))
            {
                throw new GraphReaderException("Self-referencing edges are not supported");
            }

            Double edgeWeight = weightDefault;

            if (edgeElement.hasChildNodes())
            {
                var attributes = edgeElement.getElementsByTagName("data");
                for (int j = 0; j < attributes.getLength(); ++j)
                {
                    var vertexAttribute = (Element) attributes.item(j);

                    var attributeKey = vertexAttribute.getAttribute("key");

                    //If we don't recognize the key, skip this attribute
                    if (!graphAttributes.containsKey(attributeKey))
                    {
                        continue;
                    }

                    var attributeName = graphAttributes.get(attributeKey);
                    var attributeValue = vertexAttribute.getTextContent();

                    if ("weight".equals(attributeName))
                    {
                        edgeWeight = Double.parseDouble(attributeValue);
                    }
                }

                if (edgeWeight == null)
                {
                    throw new GraphReaderException("Edge description incomplete: Edge weight not defined.");
                }

                graph.addEdge(vertexKeyMap.get(edgeSource), vertexKeyMap.get(edgeTarget), new DspEdge(edgeWeight));
            }
        }

        return graph;
    }

}

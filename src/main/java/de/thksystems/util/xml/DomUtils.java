/*
 * tksCommons
 *
 * Author  : Thomas Kuhlmann (ThK-Systems, http://www.thk-systems.de)
 * License : LGPL (https://www.gnu.org/licenses/lgpl.html)
 */
package de.thksystems.util.xml;

import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public final class DomUtils {

    private DomUtils() {
    }

    /**
     * Returns the whole DOM-tree of the given {@link Node} as a list of {@link Node}s.
     */
    public static List<Node> getNodeTreeAsList(Node node) {
        List<Node> nodeList = new ArrayList<>();
        addNodeRecursivelyToList(node, nodeList);
        return nodeList;
    }

    private static void addNodeRecursivelyToList(Node node, List<Node> nodeList) {
        nodeList.add(node);
        NodeList children = node.getChildNodes();
        for (int i = 0; children != null && i < children.getLength(); i++) {
            addNodeRecursivelyToList(children.item(i), nodeList);
        }
    }

    /**
     * Returns the whole XML-{@link Node} or -{@link Document} as a {@link String}.
     *
     * @param node               The {@link Node} to output as a {@link String}
     * @param omitXmlDeclaration <code>true</code>, if the xml-header should be omitted.
     * @param encoding           Encoding of the text-output
     */
    public static String nodeToString(Node node, boolean omitXmlDeclaration, String encoding) {
        StringWriter sw = new StringWriter();
        try {
            Transformer t = TransformerFactory.newInstance().newTransformer();
            t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            t.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            t.transform(new DOMSource(node), new StreamResult(sw));
        } catch (TransformerException te) {
            throw new IllegalArgumentException(te);
        }
        return sw.toString();
    }

    /**
     * Returns the whole XML-{@link Node} or -{@link Document} as a {@link String} with 'UTF-8' encoding and xml-header.
     */
    public static String nodeToString(Node node) {
        return nodeToString(node, false, StandardCharsets.UTF_8.name());
    }

    /**
     * Creates a new {@link Element} with the given name, adds it to the parent {@link Node} and returns it.
     */
    public static Element createAndAddElement(Node parentNode, String nodeName) {
        Document doc = getOwningDocument(parentNode);
        Element newElem = doc.createElement(nodeName);
        parentNode.appendChild(newElem);
        return newElem;
    }

    /**
     * Returns the owning {@link Document}.
     *
     * @return Owning {@link Document} or the {@link Node} itself, if it is a {@link Document}.
     */
    public static Document getOwningDocument(Node parentNode) {
        if (parentNode instanceof Document) {
            return (Document) parentNode;
        }
        return parentNode.getOwnerDocument();
    }

    /**
     * Creates a new {@link Attr}ibute with the given name and value, adds it to the parent {@link Node} and returns it.
     */
    public static Attr createAndAddAttribute(Node parentNode, String attrName, String attrValue) {
        Document doc = getOwningDocument(parentNode);
        Attr attr = doc.createAttribute(attrName);
        attr.setValue(attrValue);
        parentNode.getAttributes().setNamedItem(attr);
        return attr;
    }

    /**
     * Creates a new {@link Text}Node with the given texts, adds it to the parent {@link Node} and returns it.
     */
    public static Text createAndAddTextNode(Node parentNode, String text) {
        Document doc = getOwningDocument(parentNode);
        Text textNode = doc.createTextNode(text);
        parentNode.appendChild(textNode);
        return textNode;
    }

}

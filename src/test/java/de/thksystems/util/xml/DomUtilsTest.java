package de.thksystems.util.xml;

import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

public class DomUtilsTest {

    private Document createDocument() throws ParserConfigurationException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        return docBuilder.newDocument();
    }

    @Test
    public void testGetOwningDocument() throws ParserConfigurationException {
        Document doc = createDocument();
        assertEquals(doc, DomUtils.getOwningDocument(doc));
        Element rootElem = DomUtils.createAndAddElement(doc, "root");
        assertEquals(doc, DomUtils.getOwningDocument(rootElem));
    }

    @Test
    public void testCreateAndAdd() throws ParserConfigurationException {
        Document doc = createDocument();
        Element rootElem = DomUtils.createAndAddElement(doc, "root");
        DomUtils.createAndAddAttribute(rootElem, "foo", "bar");
        DomUtils.createAndAddTextNode(rootElem, "test");
        assertEquals("<root foo=\"bar\">test</root>", DomUtils.nodeToString(doc, true, "UTF-8"));
    }

    @Test
    public void testGetNodeTreeAsList() throws ParserConfigurationException {
        Document doc = createDocument();
        Element rootElem = DomUtils.createAndAddElement(doc, "root");
        DomUtils.createAndAddAttribute(rootElem, "foo", "bar");
        Element otherElem = DomUtils.createAndAddElement(rootElem, "other");
        Element otherChildElem = DomUtils.createAndAddElement(otherElem, "otherChild");
        Text textElem = DomUtils.createAndAddTextNode(rootElem, "test");
        List<Node> asList = DomUtils.getNodeTreeAsList(doc);
        assertEquals(5, asList.size());
        assertEquals(doc, asList.get(0));
        assertEquals(rootElem, asList.get(1));
        assertEquals(otherElem, asList.get(2));
        assertEquals(otherChildElem, asList.get(3));
        assertEquals(textElem, asList.get(4));
    }
}

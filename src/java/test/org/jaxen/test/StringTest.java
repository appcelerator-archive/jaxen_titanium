/*
 * $Header$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 *
 * Copyright (C) 2005 Elliotte Rusty Harold
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions, and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions, and the disclaimer that follows 
 *    these conditions in the documentation and/or other materials 
 *    provided with the distribution.
 *
 * 3. The name "Jaxen" must not be used to endorse or promote products
 *    derived from this software without prior written permission.  For
 *    written permission, please contact license@jaxen.org.
 * 
 * 4. Products derived from this software may not be called "Jaxen", nor
 *    may "Jaxen" appear in their name, without prior written permission
 *    from the Jaxen Project Management (pm@jaxen.org).
 * 
 * In addition, we request (but do not require) that you include in the 
 * end-user documentation provided with the redistribution and/or in the 
 * software itself an acknowledgement equivalent to the following:
 *     "This product includes software developed by the
 *      Jaxen Project (http://www.jaxen.org/)."
 * Alternatively, the acknowledgment may be graphical using the logos 
 * available at http://www.jaxen.org/
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE Jaxen AUTHORS OR THE PROJECT
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 *
 * ====================================================================
 * This software consists of voluntary contributions made by many 
 * individuals on behalf of the Jaxen Project and was originally 
 * created by bob mcwhirter <bob@werken.com> and 
 * James Strachan <jstrachan@apache.org>.  For more information on the 
 * Jaxen Project, please see <http://www.jaxen.org/>.
 * 
 * $Id$
 */

package org.jaxen.test;

import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import junit.framework.TestCase;

import org.jaxen.FunctionCallException;
import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.jaxen.dom.DOMXPath;
import org.w3c.dom.Document;

/**
 * @author Elliotte Rusty Harold
 *
 */
public class StringTest extends TestCase {

    private Document doc;
    
    public void setUp() throws ParserConfigurationException
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        doc = builder.newDocument();
    }


    public StringTest(String name) {
        super(name);
    }

    // test case for JAXEN-55
    public void testStringFunctionOperatesOnFirstNodeInDocumentOrder() 
      throws JaxenException {
        
        XPath xpath = new DOMXPath("string(//x)");
        org.w3c.dom.Element a = doc.createElementNS("", "a");
        org.w3c.dom.Element b = doc.createElementNS("", "b");
        doc.appendChild(a);
        a.appendChild(b);
        org.w3c.dom.Element x2 = doc.createElementNS("", "x");
        org.w3c.dom.Element x3 = doc.createElementNS("", "x");
        org.w3c.dom.Element x4 = doc.createElementNS("", "x");
        a.appendChild(x4);
        b.appendChild(x2);
        b.appendChild(x3);
        x2.appendChild(doc.createTextNode("2"));
        x3.appendChild(doc.createTextNode("3"));
        x4.appendChild(doc.createTextNode("4"));
        
        List result = xpath.selectNodes(doc);
        assertEquals(1, result.size());
        assertEquals("2", result.get(0));
        
    }
    
    public void testStringValueOfComment() 
      throws JaxenException {
        
        XPath xpath = new DOMXPath("string(/a/comment())");
        org.w3c.dom.Element a = doc.createElementNS("", "a");
        doc.appendChild(a);
        a.appendChild(doc.createComment("data"));
        
        String result = (String) xpath.evaluate(doc);
        assertEquals("data", result);
        
    }
    
    public void testStringValueOfNamespaceNode() 
      throws JaxenException {
        
        XPath xpath = new DOMXPath("string(/a/namespace::node())");
        org.w3c.dom.Element a = doc.createElementNS("", "a");
        doc.appendChild(a);
        
        String result = (String) xpath.evaluate(doc);
        assertEquals("http://www.w3.org/XML/1998/namespace", result);
        
    }
    
    public void testSmallNumbersDontUseExponentialNotation() throws JaxenException {
        XPath xpath = new DOMXPath("string(0.0000003)");
        String result = (String) xpath.evaluate(null);
        assertEquals("0.0000003", result);
    }

    public void testBigNumbersDontUseExponentialNotation() throws JaxenException {
        XPath xpath = new DOMXPath("string(100000000.5)");
        String result = (String) xpath.evaluate(null);
        assertEquals("100000000.5", result);
    }

    public void testStringOfInfinity() throws JaxenException {
        XPath xpath = new DOMXPath("string(1 div 0)");
        String result = (String) xpath.evaluate(null);
        assertEquals("Infinity", result);
    }

    public void testStringOfNegativeInfinity() throws JaxenException {
        XPath xpath = new DOMXPath("string(-1 div 0)");
        String result = (String) xpath.evaluate(null);
        assertEquals("-Infinity", result);
    }

    public void testStringOfNegativeZero() throws JaxenException {
        XPath xpath = new DOMXPath("string(-0)");
        String result = (String) xpath.evaluate(null);
        assertEquals("0", result);
    }

    public void testIntegersAreFormattedAsInts() throws JaxenException {
        XPath xpath = new DOMXPath("string(12)");
        String result = (String) xpath.evaluate(null);
        assertEquals("12", result);
    } 

    public void testStringFunctionRequiresAtMostOneArgument() 
      throws JaxenException {
        
        XPath xpath = new DOMXPath("string('a', 1)");
        
        try {
            xpath.selectNodes(doc);
            fail("Allowed string function with two arguments");
        }
        catch (FunctionCallException ex) {
            assertNotNull(ex.getMessage());
        }
        
    }
}

//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.2-hudson-jaxb-ri-2.2-63- 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.12.08 at 03:34:25 PM CET 
//


package org.escidoc.core.domain.sru;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.apache.axis.types.PositiveInteger;

public class Adapter3
    extends XmlAdapter<String, PositiveInteger>
{


    public PositiveInteger unmarshal(String value) {
        return new PositiveInteger(value);
    }

    public String marshal(PositiveInteger value) {
        if (value == null) {
            return null;
        }
        return value.toString();
    }

}


package com.template.webservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour anonymous complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="FromCurrency" type="{http://www.webserviceX.NET/}Currency2"/>
 *         &lt;element name="ToCurrency" type="{http://www.webserviceX.NET/}Currency2"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "fromCurrency",
    "toCurrency"
})
@XmlRootElement(name = "ConversionRate")
public class ConversionRate {

    @XmlElement(name = "FromCurrency", required = true)
    protected Currency2 fromCurrency;
    @XmlElement(name = "ToCurrency", required = true)
    protected Currency2 toCurrency;

    /**
     * Obtient la valeur de la propriété fromCurrency.
     * 
     * @return
     *     possible object is
     *     {@link Currency2 }
     *     
     */
    public Currency2 getFromCurrency() {
        return fromCurrency;
    }

    /**
     * Définit la valeur de la propriété fromCurrency.
     * 
     * @param value
     *     allowed object is
     *     {@link Currency2 }
     *     
     */
    public void setFromCurrency(Currency2 value) {
        this.fromCurrency = value;
    }

    /**
     * Obtient la valeur de la propriété toCurrency.
     * 
     * @return
     *     possible object is
     *     {@link Currency2 }
     *     
     */
    public Currency2 getToCurrency() {
        return toCurrency;
    }

    /**
     * Définit la valeur de la propriété toCurrency.
     * 
     * @param value
     *     allowed object is
     *     {@link Currency2 }
     *     
     */
    public void setToCurrency(Currency2 value) {
        this.toCurrency = value;
    }

}

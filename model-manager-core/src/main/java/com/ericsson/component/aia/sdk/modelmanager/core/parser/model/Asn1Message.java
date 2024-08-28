/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2014
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.component.aia.sdk.modelmanager.core.parser.model;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

/**
 * This class holds a single ASN.1 message that is encapsulated in a MESSAGE_CONTENTS parameter in an event.
 */
public class Asn1Message implements Serializable {

    private static final long serialVersionUID = -5012122814899946087L;

    private String messageName = "";

    private Map<String, String> messageAttributes = new TreeMap<String, String>();

    /**
     * @param messageName
     *            - message name
     * @param messageAttributes
     *            - attributes
     */
    public Asn1Message(final String messageName, final Map<String, String> messageAttributes) {
        this.messageName = messageName;
        this.messageAttributes = messageAttributes;
    }

    /**
     * Dump the contents of the message to a string
     *
     * @return The message as a string
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();

        builder.append(messageName);
        builder.append('[');

        boolean first = true;
        for (final String attribute : messageAttributes.keySet()) {
            if (first) {
                first = false;
            } else {
                builder.append(',');
            }
            builder.append(attribute);
            builder.append(':');
            builder.append(messageAttributes.get(attribute));
        }

        builder.append(']');

        return builder.toString();
    }

    /**
     * @return message
     */
    public String getMessageName() {
        return messageName;
    }

    /**
     * @param messageName
     *            - message
     */
    public void setMessageName(final String messageName) {
        this.messageName = messageName;
    }

    /**
     * @return map
     */
    public Map<String, String> getMessageAttributes() {
        return messageAttributes;
    }

    /**
     * @param messageAttributes
     *            - messages
     */
    public void setMessageAttributes(final Map<String, String> messageAttributes) {
        this.messageAttributes = messageAttributes;
    }
}

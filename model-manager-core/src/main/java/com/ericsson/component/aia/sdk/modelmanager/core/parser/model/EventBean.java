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

/**
 * This class is the base class for all event class java beans. It has member data for the event time stamp
 * and the event NE name, with getters and setters for those. It also has abstract methods for setting
 * the event class data from the raw event and for generating a CSV representation of the event
 *
 */

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author aia
 */
public abstract class EventBean implements Comparable<EventBean>, Serializable {

    protected static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    static {
        DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = LoggerFactory.getLogger(EventBean.class);

    protected long timestamp;

    protected String subNetwork = "unknown";

    protected String networkElement;

    protected String name;

    //Default value is -1 to indicate not set event id
    protected int identifier = -1;

    protected String version;

    private byte[] rawData;

    private List<Asn1Message> asn1MessageList = null;

    /**
     * Define a default constructor
     */
    public EventBean() {
        // empty
    }

    /**
     * Define a constructor for the base properties
     *
     * @param timestamp
     *            - timestamp
     * @param subNetwork
     *            - network
     * @param nElem
     *            - element
     * @param name
     *            - name
     * @param identifier
     *            - identifier
     * @param version
     *            - version
     */
    public EventBean(final long timestamp, final String subNetwork, final String nElem, final String name, final int identifier,
                     final String version) {
        this.timestamp = timestamp;
        this.subNetwork = subNetwork;
        this.networkElement = nElem;
        this.name = name;
        this.identifier = identifier;
        this.version = version;
    }

    /**
     * @param source
     *            - source
     */
    public EventBean(final EventBean source) {
        this.timestamp = source.timestamp;
        this.subNetwork = source.subNetwork;
        this.networkElement = source.networkElement;
        this.name = source.name;
        this.identifier = source.identifier;
        this.version = source.version;
    }

    /**
     * Get the event time stamp
     *
     * @return The event time stamp
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Get the event NE
     *
     * @return The event NE
     */
    public String getNe() {
        return networkElement;
    }

    /**
     * Getter for the SubNetwork name
     *
     * @return network
     */
    public String getSubNetwork() {
        return subNetwork;
    }

    /**
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * @return event id
     */
    public int getIdentifier() {
        return identifier;
    }

    /**
     * @return new version
     */
    public String getVersion() {
        return version;
    }

    /**
     * @return byte array
     */
    public byte[] getRawData() {
        return rawData;
    }

    /**
     * @return byte array
     */
    public byte[] getAsn1Data() {
        return null;
    }

    /**
     * @return list of messages
     */
    public List<Asn1Message> getAsn1MessageList() {
        return asn1MessageList;
    }

    /**
     * @param L3MESSAGE_LENGTH
     *            - message
     */
    public void setL3MESSAGE_LENGTH(final int L3MESSAGE_LENGTH) {
    }

    /**
     * Set the value of the L3MESSAGE_CONTENTS parameter
     *
     * @param L3MESSAGE_CONTENTS
     *            The new value of the L3MESSAGE_CONTENTS parameter
     */
    public void setL3MESSAGE_CONTENTS(final byte[] L3MESSAGE_CONTENTS) {
    }

    /**
     * @param newName
     *            - new name
     */
    public void setName(final String newName) {
        name = newName;
    }

    /**
     * @param newId
     *            - new id
     */
    public void setId(final int newId) {
        identifier = newId;
    }

    /**
     * @param subNetwork
     *            - subnetwork
     */
    public void setSubNetwork(final String subNetwork) {
        this.subNetwork = subNetwork;
    }

    /**
     * @param newVersion
     *            - new version
     */
    public void setVersion(final String newVersion) {
        version = newVersion;
    }

    /**
     * Set the event time stamp, always UTC
     *
     * @param newTimestamp
     *            The event time stamp to set as a UTC time stamp
     */
    public void setTimestamp(final long newTimestamp) {
        timestamp = newTimestamp;
    }

    /**
     * Set the event NE
     *
     * @param newNe
     *            The new NE
     */
    public void setNe(final String newNe) {
        networkElement = newNe;
    }

    /**
     * Set the event raw data
     *
     * @param rawData
     *            The new NE
     */
    public void setRawData(final byte[] rawData) {
        this.rawData = rawData; // NOPMD
    }

    /**
     * Get the absolute name for this event
     *
     * @return the topic name
     */
    public abstract String getAbsoluteName();

    /**
     * Set the data of the event from a byte array, this method is generated from the XML schema for events
     *
     * @param event
     *            The event to set data for
     * @param data
     *            The byte array
     * @param offset
     *            The offset in the array to start at
     */
    public abstract void setData(Event event, byte[] data, int offset);

    /**
     * This method is used to set a list of messages received in ASN.1
     *
     * @param asn1MessageList
     *            The ASN.1 message list
     */
    public void setAsn1MessageList(final List<Asn1Message> asn1MessageList) {
        this.asn1MessageList = asn1MessageList;
    }

    /**
     * Output the event as a hash map
     *
     * @return A hash map containing the event
     */
    public Map<String, Object> getMap() {
        final Map<String, Object> outputMap = new HashMap<String, Object>();

        outputMap.put("timestamp", timestamp);
        outputMap.put("subNetwork", subNetwork);
        outputMap.put("ne", networkElement);
        outputMap.put("name", name);
        outputMap.put("id", identifier);
        outputMap.put("version", version);
        outputMap.put("rawData", rawData);
        outputMap.put("asn1MessageList", asn1MessageList);

        return outputMap;
    }

    /**
     * Output the event as a CSV string
     *
     * @return The event data as a CSV string
     */
    public String getCSVString() {
        final StringBuilder builder = new StringBuilder();

        builder.append(DATE_FORMAT.format(new Timestamp(timestamp)));
        builder.append(',');
        builder.append('"');
        builder.append(subNetwork);
        builder.append('"');
        builder.append(',');
        builder.append('"');
        builder.append(networkElement);
        builder.append('"');
        builder.append(',');
        builder.append('"');
        builder.append(name);
        builder.append('"');
        builder.append(',');
        builder.append('"');
        builder.append(identifier);
        builder.append('"');
        builder.append(',');
        builder.append('"');
        builder.append(version);
        builder.append('"');

        return builder.toString();
    }

    /**
     * Output the event as a CSV string with no double quotes around strings or arrays
     *
     * @return The event data as a CSV string
     */
    public String getCSVStringNoQuotes() {
        final StringBuilder builder = new StringBuilder();

        builder.append(DATE_FORMAT.format(new Timestamp(timestamp)));
        builder.append(',');
        builder.append(subNetwork);
        builder.append(',');
        builder.append(networkElement);
        builder.append(',');
        builder.append(name);
        builder.append(',');
        builder.append(identifier);
        builder.append(',');
        builder.append(version);

        return builder.toString();
    }

    /**
     * Output the event as a CSV string without header fields
     *
     * @return The event data as a CSV string without header fields
     */
    public abstract String getCSVStringWithoutHeader();

    /**
     * Output the event as a CSV string without header fields
     *
     * @return The event data as a CSV string without header fields
     */
    public abstract String getCSVStringNoQuotesWithoutHeader();

    /**
     * Method to decode an event, prefixing each parameter with its name
     *
     * @return string containing the event
     */
    public String getDecodedString() {
        final StringBuilder builder = new StringBuilder();

        builder.append(DATE_FORMAT.format(new Timestamp(timestamp)));
        builder.append(",SN=");
        builder.append(subNetwork);
        builder.append(",NE=");
        builder.append(networkElement);
        builder.append(',');
        builder.append(name);
        builder.append(",ID=");
        builder.append(identifier);
        builder.append(",VERSION=");
        builder.append(version);

        return builder.toString();
    }

    /**
     * Compare two event bean objects by time stamp
     *
     * @param otherBean
     *            The bean to compare this bean with
     * @return The comparison result
     */
    @Override
    public int compareTo(final EventBean otherBean) {
        try {
            if (this.timestamp != otherBean.timestamp) {
                if (this.timestamp > otherBean.timestamp) {
                    return 1;
                }
                return -1;
            }

            if (!this.subNetwork.equals(otherBean.subNetwork)) {
                return this.subNetwork.compareTo(otherBean.subNetwork);
            }

            if (!this.networkElement.equals(otherBean.networkElement)) {
                return this.networkElement.compareTo(otherBean.networkElement);
            }

            if (!this.name.equals(otherBean.name)) {
                return this.name.compareTo(otherBean.name);
            }

            if (this.identifier != otherBean.identifier) {
                //no overflow as ids range is limited
                return this.identifier - otherBean.identifier;
            }

            if (!this.version.equals(otherBean.version)) {
                return this.version.compareTo(otherBean.version);
            }
        } catch (final IllegalArgumentException e) {
            LOGGER.warn("compare error comparing this object " + this.toString() + " with " + otherBean.toString(), e);
            throw e;
        }

        return 0;
    }

    /**
     * @param parameter
     *            - parameter
     * @param offset
     *            - offset
     * @return adjust offset
     */
    protected int adjustOffset(final EventParameter parameter, final int offset) {
        int adjustedOffset = offset;
        if ((parameter.isUseValid() || parameter.isOptional()) && !parameter.isValidLTEembeddedbitFlag()) {
            adjustedOffset++;
        }
        adjustedOffset += parameter.getNumberOfBytes();
        return adjustedOffset;

    }
}

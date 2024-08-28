/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2015
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.component.aia.sdk.modelmanager.core.parser.util;

/**
 * @author aia
 */
public class SchemaUtil {

    public static final String FILE_SEPARATOR = "/";

    public static final String RESOURCES_PARENT_DIRECTORY = "xml";

    public static final String SCHEMA_TYPE_NAMESPACE = "http://www.ericsson.com/SchemaTypes";

    public static final String EVENT_DEFINITION_NAMESPACE = "http://www.ericsson.com/PmEvents";

    public static final String DEFAULT_EVENT_CLASS_PACKAGE = "com.ericsson.component.aia.model.generated.eventbean";

    public static final String SCHEMA_FILE_EXTENSION = ".xml";

    public static final String SCHEMA_TYPE_XML_FILE_PROPERTY_NAME = "SCHEMA_TYPE_XML_FILE";

    public static final String SCHEMA_TYPE_XML_FILE = System.getProperty(SCHEMA_TYPE_XML_FILE_PROPERTY_NAME,
            RESOURCES_PARENT_DIRECTORY + FILE_SEPARATOR + "SchemaTypes.xml");

    public static final String DEFAULT_SCHEMA_TYPE_SCHEMA_FILE = RESOURCES_PARENT_DIRECTORY + FILE_SEPARATOR + "SchemaTypes.xsd";

    public static final String EVENT_SCHEMA_XSD_FILE_LOCATION = RESOURCES_PARENT_DIRECTORY + FILE_SEPARATOR + "EventFormat.xsd";

    public static final String EMPTY_STRING = "";

    private SchemaUtil() {

    }

}
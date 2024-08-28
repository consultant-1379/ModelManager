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
package com.ericsson.component.aia.sdk.modelmanager.core.parser.util;

import static com.ericsson.component.aia.sdk.modelmanager.core.parser.util.GenerateEventBeansUtil.ARRAY_BRACKETS;
import static com.ericsson.component.aia.sdk.modelmanager.core.parser.util.GenerateEventBeansUtil.BASE_EVENT_BEAN_STG_FILE_WITH_DIRECTORY;
import static com.ericsson.component.aia.sdk.modelmanager.core.parser.util.GenerateEventBeansUtil.JAVA_EXTENSION;
import static com.ericsson.component.aia.sdk.modelmanager.core.parser.util.GenerateEventBeansUtil.NULL_STRING;
import static com.ericsson.component.aia.sdk.modelmanager.core.parser.util.GenerateEventBeansUtil.SUB_EVENT_BEAN_STG_FILE;
import static com.ericsson.component.aia.sdk.modelmanager.core.parser.util.GenerateEventBeansUtil.TRUE_FLAG;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

import com.ericsson.component.aia.sdk.modelmanager.core.parser.exception.ResourceNotFoundException;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.model.Event;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.model.EventParameter;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.resource.ResourceFileFinder;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.type.SchemaEnum;

/**
 * @author aia
 */
@SuppressWarnings("PMD")
public class JavaClassGenerator {
    private static final Logger LOG = LoggerFactory.getLogger(JavaClassGenerator.class);

    /**
     * Generate a base java class for all events of this type in the schema
     *
     * @param packageDir
     *            The directory in which to generate the Java class
     * @param mappedEvent
     *            Set of parameters for this event
     * @param packageName
     *            package name
     * @throws IOException
     *             - exception
     */
    public void generateBaseJavaClass(final File packageDir, final MappedEvent mappedEvent, final String packageName) throws IOException {
        final File eventBeanFile = new File(packageDir, mappedEvent.getEvent().getName() + JAVA_EXTENSION);
        final PrintWriter classWriter = new PrintWriter(new BufferedWriter(new FileWriter(eventBeanFile)));
        final STGroup group = getTemplateFile(BASE_EVENT_BEAN_STG_FILE_WITH_DIRECTORY);
        final ST stringTemplate = group.getInstanceOf("main");
        String isDataConversionNeeded = null;
        String asn1Parameter = NULL_STRING;
        String parameterJavaType = null;
        String parameterInitValue = null;
        String[] arrayElements;
        for (final EventParameter parameter : mappedEvent.getParameterSet()) {
            parameterJavaType = TypeConversionParserTypesToJava.typeSqlToJava(parameter.getParserType());
            parameterInitValue = getParameterInitValue(parameter);
            arrayElements = null;
            if (parameter.isStructArray()) {
                parameterJavaType += ARRAY_BRACKETS;
                parameterInitValue = getParameterArrayInitValue(parameterInitValue, parameter.getMaxStructArraySize());
                arrayElements = initializeArrayElement(parameter.getMaxStructArraySize());
            } else if (parameter.isEventArrayParameter()) {
                parameterJavaType += ARRAY_BRACKETS;
                parameterInitValue = getParameterArrayInitValue(parameterInitValue, parameter.getEventArrayParameterSize());
                arrayElements = initializeArrayElement(parameter.getEventArrayParameterSize());
            }

            stringTemplate.addAggr("parameter.{type, name, initValue, arrayElements, isByteArray, isString}", parameterJavaType, parameter.getName(),
                    parameterInitValue, arrayElements, getBinaryFlag(parameter), isStringType(parameterJavaType));

            if (isTypeBinary(parameter)) {
                isDataConversionNeeded = TRUE_FLAG;
            }

            // There would be just one ASN.1 parameter in an event ,so
            // asn1Parameter is set once and never overridden
            asn1Parameter = asn1Parameter.equals(NULL_STRING) ? getAsn1Param(parameter) : asn1Parameter;
        }
        addPackageNameToClass(stringTemplate, packageName);
        addSchemaTypeToClass(stringTemplate, packageDir, mappedEvent, isDataConversionNeeded, asn1Parameter);
        writeClassFile(stringTemplate, classWriter);
    }

    private STGroupFile getTemplateFile(final String templateFile) throws ResourceNotFoundException, MalformedURLException {
        return new STGroupFile(new ResourceFileFinder().getFileResourceURL(templateFile), STGroup.defaultGroup.encoding,
                STGroup.defaultGroup.delimiterStartChar, STGroup.defaultGroup.delimiterStopChar);
    }

    private void addPackageNameToClass(final ST stringTemplate, final String packageName) {
        stringTemplate.add("packageName", packageName);
    }

    /**
     * @param stringTemplate
     *            - template
     * @param packageDir
     *            - package dir
     * @param mappedEvent
     *            - mapped event
     * @param isDataConversionNeeded
     *            - if conversion is needed
     * @param asn1Parameter
     *            - ansi parameter
     */
    private void addSchemaTypeToClass(final ST stringTemplate, final File packageDir, final MappedEvent mappedEvent,
                                      final String isDataConversionNeeded, final String asn1Parameter) {
        stringTemplate.add("schemaType", packageDir.getName());
        stringTemplate.addAggr("event.{name, dataConvertersNeeded, asn1Parameter}", mappedEvent.getEvent().getName(), isDataConversionNeeded,
                asn1Parameter);
    }

    /**
     * @param parameterJavaType
     * @return
     */
    private String isStringType(final String parameterJavaType) {
        return "String".equals(parameterJavaType) ? TRUE_FLAG : null;
    }

    /**
     * @param parameter
     * @return
     */
    private String getBinaryFlag(final EventParameter parameter) {
        return isTypeBinary(parameter) ? TRUE_FLAG : null;
    }

    /**
     * @param stringTemplate
     * @param classWriter
     */
    private void writeClassFile(final ST stringTemplate, final PrintWriter classWriter) {
        classWriter.println(stringTemplate.render());
        classWriter.close();
    }

    /**
     * Generate a java sub class this event in this schema that inherits from the base java class
     *
     * @param schemaPackageDir
     *            The directory in which to generate the Java class
     * @param schemaType
     *            The base schema type for this schema
     * @param event
     *            The event to generate the mapping for
     * @param packageName
     *            package name
     * @throws IOException
     *             - exception
     */
    public void generateSubJavaClass(final File schemaPackageDir, final SchemaEnum schemaType, final Event event, final String packageName)
            throws IOException {
        final File eventBeanFile = new File(schemaPackageDir, event.getName() + JAVA_EXTENSION);

        final PrintWriter classWriter = new PrintWriter(new BufferedWriter(new FileWriter(eventBeanFile)));

        final STGroup stringTemplateGroup = getTemplateFile(SUB_EVENT_BEAN_STG_FILE);

        final ST stringTemplate = stringTemplateGroup.getInstanceOf("main");

        stringTemplate.add("schema", schemaPackageDir.getName());
        stringTemplate.add("schemaType", schemaType.value());
        stringTemplate.add("event", event.getName());
        stringTemplate.add("packageName", packageName);

        processSubClassEventParameter(event, stringTemplate);

        writeClassFile(stringTemplate, classWriter);
    }

    /**
     *
     * @param event
     *            - event type
     * @param stringTemplate
     *            - template
     */
    private void processSubClassEventParameter(final Event event, final ST stringTemplate) {
        final List<EventParameter> eventParameterList = event.getParameterList();

        boolean isoffsetToUseNeed = false;
        String setOffSetToUse = null;

        String defaultValuesNeeded = null;
        String lastParameterName = null;

        for (final EventParameter parameter : eventParameterList) {

            final String parameterJavaType = TypeConversionParserTypesToJava.typeSqlToJava(parameter.getParserType());

            String structArrayParam = null;
            String eventArrayParam = null;

            if (parameter.isStructArray()) {
                if (!isoffsetToUseNeed) {
                    isoffsetToUseNeed = true;
                    setOffSetToUse = TRUE_FLAG;
                } else {
                    setOffSetToUse = null;
                }
                structArrayParam = TRUE_FLAG;
            } else if (parameter.isEventArrayParameter()) {
                eventArrayParam = TRUE_FLAG;
            }

            if (defaultValuesNeeded == null && isAsn1Parameter(parameter)) {
                defaultValuesNeeded = TRUE_FLAG;
            }

            stringTemplate.addAggr(
                    "parameter.{type, name, structArrayParam, eventArrayParam, isLastStructParam, isoffsetToUseNeed, "
                            + "dataMethod, bits, asn1Parameter, lastParameterName}",
                    parameterJavaType, parameter.getName(), structArrayParam, eventArrayParam, parameter.isStructLastParameter() ? TRUE_FLAG : null,
                    setOffSetToUse, getJavaDataMethod(parameter), getNumberOfBits(parameter), isAsn1Parameter(parameter) ? TRUE_FLAG : null,
                    lastParameterName);
            lastParameterName = parameter.getName();
        }

        stringTemplate.add("defaultValuesNeeded", defaultValuesNeeded);

    }

    /**
     *
     * @param parameter
     *            - parameter
     * @return
     */
    private Integer getNumberOfBits(final EventParameter parameter) {
        return parameter.getType().equals(TypeConversionParamToParserTypes.P_IBCD) ? parameter.getNumberOfBits() : null;
    }

    /**
     *
     * @param parameter
     *            - parameter
     * @return
     */
    private boolean isTypeBinary(final EventParameter parameter) {
        return (parameter.getParserType() == ParserType.BYTE_ARRAY)
                || (((parameter.isStructArray() || parameter.isEventArrayParameter()) && parameter.getParserType() == ParserType.BYTE));
    }

    /**
     * Method to return the initial value for an array parameter
     *
     * @param elementInitValue
     *            The initial value of an array element
     * @param maxArraySize
     *            The maximum number of elements in the array
     * @return the initial value as a string
     */
    private String getParameterArrayInitValue(final String parameterInitValue, final int maxArraySize) {
        final StringBuilder builder = new StringBuilder();

        builder.append('{');

        for (int i = 0; i < maxArraySize; i++) {
            if (i > 0) {
                builder.append(',');
            }
            builder.append(parameterInitValue);
        }

        builder.append('}');

        return builder.toString();
    }

    /**
     * @param names
     *            - the names
     * @return string builder
     */
    protected StringBuilder getStringArrayForArrayElements(final List<String> names) {
        final StringBuilder builder = new StringBuilder();
        for (final String name : names) {
            builder.append(name).append(", ");
        }
        if (builder.length() > 0) {
            builder.deleteCharAt(builder.length() - 2);
        }
        return builder;
    }

    /**
     * Method to return the getData method for a Parser type
     *
     * @param parameter
     *            The parameter to get the method for
     * @return the method as a string
     */
    public String getJavaDataMethod(final EventParameter parameter) {
        final String sqlDataMethod = JavaDataTypeEnum.getValue(parameter.getParserType());
        if (sqlDataMethod != null) {
            return sqlDataMethod;
        }
        LOG.warn("parameter with incorrect type \"" + parameter.getParserType() + "\" found: " + parameter);
        return "";
    }

    /**
     * Method to return the initial value for a parameter
     *
     * @param parameter
     *            The parameter to get the method for
     * @return the initial value as a string
     */
    public String getParameterInitValue(final EventParameter parameter) {
        final String paramInitValue = JavaDataTypeEnum.getInitValue(parameter.getParserType());
        if (paramInitValue != null) {
            return paramInitValue;
        }
        LOG.warn("parameter with incorrect type \"" + parameter.getParserType() + "\" found: " + parameter);
        return "";
    }

    /**
     * @param size
     *            - the size of array
     * @return string array
     */
    protected String[] initializeArrayElement(final int size) {
        final String[] arrayElements = new String[size];

        for (int i = 0; i < size; i++) {
            arrayElements[i] = new String(Integer.toString(i));
        }
        return arrayElements;
    }

    /**
     * check if it contains Asn1 part
     *
     * @param parameter
     *            - parameter
     * @return get method for asn1 parameter
     */
    protected String getAsn1Param(final EventParameter parameter) {
        if (isAsn1Parameter(parameter)) {
            return "get" + parameter.getName() + "()";
        }
        return NULL_STRING;
    }

    /**
     *
     * @param parameter
     *            - parameter
     * @return
     */
    private static boolean isAsn1Parameter(final EventParameter parameter) {
        return parameter.getName().endsWith("MESSAGE_CONTENTS");
    }
}
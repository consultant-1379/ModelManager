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

import com.ericsson.component.aia.sdk.modelmanager.core.parser.decoder.GenericParameterDecoder;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.model.EventParameter;

/**
 * @author aia
 */
public class ParameterDecoderFactory {

    private ParameterDecoderFactory() {

    }

    /**
     * Parameter decoder
     *
     * @param eventParameter
     *            - the event parameter
     * @return the generic decoder
     */
    public static GenericParameterDecoder getParameterDecoder(final EventParameter eventParameter) {
        final ParserType parserType = eventParameter.getParserType();
        if (eventParameter.isStructArray()) {
            return parserType.getStructParameterDecoder(eventParameter);
        } else if (eventParameter.isEventArrayParameter()) {
            return parserType.getEventArrayByteParameterDecoder(eventParameter);
        }
        return parserType.getParameterDecoder(eventParameter);
    }

}

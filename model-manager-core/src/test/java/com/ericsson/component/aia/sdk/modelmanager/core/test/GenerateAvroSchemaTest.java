/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2017
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.component.aia.sdk.modelmanager.core.test;

import java.io.IOException;
import java.nio.file.Paths;

import org.junit.Test;

import com.ericsson.component.aia.sdk.modelmanager.core.parser.avro.AvroSchemaGenerator;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.exception.SchemaException;

/**
 * @author ezsalro
 *
 */
public class GenerateAvroSchemaTest {

    @Test
    public void testSchemaGeneration() throws SchemaException, IOException {

        final AvroSchemaGenerator jsonGenerator = new AvroSchemaGenerator();
        jsonGenerator.generate(Paths.get("src/main/resources/").toAbsolutePath().toString(), Paths.get("target/output").toAbsolutePath().toString());

    }
}

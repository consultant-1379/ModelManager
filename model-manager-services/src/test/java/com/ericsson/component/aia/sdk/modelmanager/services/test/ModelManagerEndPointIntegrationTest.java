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
package com.ericsson.component.aia.sdk.modelmanager.services.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.ericsson.aia.metadata.exception.MetaDataServiceException;
import com.ericsson.component.aia.sdk.modelmanager.services.configuration.EmbeddedMongoConfiguration;
import com.ericsson.component.aia.sdk.modelmanager.services.configuration.ModelManagerApplication;
import com.ericsson.component.aia.sdk.modelmanager.services.configuration.ModelManagerTestBeans;
import com.ericsson.component.aia.sdk.modelmanager.services.configuration.TestUtils;
import com.ericsson.component.aia.sdk.modelmanager.services.endpoints.response.ServiceResponse;

/**
 * Integration test for {@link ApplicationManagerEndPoint}
 *
 * @author ezsalro
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { ModelManagerTestBeans.class, EmbeddedMongoConfiguration.class,
        ModelManagerApplication.class }, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
public class ModelManagerEndPointIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TestUtils testUtils;

    public String addParser(String type, String name) throws Exception {

        final String xml = new String(Files.readAllBytes(Paths.get(new File("src/test/resources/" + type + "/" + name + ".xml").toURI())));

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        final HttpEntity<String> entity = new HttpEntity<>(xml, headers);

        final ResponseEntity<String> response = this.restTemplate.exchange("/model/" + type + "/" + name, HttpMethod.POST, entity, String.class);
        assertThat(response.getStatusCodeValue(), equalTo(200));

        return response.getBody().toString();

    }

    @Test
    public void addAndGetParser() throws Exception {

        final String idV7 = addParser("celltrace", "M_V7");
        assertThat(idV7, equalTo("model:celltrace:m_v7"));

        final String idAA11 = addParser("celltrace", "S_AA11");
        assertThat(idAA11, equalTo("model:celltrace:s_aa11"));

        Collection<String> parsers = getParsersByType("celltrace");
        assertThat(parsers.size(), equalTo(2));

        final ResponseEntity<String> getResponse = this.restTemplate.getForEntity("/model/id/" + idV7, String.class);

        final String xml = new String(Files.readAllBytes(Paths.get(new File("src/test/resources/celltrace/M_V7.xml").toURI())));

        final String storedXml = getResponse.getBody();

        assertEquals(xml, storedXml);

        this.restTemplate.delete("/model/" + idV7);

        parsers = getParsersByType("celltrace");
        assertThat(parsers.size(), equalTo(1));

        this.restTemplate.delete("/model/" + idAA11);

        //test should remove all XMl
        parsers = getParsersByType("celltrace");
        assertThat(parsers.size(), equalTo(0));

    }

    @Test
    public void validateXML() throws Exception {

        final String invalidXml = "bla bla bla";

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        HttpEntity<String> entity = new HttpEntity<>(invalidXml, headers);

        ResponseEntity<Boolean> response = this.restTemplate.exchange("/model/validate/", HttpMethod.POST, entity, Boolean.class);
        assertThat(response.getStatusCodeValue(), equalTo(200));
        assertThat(response.getBody(), equalTo(false));

        final String validXML = new String(Files.readAllBytes(Paths.get(new File("src/test/resources/celltrace/M_V7.xml").toURI())));

        entity = new HttpEntity<>(validXML, headers);
        response = this.restTemplate.exchange("/model/validate/", HttpMethod.POST, entity, Boolean.class);
        assertThat(response.getStatusCodeValue(), equalTo(200));
        assertThat(response.getBody(), equalTo(true));

    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Collection<String> getParsersByType(String type) {
        final ResponseEntity<ServiceResponse> response = this.restTemplate.getForEntity("/model/" + type, ServiceResponse.class);

        assertThat(response.getStatusCodeValue(), equalTo(200));

        return (Collection<String>) response.getBody().getData();
    }

    @After
    public void cleanUp() throws MetaDataServiceException {
        testUtils.clearMetaStore();
    }

}

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
package com.ericsson.component.aia.sdk.modelmanager.services.endpoints;

import static org.springframework.http.ResponseEntity.status;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.ericsson.component.aia.sdk.modelmanager.ModelManager;
import com.ericsson.component.aia.sdk.modelmanager.services.endpoints.response.ServiceResponse;
import com.ericsson.component.aia.sdk.pba.model.PBAInstance;

/**
 * This rest end point is used to serve up the services which are available to the UI. A service is represented as a special variety of application.
 */
@CrossOrigin
@Controller
public class ModelManagerEndPoint {

    @Autowired
    private ModelManager modelManager;

    /**
     * This POST end point accepts a XML file and returns {@link HttpStatus.OK} code.
     *
     * @param xml
     *            - model XML
     * @return if model is valid
     */
    @RequestMapping(consumes = "application/xml", value = "/model/validate/", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<Boolean> validate(@RequestBody final String xml) {
        return status(HttpStatus.OK).body(modelManager.validate(xml));
    }

    /**
     * Get the available models names.
     *
     * @param type
     *            the model type
     * @return all available models for this type.
     */
    @RequestMapping(value = "/model/{type}", method = RequestMethod.GET)
    public ResponseEntity<ServiceResponse<Collection<String>>> getModelByType(@PathVariable("type") final String type) {
        final ServiceResponse<Collection<String>> serviceResponse = new ServiceResponse<>();
        serviceResponse.setData(modelManager.listParsersByType(type));
        return status(HttpStatus.OK).body(serviceResponse);
    }

    /**
     * Get the available models.
     *
     * @return all available models for this type.
     */
    @RequestMapping(value = "/model/", method = RequestMethod.GET)
    public ResponseEntity<ServiceResponse<Collection<String>>> getAllModelTypes() {
        final ServiceResponse<Collection<String>> serviceResponse = new ServiceResponse<>();
        serviceResponse.setData(modelManager.listAllParserTypes());
        return status(HttpStatus.OK).body(serviceResponse);
    }

    /**
     * This POST end point accepts a XML file and returns {@link HttpStatus.OK} code along with the Id of the new model.
     *
     * @param name
     *            - model name
     * @param type
     *            - model type
     * @param model
     *            - model XML
     * @return the ID generated for this model
     */
    @RequestMapping(consumes = "application/xml", value = "/model/{type}/{name}", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<String> addModel(@PathVariable("type") final String type, @PathVariable("name") final String name,
                                           @RequestBody final String model) {
        return status(HttpStatus.OK).body(modelManager.addParser(name, type, model));
    }

    /**
     * Update model.
     *
     * @param modelId
     *            the model id
     * @param model
     *            the model
     */
    @RequestMapping(consumes = "application/xml", value = "/model/{id:.+}", method = RequestMethod.PUT)
    @ResponseStatus(value = HttpStatus.OK)
    public void updateModel(@PathVariable("id") final String modelId, @RequestBody final String model) {
        modelManager.updateParser(modelId, model);
    }

    /**
     * This DELETE endpoint accepts the model Id to be deleted and returns {@link HttpStatus.OK} if the model is deleted successfully.
     *
     * @param modelId
     *            the unique id of the model to delete.
     *
     */
    @RequestMapping(value = "/model/{id:.+}", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.OK)
    public void deleteModel(@PathVariable("id") final String modelId) {
        modelManager.deleteParser(modelId);
    }

    /**
     * This GET endpoint returns the a {@link ResponseEntity} containing the model XML which corresponds to the specified ID
     *
     * @param modelId
     *            the model id
     * @return {@link ResponseEntity} containing the {@link PBAInstance} which corresponds to the specified service ID.
     *
     */
    @RequestMapping(produces = "application/xml", value = "/model/id/{id:.+}", method = RequestMethod.GET)
    public ResponseEntity<String> getModel(@PathVariable("id") final String modelId) {
        return status(HttpStatus.OK).body(modelManager.getParser(modelId));
    }

    /**
     * This GET endpoint returns the a {@link ResponseEntity} containing the model XML which corresponds to the specified model name/type
     *
     * @param name
     *            - the model name
     * @param type
     *            - the model type
     * @return the model XML.
     */
    @RequestMapping(produces = "application/xml", value = "/model/{type}/{name}", method = RequestMethod.GET)
    public ResponseEntity<String> getModel(@PathVariable("type") final String type, @PathVariable("name") final String name) {
        return status(HttpStatus.OK).body(modelManager.getParser(name, type));
    }
}

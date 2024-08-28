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
package com.ericsson.component.aia.sdk.modelmanager.core.config;

/**
 * The Class ApplicationManagerConfiguration contains all of the configuration necessary for application manager. It should be initialised once at
 * start up.
 */
public class ModelManagerConfiguration {

    public static String storageLocation;
    public static String modelCatalogName;

    public static String dockerClientUsername;
    public static String dockerClientPassword;

    public static String dockerRepoBasePath;
    public static String dockerRepoServerUrl;

    public static String artifactoryServerUrl;
    public static String artifactoryServerPath;

    public static String gitServiceType;
    public static String gitServiceUrl;
    public static String gitAccessToken;

    private ModelManagerConfiguration() {

    }
}

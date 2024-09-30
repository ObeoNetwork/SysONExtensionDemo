/*******************************************************************************
 * Copyright (c) 2024 Obeo.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Obeo - initial API and implementation
 *******************************************************************************/
package org.eclipse.easymod.services.diagram;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.client5.http.async.methods.SimpleRequestBuilder;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.util.Timeout;
import org.eclipse.easymod.services.utils.CreateRepresentationVariables;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.sirius.components.collaborative.dto.CreateRepresentationInput;
import org.eclipse.sirius.components.core.RepresentationMetadata;
import org.eclipse.sirius.components.core.api.IEditingContext;
import org.eclipse.sirius.components.core.api.IObjectService;
import org.eclipse.sirius.components.core.api.IRepresentationDescriptionSearchService;
import org.eclipse.sirius.components.core.api.IURLParser;
import org.eclipse.sirius.components.representations.IRepresentationDescription;
import org.eclipse.sirius.web.application.representation.services.RepresentationApplicationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.google.gson.Gson;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Methodology form services.
 *
 * @author ebausson
 */
public class MethodologyFormServices {

    public static final String EOL = "\n"; //$NON-NLS-1$

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodologyFormServices.class);

    @Autowired
    private RepresentationApplicationService representationApplicationService;

    private final IRepresentationDescriptionSearchService representationDescriptionSearchService;

    private final IURLParser urlParser;

    private final IObjectService objectService;

    public MethodologyFormServices(IRepresentationDescriptionSearchService representationDescriptionSearchService,
            IURLParser urlParser, IObjectService objectService) {
        this.representationDescriptionSearchService = Objects.requireNonNull(representationDescriptionSearchService);
        this.urlParser = Objects.requireNonNull(urlParser);
        this.objectService = Objects.requireNonNull(objectService);
    }

    /**
     * Get all diagrams existing in the editing context that have the given representation description ids.
     *
     * @param object
     *            the object on which the service has been called.
     * @param representationDescriptionIds
     *            the given representation description ids.
     * @param editingContext
     *            the editingContext in which to search.
     * @return all diagrams existing in the editing context that have the given representation description ids.
     */
    public List<RepresentationMetadata> getAllRepresentations(EObject object, List<String> representationDescriptionIds,
            IEditingContext editingContext) {
        Page<RepresentationMetadata> representationsPage = representationApplicationService
                .findAllByEditingContextId(editingContext.getId(), Pageable.ofSize(20));
        return representationsPage.stream().filter(desc -> {
            List<String> sourceIds = this.urlParser.getParameterValues(desc.getDescriptionId()).get("sourceElementId");
            if (sourceIds != null && sourceIds.size() > 0) {
                return representationDescriptionIds.contains(sourceIds.get(0));
            }
            return false;
        }).toList();
    }

    /**
     * Get label from a given representation.
     * 
     * @param representationMetadata
     *            the representation with the label to display
     * @return the label f the representation
     */
    public String getLabel(RepresentationMetadata representationMetadata) {
        return representationMetadata.getLabel();
    }

    /**
     * Create a representation from its id on given object.
     * 
     * @param object
     *            root object of the representation
     * @param representationDescriptionId
     *            id of the representation to create
     * @param editingContext
     *            the editing context
     * @return {@code true} if the representation has been created, {@code false} otherwise
     */
    public boolean createRepresentation(EObject object, String representationDescriptionId,
            IEditingContext editingContext) {
        Optional<IRepresentationDescription> representationDescription = this.representationDescriptionSearchService
                .findById(editingContext, representationDescriptionId);
        try {
            this.createRepresentation(object, representationDescription.get(), editingContext);
        } catch (IOException e) {
            LOGGER.error("Error while creating representation", e);
            return false;
        }
        return true;
    }

    private void createRepresentation(EObject object, IRepresentationDescription representationDescription,
            IEditingContext editingContext) throws IOException {

        Gson gson = new Gson();

        StringBuilder query = new StringBuilder();
        query.append("mutation createRepresentation($input: CreateRepresentationInput!) {").append(EOL);
        query.append("    createRepresentation(input: $input) {").append(EOL);
        query.append("        __typename").append(EOL);
        query.append("        ... on CreateRepresentationSuccessPayload {").append(EOL);
        query.append("          representation {").append(EOL);
        query.append("            __typename").append(EOL);
        query.append("            id").append(EOL);
        query.append("            label").append(EOL);
        query.append("          }").append(EOL);
        query.append("        }").append(EOL);
        query.append("    }").append(EOL);
        query.append("}");

        String objectId = this.objectService.getId(object);
        UUID randomUUID = UUID.randomUUID();
        CreateRepresentationInput input = new CreateRepresentationInput(randomUUID, editingContext.getId(),
                representationDescription.getId(), objectId, representationDescription.getLabel());
        CreateRepresentationVariables createRepresentationVariables = new CreateRepresentationVariables(input);

        HashMap<String, Object> map = new LinkedHashMap<>();
        map.put("operationName", "createRepresentation");
        map.put("query", query.toString());
        map.put("variables", createRepresentationVariables);

        String jsonMap = gson.toJson(map);

        this.sendHttpGraphQLRequest(jsonMap);

    }

    private void sendHttpGraphQLRequest(String requestContent) throws IOException {
        RequestAttributes currentRequestAttributes = RequestContextHolder.currentRequestAttributes();
        if (currentRequestAttributes instanceof ServletRequestAttributes sra) {
            HttpServletRequest request = sra.getRequest();
            String scheme = request.getScheme();
            String serverName = request.getServerName();
            int serverPort = request.getServerPort();
            String requestURI = request.getRequestURI();
            StringBuilder entryPoint = new StringBuilder();
            entryPoint.append(scheme).append("://");
            entryPoint.append(serverName).append(":");
            entryPoint.append(serverPort);
            entryPoint.append(requestURI);
            CloseableHttpAsyncClient client = HttpAsyncClients.createDefault();
            client.start();
            try (StringEntity stringEntity = new StringEntity(requestContent, ContentType.APPLICATION_JSON)) {
                SimpleHttpRequest httpRequest = SimpleRequestBuilder.post(entryPoint.toString())
                        .setBody(stringEntity.getContent().readAllBytes(), ContentType.APPLICATION_JSON).build();
                RequestConfig requestConfig = RequestConfig.custom()
                        .setConnectionRequestTimeout(Timeout.of(100, TimeUnit.MILLISECONDS))
                        .setResponseTimeout(Timeout.of(100, TimeUnit.MILLISECONDS)).build();
                HttpClientContext httpContext = HttpClientContext.create();
                httpContext.setRequestConfig(requestConfig);
                Future<SimpleHttpResponse> response = client.execute(httpRequest, httpContext, null);
                response.get(50, TimeUnit.MILLISECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
            } finally {
                client.close();
            }
        }
    }
}

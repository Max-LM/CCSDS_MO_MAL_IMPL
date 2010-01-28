/* ----------------------------------------------------------------------------
 * (C) 2010      European Space Agency
 *               European Space Operations Centre
 *               Darmstadt Germany
 * ----------------------------------------------------------------------------
 * System       : CCSDS MO MAL Implementation
 * Author       : cooper_sf
 *
 * ----------------------------------------------------------------------------
 */
package org.ccsds.moims.mo.mal.impl;

import org.ccsds.moims.mo.mal.impl.util.MALClose;
import java.util.Hashtable;
import org.ccsds.moims.mo.mal.MALService;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.structures.QoSLevel;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.transport.MALEndPoint;
import org.ccsds.moims.mo.mal.impl.transport.TransportSingleton;
import org.ccsds.moims.mo.mal.provider.MALInteractionHandler;
import org.ccsds.moims.mo.mal.transport.MALTransport;

/**
 * Base class that is used by service providers, both providers and Brokers.
 */
public abstract class ServiceComponentImpl extends MALClose
{
  protected final MessageSend sendHandler;
  protected final MessageReceive receiveHandler;
  protected final MALInteractionHandler handler;
  protected final String localName;
  protected final String protocol;
  protected final MALService service;
  protected final Blob authenticationId;
  protected final QoSLevel[] expectedQos;
  protected final Integer priorityLevelNumber;
  protected final Hashtable defaultQoSProperties;
  protected final URI localUri;
  protected final MALTransport transport;
  protected final MALEndPoint endpoint;
  protected final EndPointAdapter endpointAdapter;
  protected final Address msgAddress;

  /**
   * Constructor.
   * @param parent Parent object.
   * @param impl MAL impl.
   * @param localName Local name of this component.
   * @param protocol The protocol to use.
   * @param service The service.
   * @param authenticationId Athentication identifier.
   * @param expectedQos Expected QoS.
   * @param priorityLevelNumber Number of priority levels.
   * @param defaultQoSProperties Default QOS properties.
   * @param handler Service interaction handler.
   * @throws MALException on error.
   */
  public ServiceComponentImpl(MALClose parent,
          MALImpl impl,
          String localName,
          String protocol,
          MALService service,
          Blob authenticationId,
          QoSLevel[] expectedQos,
          int priorityLevelNumber,
          Hashtable defaultQoSProperties,
          MALInteractionHandler handler) throws MALException
  {
    super(parent);

    this.sendHandler = impl.getSendingInterface();
    this.receiveHandler = impl.getReceivingInterface();
    this.handler = handler;
    this.localName = localName;
    this.protocol = protocol;
    this.service = service;
    this.authenticationId = authenticationId;
    if (null != expectedQos)
    {
      this.expectedQos = java.util.Arrays.copyOf(expectedQos, expectedQos.length);
    }
    else
    {
      this.expectedQos = null;
    }
    this.priorityLevelNumber = priorityLevelNumber;
    if (null != defaultQoSProperties)
    {
      this.defaultQoSProperties = (Hashtable) defaultQoSProperties.clone();
    }
    else
    {
      this.defaultQoSProperties = null;
    }

    if (null != service)
    {
      this.transport = TransportSingleton.instance(protocol, impl.getInitialProperties());
      this.endpoint = transport.createEndPoint(localName, service, defaultQoSProperties);
      this.localUri = this.endpoint.getURI();
      this.msgAddress = new Address(endpoint, endpoint.getURI(), authenticationId, handler);
      this.endpointAdapter = new EndPointAdapter(receiveHandler, this.msgAddress);
      this.endpoint.setMessageListener(endpointAdapter);
    }
    else
    {
      this.transport = null;
      this.endpoint = null;
      this.endpointAdapter = null;
      this.msgAddress = null;
      this.localUri = null;
    }
  }

  /**
   * Returns the URI of this component.
   * @return the URI.
   */
  public URI getURI()
  {
    return this.localUri;
  }

  /**
   * Returns the interaction handler for messages received by this component.
   * @return the interaction handler.
   */
  public MALInteractionHandler getHandler()
  {
    return handler;
  }

  /**
   * Returns the Endpoint for sending messages from this component.
   * @return the Endpoint.
   */
  public MALEndPoint getEndpoint()
  {
    return endpoint;
  }

  /**
   * Returns the authentication identifier used by this component.
   * @return the Authentication Id.
   */
  public Blob getAuthenticationId()
  {
    return authenticationId;
  }

  /**
   * Returns the Address structure used by this component.
   * @return the Address structure.
   */
  public Address getMsgAddress()
  {
    return msgAddress;
  }

  @Override
  public void close() throws MALException
  {
    super.close();
  }
}
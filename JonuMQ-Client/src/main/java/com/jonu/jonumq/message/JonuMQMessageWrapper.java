/*
 *
 */
package com.jonu.jonumq.message;

import java.io.Serializable;

/**
 * @author prabhato
 * @version $Revision$, $Date$, $Author$
 * @since 6/14/2016
 */
public class JonuMQMessageWrapper implements Serializable
{
    String destination;
    String messageId;
    long messageCreationTime;
    long messageInTime;
    long messageOutTime;
    boolean persistent;
    JonuMQMessage message;
    boolean consumed = false;

    public boolean isConsumed()
    {
        return consumed;
    }

    public void setConsumed(boolean consumed)
    {
        this.consumed = consumed;
    }

    public JonuMQMessageWrapper()
    {

    }

    public boolean isPersistent()
    {
        return persistent;
    }

    public void setPersistent(boolean persistent)
    {
        this.persistent = persistent;
    }

    public long getMessageOutTime()
    {
        return messageOutTime;
    }

    public void setMessageOutTime(long messageOutTime)
    {
        this.messageOutTime = messageOutTime;
    }

    public long getMessageInTime()
    {
        return messageInTime;
    }

    public void setMessageInTime(long messageInTime)
    {
        this.messageInTime = messageInTime;
    }

    public long getMessageCreationTime()
    {
        return messageCreationTime;
    }

    public void setMessageCreationTime(long messageCreationTime)
    {
        this.messageCreationTime = messageCreationTime;
    }

    public String getMessageId()
    {
        return messageId;
    }

    public void setMessageId(String messageId)
    {
        this.messageId = messageId;
    }

    public void setMessage(JonuMQMessage message)
    {
        this.message = message;
    }

    public String getDestination()
    {
        return destination;
    }

    public void setDestination(String destination)
    {
        this.destination = destination;
    }

    public JonuMQMessage getMessage()
    {
        return message;
    }
}

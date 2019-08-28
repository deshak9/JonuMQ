/*
 *
 */
package com.jonu.jonumq.consumer;

import com.jonu.jonumq.channel.Channel;
import com.jonu.jonumq.message.JonuMQMessageWrapper;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author prabhato
 * @version $Revision$, $Date$, $Author$
 * @since 6/17/2016
 */
public class TopicConsumer extends Consumer
{
    @Override
    public void doProcess(Channel channel)
    {
        while (true) {
            JonuMQMessageWrapper message = super.getFirstMessage(channel);
            if (message != null) {
                long messageOutTime = message.getMessageOutTime();

                // need this temporary list to remove closed out stream
                // because if we remove here itself then other consumers will be affected
                List<ObjectOutputStream> needToRemove = new ArrayList<ObjectOutputStream>();
                for (ObjectOutputStream out : channel.getConsumerList()) {
                    try {
                        super.dispatchMessage(out, message);
                        message.setConsumed(true);
                    } catch (IOException ex) {
                        // If consumer is disconnected we can remove the consumer OUTPUT stream from the list
                        message.setMessageOutTime(messageOutTime);

                        // need this temporary list to remove closed out stream
                        // because if we remove here itself then other consumers will be affected
                        needToRemove.add(out);
                    }
                }
                channel.removeAllOutStream(needToRemove);
                // Persist the message if not consumed
                super.persistMessage(message);
            }
        }

    }
}

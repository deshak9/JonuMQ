package com.jonu.jonumq.channel;

import com.jonu.jonumq.message.JonuMQMessageWrapper;

import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author prabhato
 * @version $Revision$, $Date$, $Author$
 * @since 6/9/2016
 */
public class Channel
{
    private final Logger logger = Logger.getLogger(Channel.class.getName());
    LinkedBlockingQueue<JonuMQMessageWrapper> list = new LinkedBlockingQueue<JonuMQMessageWrapper>();

    private LinkedBlockingQueue<JonuMQMessageWrapper> listOfMessages;
    private LinkedBlockingQueue<JonuMQMessageWrapper> listOfPersistMessage;
    private List<ObjectOutputStream> consumerList;
    private ChannelType channelType = null;
    private volatile boolean running = true;
    private final int MAX_MESSAGES = Integer.MAX_VALUE;

    public Channel()
    {
        this.listOfMessages = new LinkedBlockingQueue<JonuMQMessageWrapper>(MAX_MESSAGES);
        this.listOfPersistMessage = new LinkedBlockingQueue<JonuMQMessageWrapper>(MAX_MESSAGES);
        this.consumerList = new ArrayList<ObjectOutputStream>();
    }

    public void addMessage(JonuMQMessageWrapper jonuMQMessageWrapper)
    {
        if (jonuMQMessageWrapper == null) {
            throw new NullPointerException("Message object can't be null");
        }

        haltIfReachedMaxMessage();

        // if queue reach max size this method will return False
        this.listOfMessages.offer(jonuMQMessageWrapper);
    }

    private void haltIfReachedMaxMessage()
    {
        if (listOfMessages.size() >= MAX_MESSAGES) {
            logger.log(Level.INFO, "Buffer message queue is full halting producer for a while to send message");
            while (listOfMessages.size() > MAX_MESSAGES - 10) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public JonuMQMessageWrapper getFirstMessage() throws InterruptedException
    {
        // this method block the queue until there is some data in queue
        return listOfMessages.take();
    }

    public void addConsumer(ObjectOutputStream out)
    {
        consumerList.add(out);
    }

    public ChannelType getChannelType()
    {
        return channelType;
    }

    public void setChannelType(ChannelType channelType)
    {
        this.channelType = channelType;
    }

    public boolean isRunning()
    {
        return running;
    }

    public void setRunning(boolean running)
    {
        this.running = running;
    }

    public int getMessageCount()
    {
        return listOfMessages.size();
    }

    public List<ObjectOutputStream> getConsumerList()
    {
        return consumerList;
    }

    public void setConsumerList(List<ObjectOutputStream> consumerList)
    {
        this.consumerList = consumerList;
    }

    public void removeOutStream(ObjectOutputStream out)
    {
        consumerList.remove(out);
    }

    public void removeAllOutStream(List<ObjectOutputStream> needToRemove)
    {
        consumerList.removeAll(needToRemove);
    }
}

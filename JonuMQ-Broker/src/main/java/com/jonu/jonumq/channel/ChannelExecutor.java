package com.jonu.jonumq.channel;

import com.jonu.jonumq.consumer.MessageConsumption;
import com.jonu.jonumq.exception.DuplicateChannelException;
import com.jonu.jonumq.exception.UnknownDefinedChannel;

import java.util.HashMap;
import java.util.Map;

/**
 * @author prabhato
 * @version $Revision$, $Date$, $Author$
 * @since 6/9/2016
 */
public class ChannelExecutor
{

    private static ChannelExecutor channelExecutor = new ChannelExecutor();

    private static Map<String, Channel> channels;

    private ChannelExecutor()
    {
        channels = new HashMap<String, Channel>();
    }

    public static ChannelExecutor getChannelExecutor()
    {
        return channelExecutor;
    }

    public Channel getChannel(String channelName)
    {
        if (channelName == null || channelName.isEmpty()) {
            throw new UnknownDefinedChannel("Channel Name can't be null or empty");
        }
        if (channels.containsKey(channelName)) {
            return channels.get(channelName);
        } else {
            return null;
        }
    }

    public Channel addNewChannel(String channelName)
    {
        checkIfChannelAlreadyExist(channelName);
        Channel channel = new Channel();

        startMessageConsumption(channel);

        channels.put(channelName, channel);
        return channel;
    }

    private void checkIfChannelAlreadyExist(String channelName)
    {
        if (channels.containsKey(channelName)) {
            throw new DuplicateChannelException("Channel Already exist with the name " + channelName);
        }
    }

    private void startMessageConsumption(Channel channel)
    {
        Thread consume = new MessageConsumption(channel);
        consume.start();
    }
}

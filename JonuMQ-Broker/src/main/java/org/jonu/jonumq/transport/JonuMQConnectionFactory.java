package org.jonu.jonumq.transport;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSException;

/**
 * @author prabhato
 * @version $Revision$, $Date$, $Author$
 * @since 6/10/2016
 */
public class JonuMQConnectionFactory implements ConnectionFactory
{
    private final String DEFAULT_HOST = "localhost";
    private final int DEFAULT_PORT = 2056;
    private String host;
    private int port;

    public JonuMQConnectionFactory(String host, int port)
    {
        this.host = host;
        this.port = port;
    }

    public JonuMQConnectionFactory(String host)
    {
        this.host = host;
        this.port = DEFAULT_PORT;
    }

    public JonuMQConnectionFactory()
    {
        this.host = DEFAULT_HOST;
        this.port = DEFAULT_PORT;
    }


    @Override
    public Connection createConnection() throws JMSException
    {
        return createJonuMQConnection();
    }

    private JonuMQConnection createJonuMQConnection()
    {
        return new JonuMQConnection();
    }

    @Override
    public Connection createConnection(String s, String s2) throws JMSException
    {
        return null;  //$REVIEW$ To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public JMSContext createContext()
    {
        return null;  //$REVIEW$ To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public JMSContext createContext(String s, String s2)
    {
        return null;  //$REVIEW$ To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public JMSContext createContext(String s, String s2, int i)
    {
        return null;  //$REVIEW$ To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public JMSContext createContext(int i)
    {
        return null;  //$REVIEW$ To change body of implemented methods use File | Settings | File Templates.
    }
}

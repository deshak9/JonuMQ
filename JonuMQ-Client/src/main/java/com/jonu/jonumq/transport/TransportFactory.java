/*
 *
 */
package com.jonu.jonumq.transport;

import com.jonu.jonumq.message.JonuMQMessageWrapper;
import com.jonu.jonumq.message.JonuMQWireMessage;
import sun.net.ConnectionResetException;

import javax.jms.JMSException;
import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author prabhato
 * @version $Revision$, $Date$, $Author$
 * @since 6/22/2016
 */
public class TransportFactory
{
    private final Logger logger = Logger.getLogger(TransportFactory.class.getName());
    private Socket client = null;
    private ObjectOutputStream out = null;
    private ObjectInputStream in = null;
    private String host;
    private int port;

    public TransportFactory(String host, int port)
    {
        this.host = host;
        this.port = port;
    }

    public ObjectInputStream getIn()
    {
        return in;
    }

    public void setIn(ObjectInputStream in)
    {
        this.in = in;
    }

    public ObjectOutputStream getOut()
    {
        return out;
    }

    public void setOut(ObjectOutputStream out)
    {
        this.out = out;
    }

    public Socket getClient()
    {
        return client;
    }

    public void setClient(Socket client)
    {
        this.client = client;
    }

    public void start() throws JMSException
    {
        startClient();
    }

    private void startClient()
    {
        if (client != null) {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();  //$REVIEW$ To change body of catch statement use File | Settings | File Templates.
            } finally {
                client = null;
            }
        }
        while (client == null) {
            try {
                client = new Socket(host, port);
            } catch (IOException e) {
                client = null;
                logger.log(Level.SEVERE, "Either remote server is not running or there is some issue connecting to host: " +
                        host + " and port: " + port + "  Retrying again after 10 seconds");

                //e.printStackTrace();  //$REVIEW$ To change body of catch statement use File | Settings | File Templates.
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    public void createOutPutStream() throws JMSException
    {
        while (out == null) {
            getOutPutStream();
        }
    }

    private void getOutPutStream() throws JMSException
    {
        try {
            if (client == null) {
                startClient();
            }
            out = new ObjectOutputStream(new DataOutputStream(client.getOutputStream()));
        } catch (IOException e) {
            closeOutStream();
            startClient();
            logger.log(Level.SEVERE, "");
            e.printStackTrace();
        }
    }

    public void send(JonuMQWireMessage wireMessage) throws JMSException
    {
        try {
            if (out == null) {
                getOutPutStream();
            }
            // when we send same object even after modifying the underline variable, ObjectOutStream will send same old object
            // In our case wireMessage is same object we are setting only the message
            // Resolution : use Unshared write instead of writeObject
            out.writeUnshared(wireMessage);
        } catch (ConnectionResetException ex) {
            closeOutStream();
            retrySend();
            send(wireMessage);
        } catch (IOException e) {
            closeOutStream();
            retrySend();
            send(wireMessage);
        }
    }

    private void retrySend() throws JMSException
    {
        createOutPutStream();
    }

    private void closeOutStream()
    {
        try {
            if (out != null) {
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            out = null;
        }
    }

    public void createInPutStream()
    {
        while (in == null) {
            getInPutStream();
        }
    }

    private void getInPutStream()
    {
        try {
            if (client == null) {
                startClient();
            }
            in = new ObjectInputStream(new DataInputStream(client.getInputStream()));
        } catch (IOException e) {
            closeInStream();
            startClient();
            logger.log(Level.SEVERE, "");
            e.printStackTrace();
        }
    }

    private void closeInStream()
    {
        try {
            if (in != null) {
                in.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            in = null;
        }
    }

    public JonuMQMessageWrapper readNextMessage(JonuMQWireMessage consumerRegisteringMessage) throws ClassNotFoundException, JMSException
    {
        if (in == null) {
            getInPutStream();
        }

        JonuMQMessageWrapper message = null;
        try {
            message = (JonuMQMessageWrapper) in.readObject();
        } catch (ConnectException e) {
            retryReceive(consumerRegisteringMessage);
        } catch (ConnectionResetException e) {
            retryReceive(consumerRegisteringMessage);
        } catch (IOException e) {
            e.printStackTrace();
            retryReceive(consumerRegisteringMessage);
        }
        return message;

    }

    private void retryReceive(JonuMQWireMessage consumerRegisteringMessage) throws JMSException
    {
        closeInStream();
        startClient();

        // register the consumer again
        send(consumerRegisteringMessage);
        createInPutStream();
    }
}

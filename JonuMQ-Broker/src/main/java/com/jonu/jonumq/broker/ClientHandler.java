/*
 *
 */
package com.jonu.jonumq.broker;

import com.jonu.jonumq.channel.ChannelExecutor;
import com.jonu.jonumq.client.ClientTypeHandler;
import com.jonu.jonumq.client.ClientTypeResolver;
import com.jonu.jonumq.exception.BrokerStoppedException;
import com.jonu.jonumq.message.JonuMQWireMessage;
import sun.net.ConnectionResetException;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author prabhato
 * @version $Revision$, $Date$, $Author$
 * @since 6/15/2016
 */
public class ClientHandler extends Thread
{
    private static final Logger logger = Logger.getLogger(ClientHandler.class.getName());
    private Socket socket;
    private ChannelExecutor executor;

    public ClientHandler(Socket socket, ChannelExecutor executor)
    {
        this.socket = socket;
        this.executor = executor;
    }

    @Override
    public void run()
    {
        DataInputStream in = null;
        DataOutputStream out = null;

        try {
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            doProcess(in, out);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BrokerStoppedException e) {
            e.printStackTrace();
        } finally {

        }
    }

    private void doProcess(DataInputStream in, DataOutputStream out) throws BrokerStoppedException
    {
        ObjectInputStream objectInputStream = null;
        while (true) {
            try {
                if (objectInputStream == null) {
                    objectInputStream = getInputStream(in);
                    //objectInputStream = null;
                }
            } catch (IOException e) {
                logger.log(Level.INFO, "Client got disconnected");
                e.printStackTrace();
                break;
            }
            byte[] bytes = new byte[100];
            System.out.println(new String(bytes));
            if (objectInputStream != null) {
                if (!Server.isBrokerRunning()) {
                    logger.log(Level.SEVERE, "Broker was stopped, stopping all work");
                    throw new BrokerStoppedException("Broker was stopped, stopping all work");
                }
                // start Processing if we receive any data on input stream
                try {
                    JonuMQWireMessage wireMessage = receiveObject(objectInputStream);
                    if (wireMessage != null) {
                        ClientTypeHandler client = null;

                        client = ClientTypeResolver.resolve(wireMessage);
                        client.doProcess(wireMessage, out, executor);
                    }
                } catch (ConnectionResetException cre) {
                    logger.log(Level.INFO, "Client got disconnected");
                    break;
                } catch (SocketException ex) {
                    logger.log(Level.INFO, "Client got disconnected");
                    break;
                } catch (IOException e) {
                    logger.log(Level.INFO, "Client got disconnected");
                    e.printStackTrace();
                    break;
                } catch (ClassNotFoundException e) {
                    logger.log(Level.INFO, "Client got disconnected");
                    e.printStackTrace();
                    break;
                }
            }
        }

    }

    private ObjectInputStream getInputStream(DataInputStream in) throws IOException
    {
        //return new ObjectInputStream(in);
        return null;
    }

    private boolean checkIfAnyObjectReceived(DataInput in)
    {

        try {
            short value = in.readShort();
            if (value > 0)
                return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private JonuMQWireMessage receiveObject(ObjectInputStream in) throws IOException, ClassNotFoundException
    {
        return (JonuMQWireMessage) in.readObject();
    }
}

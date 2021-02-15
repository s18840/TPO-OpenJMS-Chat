package jms1;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;

public class Client1 extends JFrame {
    private Context context = null;
    private TopicConnectionFactory factory = null;
    private TopicConnection connection = null;
    private TopicSession session = null;
    private Topic topic = null;
    private TopicSubscriber subscriber = null;
    private TopicPublisher publisher = null;
    private String port;
    private String name;
    public static  String ANSI_CYAN = "\u001B[36m";
    public static  String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_YELLOW = "\u001B[33m";


    public Client1(String name) {
    this.name=name;
    }
    public Client1() {

    }
    public void sendMes(String msg){
        Properties initialProperties = new Properties();
        initialProperties.put(InitialContext.INITIAL_CONTEXT_FACTORY,
                "org.exolab.jms.jndi.InitialContextFactory");
        initialProperties.put(InitialContext.PROVIDER_URL,
                "tcp://localhost:1234");

        try {
            context = new InitialContext(initialProperties);
            factory = (TopicConnectionFactory) context
                    .lookup("ConnectionFactory");
            topic = (Topic) context.lookup("topic1");
            connection = factory.createTopicConnection();
            session = connection.createTopicSession(false,
                    TopicSession.AUTO_ACKNOWLEDGE);
            publisher = session.createPublisher(topic);
            EventMessage eventMessage = new EventMessage(1,
                    msg);
            ObjectMessage objectMessage = session.createObjectMessage();
            objectMessage.setObject(eventMessage);
            connection.start();
            publisher.publish(objectMessage);
            //System.out.println(this.getClass().getName()
            //        + " has sent a message : " + eventMessage);

        } catch (NamingException e) {
            e.printStackTrace();
        } catch (JMSException e) {
            e.printStackTrace();
        }
        if (context != null) {
            try {
                context.close();
            } catch (NamingException ex) {
                ex.printStackTrace();
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (JMSException ex) {
                ex.printStackTrace();
            }
        }
    }
    public void receiveMessage() {
        Properties initialProperties = new Properties();
        initialProperties.put(InitialContext.INITIAL_CONTEXT_FACTORY,
                "org.exolab.jms.jndi.InitialContextFactory");
        initialProperties.put(InitialContext.PROVIDER_URL,
                "tcp://localhost:1234");
        try {
            context = new InitialContext(initialProperties);
            factory = (TopicConnectionFactory) context
                    .lookup("ConnectionFactory");
            topic = (Topic) context.lookup("topic1");
            connection = factory.createTopicConnection();
            session = connection.createTopicSession(false,
                    TopicSession.AUTO_ACKNOWLEDGE);
            subscriber = session.createSubscriber(topic);
            connection.start();
            Message message = subscriber.receive();
            if (message instanceof ObjectMessage) {
                Object object = ((ObjectMessage) message).getObject();
                //System.out.println(name+": "+(EventMessage) object);
                //System.out.println((EventMessage) object);

                System.out.println((EventMessage)object);
            }

        } catch (NamingException e) {

            e.printStackTrace();
        } catch (JMSException e) {

            e.printStackTrace();
        }
        if (context != null) {
            try {
                context.close();
            } catch (NamingException ex) {
                ex.printStackTrace();
            }
        }

        if (connection != null) {
            try {
                connection.close();
            } catch (JMSException ex) {
                ex.printStackTrace();
            }
        }

    }

    public static void main(String[] args) throws IOException,InterruptedException {
        //Client client1 = new Client(args[0]);
        Client client1 = new Client(args[0]);
        System.out.println("Welcome "+ args[0]+ " to the Stranger Chat.");
        System.out.println("You are connected with "+args[1]+", say Hi!");

        Thread send = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Scanner sc = new Scanner(System.in);
                    while (true) {
                        synchronized (this){
                            byte[] sd = new byte[1000];
                            sd = sc.nextLine().getBytes();
                            String msg = new String(sd);

                            //System.out.println(args[1]+ " says: "
                            //        + msg);

                            if (msg.equals("bye")) {
                                client1.sendMes(msg);
                                System.out.println("Exiting... ");
                                System.exit(0);
                            }
                            client1.sendMes(msg);
                        }
                    }
                }catch(Exception e){

                }
            }
        });

        Thread receive = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    while (true) {
                        synchronized (this){
                            //client1.receiveMessage(args[1]);
                            client1.receiveMessage();
                        }
                    }
                }catch(Exception e){

                }
            }
        });
        receive.start();
        send.start();
        send.join();
        receive.join();




    }
}

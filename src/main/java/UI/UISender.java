package UI;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.log4j.BasicConfigurator;

import data.Person;
import helper.XMLConvert;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.Properties;

import javax.swing.JTable;
import java.awt.FlowLayout;

public class UISender extends JFrame implements ActionListener{
  private JPanel pnl1;
private JLabel lblTitle;
private JTextArea text;
private JScrollPane scroll;
private JButton btn1;
private JPanel panel2;
private JPanel panel3;
private JPanel panel4;
private JLabel lblNewLabel;
private JTextArea textCenter;
private JTextField textSend;
private JButton btnNewButton;
public static void main(String[] args) {
	EventQueue.invokeLater(new Runnable() {
		public void run() {
			try {
				UISender frame = new UISender();
				frame.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	});
}

public UISender() {
	// TODO Auto-generated method stub
	setSize(651, 428);
    setLocationRelativeTo(null);
    setResizable(false);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    
    pnl1=new JPanel();
    getContentPane().add(pnl1, BorderLayout.NORTH);
    pnl1.setLayout(new BorderLayout());
    
    panel2 = new JPanel();
    FlowLayout flowLayout = (FlowLayout) panel2.getLayout();
    pnl1.add(panel2, BorderLayout.NORTH);
    
    lblNewLabel = new JLabel("May2");
    lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 25));
    panel2.add(lblNewLabel);
    
    
    panel3 = new JPanel();
    pnl1.add(panel3, BorderLayout.CENTER);
    
    textCenter = new JTextArea(16,70);
    panel3.add(textCenter);
    
    panel4 = new JPanel();
    pnl1.add(panel4, BorderLayout.SOUTH);
    
    textSend = new JTextField(50);
    panel4.add(textSend);
    
    btnNewButton = new JButton("Send");
    panel4.add(btnNewButton);
    
//    panel2.setLayout(null);
    btnNewButton.addActionListener(this);
	textSend.addActionListener(this);
    
}
private void event() throws Exception {
	BasicConfigurator.configure();
	//config environment for JNDI
	Properties settings = new Properties();
	settings.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
	settings.setProperty(Context.PROVIDER_URL, "tcp://localhost:61616");
	//create context
	Context ctx = new InitialContext(settings);
	//lookup JMS connection factory
	ConnectionFactory factory = (ConnectionFactory) ctx.lookup("ConnectionFactory");
	//lookup destination. (If not exist-->ActiveMQ create once)
	Destination destination = (Destination) ctx.lookup("dynamicQueues/thanthidet");
	//get connection using credential
	Connection con = factory.createConnection("admin", "admin");
	//connect to MOM
	con.start();
	//create session
	Session session = con.createSession(/* transaction */false, /* ACK */Session.AUTO_ACKNOWLEDGE);
	//create producer
	MessageProducer producer = session.createProducer(destination);
	//create text message
	Message msg = session.createTextMessage("hello mesage from ActiveMQ");
	producer.send(msg);
	try {
			String name = textSend.getText();
			Person p = new Person(1001,name, new Date());
			String xml = new XMLConvert<Person>(p).object2XML(p);
			msg = session.createTextMessage(xml);
			producer.send(msg);
			textSend.setText("");
			textCenter.setText(textCenter.getText() + "\n" + name);
			System.out.println(name);

	} finally {
		session.close();
		con.close();
		System.out.println("Finished...");
	}
}

public void actionPerformed(ActionEvent e) {
	// TODO Auto-generated method stub
	try {
		event();
	} catch (Exception e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
}
}

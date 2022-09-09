package UI;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.log4j.BasicConfigurator;
import org.w3c.dom.events.MouseEvent;

import data.Person;
import helper.XMLConvert;



public class UIReceiver extends JFrame implements ActionListener{
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
						UIReceiver frame = new UIReceiver();
						frame.setVisible(true);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
public UIReceiver() {

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
    
    lblNewLabel = new JLabel("May1");
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
    
    btnNewButton.addActionListener(this);
    textSend.addActionListener(this);
}
private void event() throws Exception {
	BasicConfigurator.configure();
	// thiết lập môi trường cho JJNDI
	Properties settings = new Properties();
	settings.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
	settings.setProperty(Context.PROVIDER_URL, "tcp://localhost:61616");
	// tạo context
	Context ctx = new InitialContext(settings);
	// lookup JMS connection factory
	Object obj = ctx.lookup("ConnectionFactory");
	ConnectionFactory factory = (ConnectionFactory) obj;
	// lookup destination
	Destination destination = (Destination) ctx.lookup("dynamicQueues/thanthidet");
	// tạo connection
	Connection con = factory.createConnection("admin", "admin");
	// nối đến MOM
	con.start();
	// tạo session
	Session session = con.createSession(/* transaction */false, /* ACK */Session.CLIENT_ACKNOWLEDGE);
	// tạo consumer
	MessageConsumer receiver = session.createConsumer(destination);
	// blocked-method for receiving message - sync
	// receiver.receive();
	// Cho receiver lắng nghe trên queue, chừng có message thì notify - async
	System.out.println("Tý was listened on queue...");
	receiver.setMessageListener(new MessageListener() {

		// có message đến queue, phương thức này được thực thi
		public void onMessage(Message msg) {// msg là message nhận được
			try {
				if (msg instanceof TextMessage) {
					TextMessage tm = (TextMessage) msg;
					String txt = tm.getText();
					System.out.println("Nhận được " + txt);

					int indexStart = txt.indexOf("<hoten>");
					int indexEnd = txt.indexOf("</hoten>");
					int indexMSStart = txt.indexOf("<mssv>");
					int indexMSEnd = txt.indexOf("</mssv>");
					System.out.println("index " + indexStart);
						System.out.println(txt);
						String textMS = txt.substring(indexMSStart, indexMSEnd);
						String text = txt.substring(indexStart + 7, indexEnd);
						text.replaceAll("<hoten>", "");
						textMS.replaceAll("<mssv>", "");
						if(textCenter.getText().indexOf(textMS) == -1) {
							textCenter.append("\nMSSV: " + textMS);
						}
						textCenter.append("\nContent: " + text);
					
					msg.acknowledge();// gửi tín hiệu ack
				} else if (msg instanceof ObjectMessage) {
					ObjectMessage om = (ObjectMessage) msg;
					System.out.println(om);
				}
//others message type....
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	});
}
public void actionPerformed(ActionEvent e) {
	try {
		event();
	} catch (Exception e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	
}

}

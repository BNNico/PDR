import java.awt.*;
import java.awt.event.*;

public class Test_2 extends Frame {
    public TextArea		text;
	public TextField	data;
	Sentence_itf		sentence;
	static String		myName;
    
    public static void main(String argv[]) {
        if (argv.length != 1) {
            System.out.println("java Test <name>");
            return;
        }
        myName = argv[0];

        // initialize the system
        Client.init();
        
        // look up the IRC object in the name server
		// if not found, create it, and register it in the name server
		Sentence_itf s = (Sentence_itf)Client.lookup("IRC");
		if (s == null) {
			s = (Sentence_itf)Client.create(new Sentence());
			Client.register("IRC", s);
		}
        // create the graphical part
        new Test_2(s);
    }

    public Test_2(Sentence_itf s) {

        setLayout(new FlowLayout());

        text=new TextArea(10,60);
        text.setEditable(false);
        text.setForeground(Color.red);
        add(text);

        data=new TextField(60);
        add(data);

        Button write_button = new Button("WL");
        write_button.addActionListener(new writeListener(this));
        add(write_button);
        Button read_button = new Button("RL");
        read_button.addActionListener(new readListener(this));
        add(read_button);
        Button read_unlock = new Button("read unlock");
        read_unlock.addActionListener(new read_unlock(this));
        add(read_unlock);
        Button write_unlock = new Button("write unlock");
        write_unlock.addActionListener(new write_unlock(this));
        add(write_unlock);
        
        setSize(470,300);
        text.setBackground(Color.black); 
        show();
        
        sentence = s;
    }
    



    class readListener implements ActionListener {
        Test_2 Test;
        public readListener (Test_2 i) {
            Test = i;
        }
        public void actionPerformed (ActionEvent e) {
            
            // lock the object in read mode
            Test.sentence.lock_read();
            System.out.println("lock read finished");
        }
    }
    
    

    class writeListener implements ActionListener {
        Test_2 Test;
        public writeListener (Test_2 i) {
                Test = i;
        }
        public void actionPerformed (ActionEvent e) {
            
                
            // lock the object in write mode
            Test.sentence.lock_write();
            
            
            System.out.println("lock write finished");
        }
    }
    class write_unlock implements ActionListener {
        Test_2 Test;
        public write_unlock (Test_2 i) {
                Test = i;
        }
        public void actionPerformed (ActionEvent e) {
            System.out.println("write unlock start");
            // get the value to be written from the buffer
            String s = Test.data.getText();
            // invoke the method
            Test.sentence.write(Irc.myName+" wrote "+s);
            Test.data.setText("");

            // unlock the object
            Test.sentence.unlock();
            System.out.println("write unlocked");
    }
}
    class read_unlock implements ActionListener {
        Test_2 Test;
        public read_unlock (Test_2 i) {
                Test = i;
        }
        public void actionPerformed (ActionEvent e) {
            // invoke the method
            String s = Test.sentence.read();

            // unlock the object
            Test.sentence.unlock();
            
            // display the read value
            Test.text.append(s+"\n");
        }
    }
}

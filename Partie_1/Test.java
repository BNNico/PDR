import java.awt.*;
import java.awt.event.*;

public class Test extends Frame {
    public TextArea		text;
	public TextField	data;
	SharedObject		sentence;
	static String		myName;
    
    public static void main(String argv[]) {
        if (argv.length != 1) {
            System.out.println("java Test <name>");
            return;
        }
        myName = argv[0];

        // initialize the system
        Client.init();
        
        // look up the Test object in the name server
        // if not found, create it, and register it in the name server
        SharedObject s = Client.lookup("Test");
        if (s == null) {
            s = Client.create(new Sentence());
            Client.register("Test", s);
        }
        // create the graphical part
        new Test(s);
    }

    public Test(SharedObject s) {

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
        Test Test;
        public readListener (Test i) {
            Test = i;
        }
        public void actionPerformed (ActionEvent e) {
            
            // lock the object in read mode
            Test.sentence.lock_read();
            System.out.println("lock read finished");
        }
    }
    
    

    class writeListener implements ActionListener {
        Test Test;
        public writeListener (Test i) {
                Test = i;
        }
        public void actionPerformed (ActionEvent e) {
            
                
            // lock the object in write mode
            Test.sentence.lock_write();
            
            
            System.out.println("lock write finished");
        }
    }
    class write_unlock implements ActionListener {
        Test Test;
        public write_unlock (Test i) {
                Test = i;
        }
        public void actionPerformed (ActionEvent e) {
            System.out.println("write unlock start");
            // get the value to be written from the buffer
            String s = Test.data.getText();
            // invoke the method
            ((Sentence)(Test.sentence.obj)).write(Test.myName+" wrote "+s);
            Test.data.setText("");
            int n = (Test.sentence.getId());
            System.out.println(Test.myName+" wrote "+s);
            System.out.println("id = " + n);
            // unlock the object
            Test.sentence.unlock();
            System.out.println("write unlocked");
    }
}
    class read_unlock implements ActionListener {
        Test Test;
        public read_unlock (Test i) {
                Test = i;
        }
        public void actionPerformed (ActionEvent e) {
            // invoke the method
            String s = ((Sentence)(Test.sentence.obj)).read();
            int n = (Test.sentence.getId());
            System.out.println("s = " + s);
            System.out.println("id = " + n);

            // unlock the object
            Test.sentence.unlock();
            
            // display the read value
            Test.text.append(s+"\n");
        }
    }
}

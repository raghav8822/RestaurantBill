import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import  java.sql.*;
import java.util.Objects;

public class Bill {
    public static void main(String[] args) {
        MyFrame frame = new MyFrame();
    }
}

class MyFrame extends JFrame implements ActionListener {
    JLabel label1, label2;
    JTextField t1, t2; 
    JTextPane screen1, screen2;
    JButton add,generate;
    Font font = new Font(Font.SANS_SERIF, Font.ITALIC, 16);
    Font font2 = new Font(Font.SANS_SERIF, Font.BOLD, 25);

    MyFrame() 
    {
        setTitle("ORDER HERE");
        setSize(700, 500); 
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        Container c = getContentPane(); 
        c.setLayout(null);

        // Dish label
        label1 = new JLabel("Dish"); 
        label1.setBounds(62, 30, 100, 25);
        c.add(label1);

        // Dish input box
        t1 = new JTextField(); 
        t1.setBounds(120, 30, 150, 25); 
        c.add(t1);

        // Quantity label
        label2 = new JLabel("Quantity"); 
        label2.setBounds(40, 80, 100, 25);
        c.add(label2);

        // Quantity inputbox
        t2 = new JTextField();
        t2.setBounds(120, 80, 150, 25); 
        c.add(t2);

        // add button
        add = new JButton("Add to order"); 
        add.setBounds(40, 300, 170, 25);
        add.setForeground(Color.BLUE);
        c.add(add);
        add.addActionListener(this);
        

        // Generate button
        generate = new JButton("Generate Bill"); 
        generate.setBounds(40, 350, 170, 25);
        c.add(generate);
        generate.addActionListener(this);

        // display of form details
        screen1 = new JTextPane();
        screen1.setBounds(40, 130, 270, 75); 
        screen1.setBorder(new EmptyBorder(5, 10, 5, 10));
        screen1.setFont(font);
        screen1.setText("Enter your order above...");
        screen1.setEditable(false);
        c.add(screen1);

        // display of menu
        screen2 = new JTextPane();
        screen2.setBounds(350, 30, 300, 375); 
        screen2.setBorder(new EmptyBorder(70, 30, 10, 10));
        screen2.setBackground(Color.orange);
        screen2.setForeground(Color.DARK_GRAY);
        screen2.setFont(font2);
        screen2.setEditable(false);
        c.add(screen2);
        screen2.setText("""
                ******* MENU *******
                Pizza - $7.0
                Burger - $6.0
                Fries - $4.0
                Sandwich - $5.0
                Coffee - $3.2
                Tea - $3.0
                Soft Drink - $3.5
                ********************""");

        c.setBackground(Color.orange);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e)
    {
        String url = "jdbc:mysql://localhost:3306/cafe";
        String username = "root";
        String password = "aryahi2007"; //user dependant

        try {
            Connection connection = DriverManager.getConnection(url, username, password);

            if (e.getSource() == add)
            {
                String order = t1.getText();
                String quant = t2.getText();

                String sqlchk = "select dish from menu";
                Statement check = connection.createStatement();
                ResultSet result = check.executeQuery(sqlchk);

                int checker = 0;
                while(result.next())
                {
                    if(Objects.equals(order, result.getString("dish"))) {
                        checker = 1;
                    }
                }

                if(checker == 1)
                {
                    screen1.setText("Added to order:\n" + order + " " + quant );
                    int quantity = Integer.parseInt(quant);

                    String sql = "Insert into ordered (Dish, quantity) values (?,?)";
                    PreparedStatement statement = connection.prepareStatement(sql);
                    statement.setString(1, order);
                    statement.setInt(2, quantity);

                    statement.executeUpdate();
                    statement.close();
                }
                else
                {
                    screen1.setText("Please enter from the menu...");
                }
            }

            else if (e.getSource() == generate)
            {
                String sql2 = "select ordered.dish, ordered.quantity, ordered.quantity*menu.Cost as cost\n"
                        + "from ordered,menu\n"
                        + "where ordered.dish=menu.dish";
                Statement statement2 = connection.createStatement();
                ResultSet result1 = statement2.executeQuery(sql2);

                int count = 0;
                float totalPrice = 0;
                StringBuilder billstr = new StringBuilder();
                while(result1.next())
                {
                    String meal2 = result1.getString("dish");
                    int quantity2 = result1.getInt("quantity");
                    float cost = result1.getFloat("cost");
                    totalPrice += result1.getFloat("cost");
                    count++;
                    billstr.append(count).append(". ").append(meal2).append(" ").append(quantity2).append(" - $").append(cost).append("\n");
                }

                setVisible(false);
                MyFrame2 frame2 = new MyFrame2();
                frame2.screen3.setText("\n" + billstr + "\nTotal Amount: $" + totalPrice);

                String sql4 = "Truncate table ordered";
                Statement statement4 = connection.createStatement();
                statement4.execute(sql4);
            }

            connection.close();

        } catch (SQLException e2) {
            System.out.println("Error!");
            e2.printStackTrace();
        }
    }
}

class MyFrame2 extends JFrame {
    JTextPane screen3;
    JLabel label2;
    Font font = new Font(Font.SERIF, Font.BOLD, 75);
    Font font2 = new Font(Font.MONOSPACED, Font.PLAIN, 22);

    MyFrame2()
    {
        setTitle("Bill To Pay");
        setSize(500, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        Container c2 = getContentPane();
        c2.setLayout(null);

        // header
        label2 = new JLabel("BILL", JLabel.CENTER);
        label2.setBounds(50,50,400,75);
        label2.setFont(font);
        label2.setForeground(Color.white);
        c2.add(label2);

        // display of bill
        screen3 = new JTextPane();
        screen3.setBounds(45, 120, 400, 525);
        screen3.setBorder(new EmptyBorder(10, 30, 10, 30));
        screen3.setBackground(Color.pink);
        screen3.setForeground(Color.DARK_GRAY);
        screen3.setFont(font2);
        screen3.setEditable(false);
        c2.add(screen3);

        c2.setBackground(Color.pink);
        setVisible(true);
    }
}


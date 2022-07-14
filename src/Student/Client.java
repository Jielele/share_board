package Student;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

import static java.lang.Integer.parseInt;

public class Client {
    static int i = 1;
    private BufferedReader br = null;
    private PrintStream ps = null;
    private String nickName = null;
    SChatField scf = new SChatField();
    SWhiteBoard sWhiteBoard = new SWhiteBoard();

    public Client() throws Exception {
        nickName = javax.swing.JOptionPane.showInputDialog("请输入用户名");
        scf.setTitle(nickName + "聊天窗口");
        Socket s = new Socket("192.168.79.1", 9999);
        br = new BufferedReader(new InputStreamReader(s.getInputStream()));
        ps = new PrintStream(s.getOutputStream());
        scf.setVisible(false);
        sWhiteBoard.setVisible(true);

        ps.println("LOGIN#" + nickName);
        scf.run();
    }

    class SWhiteBoard extends JFrame implements ActionListener, MouseListener, MouseMotionListener {
        private Graphics g;
        private int[] shapePoint = new int[4];
        private JPanel jpl = new JPanel();
        private String nowButton = "铅笔", nowColor = "black";//表示目前所选的绘制功能按钮（默认为绘制曲线）
        private JButton pencil = new JButton("铅笔"),
                round = new JButton("圆形"), rectangle = new JButton("矩形"),
                beeline = new JButton("直线"), save = new JButton("保存"),
                information = new JButton("信息"),
                red = new JButton("红"), yellow = new JButton("黄"),
                black = new JButton("黑"), blue = new JButton("蓝");
        boolean b = false;

        public SWhiteBoard() {
            this.setTitle("Student端");
            this.setDefaultCloseOperation(EXIT_ON_CLOSE);
            this.setSize(800, 600);
            this.setLocation(300, 225);
            this.setVisible(true);
            this.setLayout(null);
            this.add(jpl);
            jpl.setBackground(Color.white);
            jpl.setSize(800, 550);
            jpl.setLocation(0, 50);

            //设置各类字体
            Font font1 = new Font("楷体", Font.BOLD, 10);
            Font font2 = new Font("楷体", Font.BOLD, 20);
            Font font3 = new Font("宋体", Font.BOLD, 20);


            this.add(pencil);
            pencil.setSize(80, 50);
            pencil.setLocation(0, 0);
            pencil.setFont(font2);
            this.add(round);
            round.setSize(80, 50);
            round.setLocation(80, 0);
            round.setFont(font2);
            this.add(rectangle);
            rectangle.setSize(80, 50);
            rectangle.setLocation(160, 0);
            rectangle.setFont(font2);
            this.add(beeline);
            beeline.setSize(80, 50);
            beeline.setLocation(240, 0);
            beeline.setFont(font2);
            this.add(save);
            save.setSize(80, 50);
            save.setLocation(320, 0);
            save.setFont(font2);
            this.add(information);
            information.setSize(80, 50);
            information.setLocation(400, 0);
            information.setFont(font2);

/*            this.add(black);
            black.setSize(70, 25);
            black.setLocation(480, 0);
            black.setFont(font1);
            black.setBackground(Color.black);
            this.add(red);
            red.setSize(70, 25);
            red.setLocation(480, 25);
            red.setFont(font1);
            red.setBackground(Color.red);
            this.add(yellow);
            yellow.setSize(70, 25);
            yellow.setLocation(560, 0);
            yellow.setFont(font1);
            yellow.setBackground(Color.yellow);
            this.add(blue);
            blue.setSize(70, 25);
            blue.setLocation(560, 25);
            blue.setFont(font1);
            blue.setBackground(Color.blue);

            black.addActionListener(this);
            red.addActionListener(this);
            blue.addActionListener(this);
            yellow.addActionListener(this);*/

            pencil.addActionListener(this);
            round.addActionListener(this);
            rectangle.addActionListener(this);
            beeline.addActionListener(this);
            save.addActionListener(this);
            information.addActionListener(this);

            jpl.addMouseListener(this);
            jpl.addMouseMotionListener(this);

            //获取画笔
            g = jpl.getGraphics();
            g.setColor(Color.BLACK);
            //将Graphics转为Graphics2D
            Graphics2D g2d = (Graphics2D) g;
            g2d.setStroke(new BasicStroke(3.0f));

        }


        @Override
        public void actionPerformed(ActionEvent e) {
            String s = e.getActionCommand();
            if (s.equals("铅笔") || s.equals("三角形") || s.equals("圆形") || s.equals("矩形") || s.equals("直线")) {
                nowButton = s;
            } else if (s.equals("保存")) {
                new test01();
            } else if (s.equals("信息")) {
                if (b == false) {
                    scf.setVisible(true);
                    b = true;
                }
            }
        }

        class test01 {
            public test01() {
                //创建一个robot对象
                Robot robut = null;
                try {
                    robut = new Robot();
                    //获取屏幕分辨率
                    Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
                    //打印屏幕分辨率
                    System.out.println(d);
                    //创建该分辨率的矩形对象
                    Rectangle screenRect = new Rectangle(d);
                    //根据这个矩形截图
                    BufferedImage bufferedImage = robut.createScreenCapture(screenRect);
                    //保存截图
                    File file = new File(i + "截图" + i + ".png");
                    try {
                        ImageIO.write(bufferedImage, "png", file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (AWTException e) {
                    e.printStackTrace();
                }
                i++;
            }
        }

        @Override
        public void mouseClicked(MouseEvent e) {

        }

        @Override
        //MouseListener中的方法
        public void mousePressed(MouseEvent e) {
            //记录鼠标按下的坐标
            shapePoint[0] = e.getX();
            shapePoint[1] = e.getY();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
//记录鼠标松开的坐标
            shapePoint[2] = e.getX();
            shapePoint[3] = e.getY();

            //判断最后按下的是哪个图形按钮
            switch (nowButton) {
                case "直线":
                    System.out.println("直线" + shapePoint[0] + " " + shapePoint[1] + " " + shapePoint[2] + " " + shapePoint[3]);
                    //绘制直线
                    g.drawLine(shapePoint[0], shapePoint[1], shapePoint[2], shapePoint[3]);
                    //调用发送图形方法
                    break;
                case "圆形":
                    System.out.println("圆形" + shapePoint[0] + " " + shapePoint[1] + " " + shapePoint[2] + " " + shapePoint[3]);
                    //记录圆形左上角坐标点，并计算其宽高
                    int x1 = Math.min(shapePoint[0], shapePoint[2]);
                    int y1 = Math.min(shapePoint[1], shapePoint[3]);
                    int width = Math.abs(shapePoint[0] - shapePoint[2]);
                    int height = Math.abs(shapePoint[1] - shapePoint[3]);
                    shapePoint[0] = x1;
                    shapePoint[1] = y1;
                    shapePoint[2] = width;
                    shapePoint[3] = height;
                    //绘制椭圆
                    g.fillOval(shapePoint[0], shapePoint[1], shapePoint[2], shapePoint[3]);
                    //调用发送图形方法
                    break;
                case "矩形":
                    //实现方法与画圆类似
                    System.out.println("矩形" + shapePoint[0] + " " + shapePoint[1] + " " + shapePoint[2] + " " + shapePoint[3]);
                    x1 = Math.min(shapePoint[0], shapePoint[2]);
                    y1 = Math.min(shapePoint[1], shapePoint[3]);
                    width = Math.abs(shapePoint[0] - shapePoint[2]);
                    height = Math.abs(shapePoint[1] - shapePoint[3]);
                    shapePoint[0] = x1;
                    shapePoint[1] = y1;
                    shapePoint[2] = width;
                    shapePoint[3] = height;
                    g.fillRect(shapePoint[0], shapePoint[1], shapePoint[2], shapePoint[3]);
                    break;
            }
        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (nowButton.equals("铅笔")) {
                shapePoint[2] = shapePoint[0];
                shapePoint[3] = shapePoint[1];

                shapePoint[0] = e.getX();
                shapePoint[1] = e.getY();

                g.drawLine(shapePoint[0], shapePoint[1], shapePoint[2], shapePoint[3]);
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {

        }


    }

    class SChatField extends JFrame implements ActionListener {
        private JPanel jpl = new JPanel();
        private JButton send = new JButton("发送");
        private JTextArea jta = new JTextArea();
        private JTextField jtf = new JTextField();

        public SChatField() {
            this.setTitle(nickName + "聊天窗口");
            this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
            this.setSize(300, 600);
            this.setLocation(1090, 225);
            this.setVisible(true);
            this.add(jpl);
            jpl.setLayout(null);

            //设置各类字体
            Font font1 = new Font("楷体", Font.BOLD, 30);
            Font font2 = new Font("楷体", Font.BOLD, 20);
            Font font3 = new Font("宋体", Font.BOLD, 20);

            jpl.add(send);
            send.setSize(80, 40);
            send.setLocation(205, 480);
            send.setFont(font2);
            jpl.add(jta);
            jta.setSize(285, 480);
            jta.setLocation(0, 0);
            jta.setFont(font2);
            jta.setEditable(false);
            jta.setBorder(BorderFactory.createLineBorder(Color.gray, 1));
            jta.setLineWrap(true);
            jpl.add(jtf);
            jtf.setSize(205, 40);
            jtf.setLocation(0, 482);
            jtf.setFont(font2);

            send.addActionListener(this);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String s = e.getActionCommand();
            if (s.equals("发送")) {
                ps.println("MSG#" + nickName + "#" + jtf.getText());
                Date date = new Date();
                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                jta.append(formatter.format(date) + "\n" + "我说:" + jtf.getText() + "\n");
                jtf.setText("");
            }
        }

        public void run() {//客户端与服务器端发消息的线程
            while (true) {
                try {
                    String msg = br.readLine();//读取服务器是否发送了消息给该客户端
                    System.out.println(msg);
                    String[] strs = msg.split("#");
                    //判断是否为服务器发来的登陆信息
                    if (strs[0].equals("GPH")) {
                        if (strs[1].equals("直线")) {
                            sWhiteBoard.g.drawLine(parseInt(strs[2]), parseInt(strs[3]),parseInt(strs[4]) , parseInt(strs[5]) );
                        } else if (strs[1].equals("圆形")) {
                            sWhiteBoard.g.fillOval(parseInt(strs[2]), parseInt(strs[3]),parseInt(strs[4]) , parseInt(strs[5]) );
                        } else if (strs[1].equals("铅笔")) {
                            sWhiteBoard.g.drawLine(parseInt(strs[2]), parseInt(strs[3]),parseInt(strs[4]) , parseInt(strs[5]) );
                        } else if (strs[1].equals("矩形")) {
                            sWhiteBoard.g.fillRect(parseInt(strs[2]), parseInt(strs[3]),parseInt(strs[4]) , parseInt(strs[5]) );
                        }
                    } else if (strs[0].equals("MSG")) {//接到服务器发送消息的信息
                        if (!strs[1].equals(nickName)) {
                            Date date = new Date();
                            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                            scf.jta.append(formatter.format(date) + "\n" + strs[1] + "说：" + strs[2] + "\n");
                        }
                    } else if (strs[0].equals("ENPTY")) {
                        sWhiteBoard.jpl.paint(sWhiteBoard.g);
                    } else if (strs[0].equals("COLOR")) {
                        switch (strs[1]) {
                            case "red":
                                sWhiteBoard.g.setColor(Color.red);
                                break;
                            case "yellow":
                                sWhiteBoard.g.setColor(Color.yellow);
                                break;
                            case "black":
                                sWhiteBoard.g.setColor(Color.black);
                                break;
                            case "blue":
                                sWhiteBoard.g.setColor(Color.blue);
                                break;
                        }
                    }else if(strs[0].equals("FILE"))
                    {
                        Date date = new Date();
                        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                        scf.jta.append(formatter.format(date) + "\n" + "我收到了一个文件\n");
                        String choice = javax.swing.JOptionPane.showInputDialog("你收到了一个文件，是否查看？（Y或者N）");
                        if(choice.equals("Y"))
                        {
                            readFile(strs[1]);
                        }
                    }
                } catch (Exception ex) {//如果服务器端出现问题，则客户端强制下线
                    javax.swing.JOptionPane.showMessageDialog(this, "您已被系统请出聊天室！");
                    System.exit(0);
                }
            }
        }

        public void readFile(String m)throws Exception{
            File f = new File(m);
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);
            while(true){
                String s = br.readLine();
                if(s==null) break;
                scf.jta.append(s);
                System.out.println(s);
            }
            fr.close();
        }


    }
}

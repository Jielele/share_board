package Teacher;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class server extends JFrame implements Runnable {
    static int i=0;
    private ServerSocket ss;
    ArrayList<ChatThread> users = new ArrayList<>();//容量能够动态增长的数组
    DefaultListModel<String> dl = new DefaultListModel<String>();
    JList<String> userList = new JList<String>(dl);//显示对象列表并且允许用户选择一个或多个项的组件。单独的模型 ListModel 维护列表的内容。
    TChatField tChatField=new TChatField();

    public server() throws Exception {
        ss = new ServerSocket(9999);
        new Thread(this).start();//监听用户端的加入
        tChatField.setVisible(false);
        new TWhiteBoard();
    }


    public void run() {
        while (true) {
            try {
                Socket s = ss.accept();
                ChatThread ct = new ChatThread(s); //为该客户开一个线程
                users.add(ct); //将每个线程加入到users
                //发送Jlist里的用户登陆信息，为了防止后面登陆的用户无法更新有前面用户的好友列表
                ct.start();
            } catch (Exception ex) {
                ex.printStackTrace();
                System.exit(0);
            }
        }
    }

    public void sendMessage(String msg) {  //服务器端发送给所有用户
        for (ChatThread ct : users) {
            ct.ps.println(msg);
        }
    }

    class ChatThread extends Thread {
        Socket s;
        private BufferedReader br = null;
        PrintStream ps = null;
        public boolean canRun = true;
        String nickName = null;

        public ChatThread(Socket s) throws Exception {
            this.s = s;
            br = new BufferedReader(new InputStreamReader(s.getInputStream()));
            ps = new PrintStream(s.getOutputStream());
        }

        public void run() {
            while (canRun) {
                try {
                    String msg = br.readLine();//接收客户端发来的消息
                    System.out.println(msg);
                    String[] strs = msg.split("#");
                    if (strs[0].equals("LOGIN")) {//收到来自客户端的上线消息
                        nickName = strs[1];
                        dl.addElement(nickName);
                        userList.repaint();
                        System.out.println("client " + (dl.size() - 1) + " is connected!");
                    } else if (strs[0].equals("MSG")) {
                        sendMessage(msg);
                        Date date = new Date();
                        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                        tChatField.jta.append(formatter.format(date) + "\n" + strs[1]+"说："+strs[2]+ "\n");
                        System.out.println("msg");
                    } else if (strs[0].equals("LOGOUT")) {//收到来自客户端的注销消息
                        dl.removeElement(strs[1]);
                        // 更新List列表
                        userList.repaint();
                    }
                } catch (Exception ex) {
                }
            }
        }
    }


    /**
     * Description:聊天窗口
     *
     * @author: wjl
     * @date: 2022/6/20 18：45
     * @Return:
     */
    class TChatField extends JFrame implements ActionListener {
        JFileChooser fileDlg = new JFileChooser();
        private JPanel jpl = new JPanel();
        private JButton send = new JButton("发送"), file = new JButton("文件"),
                submit = new JButton("交付文件");
        private JTextArea jta = new JTextArea();
        private JTextField jtf = new JTextField();

        public TChatField() {
            this.setTitle("聊天窗口");
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
            jpl.add(file);
            file.setSize(100, 40);
            file.setLocation(0, 520);
            file.setFont(font2);
            jpl.add(submit);
            submit.setSize(185, 40);
            submit.setLocation(100, 520);
            submit.setFont(font2);
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
            file.addActionListener(this);
            submit.addActionListener(this);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String s = e.getActionCommand();
            switch (s) {
                case "发送": {
                    for (ChatThread ct : users) {
                        ct.ps.println("MSG#老师# "+ jtf.getText());
                    }
                    Date date = new Date();
                    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                    jta.append(formatter.format(date) + "\n" + "我说:" + jtf.getText() + "\n");
                    jtf.setText("");
                    break;
                }
                case "文件":
                    selectFile();
                    submit.setText(" 交付文件 ");
                    send.setText("发送 ");
                    break;
                case " 交付文件 ": {
                    sendFile();
                    Date date = new Date();
                    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                    jta.append(formatter.format(date) + "\n" + "我发送了一个文件\n");
                    submit.setText("交付文件");
                    send.setText("发送");
                    break;
                }
            }
        }

        public void selectFile() {
            fileDlg.showOpenDialog(fileDlg);
            String filename = fileDlg.getSelectedFile().getAbsolutePath();
            jtf.setText(filename);
        }

        public void sendFile() {
            try {
                for (ChatThread ct : users) {
                    File file = fileDlg.getSelectedFile();
                    String name = file.getName();//获取文件完整名称
                    String[] fileName = name.split("\\.");//将文件名按照.来分割，因为.是正则表达式中的特殊字符，因此需要转义
                    String fileLast = fileName[fileName.length - 1];//后缀名
                    //写入信息到输出流
                    ct.ps.println("FILE#" + file);
                }
                JOptionPane.showMessageDialog(new JDialog(), "传输完成");
                jtf.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(new JDialog(), ex.getMessage());
            }
        }
    }

    class test01{
        public test01() {
            //创建一个robot对象
            Robot robut= null;
            try {
                robut = new Robot();
                //获取屏幕分辨率
                Dimension d=  Toolkit.getDefaultToolkit().getScreenSize();
                //打印屏幕分辨率
                System.out.println(d);
                //创建该分辨率的矩形对象
                Rectangle screenRect=new  Rectangle(d);
                //根据这个矩形截图
                BufferedImage bufferedImage=robut.createScreenCapture(screenRect);
                //保存截图
                File file=new File("截图"+i+".png");
                try {
                    ImageIO.write(bufferedImage,"png",file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (AWTException e) {
                e.printStackTrace();
            }
            i++;
        }
    }


    /**
     * Description:共享白板
     *
     * @author: wjl
     * @date: 2022/6/20 17:57
     * @Return:
     */
    class TWhiteBoard extends JFrame implements ActionListener, MouseListener, MouseMotionListener {
        private Graphics g;
        private int[] shapePoint = new int[4];
        private JPanel jpl = new JPanel();
        private String nowButton = "铅笔";//表示目前所选的绘制功能按钮（默认为绘制曲线）
        private JButton pencil = new JButton("铅笔"),
                round = new JButton("圆形"), rectangle = new JButton("矩形"),
                beeline = new JButton("直线"), save = new JButton("保存"),
                empty = new JButton("清空"), information = new JButton("信息"),
                red = new JButton("红"), yellow = new JButton("黄"),
                black = new JButton("黑"), blue = new JButton("蓝");
        boolean b = false;

        public TWhiteBoard() {
            this.setTitle("Teacher端");
            this.setDefaultCloseOperation(EXIT_ON_CLOSE);
            this.setSize(800, 600);
            this.setLocation(300, 225);
            this.setVisible(true);
            this.setLayout(null);
            this.add(jpl);
            jpl.setBackground(Color.white);
            jpl.setSize(800, 550);
            jpl.setLocation(0, 50);

            /*JPanel buttonBoard=new JPanel();
            buttonBoard.setPreferredSize(new Dimension(80,50));*/

/*//添加颜色按钮
            Color[]colors={Color.red,Color.yellow,Color.blue,Color.green,Color.black,Color.white};
            String[]colorButtonNames={"红","黄","蓝","绿","黑","白"};
            JButton[]CjbtList=new JButton[colorButtonNames.length];
            for(int i=0;i<colorButtonNames.length;i++){
                CjbtList[i]=new JButton();
                CjbtList[i].setActionCommand(colorButtonNames[i]);
                CjbtList[i].setBackground(colors[i]);
                buttonBoard.add(CjbtList[i]);
            }

            //设置窗体界面布局为网格布局，设置布局为一行两列
            GridLayout grid=new GridLayout(1,2);
            buttonBoard.setBackground(Color.blue);
            buttonBoard.setLayout(grid);
            buttonBoard.setLocation(600,0);
            this.add(buttonBoard);*/


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
            this.add(empty);
            empty.setSize(80, 50);
            empty.setLocation(320, 0);
            empty.setFont(font2);
            this.add(save);
            save.setSize(80, 50);
            save.setLocation(400, 0);
            save.setFont(font2);
            this.add(information);
            information.setSize(80, 50);
            information.setLocation(480, 0);
            information.setFont(font2);
            this.add(black);
            black.setSize(70, 25);
            black.setLocation(560, 0);
            black.setFont(font1);
            black.setBackground(Color.black);
            this.add(red);
            red.setSize(70, 25);
            red.setLocation(560, 25);
            red.setFont(font1);
            red.setBackground(Color.red);
            this.add(yellow);
            yellow.setSize(70, 25);
            yellow.setLocation(640, 0);
            yellow.setFont(font1);
            yellow.setBackground(Color.yellow);
            this.add(blue);
            blue.setSize(70, 25);
            blue.setLocation(640, 25);
            blue.setFont(font1);
            blue.setBackground(Color.blue);

            black.addActionListener(this);
            red.addActionListener(this);
            blue.addActionListener(this);
            yellow.addActionListener(this);

            pencil.addActionListener(this);
            round.addActionListener(this);
            rectangle.addActionListener(this);
            beeline.addActionListener(this);
            empty.addActionListener(this);
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
        public void mouseClicked(MouseEvent e) {

        }

        //MouseListener中的方法
        public void mousePressed(MouseEvent e) {
            //记录鼠标按下的坐标
            shapePoint[0] = e.getX();
            shapePoint[1] = e.getY();
        }

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
                    sendShape();
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
                    sendShape();
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
                    sendShape();
                    break;
            }
        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }

        //绘制铅笔的方法
        public void mouseDragged(MouseEvent e) {
            if (nowButton.equals("铅笔")) {
                shapePoint[2] = shapePoint[0];
                shapePoint[3] = shapePoint[1];

                shapePoint[0] = e.getX();
                shapePoint[1] = e.getY();

                g.drawLine(shapePoint[0], shapePoint[1], shapePoint[2], shapePoint[3]);

                sendShape();
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {

        }

        public void sendShape() {
            System.out.println("GPH#" + nowButton + "#" + shapePoint[0] + "#" + shapePoint[1] + "#" + shapePoint[2] + "#" + shapePoint[3]);
            for (ChatThread ct : users) {
                ct.ps.println("GPH#" + nowButton + "#" + shapePoint[0] + "#" + shapePoint[1] + "#" + shapePoint[2] + "#" + shapePoint[3]);
            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String s = e.getActionCommand();
            switch (s) {
                case "铅笔":
                case "圆形":
                case "矩形":
                case "直线":
                    nowButton = s;
                    break;
                case "清空":
                    jpl.paint(g);
                    for (ChatThread ct : users) {
                        ct.ps.println("ENPTY#");
                    }
                    break;
                case "保存":
                    new test01();
                    break;
                case "信息":
                    if (b == false) {
                        tChatField.setVisible(true);
                        b = true;
                    }
                    break;
                case"红":System.out.println("change to red");g.setColor(Color.red);sendColor("red");break;
                case"黄":g.setColor(Color.yellow);sendColor("yellow");break;
                case"蓝":g.setColor(Color.blue);sendColor("blue");break;
                case"黑":g.setColor(Color.black);sendColor("black");break;
            }
        }

        public void sendColor(String s) {
            for (ChatThread ct : users) {
                ct.ps.println("COLOR#"+s);
            }
        }

    }


}

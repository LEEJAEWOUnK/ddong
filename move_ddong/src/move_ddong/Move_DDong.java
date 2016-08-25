package move_ddong;

import java.util.*;
import java.util.Timer;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class Move_DDong {
	public static void main(String[] ar) {
		game_Frame fms = new game_Frame();
		fms.setBackground(Color.white);
	}
}

class game_Frame extends JFrame implements KeyListener, Runnable {

	public void start() {
		addKeyListener(this); // 키보드 이벤트 실행
		th = new Thread(this); // 스레드 생성
		th.start(); // 스레드 실행
	}

	Random rnd = new Random();
	int f_width;
	int f_height;
	int x, y; // 캐릭터의 좌표 변수
	int xw, xh;
	static int xx, yy; // 똥 맞추었을때 점수띄우기위한 변수
	int cc = 0;
	boolean KeyLeft = false;
	boolean KeyRight = false;
	boolean KeySpace = false;
	boolean start = false;
	boolean end = false;
	boolean cr = false;
	ArrayList ddong_arr = new ArrayList();
	ArrayList Missile_arr = new ArrayList();
	ArrayList Item_arr = new ArrayList();
	int score = 0;
	int count = 0;
	int missile_count = 5;
	int speed = 1000;
	int level = 1;
	int difficult = 1500;
	int ddong_count = 0;
	double ddong_speed; // 해
	int random;
	Thread th; // 스레드 생성
	Toolkit tk = Toolkit.getDefaultToolkit();
	Image me_img;
	Image ddong_img;
	Image BackGround_img;
	Image buffImage;
	Image dfloor_img;
	Image Missile_img;
	Image Item_img;
	Graphics buffg;
	Item item;
	DDong dg;
	Missile ms;
	Font a;

	game_Frame() {
		init();
		start();
		setTitle("똥피하기!");
		setSize(f_width, f_height);
		Dimension screen = tk.getScreenSize();
		int f_xpos = (int) (screen.getWidth() / 2 - f_width / 2);
		int f_ypos = (int) (screen.getHeight() / 2 - f_height / 2);
		setLocation(f_xpos, f_ypos);
		setResizable(false);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public void init() {
		x = 170; // 캐릭터의 최초 좌표.
		y = 530;
		xw = 36;
		xh = 64;
		f_width = 400;
		f_height = 600;
		Item_img = new ImageIcon("image/아이템.jpg").getImage();
		Missile_img = new ImageIcon("image/미사일.jpg").getImage();
		me_img = new ImageIcon("image/멈춤1.jpg").getImage();
		ddong_img = new ImageIcon("image/똥1.jpg").getImage();
		BackGround_img = new ImageIcon("image/배경이미지.jpg").getImage();
		dfloor_img = new ImageIcon("image/똥바닥.jpg").getImage();
	}

	public void run() {
		try {
			int D_count = 0;
			while (true) {
				random = rnd.nextInt(10);
				Thread.sleep(20);
				if (start) {
					keyControl();
					DDongProcess();
					ItemProcess();
					if (D_count > speed) {// 수를 줄일수록 내려오는 간격이 짧음 간격조절
						enCreate();
						D_count = 0;
					}
					if (cr && cc + 80 < count) {
						cr = false;
					}
					D_count += 10;
					score = count;
					count++;
					if (count > difficult) {
						D_count += 7; // 늘릴 수록 많이 내려옴
						difficult *= 1.5;
						level++;
						speed -= 70;
					}
				}
				repaint(); // 갱신된 x,y값으로 이미지 새로 그리기
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean Crash(int x1, int y1, int x2, int y2, Image img1, Image img2) {
		boolean check = false;
		if (Math.abs((x1 + img1.getWidth(null) / 2) - (x2 + img2.getWidth(null) / 2)) < (img2.getWidth(null) / 2
				+ img1.getWidth(null) / 2)
				&& Math.abs((y1 + img1.getHeight(null) / 2)
						- (y2 + img2.getHeight(null) / 2)) < (img2.getHeight(null) / 2 + img1.getHeight(null) / 2)) {
			check = true;
		} else
			check = false;
		return check;
	}

	public void enCreate() {
		double rx;
		double ry;
		for (int i = 0; i < level + 2; i++) { // i의 최댓값을 늘리면 더많이내려오
			rx = Math.random() * (f_width - xw); // x 좌표 랜덤
			ry = Math.random() * 150; // 내려올때 불규칙함을 만들어줌'
			DDong en = new DDong((int) rx, (int) ry - 100);
			ddong_arr.add(en);
		}
		int rand = level + 5;
		if (random < rand) {
			rx = Math.random() * (f_width - xw); // x 좌표 랜덤
			ry = Math.random() * 150;
			Item item = new Item((int) rx, (int) ry - 100);
			Item_arr.add(item);
		}
	}

	public void paint(Graphics g) {
		buffImage = createImage(f_width, f_height);
		buffg = buffImage.getGraphics();
		update(g);
	}

	public void update(Graphics g) {
		Draw_Char();
		Draw_Missile();
		if (cr)
			Draw_plus();
		DDongProcess();
		MissileProcess();
		ItemProcess();
		draw();
		g.drawImage(buffImage, 0, 0, this);
	}

	public void Draw_plus() {
		if (end)
			yy = 0; // 야매로 숨김
		buffg.setColor(Color.red);
		buffg.setFont(new Font("Default", Font.BOLD, 20));
		buffg.drawString(" +30", xx, yy);
	}

	public void draw() {
		buffg.setColor(Color.black);
		buffg.setFont(new Font("Default", Font.BOLD, 20));
		buffg.drawString("점수 : " + score, 220, 50);
		buffg.drawString("게임시작 : Enter", 220, 70);
		buffg.drawString("남은 미사일 : " + missile_count, 220, 90);
		if (end)
			end();
	}

	public void end() {
		buffg.setColor(Color.red);
		me_img = new ImageIcon("image/똥맞음.jpg").getImage();
		buffg.drawString("SCORE : " + score, 100, 290);
		buffg.drawString("G A M E   O V E R !!", 100, 250);
		count = 0;
		difficult = 1500;
		level = 1;
		speed = 1000;
		missile_count = 5;
		ddong_arr.clear();
		Item_arr.clear();
		Missile_arr.clear();
	}

	public void Draw_Missile() {
		for (int i = 0; i < Missile_arr.size(); i++) {
			ms = (Missile) (Missile_arr.get(i));
			buffg.drawImage(Missile_img, ms.x, ms.y, this);
		}
	}

	public void MissileProcess() {
		if (KeySpace && missile_count > 0 && missile_count <= 5) {
			ms = new Missile(x, y - 20);
			Missile_arr.add(ms);
			missile_count--;
		}
		for (int i = 0; i < Missile_arr.size(); i++) {
			ms = (Missile) Missile_arr.get(i);
			ms.move();

			if (ms.y < 35)
				Missile_arr.remove(i);

			for (int j = 0; j < ddong_arr.size(); ++j) {
				dg = (DDong) ddong_arr.get(j);
				if (Crash(ms.x, ms.y, dg.x, dg.y, Missile_img, ddong_img)) {
					cr = true;
					xx = dg.x;
					yy = dg.y;
					cc = count;
					Draw_plus();
					count += 30;
					ddong_arr.remove(j);
					Missile_arr.remove(i);
				}
			}
		}
	}

	public void ItemProcess() {
		for (int i = 0; i < Item_arr.size(); i++) {
			item = (Item) (Item_arr.get(i));
			buffg.drawImage(Item_img, item.x, item.y, this);
			item.move();

			if (item.y > 560)
				Item_arr.remove(i);

			if (Crash(x, y, item.x, item.y, me_img, Item_img)) {
				Item_arr.remove(i); // 적을제거합니다
				missile_count++;
				if (missile_count > 5)
					missile_count = 5;
			}
		}
	}

	public void DDongProcess() {
		for (int i = 0; i < ddong_arr.size(); i++) {
			dg = (DDong) (ddong_arr.get(i));
			buffg.drawImage(ddong_img, dg.x, dg.y, this);
			dg.move();

			if (dg.y > 560) {
				ddong_count++;
				buffg.drawImage(dfloor_img, dg.x, 570, this);
				if (ddong_count > 120) { // 120이 넘어가면 똥떨어진 이미지 삭제하는거
					ddong_arr.remove(i);
					ddong_count = 0;
				}
			}

			if (Crash(x, y, dg.x, dg.y, me_img, ddong_img)) {
				if (dg.y > 560)
					continue;
				ddong_arr.remove(i);
				// 적을제거합니다
				end = true;
				start = false;
			}
		}
	}

	public void Draw_Char() {
		buffg.clearRect(0, 0, f_width, f_height);
		buffg.drawImage(me_img, x, y, this);
	}

	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_LEFT:
			KeyLeft = true;
			if (start && end == false)
				me_img = new ImageIcon("image/좌측이동1.gif").getImage();
			break;
		case KeyEvent.VK_RIGHT:
			KeyRight = true;
			if (start && end == false)
				me_img = new ImageIcon("image/우측이동1.gif").getImage();
			break;
		case KeyEvent.VK_SPACE:
			if (start == true) {
				KeySpace = true;
				MissileProcess();
				KeySpace = false;
			}
			break;
		case KeyEvent.VK_ENTER:
			start = true;
			me_img = new ImageIcon("image/멈춤1.jpg").getImage();
			end = false;
			break;
		}
	}

	public void keyReleased(KeyEvent e) {
		// 키보드가 눌러졌다가 때어졌을때 이벤트 처리하는 곳
		switch (e.getKeyCode()) {
		case KeyEvent.VK_LEFT:
			KeyLeft = false;
			me_img = new ImageIcon("image/멈춤1.jpg").getImage();
			break;
		case KeyEvent.VK_RIGHT:
			KeyRight = false;
			me_img = new ImageIcon("image/멈춤1.jpg").getImage();
			break;
		case KeyEvent.VK_SPACE:
			KeySpace = false;
			break;
		}
	}

	public void keyTyped(KeyEvent e) {
	}

	public void keyControl() {
		if (0 < x) {
			if (KeyLeft)
				x -= 5;
		}
		if (f_width > x + xw) {
			if (KeyRight)
				x += 5;
		}
	}

	class Missile {
		int x;
		int y;

		Missile(int x, int y) {
			this.x = x;
			this.y = y;
		}

		public void move() {
			y -= 2;
		}
	}

	class DDong {
		int x;
		int y;

		public DDong(int x, int y) {
			this.x = x;
			this.y = y;
		}

		public void move() {
			y++;
		}
	}

	class Item {
		int x;
		int y;

		public Item(int x, int y) {
			this.x = x;
			this.y = y;
		}

		public void move() {
			y++;
		}
	}
}
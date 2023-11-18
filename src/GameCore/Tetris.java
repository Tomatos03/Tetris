package GameCore;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.Arrays;

public class Tetris extends JPanel {
	private int score = 0;
	private static final int COL = 20;
	private static final int ROW = 15;
	private static final int BLOCK_WIDTH = 10;
	private static final int BLOCK_HEIGHT = 10;
	private boolean[][] nextBlock;
	private static final int TIME_DELAY_MINIMUM = 80;
	private boolean[][] nowBlock;
	private final boolean[][] blockMap = new boolean[COL][ROW]; // 存储已有方格
	private final boolean[][][] Shape = BlockV4.Shape;
	private final Timer timer;
	private  int TIME_DELAY = 1000; // 以毫秒为单位
	private boolean isPause = false;
	private Point nowBlockPos;

	public Tetris() {
		addKeyListener(KeyListener);
		Initial();
		// 启动定时器
		timer = new Timer(TIME_DELAY, ActionListener);
		timer.start();
	}

	public void Initial() {
		for (int i = 0; i < COL; i++) {
			Arrays.fill(blockMap[i], false);
		}
		score = 0;
		TIME_DELAY = 1000;
		setPause(false);
		nowBlock = getBlock();
		nextBlock = getBlock();
		// 设置下落初始位置
		nowBlockPos = setOutsetPos();
		repaint();
	}

	/**
	 * @return Point对象包含当前方块坐标信息
	 * @Description 设置方块下落初始位置
	 */
	private Point setOutsetPos() {
		return new Point((ROW + 1) / 2 , -4);
	}


	private int getRandomBlockType() {
		return (int) (Math.random() * 1000) % 7;
	}


	private boolean[][] getBlock() {
		int random = getRandomBlockType();
		return Shape[random];
	}

	// 旋转方块
	private boolean[][] RotateBlock(boolean[][] shape, int arc) {
		if (arc == 0) {
			return shape;
		}
		int height = shape.length;
		int width = shape[0].length;
		int[] b;
		try {
			b = getRotationTemplate(width);
		} catch (IOException e) {
			return null;
		}
		// 拷贝副本
		boolean[][] resultMap = new boolean[height][width];
		for (int i = 0; i < height; i++) {
			resultMap[i] = Arrays.copyOf(shape[i],shape[i].length);
		}
		while (arc-- > 0) {
			boolean[][] c = new boolean[height][width];
			for (int i = 0, m = 0; i < height; i++) {
				for (int j = 0; j < width; j++) {
					c[i][j] = resultMap[b[m] / width][b[m] % width];
					++m;
				}
			}
			resultMap = c;
		}
		if (nowBlockPos == null || nowBlock == null) {
			return resultMap;
		}
		return isBlockOutOfBounds(nowBlockPos, resultMap) ? shape : resultMap;
	}


	/**
	 *
	 * @param length n * n 方格的长度n
	 * @return 旋转后序列
	 * @throws IOException
	 */
	private int[] getRotationTemplate(int length) throws IOException {
		/*
		*
		* 	1, 2  ->	0, 1
		*	0, 3  	2, 3
		*/
		switch (length) {
			case 2 :
				return new int[]{
						1, 2,
						0, 3};
			case 3 :
				return new int[]{
						2, 5, 8,
						1, 4, 7,
						0, 3, 6};
			case 4 :
				return new int[]{
						3, 7, 11, 15,
						2, 6, 10, 14,
						1, 5, 9, 13,
						0, 4, 8, 12,
				};
		}
		throw new IOException("匹配失败");
	}

	public void setPause(boolean value) {
		if (isPause == value) {
			return;
		}
		isPause = value;
		if (isPause) {
			timer.stop();
		} else {
			timer.restart();
		}
		repaint();
	}

	 // 清除铺满一行的方格
	private void clearLines() {
		for (int i = 0; i < blockMap.length; i++) {
			boolean line = true;
			for (int j = 0; j < blockMap[i].length; j++) {
				if (!blockMap[i][j]) {
					line = false;
					break;
				}
			}
			if (line) {
				for (int k = i; k > 0; k--) {
					blockMap[k] = blockMap[k - 1];
				}
				blockMap[0] = new boolean[ROW];
				score += ROW * 10;
				TIME_DELAY -= TIME_DELAY > TIME_DELAY_MINIMUM ? 10 : 0;
				timer.setDelay(TIME_DELAY);
			}
		}
	}
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		drawWall(g);
		drawBoardSide(g);
		drawPause(g);
		drawFallBlock(g);
		drawExistBlock(g);
	}

	private void drawFallBlock(Graphics g) {
		for (int i = 0; i < nowBlock.length; i++) {
			for (int j = 0; j < nowBlock[i].length; j++) {
				if (this.nowBlock[i][j]) {
					g.fillRect((nowBlockPos.x + j) * BLOCK_WIDTH, (nowBlockPos.y + i) * BLOCK_HEIGHT
							, BLOCK_WIDTH, BLOCK_HEIGHT);
				}
			}
		}
	}

	private void drawExistBlock(Graphics g) {
		for (int i = 0; i < blockMap.length; i++) {
			for (int j = 0; j < blockMap[i].length; j++) {
				if (blockMap[i][j]) {
					g.fillRect((j + 1) * BLOCK_WIDTH, i * BLOCK_HEIGHT, BLOCK_WIDTH, BLOCK_HEIGHT);
				}
			}
		}
	}

	private void drawWall(Graphics g) {
		//  绘制墙的两侧
		for (int i = 0; i <= COL; i++) {
			g.drawRect(0, i * BLOCK_HEIGHT, BLOCK_WIDTH, BLOCK_HEIGHT);
			g.drawRect((1 + ROW) * BLOCK_WIDTH, i * BLOCK_HEIGHT, BLOCK_WIDTH, BLOCK_HEIGHT);
		}
		// 绘制墙的底部
		for (int i = 0; i < ROW; i++) {
			g.drawRect((i + 1) * BLOCK_WIDTH, COL * BLOCK_HEIGHT, BLOCK_WIDTH, BLOCK_HEIGHT);
		}
	}

	// 绘制侧边栏控件，控件包括分数板、下一方块显示板
	private void drawBoardSide(Graphics g) {
		// 绘制分数板
		g.drawString("游戏分数: " + score, BLOCK_WIDTH * (ROW + 2) + 20, (COL - 4) * BLOCK_HEIGHT);
		// 绘制下一方块显示板
		for (int i = 0; i < nextBlock.length; i++) {
			for (int j = 0; j < nextBlock[i].length; j++) {
				if (nextBlock[i][j]) {
					g.fillRect((ROW + 5 + j) * BLOCK_WIDTH, (i + COL / 2) * BLOCK_HEIGHT, BLOCK_WIDTH, BLOCK_HEIGHT);
				}
			}
		}
	}

	private void drawPause(Graphics g) {
		if (isPause) {
			g.setColor(Color.WHITE);
			g.fillRect((ROW + 5)  * BLOCK_WIDTH , 5 * BLOCK_HEIGHT, 50, 20);
			g.setColor(Color.BLACK);
			g.drawRect((ROW + 5 ) * BLOCK_WIDTH,  5 * BLOCK_HEIGHT, 50, 20);
			g.drawString("PAUSE", (ROW + 5)  * BLOCK_WIDTH + 5 , 6 * BLOCK_HEIGHT + 5);
		}
	}

	// 方法用于判断移动后方块是否越出墙体
	private boolean isBlockOutOfBounds(Point curPos, boolean[][] inputBlocks) {

		for (int i = 0; i < inputBlocks.length; i++) {
			for (int j = 0; j < inputBlocks[i].length; j++) {
				if (!inputBlocks[i][j]) continue;

				int x = j + curPos.x;
				int y = i + curPos.y;
				if (x <= 0 || x >= ROW + 1 || y >= COL || ( y > 0 && blockMap[y][x - 1])) {
					return true;
				}
			}
		}
		return false;
	}

	// 判断方块与墙之间、方块与方块是否有接触
	private boolean isTouchBlock(Point curPos) {
		boolean isTouch = false;
		label :
		for (int i = nowBlock.length - 1; i >= 0; --i) {
			for (int j = 0; j < nowBlock[i].length; ++j) {
				if (!nowBlock[i][j]) {
					continue;
				}
				if (curPos.y + 1 + i >= COL || (curPos.y + 1 + i >= 0 && blockMap[curPos.y + 1 + i][curPos.x + j - 1])) {
					isTouch = true;
					break label;
				}
			}
		}

		for (int i = 0; isTouch && i < nowBlock.length; i++) {
			for (int j = 0; j < nowBlock[i].length; j++) {
				if (!nowBlock[i][j]) {
					continue;
				}
				// 判定方块接触后游戏是否结束
				if (curPos.y + i < 1) {
					JOptionPane.showMessageDialog(Tetris.this.getParent(), "GAME OVER");
					Initial();
					return false;
				}
				blockMap[curPos.y + i][curPos.x + j - 1] = nowBlock[i][j];
			}
		}
		return isTouch;
	}

	private void nextRound() {
		nowBlock = nextBlock;
		// 再次生成下一次方块
		nextBlock = getBlock();
		// 重置位置
		nowBlockPos = setOutsetPos();
	}


	ActionListener ActionListener = arg0 -> {
		if (isTouchBlock(nowBlockPos)) {
			clearLines();
			nextRound();
		}
		nowBlockPos.y++;
		repaint();
	};

	KeyListener KeyListener = new KeyListener() {
		@Override
		public void keyPressed(KeyEvent e) {
			// 暂停时不可操作
			if (isPause) {
				return;
			}

			switch (e.getKeyCode()) {
				case KeyEvent.VK_UP:
					nowBlock = RotateBlock(nowBlock, 1);
					repaint();
					break;
				case KeyEvent.VK_DOWN:
					if (isBlockOutOfBounds(new Point(nowBlockPos.x, nowBlockPos.y + 1), nowBlock)) {
						return;
					}
					++nowBlockPos.y;
					break;
				case KeyEvent.VK_LEFT:
					if (isBlockOutOfBounds(new Point(nowBlockPos.x - 1, nowBlockPos.y), nowBlock)) {
						return;
					}
					--nowBlockPos.x;
					break;
				case KeyEvent.VK_RIGHT:
					if (isBlockOutOfBounds(new Point(nowBlockPos.x + 1, nowBlockPos.y), nowBlock)) {
						return;
					}
					++nowBlockPos.x;
					break;
				default:
					setPause(true);
			}
			repaint();
		}

		@Override
		public void keyTyped(KeyEvent e) {
		}

		@Override
		public void keyReleased(KeyEvent e) {
		}
	};
}

package GameUI;

import GameCore.Tetris;

import javax.swing.*;
import java.awt.event.ActionListener;

public class TetrisUI extends JFrame{
	Tetris tries = new Tetris();
	ActionListener NewGameAction = e -> tries.Initial();
	ActionListener PauseAction = e -> {
		tries.setPause(true);
	};
	ActionListener ContinueAction = e -> {
		tries.setPause(false);
	};
	public TetrisUI ( ){
		add(tries);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(300, 350);
		setTitle("Tetris Game");
		setResizable(false);
		// 顶部菜单栏
		// 涉及对象JMenuBar、JMenu、JMenuItem
		// 菜单栏默认添加在窗口面板中的顶部
		JMenuBar menu = new JMenuBar();
		setJMenuBar(menu);
		JMenu gameMenu = new JMenu("菜单");
		JMenuItem newGameItem = gameMenu.add("新游戏");
		newGameItem.addActionListener(NewGameAction);
		JMenuItem pauseItem = gameMenu.add("暂停");
		pauseItem.addActionListener(PauseAction);
		JMenuItem continueItem = gameMenu.add("继续");
		continueItem.addActionListener(ContinueAction);
		menu.add(gameMenu);

		// tries获取键盘输入需要使用setFocusable()方法设置参数为true
		tries.setFocusable(true);
		setVisible(true);
	}
}
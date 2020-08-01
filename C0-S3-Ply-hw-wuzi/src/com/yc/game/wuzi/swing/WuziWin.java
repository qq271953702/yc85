package com.yc.game.wuzi.swing;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.Socket;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import com.yc.game.common.base.TwoArrayGame;
import com.yc.game.common.swing.BoardLabel;
import com.yc.game.common.swing.BoardPanel;
import com.yc.game.common.swing.BoardWin;
import com.yc.game.common.util.MutiThread;
import com.yc.game.wuzi.base.WuziGame;
import com.yc.game.wuzi.core.Imgs;

/**
 * 游戏主窗口，创建时必须传入游戏对象（参数是接口）
 * @author 廖彦
 *
 */
public class WuziWin extends BoardWin {

	// 不解释
	private static final long serialVersionUID = 1L;
	private MouseAdapter ma;
	private WuziGame game;
	private MutiThread opponent;//对手
	
	{
		
		new Thread("显示对方下的棋") {
			public void run() {
				
			while(true) {
				synchronized (this) {
					if(opponent.isNew()==true) {
						   
						   //这个opponent.getColor()是自己棋子的颜色不是对方的！！！
							if (opponent.getColor()!=game.getColor()) {
							
							System.out.println("当前对方的位置："+opponent.getX()+"|"+opponent.getY());
							// 判断5子是否成立
							if (game.getWuzi() != null) {
								return;
							}
							
							// 获取当对方棋子坐标
							int x=opponent.getX();
							int y=opponent.getY();
							System.out.println("下棋："+x+"\t"+y);
						
							
							// 在我的棋盘上下对方的棋子
							game.down(x, y);
							// 刷新界面, 显示下的子
							refresh();
						
							// 如果五子连珠成立, 则提示完成
							if (game.getWuzi() != null) {
								String color = (int) game.getWinner() == WuziGame.BLACK ? "黑" : "白";
								JOptionPane.showMessageDialog(null, color + "棋赢了!");
							}
							opponent.setNew(false);
						 }
					}
			   }
					
			}
				
				
		}
	}.start();
}

	/**
	 * 构建窗体
	 */
	public WuziWin(WuziGame game) {
		super("开森五子棋", game, Imgs.CHESS);
		this.game = game;
	}
	
	/**
	 * 构建窗体
	 * @param game
	 * @param opponent  对方的socket
	 */
	public WuziWin(WuziGame game,MutiThread opponent,String side) {
		super("开森五子棋:"+side, game, Imgs.CHESS);
		this.game = game;
		this.opponent=opponent;
	}

	@Override
	protected BoardPanel customCreateBoard(TwoArrayGame game, ImageIcon[] cellIcons) {
		return new BoardPanel(game, cellIcons, Imgs.BOARD);
	}

	@Override
	protected void initBoardLabel(BoardLabel bl, int x, int y) {
		if (ma == null) {
			ma = new MouseAdapter() {
				@Override
				// 鼠标移动事件
				public void mouseEntered(MouseEvent e) {
					refresh();
					BoardLabel ml = (BoardLabel) e.getSource();
					String pointName = game.getPointerName(ml.getBoardX(), ml.getBoardY());
					Icon icon = Imgs.getPointIcon(pointName);
					ml.setIcon(icon);
				}

				@Override
				public void mousePressed(MouseEvent e) {
				
					synchronized (this) {
						//这个opponent.getColor()是自己棋子的颜色不是对方的！！！
						if (opponent.getColor()==game.getColor()) {
							if (game.getWuzi() != null) {
								return;
							}
							
							// 获取当前点击的控件
							BoardLabel ml = (BoardLabel) e.getSource();
							// 获取控件中保存的坐标, 并在该坐标处下子
							game.down(ml.getBoardX(), ml.getBoardY());
							// 刷新界面, 显示下的子
							refresh();
							
							//把自己下的棋子传给对方
							opponent.setMySend(true);  
							System.out.println("当前自己的位置:"+ml.getBoardX()+"\t"+ml.getBoardY());
							opponent.setX(ml.getBoardX());
							opponent.setY(ml.getBoardY());
							opponent.setMySend(false);  
							
							// 如果五子连珠成立, 则提示完成
							if (game.getWuzi() != null) {
								String color = (int) game.getWinner() == WuziGame.BLACK ? "黑" : "白";
								JOptionPane.showMessageDialog(null, color + "棋赢了!");
							}
						
						}
					}	
					
				}
			};
		}
		bl.addMouseListener(ma);
	}
	
	

}

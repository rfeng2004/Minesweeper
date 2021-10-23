import javafx.application.*;
import javafx.event.*;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.*;
import java.util.Timer;
import java.util.TimerTask;

public class MineSweeper extends Application
{
	int count;
	GridPane grid = new GridPane();
	Label label = new Label();
	Label timeLabel = new Label();
	Board b = new Board();
	int secPassed = 0;
	boolean started = false;

	Timer timer;
	class MyTimerTask extends TimerTask
	{
		public void run()
		{
			Platform.runLater(new Runnable()
			{
				public void run()
				{
					secPassed ++;
					timeLabel.setText("Time: " + secPassed);
				}
			});
		}
	}

	public static void main(String[] args)
	{
		launch(args);
	}

	class Handler implements EventHandler<MouseEvent>
	{
		private int x, y, id;
		Handler(int x, int y, int id)
		{
			this.x = x;
			this.y = y;
			this.id = id;
		}

		@Override
		public void handle(MouseEvent event)
		{	
			MouseButton mb = event.getButton();
			if(!started/*count == b.getSize()*b.getSize()*/)
			{
				timer.schedule(new MyTimerTask(), 1000, 1000);
			}
			started = true;
			if(mb == MouseButton.PRIMARY)
			{
				if(((Labeled) event.getSource()).getGraphic() == null)
				{
					grid.getChildren().remove(event.getSource());
					Label l = new Label();
					l.setPrefSize(50, 50);
					l.setFont(Font.font(30));
					if(id != -1) 
					{
						l.setText(Integer.toString(id));
						l.setAlignment(Pos.CENTER);
						l.setTextAlignment(TextAlignment.CENTER);
						grid.add(l, x, y);
						if(id == 0)
						{
							handleZero(x, y);
						}
					}
					else 
					{
						Image i = new Image(getClass().getResourceAsStream("mine.png"));
						//Image i = new Image(getClass().getResourceAsStream("//Users//raymondfeng//Documents//workspace//project//src//mine.png"));
						ImageView iv = new ImageView(i);
						iv.setFitWidth(50);
						iv.setFitHeight(50);
						iv.setSmooth(true);
						iv.setCache(true);
						l.setGraphic(iv);
						grid.add(l, x, y);
					}
					if(id == -1)
					{
						label.setText("Oh no! You've hit a mine.");
						b.end();
					}
					else
					{
						count--;
						if(count == b.getNumOfMines())
						{
							label.setText("You have won!");
							b.end();
						}
					}
				}
			}
			else if(mb == MouseButton.SECONDARY)
			{
				if(((Labeled) event.getSource()).getGraphic() == null)
				{
					Image i = new Image(getClass().getResourceAsStream("flag.jpg"));
					ImageView iv = new ImageView(i);
					iv.setFitHeight(30);
					iv.setFitWidth(30);
					iv.setSmooth(true);
					iv.setCache(true);
					((Labeled) event.getSource()).setGraphic(iv);
					set(label, b);
					if(id != -1)
					{
						label.setText("You have marked a wrong cell!");
						b.end();
					}
					if(getNumMinesLeft() == 0) 
					{
						label.setText("You have won!");
						b.end();
					}
				}
				else 
				{
					((Labeled) event.getSource()).setGraphic(null);
					set(label, b);
				}
			}
		}

		public void handleZero(int x, int y)
		{
			for(int n = x-1; n <= x+1; n++)
			{
				for(int m = y-1; m <= y+1; m++)
				{
					if((n >= 0 && n < b.getSize()) && (m >= 0 && m < b.getSize()) && !(n == x && m == y))
					{
						if(grid.getChildren().contains(b.getEntry(n, m).getButton()))
						{
							//System.out.println(n + ", " + m);
							grid.getChildren().remove(b.getEntry(n, m).getButton());
							count --;
							Label l = new Label(Integer.toString(b.getEntry(n, m).getID()));
							l.setPrefSize(50, 50);
							l.setFont(Font.font(30));
							l.setAlignment(Pos.CENTER);
							l.setTextAlignment(TextAlignment.CENTER);
							grid.add(l, n, m);
							if(b.getEntry(n, m).getID() == 0)
							{
								handleZero(n, m);
							}
						}
					}
				}
			}
		}
	}

	class Cell
	{
		//0-8 if not mine, -1 if mine
		private int id = 9;

		private int x;
		private int y;

		private Button cell = new Button();

		Cell(int x, int y)
		{
			this.x = x;
			this.y = y;
			cell.setText("");
		}

		public int getID()
		{
			return id;
		}

		public void setID(int id)
		{
			this.id = id;
		}

		public void setCell()
		{
			cell.setPrefSize(50, 50);
			grid.add(cell, x, y);
		}

		public Button getButton()
		{
			return cell;
		}
	}

	class Board
	{
		private int size = 12;
		private int numOfMines = (int)(size*size/8);
		private Cell board[][] = new Cell[size][size];

		Board()
		{
			for(int x = 0; x < size; x++)
			{
				for(int y = 0; y < size; y++) board[x][y] = new Cell(x, y);
			}
			count = size*size;
		}

		public void end()
		{
			timer.cancel();
			for(int i = 0; i < getSize(); i++)
			{
				for(int j = 0; j < getSize(); j++)
				{
					//getEntry(i, j).getButton().setOnMouseClicked(new EmptyHandler());
					grid.getChildren().remove(getEntry(i, j).getButton());
					Label l = new Label();
					l.setPrefSize(50, 50);
					l.setFont(Font.font(30));
					l.setAlignment(Pos.CENTER);
					l.setTextAlignment(TextAlignment.CENTER);
					int id = getEntry(i, j).getID();
					if(id != -1) l.setText(Integer.toString(id));
					else 
					{
						Image im = new Image(getClass().getResourceAsStream("mine.png"));
						ImageView iv = new ImageView(im);
						iv.setFitWidth(50);
						iv.setFitHeight(50);
						iv.setSmooth(true);
						iv.setCache(true);
						l.setGraphic(iv);
					}
					grid.add(l, i, j);
				}
			}
		}

		public int getNumOfMines()
		{
			return numOfMines;
		}

		public int getSize()
		{
			return size;
		}

		public Cell getEntry(int a, int b)
		{
			return board[a][b];
		}

		public void setBoard()
		{
			int x = 0, y = 0, count = 0;
			if(numOfMines > size*size) 
			{
				System.out.println("Number of mines is greater than the total number of squares!");
				System.out.println("Number of mines has been reset to 0.");
				numOfMines = 0;
			}
			while(count < numOfMines)
			{
				x = (int) (Math.random()*size);
				y = (int) (Math.random()*size);
				if(board[x][y].getID() != -1)
				{
					board[x][y].setID(-1);
					count++;
				}
			}
			for(int a = 0; a < size; a++)
			{
				for(int b = 0; b < size; b++)
				{
					board[a][b].setID(findID(a, b));
					board[a][b].getButton().setOnMouseClicked(new Handler(a, b, board[a][b].getID()));
					board[a][b].setCell();
				}
			}
		}
		public int findID(int a, int b)
		{
			int ret = 0;
			if(board[a][b].getID() == -1) return -1;
			for(int i = a-1; i<=a+1; i++)
			{
				for(int j = b-1; j<=b+1; j++)
				{
					if(i == a && j == b) continue;
					if((i >= 0 && i < size) && (j >= 0 && j < size))
					{
						if(board[i][j].getID() == -1) ret++;
					}
				}
			}
			return ret;
		}
	}

	public void start(Stage stage)
	{
		timer = new Timer();
		stage.setTitle("Mine Sweeper");
		timeLabel.setPrefSize(b.getSize()*50, 50);
		timeLabel.setText("Time: 0");
		timeLabel.setFont(Font.font(30));
		label.setPrefSize(b.getSize()*50, 50);
		set(label, b);
		grid.add(timeLabel, 0, b.getSize(), b.getSize(), 1);
		grid.add(label, 0, b.getSize() + 1, b.getSize(), 1);
		Scene scene = new Scene(grid, b.getSize()*50, b.getSize()*50 + 100);
		b.setBoard();
		stage.setScene(scene);
		stage.show();
	}

	public void set(Label l, Board b)
	{
		int c = getNumMinesLeft();
		l.setText("Mine(s) Left: " + c);
		l.setFont(Font.font(30));
	}

	public int getNumMinesLeft()
	{
		int c = b.getNumOfMines();
		for(int i = 0; i < b.getSize(); i++)
		{
			for(int j = 0; j < b.getSize(); j++)
			{
				if(b.getEntry(i, j).getButton().getGraphic() != null)
				{
					c--;
				}
			}
		}
		return c;
	}
}
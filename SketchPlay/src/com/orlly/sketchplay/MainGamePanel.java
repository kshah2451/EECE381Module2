package com.orlly.sketchplay;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;



public class MainGamePanel extends SurfaceView implements SurfaceHolder.Callback{

	private GameThread thread;
	private PlayerCharacter player;
	private Bitmap bg;
	private Bitmap temp_bg;
	private DirectionalButton right_key, left_key, up_key;
	int startx = 0;
	int starty = 0;
	int[][] map_array;
	private MapRender maprender;
	
	
	
	
	public MainGamePanel(Context context, Bitmap bitmap) {
		
		super(context);
		getHolder().addCallback(this);

		
		//create new player character
		player = new PlayerCharacter(BitmapFactory.decodeResource(getResources(), R.drawable.droid_1), startx, starty);

		//create new thread
		thread = new GameThread(getHolder(), this);
		
		right_key = new DirectionalButton(BitmapFactory.decodeResource(getResources(), R.drawable.right_arrow));
		left_key = new DirectionalButton(BitmapFactory.decodeResource(getResources(), R.drawable.left_arrow));
		up_key =  new DirectionalButton(BitmapFactory.decodeResource(getResources(), R.drawable.up_arrow));
		bg = bitmap;
		
		
		Log.d("size", "width:" + Integer.toString(getWidth()));
		Log.d("size", "height:" + Integer.toString(getHeight()));

		
//		maprender = new MapRender(bg, getWidth(), getHeight());
		
//		map_array = maprender.convertTo2DArray(maprender.getPixelArray(), maprender.getHeight(), maprender.getWidth());
//		Log.d("size", Integer.toString(map_array[0].length));
		//set to true so we can interact with surface
		setFocusable(true);
		
	}
	


	
	
//	
//	@Override
//	protected void onDraw(Canvas canvas) {
//		Rect destination = new Rect(0,0,getWidth(),getHeight());
//		Rect source = new Rect(0,0,bg.getWidth(),bg.getHeight());
//		Paint paint = new Paint();
//		canvas.drawBitmap(bg,source,destination,paint);
//	}




	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		
	}
	
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		
		Log.d("create", "w:"+Integer.toString(getWidth()));
		Log.d("create", "h:"+Integer.toString(getHeight()));

		
		bg = bg.createScaledBitmap(bg, this.getWidth(), this.getHeight(), false);
		maprender = new MapRender(bg, getWidth(), getHeight());
		map_array = maprender.convertTo2DArray(maprender.getPixelArray(), maprender.getHeight(), maprender.getWidth());
		temp_bg = Bitmap.createBitmap(maprender.getPixelArray(), getWidth(), getHeight(), Bitmap.Config.ARGB_8888);

		if(temp_bg == null){
			Log.d("bg", "null");

		}
		
		
		//upon creation of surface, start thread
		thread = new GameThread(getHolder(), this);
		thread.setRunning(true);
		thread.start();
		
		
		Log.d("debug", "row:"+Integer.toString(map_array.length));
		Log.d("debug", "column:"+Integer.toString(map_array[0].length));



		//allow onDraw method to be called (it's normally set to not draw)
		//this.setWillNotDraw(false);
		

	}
	
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		boolean retry = true;
		thread.setRunning(false);

		while(retry){
			try{
				thread.join();
				retry = false;
			
			} catch (InterruptedException e){
				
				
			}
			
		}
	
	}
	
	 public void drawImages(Canvas canvas){
/*		 canvas.drawColor(Color.GREEN);
			Log.d("size", "width outside:" + Integer.toString(getWidth()));
			Log.d("size", "height outside:" + Integer.toString(getHeight()));*/

		canvas.drawBitmap(temp_bg, 0, 0, null);
/*		Rect destination = new Rect(0, 0, getWidth(), getHeight());
		Rect source = new Rect(0, 0, bg.getWidth(), bg.getHeight());*/
	//	canvas.drawBitmap(bg, source, destination, null); 
		player.draw(canvas, player.getX(), player.getY());
		 right_key.draw(canvas, getWidth()-right_key.width, this.getHeight()-right_key.height);
		 left_key.draw(canvas, getWidth()-2*right_key.width, this.getHeight()-right_key.height);
		 up_key.draw(canvas, 0, this.getHeight()-up_key.height);
	}
	
@Override

//gotta rewrite so that it's checking button touches instead of player touches
	public boolean onTouchEvent(MotionEvent event){
		//when user presses the touchscreen
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			//call action handler to check what has happened
			//player.handleActionDown((int)event.getX(), (int)event.getY(), this, right_arrow);
			
			handleTouch((int)event.getX(), (int)event.getY());
		}
		if (event.getAction() == MotionEvent.ACTION_MOVE){
			if(!player.getIsJumping()){
				handleTouch((int)event.getX(), (int)event.getY());
			}
			
		}
		
		
		
		
		//when the user lifts their finger off the screen
		if (event.getAction() == MotionEvent.ACTION_UP) {
			// touch was released
			if (player.getTouch()) {
				player.setTouch(false);

			}
			
			if(player.getIsMoving()){
				player.setIsMoving(false);
			}
			
			
		}
		
		
		return true;
	} 


	//update player location
	public void update() {
		if(player.getIsJumping()){
			player.jump();
		}
		
		
		//if is moving
	//	move player if they're going right and aren't bounded by the right side
		if((player.getX() + player.getBitmap().getWidth() <= getWidth()) && (player.getDirection()== player.getMoveRate())){			
			for(int i = player.getX(); i < getWidth() ; i++){
				if(map_array[player.getY()][i] == Color.BLACK){
					player.setClosest_right_wall(i);
					break;
				}
			}

			//if(map_array[player.getY()][player.getX()+player.getBitmap().getWidth()] != Color.BLACK){	
			if(player.getX() != player.getClosest_right_wall()){
				Log.d("debug", "x position:"+Integer.toString(player.getX()));
				Log.d("debug", "y position:"+Integer.toString(player.getClosest_right_wall()));
			//	Log.d("debug", "array value:"+ Integer.toString(map_array[player.getX()+ player.getBitmap().getWidth()][player.getY()]));
				Log.d("debug", "colour:" + Integer.toString(Color.BLACK));
				

				player.move();
			}
		}
	// move player if they're going left and aren't bounded by the left side	
		else if ((player.getX() > 0) && (player.getDirection() == -(player.getMoveRate()))){
			//if(map_array[player.getY()][player.getX()-player.getBitmap().getWidth()] != Color.BLACK){	

				player.move();
			//}

		}
		
		if((player.getY() != starty) && (player.getIsJumping() == false)){
			player.descend();
		}
		
	}

	public void handleTouch(int x, int y){
		
		
		if((x > getWidth()-right_key.width) && (x <= getWidth())){ //right
			if((y > getHeight()-right_key.height) && (y < getHeight())){
				player.setTouch(true);
				player.setIsMoving(true);
				player.setDirection(player.getMoveRate());
			}
		}
		else if ((x > getWidth()-2*right_key.width) && (x <= getWidth()-right_key.width)){ //left
			if((y > getHeight()-right_key.height) && (y < getHeight())){
				player.setTouch(true);
				player.setIsMoving(true);
				player.setDirection(-(player.getMoveRate()));
			//	player.setIsJumping(true);

			}
		}
		else if((x > 0) && (x <= up_key.width)){    //jump
			if((y > getHeight()-up_key.height) && (y < getHeight())){
				player.setTouch(true);
				player.setIsJumping(true);
			}
		}
		
		else
			player.setTouch(false);
			
	}
	
	
	//Class for the up key -- removeable if we decide to use layout buttons
	class DirectionalButton{
		
		Bitmap bitmap;
		int width;
		int height;
		boolean touched;
		DirectionalButton(Bitmap bitmap){
			
			this.bitmap = bitmap;
			this.width = bitmap.getWidth();
			this.height = bitmap.getHeight();
			
			
		}
		
		
		void draw(Canvas canvas, int x, int y){
			
			canvas.drawBitmap(this.bitmap, x, y , null);

		}
		
		
	}	
	
	
	
	
}

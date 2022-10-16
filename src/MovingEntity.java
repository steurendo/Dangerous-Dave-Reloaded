import java.util.*;

public class MovingEntity extends Entity
{
	public final static int DEAD_COUNTER = 150;
	public final static int FIGURE_SPEED = 5;
	public final static int SHOOT_SPEED = 5;
	private final static int SHOOT_RELOAD = 200;
	
	private PointD baseLocation;
	private Shoot shoot;
	private double movingNumber;
	private boolean alive;
	private int deadCounter;
	private int shootReload;

	public MovingEntity(int texture, int code, int figuresNumber, PointD location, double width, double height, int scoreValue, boolean mortal)
	{
		super(texture, code, figuresNumber, location, width, height, scoreValue, mortal);
		
		baseLocation = location.clone();
		textureY = 58;
		if (code >= 1)
			textureY += 30;
		if (code >= 2)
			textureY += 34;
		if (code >= 3)
			textureY += 42;
		if (code >= 4)
			textureY += 40;
		if (code >= 5)
			textureY += 10;
		if (code >= 6)
			textureY += 16;
		if (code >= 7)
			textureY += 30;
		if (code >= 8)
			textureY += 32;
		if (code >= 9)
			textureY += 8;
		textureWidth = 720;
		textureHeight = 304;
		movingNumber = 0;
		alive = true;
		deadCounter = -1;
		shoot = new Shoot();
		shootReload = 0;
	}
	
	public boolean isAlive() { return alive; }
	public Shoot getShoot() { return shoot; }
	public void init()
	{
		visible = true;
		alive = true;
		deadCounter = -1;
		location = baseLocation.clone();
	}
	public void die()
	{
		deadCounter = DEAD_COUNTER;
		alive = false;
	}
	public boolean checkCollisionWithShoot(Shoot shoot)
	{
		PointD shootLocation;
		
		shootLocation = shoot.getLocation();
		return (Math.abs(location.x - shootLocation.x) < width / 2 + 8 &&
			    Math.abs(location.y - shootLocation.y) < height / 2 + 3);
	}
	public void update()
	{
		if (!alive && deadCounter >= 0)
			deadCounter--;
		if (deadCounter == 0)
			visible = false;
		if (shoot.isVisible())
			shoot.update();
		if (alive)
		{
			if (Math.abs(shoot.getX() - location.x) > 400)
				shoot.setDirection(0);
			if (shootReload > 0)
				shootReload--;
			if (shootReload == 0)
			{
				int direction;
				direction = new Random().nextInt(2) == 0 ? -1 : 1;
				shoot.setLocation(new PointD(location.x + (width / 2 * direction), location.y));
				shoot.setDirection(direction);
				shootReload = SHOOT_RELOAD;
			}
			if (code == 0)
			{
				location.x += Math.cos(movingNumber * 1.8) * 4;
				location.y += Math.sin(movingNumber * 10) * 2;
			}
			else if (code == 1)
			{
				location.x -= Math.sin(movingNumber * 2) * 2.6;
				location.y += Math.cos(movingNumber * 2) * 0.8;
			}
			else if (code == 2)
			{
				location.x -= Math.cos(movingNumber * 4) * 2;
				location.y += Math.sin(movingNumber * 4) * 2;
				location.x -= Math.cos(movingNumber * 15) * 1;
				location.y += Math.sin(movingNumber * 15) * 1;
			}
			else if (code == 3)
				location.x += Math.cos(movingNumber * 4) > 0 ? 4 : -4;
			else if (code == 4)
			{
				location.x += Math.cos(movingNumber * 4) * 1.4;
				location.y += Math.sin(movingNumber * 4) * 3.4;
			}
			else if (code == 5)
			{
				location.x -= Math.cos(movingNumber * 4) * 2;
				location.y += Math.sin(movingNumber * 4) * 2;
				location.x -= Math.cos(movingNumber * 15) * 1;
				location.y -= Math.sin(movingNumber * 15) * 1;
			}
			else if (code == 6)
			{
				location.x += Math.cos(movingNumber * 4) > 0 ? 4 : -4;
				location.y += Math.sin(movingNumber * 60) * 2;
			}
			else if (code == 7)
				location.x += Math.cos(movingNumber * 4) * 4;
			movingNumber = (movingNumber + 0.01) % 360;
		}
	}
}